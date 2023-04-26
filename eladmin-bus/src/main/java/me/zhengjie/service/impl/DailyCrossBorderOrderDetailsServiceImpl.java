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
import me.zhengjie.domain.DailyCrossBorderOrderDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DailyCrossBorderOrderDetailsRepository;
import me.zhengjie.service.DailyCrossBorderOrderDetailsService;
import me.zhengjie.service.dto.DailyCrossBorderOrderDetailsDto;
import me.zhengjie.service.dto.DailyCrossBorderOrderDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.DailyCrossBorderOrderDetailsMapper;
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
* @date 2022-07-04
**/
@Service
@RequiredArgsConstructor
public class DailyCrossBorderOrderDetailsServiceImpl implements DailyCrossBorderOrderDetailsService {

    private final DailyCrossBorderOrderDetailsRepository dailyCrossBorderOrderDetailsRepository;
    private final DailyCrossBorderOrderDetailsMapper dailyCrossBorderOrderDetailsMapper;

    @Override
    public Map<String,Object> queryAll(DailyCrossBorderOrderDetailsQueryCriteria criteria, Pageable pageable){
        Page<DailyCrossBorderOrderDetails> page = dailyCrossBorderOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dailyCrossBorderOrderDetailsMapper::toDto));
    }

    @Override
    public List<DailyCrossBorderOrderDetailsDto> queryAll(DailyCrossBorderOrderDetailsQueryCriteria criteria){
        return dailyCrossBorderOrderDetailsMapper.toDto(dailyCrossBorderOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DailyCrossBorderOrderDetailsDto findById(Long id) {
        DailyCrossBorderOrderDetails dailyCrossBorderOrderDetails = dailyCrossBorderOrderDetailsRepository.findById(id).orElseGet(DailyCrossBorderOrderDetails::new);
        ValidationUtil.isNull(dailyCrossBorderOrderDetails.getId(),"DailyCrossBorderOrderDetails","id",id);
        return dailyCrossBorderOrderDetailsMapper.toDto(dailyCrossBorderOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyCrossBorderOrderDetailsDto create(DailyCrossBorderOrderDetails resources) {
        return dailyCrossBorderOrderDetailsMapper.toDto(dailyCrossBorderOrderDetailsRepository.save(resources));
    }

    @Override
    public void createAll(List<DailyCrossBorderOrderDetails> itemList) {
        dailyCrossBorderOrderDetailsRepository.saveAll(itemList);
    }

    @Override
    public List<DailyCrossBorderOrderDetails> findByOrderId(Long id) {
        return dailyCrossBorderOrderDetailsRepository.findByOrderId(id);
    }

    @Override
    public DailyCrossBorderOrderDetails findDetailId(Long detailId) {
        return dailyCrossBorderOrderDetailsRepository.findByDetailId(detailId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DailyCrossBorderOrderDetails resources) {
        DailyCrossBorderOrderDetails dailyCrossBorderOrderDetails = dailyCrossBorderOrderDetailsRepository.findById(resources.getId()).orElseGet(DailyCrossBorderOrderDetails::new);
        ValidationUtil.isNull( dailyCrossBorderOrderDetails.getId(),"DailyCrossBorderOrderDetails","id",resources.getId());
        dailyCrossBorderOrderDetails.copy(resources);
        dailyCrossBorderOrderDetailsRepository.save(dailyCrossBorderOrderDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dailyCrossBorderOrderDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DailyCrossBorderOrderDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DailyCrossBorderOrderDetailsDto dailyCrossBorderOrderDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", dailyCrossBorderOrderDetails.getOrderId());
            map.put("订单号", dailyCrossBorderOrderDetails.getOrderNo());
            map.put("状态", dailyCrossBorderOrderDetails.getStatus());
            map.put("客户ID", dailyCrossBorderOrderDetails.getCustomersId());
            map.put("店铺ID", dailyCrossBorderOrderDetails.getShopId());
            map.put("客户名称", dailyCrossBorderOrderDetails.getCustomersName());
            map.put("店铺名称", dailyCrossBorderOrderDetails.getShopName());
            map.put("时间", dailyCrossBorderOrderDetails.getDayTime());
            map.put("运单号", dailyCrossBorderOrderDetails.getLogisticsNo());
            map.put("省", dailyCrossBorderOrderDetails.getProvince());
            map.put("市", dailyCrossBorderOrderDetails.getCity());
            map.put("区", dailyCrossBorderOrderDetails.getDistrict());
            map.put("收货地址", dailyCrossBorderOrderDetails.getConsigneeAddr());
            map.put("支付时间", dailyCrossBorderOrderDetails.getPayTime());
            map.put("订单创建时间", dailyCrossBorderOrderDetails.getOrderCreateTime());
            map.put("出库时间", dailyCrossBorderOrderDetails.getDeliverTime());
            map.put("是否揽收", dailyCrossBorderOrderDetails.getLogisticsCollect());
            map.put("快递揽收时间", dailyCrossBorderOrderDetails.getLogisticsCollectTime());
            map.put("货品ID", dailyCrossBorderOrderDetails.getGoodsId());
            map.put("货品编码", dailyCrossBorderOrderDetails.getGoodsCode());
            map.put("商品名称", dailyCrossBorderOrderDetails.getGoodsName());
            map.put("商品海关备案名", dailyCrossBorderOrderDetails.getFontGoodsName());
            map.put("海关货号", dailyCrossBorderOrderDetails.getGoodsNo());
            map.put("条码", dailyCrossBorderOrderDetails.getBarCode());
            map.put("数量", dailyCrossBorderOrderDetails.getQty());
            map.put("实付总价", dailyCrossBorderOrderDetails.getPayment());
            map.put("总税额", dailyCrossBorderOrderDetails.getTaxAmount());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}