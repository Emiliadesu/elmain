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

import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CrossBorderOrderDetailsRepository;
import me.zhengjie.service.CrossBorderOrderDetailsService;
import me.zhengjie.service.dto.CrossBorderOrderDetailsDto;
import me.zhengjie.service.dto.CrossBorderOrderDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.CrossBorderOrderDetailsMapper;
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
* @date 2021-03-25
**/
@Service
@RequiredArgsConstructor
public class CrossBorderOrderDetailsServiceImpl implements CrossBorderOrderDetailsService {

    private final CrossBorderOrderDetailsRepository crossBorderOrderDetailsRepository;
    private final CrossBorderOrderDetailsMapper crossBorderOrderDetailsMapper;

    @Override
    public Map<String,Object> queryAll(CrossBorderOrderDetailsQueryCriteria criteria, Pageable pageable){
        Page<CrossBorderOrderDetails> page = crossBorderOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(crossBorderOrderDetailsMapper::toDto));
    }

    @Override
    public List<CrossBorderOrderDetailsDto> queryAll(CrossBorderOrderDetailsQueryCriteria criteria){
        return crossBorderOrderDetailsMapper.toDto(crossBorderOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CrossBorderOrderDetailsDto findById(Long id) {
        CrossBorderOrderDetails crossBorderOrderDetails = crossBorderOrderDetailsRepository.findById(id).orElseGet(CrossBorderOrderDetails::new);
        ValidationUtil.isNull(crossBorderOrderDetails.getId(),"CrossBorderOrderDetails","id",id);
        return crossBorderOrderDetailsMapper.toDto(crossBorderOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CrossBorderOrderDetailsDto create(CrossBorderOrderDetails resources) {
        return crossBorderOrderDetailsMapper.toDto(crossBorderOrderDetailsRepository.save(resources));
    }

    @Override
    public void createAll(List<CrossBorderOrderDetails> itemList) {
        crossBorderOrderDetailsRepository.saveAll(itemList);
    }

    @Override
    public List<CrossBorderOrderDetails> queryByOrderId(Long id) {
        CrossBorderOrderDetailsQueryCriteria criteria = new CrossBorderOrderDetailsQueryCriteria();
        criteria.setOrderId(id);
        List<CrossBorderOrderDetails> list = crossBorderOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return list;
    }

    @Override
    public List<CrossBorderOrderDetailsDto> toDtoList(List<CrossBorderOrderDetails> itemList) {
        return crossBorderOrderDetailsMapper.toDto(itemList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CrossBorderOrderDetails resources) {
        CrossBorderOrderDetails crossBorderOrderDetails = crossBorderOrderDetailsRepository.findById(resources.getId()).orElseGet(CrossBorderOrderDetails::new);
        ValidationUtil.isNull( crossBorderOrderDetails.getId(),"CrossBorderOrderDetails","id",resources.getId());
        crossBorderOrderDetails.copy(resources);
        crossBorderOrderDetailsRepository.save(crossBorderOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBatch(List<CrossBorderOrderDetails> itemList) {
        crossBorderOrderDetailsRepository.saveAll(itemList);
    }

    @Override
    public CrossBorderOrderDetails queryByOrderIdAndBarCode(Long orderId, String barCode) {
        return crossBorderOrderDetailsRepository.findByOrderIdAndBarCode(orderId, barCode);
    }

    @Override
    public CrossBorderOrderDetails queryByGoodsCode(Long orderId) {
        return crossBorderOrderDetailsRepository.findByGoodsCode(orderId);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            crossBorderOrderDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CrossBorderOrderDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CrossBorderOrderDetailsDto crossBorderOrderDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", crossBorderOrderDetails.getOrderId());
            map.put("订单号", crossBorderOrderDetails.getOrderNo());
            map.put("货品ID", crossBorderOrderDetails.getGoodsId());
            map.put("货品编码", crossBorderOrderDetails.getGoodsCode());
            map.put("海关货号", crossBorderOrderDetails.getGoodsNo());
            map.put("HS编码", crossBorderOrderDetails.getHsCode());
            map.put("商品名称", crossBorderOrderDetails.getGoodsName());
            map.put("数量", crossBorderOrderDetails.getQty());
            map.put("计量单位", crossBorderOrderDetails.getUnit());
            map.put("实付总价", crossBorderOrderDetails.getPayment());
            map.put("完税单价", crossBorderOrderDetails.getDutiableValue());
            map.put("关税", crossBorderOrderDetails.getTariffAmount());
            map.put("增值税", crossBorderOrderDetails.getAddedValueTaxAmount());
            map.put("消费税", crossBorderOrderDetails.getConsumptionDutyAmount());
            map.put("总税额", crossBorderOrderDetails.getTaxAmount());
            map.put("原产国", crossBorderOrderDetails.getMakeCountry());
            map.put("净重（千克）", crossBorderOrderDetails.getNetWeight());
            map.put("毛重（千克）", crossBorderOrderDetails.getGrossWeight());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


}