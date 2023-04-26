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

import me.zhengjie.domain.LocationInfo;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LocationInfoRepository;
import me.zhengjie.service.LocationInfoService;
import me.zhengjie.service.dto.LocationInfoDto;
import me.zhengjie.service.dto.LocationInfoQueryCriteria;
import me.zhengjie.service.mapstruct.LocationInfoMapper;
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
* @author 王淼
* @date 2020-10-13
**/
@Service
@RequiredArgsConstructor
public class LocationInfoServiceImpl implements LocationInfoService {

    private final LocationInfoRepository locationInfoRepository;
    private final LocationInfoMapper locationInfoMapper;

    @Override
    public Map<String,Object> queryAll(LocationInfoQueryCriteria criteria, Pageable pageable){
        Page<LocationInfo> page = locationInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(locationInfoMapper::toDto));
    }

    @Override
    public List<LocationInfoDto> queryAll(LocationInfoQueryCriteria criteria){
        return locationInfoMapper.toDto(locationInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LocationInfoDto findById(Long id) {
        LocationInfo locationInfo = locationInfoRepository.findById(id).orElseGet(LocationInfo::new);
        ValidationUtil.isNull(locationInfo.getId(),"LocationInfo","id",id);
        return locationInfoMapper.toDto(locationInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LocationInfoDto create(LocationInfo resources) {
        if(locationInfoRepository.findByLocation(resources.getLocation()) != null){
            throw new EntityExistException(LocationInfo.class,"location",resources.getLocation());
        }
        return locationInfoMapper.toDto(locationInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LocationInfo resources) {
        LocationInfo locationInfo = locationInfoRepository.findById(resources.getId()).orElseGet(LocationInfo::new);
        ValidationUtil.isNull( locationInfo.getId(),"LocationInfo","id",resources.getId());
        LocationInfo locationInfo1 = null;
        locationInfo1 = locationInfoRepository.findByLocation(resources.getLocation());
        if(locationInfo1 != null && !locationInfo1.getId().equals(locationInfo.getId())){
            throw new EntityExistException(LocationInfo.class,"location",resources.getLocation());
        }
        locationInfo.copy(resources);
        locationInfoRepository.save(locationInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            locationInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<LocationInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LocationInfoDto locationInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("库位编码", locationInfo.getLocation());
            map.put("库位类型", locationInfo.getType());
            map.put("区域", locationInfo.getArea());
            map.put(" modifyTime",  locationInfo.getModifyTime());
            map.put(" modifyUser",  locationInfo.getModifyUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}