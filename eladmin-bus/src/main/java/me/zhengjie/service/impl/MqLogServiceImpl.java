/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.service.impl;

import me.zhengjie.domain.MqLog;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.mq.CBReturnProducer;
import me.zhengjie.service.DouyinService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.MqLogRepository;
import me.zhengjie.service.MqLogService;
import me.zhengjie.service.dto.MqLogDto;
import me.zhengjie.service.dto.MqLogQueryCriteria;
import me.zhengjie.service.mapstruct.MqLogMapper;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-03-27
**/
@Service
@RequiredArgsConstructor
public class MqLogServiceImpl implements MqLogService {

    private final MqLogRepository MqLogRepository;
    private final MqLogMapper MqLogMapper;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private CBReturnProducer cbReturnProducer;

    @Override
    public Map<String,Object> queryAll(MqLogQueryCriteria criteria, Pageable pageable){
        Page<MqLog> page = MqLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(MqLogMapper::toDto));
    }

    @Override
    public List<MqLogDto> queryAll(MqLogQueryCriteria criteria){
        return MqLogMapper.toDto(MqLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public MqLogDto findById(Long id) {
        MqLog MqLog = MqLogRepository.findById(id).orElseGet(MqLog::new);
        ValidationUtil.isNull(MqLog.getId(),"MqLog","id",id);
        return MqLogMapper.toDto(MqLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public MqLogDto create(MqLog resources) {
        return MqLogMapper.toDto(MqLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(MqLog resources) {
        MqLog MqLog = MqLogRepository.findById(resources.getId()).orElseGet(MqLog::new);
        ValidationUtil.isNull( MqLog.getId(),"MqLog","id",resources.getId());
        MqLog.copy(resources);
        MqLogRepository.save(MqLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            MqLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<MqLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MqLogDto MqLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("topic", MqLog.getTopic());
            map.put("tag", MqLog.getTag());
            map.put("key", MqLog.getMsgKey());
            map.put("消息内容", MqLog.getBody());
            map.put("执行机器", MqLog.getHost());
            map.put("是否成功", MqLog.getSuccess());
            map.put("描述", MqLog.getMsg());
            map.put("创建人", MqLog.getCreateBy());
            map.put("创建时间", MqLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
    @Autowired
    private DouyinService douyinService;
    @Override
    public void rePush(String ids) {
        if (StringUtil.isBlank(ids))
            throw new BadRequestException("未选择任何记录");
        String[] idArr = ids.split(",");
        for (String idStr : idArr) {
            MqLogDto mqLogDto = findById(Long.valueOf(idStr));
            if (StringUtil.equals(MsgType.DY_CREATE_WAREHOUSE_FEE_ORDER,mqLogDto.getTag())){
                try {
                    douyinService.createWarehouseFeeOrder(mqLogDto.getBody());
                }catch (Exception e){
                    e.printStackTrace();
                }
            } else if (StringUtil.equals(mqLogDto.getTopic(),"CB_ORDER")){
                cbOrderProducer.send(
                        mqLogDto.getTag(),
                        mqLogDto.getBody(),
                        mqLogDto.getMsgKey()
                );
            }else if (StringUtil.equals(mqLogDto.getTopic(),"CB_RETURN")){
                cbReturnProducer.send(
                        mqLogDto.getTag(),
                        mqLogDto.getBody(),
                        mqLogDto.getMsgKey()
                );
            }else
                throw new BadRequestException("msgKey:"+mqLogDto.getMsgKey()+"暂不支持"+mqLogDto.getTag()+"重推操作");
        }
    }
}