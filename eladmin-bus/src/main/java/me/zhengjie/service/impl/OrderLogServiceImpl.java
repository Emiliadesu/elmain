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

import me.zhengjie.domain.OrderLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderLogRepository;
import me.zhengjie.service.OrderLogService;
import me.zhengjie.service.dto.OrderLogDto;
import me.zhengjie.service.dto.OrderLogQueryCriteria;
import me.zhengjie.service.mapstruct.OrderLogMapper;
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
* @date 2021-03-27
**/
@Service
@RequiredArgsConstructor
public class OrderLogServiceImpl implements OrderLogService {

    private final OrderLogRepository orderLogRepository;
    private final OrderLogMapper orderLogMapper;

    @Override
    public Map<String,Object> queryAll(OrderLogQueryCriteria criteria, Pageable pageable){
        Page<OrderLog> page = orderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderLogMapper::toDto));
    }

    @Override
    public List<OrderLogDto> queryAll(OrderLogQueryCriteria criteria){
        return orderLogMapper.toDto(orderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderLogDto findById(Long id) {
        OrderLog orderLog = orderLogRepository.findById(id).orElseGet(OrderLog::new);
        ValidationUtil.isNull(orderLog.getId(),"OrderLog","id",id);
        return orderLogMapper.toDto(orderLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderLogDto create(OrderLog resources) {
        return orderLogMapper.toDto(orderLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderLog resources) {
        OrderLog orderLog = orderLogRepository.findById(resources.getId()).orElseGet(OrderLog::new);
        ValidationUtil.isNull( orderLog.getId(),"OrderLog","id",resources.getId());
        orderLog.copy(resources);
        orderLogRepository.save(orderLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderLogDto orderLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", orderLog.getOrderId());
            map.put("订单号", orderLog.getOrderNo());
            map.put("操作节点", orderLog.getOptNode());
            map.put("请求报文", orderLog.getReqMsg());
            map.put("返回报文", orderLog.getResMsg());
            map.put("关键字", orderLog.getKeyWord());
            map.put("执行机器", orderLog.getHost());
            map.put("是否成功", orderLog.getSuccess());
            map.put("描述", orderLog.getMsg());
            map.put("创建人", orderLog.getCreateBy());
            map.put("创建时间", orderLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}