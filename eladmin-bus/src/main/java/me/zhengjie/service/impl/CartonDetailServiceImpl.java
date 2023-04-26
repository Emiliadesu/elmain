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

import me.zhengjie.domain.CartonDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CartonDetailRepository;
import me.zhengjie.service.CartonDetailService;
import me.zhengjie.service.dto.CartonDetailDto;
import me.zhengjie.service.dto.CartonDetailQueryCriteria;
import me.zhengjie.service.mapstruct.CartonDetailMapper;
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
* @author wangm
* @date 2021-03-28
**/
@Service
@RequiredArgsConstructor
public class CartonDetailServiceImpl implements CartonDetailService {

    private final CartonDetailRepository cartonDetailRepository;
    private final CartonDetailMapper cartonDetailMapper;

    @Override
    public Map<String,Object> queryAll(CartonDetailQueryCriteria criteria, Pageable pageable){
        Page<CartonDetail> page = cartonDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(cartonDetailMapper::toDto));
    }

    @Override
    public List<CartonDetailDto> queryAll(CartonDetailQueryCriteria criteria){
        return cartonDetailMapper.toDto(cartonDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CartonDetailDto findById(Long id) {
        CartonDetail cartonDetail = cartonDetailRepository.findById(id).orElseGet(CartonDetail::new);
        ValidationUtil.isNull(cartonDetail.getId(),"CartonDetail","id",id);
        return cartonDetailMapper.toDto(cartonDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CartonDetailDto create(CartonDetail resources) {
        return cartonDetailMapper.toDto(cartonDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CartonDetail resources) {
        CartonDetail cartonDetail = cartonDetailRepository.findById(resources.getId()).orElseGet(CartonDetail::new);
        ValidationUtil.isNull( cartonDetail.getId(),"CartonDetail","id",resources.getId());
        cartonDetail.copy(resources);
        cartonDetailRepository.save(cartonDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            cartonDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CartonDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CartonDetailDto cartonDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("wms箱id", cartonDetail.getCartonHeader().getId());
            map.put("数量", cartonDetail.getNum());
            map.put("产品id", cartonDetail.getProductId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
