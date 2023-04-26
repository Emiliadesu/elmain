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

import me.zhengjie.domain.OrderReturnLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderReturnLogRepository;
import me.zhengjie.service.OrderReturnLogService;
import me.zhengjie.service.dto.OrderReturnLogDto;
import me.zhengjie.service.dto.OrderReturnLogQueryCriteria;
import me.zhengjie.service.mapstruct.OrderReturnLogMapper;
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
* @date 2021-04-22
**/
@Service
@RequiredArgsConstructor
public class OrderReturnLogServiceImpl implements OrderReturnLogService {

    private final OrderReturnLogRepository orderReturnLogRepository;
    private final OrderReturnLogMapper orderReturnLogMapper;

    @Override
    public Map<String,Object> queryAll(OrderReturnLogQueryCriteria criteria, Pageable pageable){
        Page<OrderReturnLog> page = orderReturnLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderReturnLogMapper::toDto));
    }

    @Override
    public List<OrderReturnLogDto> queryAll(OrderReturnLogQueryCriteria criteria){
        return orderReturnLogMapper.toDto(orderReturnLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderReturnLogDto findById(Long id) {
        OrderReturnLog orderReturnLog = orderReturnLogRepository.findById(id).orElseGet(OrderReturnLog::new);
        ValidationUtil.isNull(orderReturnLog.getId(),"OrderReturnLog","id",id);
        return orderReturnLogMapper.toDto(orderReturnLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderReturnLogDto create(OrderReturnLog resources) {
        return orderReturnLogMapper.toDto(orderReturnLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderReturnLog resources) {
        OrderReturnLog orderReturnLog = orderReturnLogRepository.findById(resources.getId()).orElseGet(OrderReturnLog::new);
        ValidationUtil.isNull( orderReturnLog.getId(),"OrderReturnLog","id",resources.getId());
        orderReturnLog.copy(resources);
        orderReturnLogRepository.save(orderReturnLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderReturnLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderReturnLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderReturnLogDto orderReturnLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", orderReturnLog.getReturnId());
            map.put("订单号", orderReturnLog.getReturnNo());
            map.put("操作节点", orderReturnLog.getOptNode());
            map.put("请求报文", orderReturnLog.getReqMsg());
            map.put("返回报文", orderReturnLog.getResMsg());
            map.put("执行机器", orderReturnLog.getHost());
            map.put("是否成功", orderReturnLog.getSuccess());
            map.put("描述", orderReturnLog.getMsg());
            map.put("创建人", orderReturnLog.getCreateBy());
            map.put("创建时间", orderReturnLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}