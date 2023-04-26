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

import me.zhengjie.domain.OutboundOrderLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OutboundOrderLogRepository;
import me.zhengjie.service.OutboundOrderLogService;
import me.zhengjie.service.dto.OutboundOrderLogDto;
import me.zhengjie.service.dto.OutboundOrderLogQueryCriteria;
import me.zhengjie.service.mapstruct.OutboundOrderLogMapper;
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
* @author luob
* @date 2021-07-13
**/
@Service
@RequiredArgsConstructor
public class OutboundOrderLogServiceImpl implements OutboundOrderLogService {

    private final OutboundOrderLogRepository outboundOrderLogRepository;
    private final OutboundOrderLogMapper outboundOrderLogMapper;

    @Override
    public Map<String,Object> queryAll(OutboundOrderLogQueryCriteria criteria, Pageable pageable){
        Page<OutboundOrderLog> page = outboundOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(outboundOrderLogMapper::toDto));
    }

    @Override
    public List<OutboundOrderLogDto> queryAll(OutboundOrderLogQueryCriteria criteria){
        return outboundOrderLogMapper.toDto(outboundOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OutboundOrderLogDto findById(Long id) {
        OutboundOrderLog outboundOrderLog = outboundOrderLogRepository.findById(id).orElseGet(OutboundOrderLog::new);
        ValidationUtil.isNull(outboundOrderLog.getId(),"OutboundOrderLog","id",id);
        return outboundOrderLogMapper.toDto(outboundOrderLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundOrderLogDto create(OutboundOrderLog resources) {
        return outboundOrderLogMapper.toDto(outboundOrderLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OutboundOrderLog resources) {
        OutboundOrderLog outboundOrderLog = outboundOrderLogRepository.findById(resources.getId()).orElseGet(OutboundOrderLog::new);
        ValidationUtil.isNull( outboundOrderLog.getId(),"OutboundOrderLog","id",resources.getId());
        outboundOrderLog.copy(resources);
        outboundOrderLogRepository.save(outboundOrderLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            outboundOrderLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OutboundOrderLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OutboundOrderLogDto outboundOrderLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", outboundOrderLog.getOrderId());
            map.put("订单号", outboundOrderLog.getOrderNo());
            map.put("操作节点", outboundOrderLog.getOptNode());
            map.put("请求报文", outboundOrderLog.getReqMsg());
            map.put("返回报文", outboundOrderLog.getResMsg());
            map.put("关键字", outboundOrderLog.getKeyWord());
            map.put("执行机器", outboundOrderLog.getHost());
            map.put("是否成功", outboundOrderLog.getSuccess());
            map.put("描述", outboundOrderLog.getMsg());
            map.put("创建人", outboundOrderLog.getCreateBy());
            map.put("创建时间", outboundOrderLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}