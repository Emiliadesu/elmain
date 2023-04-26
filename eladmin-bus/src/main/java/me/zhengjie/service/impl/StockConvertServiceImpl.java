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
import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.BusStockConvertDetail;
import me.zhengjie.domain.StockConvert;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BusStockConvertDetailService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockConvertRepository;
import me.zhengjie.service.StockConvertService;
import me.zhengjie.service.dto.StockConvertDto;
import me.zhengjie.service.dto.StockConvertQueryCriteria;
import me.zhengjie.service.mapstruct.StockConvertMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-04-18
**/
@Service
@RequiredArgsConstructor
public class StockConvertServiceImpl implements StockConvertService {

    private final StockConvertRepository stockConvertRepository;
    private final StockConvertMapper stockConvertMapper;
    private final BusStockConvertDetailService stockConvertDetailService;

    @Override
    public Map<String,Object> queryAll(StockConvertQueryCriteria criteria, Pageable pageable){
        Page<StockConvert> page = stockConvertRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockConvertMapper::toDto));
    }

    @Override
    public List<StockConvertDto> queryAll(StockConvertQueryCriteria criteria){
        return stockConvertMapper.toDto(stockConvertRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockConvertDto findById(Long id) {
        StockConvert stockConvert = stockConvertRepository.findById(id).orElseGet(StockConvert::new);
        ValidationUtil.isNull(stockConvert.getId(),"StockConvert","id",id);
        return stockConvertMapper.toDto(stockConvert);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockConvertDto create(StockConvert resources) {
        return stockConvertMapper.toDto(stockConvertRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockConvert resources) {
        StockConvert stockConvert = stockConvertRepository.findById(resources.getId()).orElseGet(StockConvert::new);
        ValidationUtil.isNull( stockConvert.getId(),"StockConvert","id",resources.getId());
        stockConvert.copy(resources);
        stockConvertRepository.save(stockConvert);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockConvertRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockConvertDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockConvertDto stockConvert : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("租户编号", stockConvert.getTenantCode());
            map.put("仓库id", stockConvert.getWarehouseId());
            map.put("转移单单号", stockConvert.getConvertNo());
            map.put("备注", stockConvert.getRemark());
            map.put("系统来源", stockConvert.getSourceSys());
            map.put("转移单类型", stockConvert.getConvertType());
            map.put("业务类型", stockConvert.getBusinessType());
            map.put("通知时间", stockConvert.getCreateTime());
            map.put("完成人", stockConvert.getCompleter());
            map.put("完成时间", stockConvert.getCompleteTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public boolean add(String decData, String customersCode) {
        StockConvert stockConvert= JSONObject.parseObject(decData,StockConvert.class);
        stockConvert.setCreateTime(DateUtils.now());
        if (StringUtil.isEmpty(stockConvert.getTenantCode()))
            throw new BadRequestException("tenantCode不能为空");
        if (StringUtil.isEmpty(stockConvert.getWarehouseId()))
            throw new BadRequestException("warehouseId不能为空");
        if (StringUtil.isEmpty(stockConvert.getConvertNo()))
            throw new BadRequestException("convertNo不能为空");
        if (stockConvert.getConvertType()==null)
            throw new BadRequestException("convertType不能为空");
        if (StringUtil.isEmpty(stockConvert.getSourceSys()))
            throw new BadRequestException("sourceSys不能为空");
        if (StringUtil.isEmpty(stockConvert.getStockConvertId()))
            throw new BadRequestException("id不能为空");
        if (CollectionUtil.isEmpty(stockConvert.getDetailList()))
            throw new BadRequestException("detailList不能为空");
        int i=0;
        for (BusStockConvertDetail detail : stockConvert.getDetailList()) {
            i++;
            if (StringUtil.isEmpty(detail.getProductId()))
                throw new BadRequestException("detailList第"+i+"项productId不能为空");
            if (StringUtil.isEmpty(detail.getFmMerchantId()))
                throw new BadRequestException("detailList第"+i+"项fmMerchantId不能为空");
            if (StringUtil.isEmpty(detail.getToMerchantId()))
                throw new BadRequestException("detailList第"+i+"项toMerchantId不能为空");
            if (detail.getConvertQty()==null)
                throw new BadRequestException("detailList第"+i+"项convertQty不能为空");
            if (StringUtil.isEmpty(detail.getFmVirtualMerchantId()))
                throw new BadRequestException("detailList第"+i+"项fmVirtualMerchantId不能为空");
            if (StringUtil.isEmpty(detail.getToVirtualMerchantId()))
                throw new BadRequestException("detailList第"+i+"项toVirtualMerchantId不能为空");
        }
        create(stockConvert);
        for (BusStockConvertDetail detail : stockConvert.getDetailList()) {
            detail.setMainId(stockConvert.getId());
            stockConvertDetailService.create(detail);
        }
        return true;
    }

    @Override
    public StockConvert queryById(Long id) {
        StockConvert stockConvert=stockConvertRepository.findById(id).orElse(null);
        if (stockConvert==null){
            stockConvert=new StockConvert();
            stockConvert.setDetailList(new ArrayList<>());
        }else {
            List<BusStockConvertDetail>detailList=stockConvertDetailService.queryByMainId(id);
            stockConvert.setDetailList(detailList);
        }
        return stockConvert;
    }

    @Override
    public Object complete(Long id) {
        StockConvert convert=stockConvertRepository.getOne(id);
        if (convert==null)
            throw new BadRequestException("数据不存在");
        convert.setCompleteTime(DateUtils.now());
        convert.setCompleter(SecurityUtils.getCurrentUsername());
        update(convert);
        return "完成";
    }
}
