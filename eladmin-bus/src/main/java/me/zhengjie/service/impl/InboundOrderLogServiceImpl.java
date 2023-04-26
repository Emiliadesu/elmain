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

import me.zhengjie.domain.InboundOrderLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.InboundOrderLogRepository;
import me.zhengjie.service.InboundOrderLogService;
import me.zhengjie.service.dto.InboundOrderLogDto;
import me.zhengjie.service.dto.InboundOrderLogQueryCriteria;
import me.zhengjie.service.mapstruct.InboundOrderLogMapper;
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
* @date 2021-06-09
**/
@Service
@RequiredArgsConstructor
public class InboundOrderLogServiceImpl implements InboundOrderLogService {

    private final InboundOrderLogRepository inboundOrderLogRepository;
    private final InboundOrderLogMapper inboundOrderLogMapper;

    @Override
    public Map<String,Object> queryAll(InboundOrderLogQueryCriteria criteria, Pageable pageable){
        Page<InboundOrderLog> page = inboundOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(inboundOrderLogMapper::toDto));
    }

    @Override
    public List<InboundOrderLogDto> queryAll(InboundOrderLogQueryCriteria criteria){
        return inboundOrderLogMapper.toDto(inboundOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public InboundOrderLogDto findById(Long id) {
        InboundOrderLog inboundOrderLog = inboundOrderLogRepository.findById(id).orElseGet(InboundOrderLog::new);
        ValidationUtil.isNull(inboundOrderLog.getId(),"InboundOrderLog","id",id);
        return inboundOrderLogMapper.toDto(inboundOrderLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundOrderLogDto create(InboundOrderLog resources) {
        return inboundOrderLogMapper.toDto(inboundOrderLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InboundOrderLog resources) {
        InboundOrderLog inboundOrderLog = inboundOrderLogRepository.findById(resources.getId()).orElseGet(InboundOrderLog::new);
        ValidationUtil.isNull( inboundOrderLog.getId(),"InboundOrderLog","id",resources.getId());
        inboundOrderLog.copy(resources);
        inboundOrderLogRepository.save(inboundOrderLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            inboundOrderLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InboundOrderLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundOrderLogDto inboundOrderLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", inboundOrderLog.getOrderId());
            map.put("订单号", inboundOrderLog.getOrderNo());
            map.put("操作节点", inboundOrderLog.getOptNode());
            map.put("请求报文", inboundOrderLog.getReqMsg());
            map.put("返回报文", inboundOrderLog.getResMsg());
            map.put("关键字", inboundOrderLog.getKeyWord());
            map.put("执行机器", inboundOrderLog.getHost());
            map.put("是否成功", inboundOrderLog.getSuccess());
            map.put("描述", inboundOrderLog.getMsg());
            map.put("创建人", inboundOrderLog.getCreateBy());
            map.put("创建时间", inboundOrderLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}