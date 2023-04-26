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

import me.zhengjie.domain.OrderReturnDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderReturnDetailsRepository;
import me.zhengjie.service.OrderReturnDetailsService;
import me.zhengjie.service.dto.OrderReturnDetailsDto;
import me.zhengjie.service.dto.OrderReturnDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.OrderReturnDetailsMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-04-14
**/
@Service
@RequiredArgsConstructor
public class OrderReturnDetailsServiceImpl implements OrderReturnDetailsService {

    private final OrderReturnDetailsRepository OrderReturnDetailsRepository;
    private final OrderReturnDetailsMapper OrderReturnDetailsMapper;

    @Override
    public List<OrderReturnDetails> queryByReturnId(Long id) {
        OrderReturnDetailsQueryCriteria criteria = new OrderReturnDetailsQueryCriteria();
        criteria.setReturnId(id);
        return OrderReturnDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<OrderReturnDetails> queryByReturnIdAndBarCode(Long id, String barcode) {
        List<OrderReturnDetails> byReturnIdAndBarCode = OrderReturnDetailsRepository.findByReturnIdAndBarCode(id, barcode);
        return byReturnIdAndBarCode;
    }

    @Override
    public void createAll(List<OrderReturnDetails> itemList) {
        OrderReturnDetailsRepository.saveAll(itemList);
    }

    @Override
    public Map<String,Object> queryAll(OrderReturnDetailsQueryCriteria criteria, Pageable pageable){
        Page<OrderReturnDetails> page = OrderReturnDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(OrderReturnDetailsMapper::toDto));
    }

    @Override
    public List<OrderReturnDetailsDto> queryAll(OrderReturnDetailsQueryCriteria criteria){
        return OrderReturnDetailsMapper.toDto(OrderReturnDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderReturnDetailsDto findById(Long id) {
        OrderReturnDetails OrderReturnDetails = OrderReturnDetailsRepository.findById(id).orElseGet(OrderReturnDetails::new);
        ValidationUtil.isNull(OrderReturnDetails.getId(),"OrderReturnDetails","id",id);
        return OrderReturnDetailsMapper.toDto(OrderReturnDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderReturnDetailsDto create(OrderReturnDetails resources) {
        return OrderReturnDetailsMapper.toDto(OrderReturnDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderReturnDetails resources) {
        OrderReturnDetails OrderReturnDetails = OrderReturnDetailsRepository.findById(resources.getId()).orElseGet(OrderReturnDetails::new);
        ValidationUtil.isNull( OrderReturnDetails.getId(),"OrderReturnDetails","id",resources.getId());
        OrderReturnDetails.copy(resources);
        OrderReturnDetailsRepository.save(OrderReturnDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            OrderReturnDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderReturnDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderReturnDetailsDto OrderReturnDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("退货单ID", OrderReturnDetails.getReturnId());
            map.put("物流订单号", OrderReturnDetails.getLogisticsNo());
            map.put("货品ID", OrderReturnDetails.getGoodsId());
            map.put("货品编码", OrderReturnDetails.getGoodsCode());
            map.put("海关货号", OrderReturnDetails.getGoodsNo());
            map.put("条形码", OrderReturnDetails.getBarCode());
            map.put("HS编码", OrderReturnDetails.getHsCode());
            map.put("前端商品名称", OrderReturnDetails.getFontGoodsName());
            map.put("商品名称", OrderReturnDetails.getGoodsName());
            map.put("数量", OrderReturnDetails.getQty());
            map.put("总税额", OrderReturnDetails.getTaxAmount());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


}