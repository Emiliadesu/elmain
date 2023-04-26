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

import me.zhengjie.domain.DataOrderDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DataOrderDetailRepository;
import me.zhengjie.service.DataOrderDetailService;
import me.zhengjie.service.dto.DataOrderDetailDto;
import me.zhengjie.service.dto.DataOrderDetailQueryCriteria;
import me.zhengjie.service.mapstruct.DataOrderDetailMapper;
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
public class DataOrderDetailServiceImpl implements DataOrderDetailService {

    private final DataOrderDetailRepository dataOrderDetailRepository;
    private final DataOrderDetailMapper dataOrderDetailMapper;

    @Override
    public Map<String,Object> queryAll(DataOrderDetailQueryCriteria criteria, Pageable pageable){
        Page<DataOrderDetail> page = dataOrderDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dataOrderDetailMapper::toDto));
    }

    @Override
    public List<DataOrderDetailDto> queryAll(DataOrderDetailQueryCriteria criteria){
        return dataOrderDetailMapper.toDto(dataOrderDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DataOrderDetailDto findById(Long id) {
        DataOrderDetail dataOrderDetail = dataOrderDetailRepository.findById(id).orElseGet(DataOrderDetail::new);
        ValidationUtil.isNull(dataOrderDetail.getId(),"DataOrderDetail","id",id);
        return dataOrderDetailMapper.toDto(dataOrderDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataOrderDetailDto create(DataOrderDetail resources) {
        return dataOrderDetailMapper.toDto(dataOrderDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DataOrderDetail resources) {
        DataOrderDetail dataOrderDetail = dataOrderDetailRepository.findById(resources.getId()).orElseGet(DataOrderDetail::new);
        ValidationUtil.isNull( dataOrderDetail.getId(),"DataOrderDetail","id",resources.getId());
        dataOrderDetail.copy(resources);
        dataOrderDetailRepository.save(dataOrderDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dataOrderDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DataOrderDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DataOrderDetailDto dataOrderDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", dataOrderDetail.getOrderNo());
            map.put("交易号", dataOrderDetail.getCrossBorderNo());
            map.put("客户ID", dataOrderDetail.getCustomersId());
            map.put("店铺ID", dataOrderDetail.getShopId());
            map.put("客户名称", dataOrderDetail.getCustomersName());
            map.put("店铺名称", dataOrderDetail.getShopName());
            map.put("运单号", dataOrderDetail.getLogisticsNo());
            map.put("货品ID", dataOrderDetail.getGoodsId());
            map.put("货品编码", dataOrderDetail.getGoodsCode());
            map.put("海关货号", dataOrderDetail.getGoodsNo());
            map.put("HS编码", dataOrderDetail.getHsCode());
            map.put("前端商品名称", dataOrderDetail.getFontGoodsName());
            map.put("商品名称", dataOrderDetail.getGoodsName());
            map.put("数量", dataOrderDetail.getQty());
            map.put("实付总价", dataOrderDetail.getPayment());
            map.put("完税单价", dataOrderDetail.getDutiableValue());
            map.put("完税总价", dataOrderDetail.getDutiableTotalValue());
            map.put("关税", dataOrderDetail.getTariffAmount());
            map.put("增值税", dataOrderDetail.getAddedValueTaxAmount());
            map.put("消费税", dataOrderDetail.getConsumptionDutyAmount());
            map.put("耗材编码", dataOrderDetail.getConsumableMaterialCode());
            map.put("耗材数量", dataOrderDetail.getConsumableMaterialNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}