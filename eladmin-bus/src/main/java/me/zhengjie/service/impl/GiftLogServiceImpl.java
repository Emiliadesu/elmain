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

import me.zhengjie.domain.GiftLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.GiftLogRepository;
import me.zhengjie.service.GiftLogService;
import me.zhengjie.service.dto.GiftLogDto;
import me.zhengjie.service.dto.GiftLogQueryCriteria;
import me.zhengjie.service.mapstruct.GiftLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author leningzhou
* @date 2022-01-17
**/
@Service
@RequiredArgsConstructor
public class GiftLogServiceImpl implements GiftLogService {

    private final GiftLogRepository giftLogRepository;
    private final GiftLogMapper giftLogMapper;

    @Override
    public Map<String,Object> queryAll(GiftLogQueryCriteria criteria, Pageable pageable){
        Page<GiftLog> page = giftLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(giftLogMapper::toDto));
    }

    @Override
    public List<GiftLogDto> queryAll(GiftLogQueryCriteria criteria){
        return giftLogMapper.toDto(giftLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public GiftLogDto findById(Long id) {
        GiftLog giftLog = giftLogRepository.findById(id).orElseGet(GiftLog::new);
        ValidationUtil.isNull(giftLog.getId(),"GiftLog","id",id);
        return giftLogMapper.toDto(giftLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public GiftLogDto create(GiftLog resources) {
        return giftLogMapper.toDto(giftLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(GiftLog resources) {
        GiftLog giftLog = giftLogRepository.findById(resources.getId()).orElseGet(GiftLog::new);
        ValidationUtil.isNull( giftLog.getId(),"GiftLog","id",resources.getId());
        giftLog.copy(resources);
        giftLogRepository.save(giftLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            giftLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<GiftLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (GiftLogDto giftLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("赠品ID", giftLog.getGiftId());
            map.put("赠品条码", giftLog.getGiftCode());
            map.put("操作节点", giftLog.getOptNode());
            map.put("请求报文", giftLog.getReqMsg());
            map.put("返回报文", giftLog.getResMsg());
            map.put("关键字", giftLog.getKeyWord());
            map.put("执行机器", giftLog.getHost());
            map.put("是否成功", giftLog.getSuccess());
            map.put("描述", giftLog.getMsg());
            map.put("花费时间", giftLog.getCostTime());
            map.put("时间描述", giftLog.getCostTimeMsg());
            map.put("创建人", giftLog.getCreateBy());
            map.put("创建时间", giftLog.getCreateTime());
            map.put("异常堆栈信息", giftLog.getStack());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}