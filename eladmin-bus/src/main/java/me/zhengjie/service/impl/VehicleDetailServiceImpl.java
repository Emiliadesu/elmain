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

import me.zhengjie.domain.VehicleDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.VehicleDetailRepository;
import me.zhengjie.service.VehicleDetailService;
import me.zhengjie.service.dto.VehicleDetailDto;
import me.zhengjie.service.dto.VehicleDetailQueryCriteria;
import me.zhengjie.service.mapstruct.VehicleDetailMapper;
import org.springframework.data.domain.Example;
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
* @date 2021-04-01
**/
@Service
@RequiredArgsConstructor
public class VehicleDetailServiceImpl implements VehicleDetailService {

    private final VehicleDetailRepository vehicleDetailRepository;
    private final VehicleDetailMapper vehicleDetailMapper;

    @Override
    public Map<String,Object> queryAll(VehicleDetailQueryCriteria criteria, Pageable pageable){
        Page<VehicleDetail> page = vehicleDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(vehicleDetailMapper::toDto));
    }

    @Override
    public List<VehicleDetailDto> queryAll(VehicleDetailQueryCriteria criteria){
        return vehicleDetailMapper.toDto(vehicleDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public VehicleDetailDto findById(Long id) {
        VehicleDetail vehicleDetail = vehicleDetailRepository.findById(id).orElseGet(VehicleDetail::new);
        ValidationUtil.isNull(vehicleDetail.getId(),"VehicleDetail","id",id);
        return vehicleDetailMapper.toDto(vehicleDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehicleDetailDto create(VehicleDetail resources) {
        return vehicleDetailMapper.toDto(vehicleDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehicleDetailDto addDetail(VehicleDetail resources) {
        VehicleDetail detail=vehicleDetailRepository.findOne(Example.of(resources)).orElse(null);
        if (detail==null){
            resources.setQty(1);
            return create(resources);
        }
        detail.setQty(detail.getQty()+1);
        return vehicleDetailMapper.toDto(vehicleDetailRepository.save(detail));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(VehicleDetail resources) {
        VehicleDetail vehicleDetail = vehicleDetailRepository.findById(resources.getId()).orElseGet(VehicleDetail::new);
        ValidationUtil.isNull( vehicleDetail.getId(),"VehicleDetail","id",resources.getId());
        vehicleDetail.copy(resources);
        vehicleDetailRepository.save(vehicleDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            vehicleDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<VehicleDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (VehicleDetailDto vehicleDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("出库车辆信息头", vehicleDetail.getVechileHeaderId());
            map.put("车牌号", vehicleDetail.getVechileNo());
            map.put("车次", vehicleDetail.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
