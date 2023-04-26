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

import cn.hutool.core.collection.CollectionUtil;
import me.zhengjie.domain.DetailType;
import me.zhengjie.domain.StockAttr;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.StockAttrService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DetailTypeRepository;
import me.zhengjie.service.DetailTypeService;
import me.zhengjie.service.dto.DetailTypeDto;
import me.zhengjie.service.dto.DetailTypeQueryCriteria;
import me.zhengjie.service.mapstruct.DetailTypeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-04-15
**/
@Service
@RequiredArgsConstructor
public class DetailTypeServiceImpl implements DetailTypeService {

    private final DetailTypeRepository detailTypeRepository;
    private final DetailTypeMapper detailTypeMapper;

    @Autowired
    private StockAttrService stockAttrService;

    @Override
    public Map<String,Object> queryAll(DetailTypeQueryCriteria criteria, Pageable pageable){
        Page<DetailType> page = detailTypeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(detailTypeMapper::toDto));
    }

    @Override
    public List<DetailTypeDto> queryAll(DetailTypeQueryCriteria criteria){
        return detailTypeMapper.toDto(detailTypeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DetailTypeDto findById(Long id) {
        DetailType detailType = detailTypeRepository.findById(id).orElseGet(DetailType::new);
        ValidationUtil.isNull(detailType.getId(),"DetailType","id",id);
        return detailTypeMapper.toDto(detailType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DetailTypeDto create(DetailType resources) {
        return detailTypeMapper.toDto(detailTypeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DetailType resources) {
        DetailType detailType = detailTypeRepository.findById(resources.getId()).orElseGet(DetailType::new);
        ValidationUtil.isNull( detailType.getId(),"DetailType","id",resources.getId());
        detailType.copy(resources);
        detailTypeRepository.save(detailType);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            detailTypeRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DetailTypeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DetailTypeDto detailType : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("租户编号", detailType.getTenantCode());
            map.put("仓库id", detailType.getWarehouseId());
            map.put("商家id", detailType.getMerchantId());
            map.put("产品id", detailType.getProductId());
            map.put("原始批次号", detailType.getLotNo());
            map.put("客户批次号", detailType.getCustomerBatchNo());
            map.put("变更前细分类型", detailType.getFmSubType());
            map.put("目标细分类型", detailType.getToSubType());
            map.put("通知时间", detailType.getCreateTime());
            map.put("是否完成", detailType.getIsComplete());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void complete(Long id) {
        DetailType detailType=detailTypeRepository.findById(id).orElse(null);
        if (detailType==null)
            throw new BadRequestException("数据不存在");
        List<StockAttr>stockAttrs=stockAttrService.findByCustomerBatchNoAndSubType(detailType.getCustomerBatchNo(),detailType.getFmSubType());
        if (CollectionUtil.isEmpty(stockAttrs))
            throw new BadRequestException("没有该客户批次号的库存");
        for (StockAttr stockAttr : stockAttrs) {
            stockAttr.setSubType(detailType.getToSubType());
            stockAttr.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            stockAttrService.update(stockAttr);
        }
        detailType.setIsComplete("1");
        detailType.setCompleteOpter(SecurityUtils.getCurrentUsername());
        update(detailType);
    }

    @Override
    public boolean add(DetailType detailType, String customersCode) {
        if (StringUtil.isEmpty(detailType.getTenantCode()))
            throw new BadRequestException("tenantCode不能为空");
        if (StringUtil.isEmpty(detailType.getWarehouseId()))
            throw new BadRequestException("warehouseId不能为空");
        if (StringUtil.isEmpty(detailType.getMerchantId()))
            throw new BadRequestException("merchantId不能为空");
        if (StringUtil.isEmpty(detailType.getCustomerBatchNo()))
            throw new BadRequestException("customerBatchNo不能为空");
        if (StringUtil.isEmpty(detailType.getFmSubType()))
            throw new BadRequestException("fmSubType不能为空");
        if (StringUtil.isEmpty(detailType.getToSubType()))
            throw new BadRequestException("toSubType不能为空");
        detailType.setIsComplete("0");
        detailType.setCreateTime(DateUtils.now());
        create(detailType);
        return true;
    }
}
