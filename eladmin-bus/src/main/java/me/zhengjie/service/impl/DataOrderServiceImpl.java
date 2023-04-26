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

import me.zhengjie.domain.DataOrder;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DataOrderRepository;
import me.zhengjie.service.DataOrderService;
import me.zhengjie.service.dto.DataOrderDto;
import me.zhengjie.service.dto.DataOrderQueryCriteria;
import me.zhengjie.service.mapstruct.DataOrderMapper;
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
* @date 2022-03-30
**/
@Service
@RequiredArgsConstructor
public class DataOrderServiceImpl implements DataOrderService {

    private final DataOrderRepository dataOrderRepository;
    private final DataOrderMapper dataOrderMapper;

    @Override
    public Map<String,Object> queryAll(DataOrderQueryCriteria criteria, Pageable pageable){
        Page<DataOrder> page = dataOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dataOrderMapper::toDto));
    }

    @Override
    public List<DataOrderDto> queryAll(DataOrderQueryCriteria criteria){
        return dataOrderMapper.toDto(dataOrderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DataOrderDto findById(Long id) {
        DataOrder dataOrder = dataOrderRepository.findById(id).orElseGet(DataOrder::new);
        ValidationUtil.isNull(dataOrder.getId(),"DataOrder","id",id);
        return dataOrderMapper.toDto(dataOrder);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataOrderDto create(DataOrder resources) {
        return dataOrderMapper.toDto(dataOrderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DataOrder resources) {
        DataOrder dataOrder = dataOrderRepository.findById(resources.getId()).orElseGet(DataOrder::new);
        ValidationUtil.isNull( dataOrder.getId(),"DataOrder","id",resources.getId());
        dataOrder.copy(resources);
        dataOrderRepository.save(dataOrder);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dataOrderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DataOrderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DataOrderDto dataOrder : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", dataOrder.getOrderNo());
            map.put("交易号", dataOrder.getCrossBorderNo());
            map.put("客户ID", dataOrder.getCustomersId());
            map.put("店铺ID", dataOrder.getShopId());
            map.put("客户名称", dataOrder.getCustomersName());
            map.put("店铺名称", dataOrder.getShopName());
            map.put("清关抬头ID", dataOrder.getClearCompanyId());
            map.put("清关抬头名称", dataOrder.getClearCompanyName());
            map.put("电商平台代码", dataOrder.getPlatformCode());
            map.put("电商平台名称", dataOrder.getPlatformName());
            map.put("承运商ID", dataOrder.getSupplierId());
            map.put("承运商名称", dataOrder.getSupplierName());
            map.put("运单号", dataOrder.getLogisticsNo());
            map.put("申报单号", dataOrder.getDeclareNo());
            map.put("总署清单编号", dataOrder.getInvtNo());
            map.put("订单创建时间", dataOrder.getOrderCreateTime());
            map.put("实付金额", dataOrder.getPayment());
            map.put("关税", dataOrder.getTariffAmount());
            map.put("增值税", dataOrder.getAddedValueTaxAmount());
            map.put("消费税", dataOrder.getConsumptionDutyAmount());
            map.put("总税额", dataOrder.getTaxAmount());
            map.put("省", dataOrder.getProvince());
            map.put("市", dataOrder.getCity());
            map.put("区", dataOrder.getDistrict());
            map.put("包材编码", dataOrder.getMaterialCode());
            map.put("抖音4PL单", dataOrder.getFourPl());
            map.put("包裹重量", dataOrder.getPackWeight());
            map.put("清关开始时间", dataOrder.getClearStartTime());
            map.put("清关完成时间", dataOrder.getClearSuccessTime());
            map.put("出库时间", dataOrder.getDeliverTime());
            map.put("创建时间", dataOrder.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}