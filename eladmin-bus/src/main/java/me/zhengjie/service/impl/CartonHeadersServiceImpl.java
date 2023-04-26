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

import me.zhengjie.domain.CartonHeaders;
import me.zhengjie.domain.StockOutTolly;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.service.StockOutTollyService;
import me.zhengjie.service.WmsOutstockService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CartonHeadersRepository;
import me.zhengjie.service.CartonHeadersService;
import me.zhengjie.service.dto.CartonHeadersDto;
import me.zhengjie.service.dto.CartonHeadersQueryCriteria;
import me.zhengjie.service.mapstruct.CartonHeadersMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
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
* @date 2021-03-28
**/
@Service
@RequiredArgsConstructor
public class CartonHeadersServiceImpl implements CartonHeadersService {

    private final CartonHeadersRepository cartonHeadersRepository;
    private final CartonHeadersMapper cartonHeadersMapper;

    @Autowired
    private StockOutTollyService stockOutTollyService;

    @Autowired
    private WmsOutstockService wmsOutstockService;

    @Override
    public Map<String,Object> queryAll(CartonHeadersQueryCriteria criteria, Pageable pageable){
        Page<CartonHeaders> page = cartonHeadersRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(cartonHeadersMapper::toDto));
    }

    @Override
    public List<CartonHeadersDto> queryAll(CartonHeadersQueryCriteria criteria){
        return cartonHeadersMapper.toDto(cartonHeadersRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CartonHeadersDto findById(Long id) {
        CartonHeaders cartonHeaders = cartonHeadersRepository.findById(id).orElseGet(CartonHeaders::new);
        ValidationUtil.isNull(cartonHeaders.getId(),"CartonHeaders","id",id);
        return cartonHeadersMapper.toDto(cartonHeaders);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartonHeadersDto create(CartonHeaders resources) {
        StockOutTolly stockOutTolly=stockOutTollyService.queryById(resources.getStockOutTolly().getId());
        //WmsOutstock wmsOutstock=wmsOutstockService.queryById(stockOutTolly.getWmsOutstock().getId());
        resources.setPackagedBy(SecurityUtils.getCurrentUsername());
        resources.setPackageTime(new Timestamp(System.currentTimeMillis()));
        resources.setCartonNo(stockOutTolly.getWmsOutstock().getOutOrderSn());
        if (StringUtil.isNotBlank(resources.getMaterials())){
            //转半角符
            resources.setMaterials(StringUtil.toDBC(resources.getMaterials()));
        }
        return cartonHeadersMapper.toDto(cartonHeadersRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CartonHeaders resources) {
        CartonHeaders cartonHeaders = cartonHeadersRepository.findById(resources.getId()).orElseGet(CartonHeaders::new);
        ValidationUtil.isNull( cartonHeaders.getId(),"CartonHeaders","id",resources.getId());
        cartonHeaders.copy(resources);
        cartonHeadersRepository.save(cartonHeaders);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            cartonHeadersRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CartonHeadersDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CartonHeadersDto cartonHeaders : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("实际重量", cartonHeaders.getActualGrossWeight());
            map.put("wms箱号", cartonHeaders.getCartonNo());
            map.put("出库单号", cartonHeaders.getStockOutTolly().getTallyOrderSn());
            map.put("箱子id", cartonHeaders.getCartonId());
            map.put("是否第一次上报(第一次理货)", cartonHeaders.getIsFirst());
            map.put("包装材质", cartonHeaders.getMaterials());
            map.put("装箱时间", cartonHeaders.getPackageTime());
            map.put("装箱人", cartonHeaders.getPackagedBy());
            map.put("运单号", cartonHeaders.getWayBill());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CartonHeaders queryByCartonNo(String cartonNo) {
        CartonHeaders cartonHeaders=new CartonHeaders();
        cartonHeaders.setCartonNo(cartonNo);
        return cartonHeadersRepository.findOne(Example.of(cartonHeaders)).orElse(null);
    }
}
