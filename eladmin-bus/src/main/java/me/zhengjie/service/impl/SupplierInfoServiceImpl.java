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

import me.zhengjie.domain.SupplierInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SupplierInfoRepository;
import me.zhengjie.service.SupplierInfoService;
import me.zhengjie.service.dto.SupplierInfoDto;
import me.zhengjie.service.dto.SupplierInfoQueryCriteria;
import me.zhengjie.service.mapstruct.SupplierInfoMapper;
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
* @date 2021-03-06
**/
@Service
@RequiredArgsConstructor
public class SupplierInfoServiceImpl implements SupplierInfoService {

    private final SupplierInfoRepository supplierInfoRepository;
    private final SupplierInfoMapper supplierInfoMapper;

    @Override
    public Map<String,Object> queryAll(SupplierInfoQueryCriteria criteria, Pageable pageable){
        Page<SupplierInfo> page = supplierInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(supplierInfoMapper::toDto));
    }

    @Override
    public List<SupplierInfoDto> queryAll(SupplierInfoQueryCriteria criteria){
        return supplierInfoMapper.toDto(supplierInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SupplierInfoDto findById(Long id) {
        SupplierInfo supplierInfo = supplierInfoRepository.findById(id).orElseGet(SupplierInfo::new);
        ValidationUtil.isNull(supplierInfo.getId(),"SupplierInfo","id",id);
        return supplierInfoMapper.toDto(supplierInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SupplierInfoDto create(SupplierInfo resources) {
        return supplierInfoMapper.toDto(supplierInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SupplierInfo resources) {
        SupplierInfo supplierInfo = supplierInfoRepository.findById(resources.getId()).orElseGet(SupplierInfo::new);
        ValidationUtil.isNull( supplierInfo.getId(),"SupplierInfo","id",resources.getId());
        supplierInfo.copy(resources);
        supplierInfoRepository.save(supplierInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            supplierInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SupplierInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SupplierInfoDto supplierInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("供应商名称", supplierInfo.getSupplierName());
            map.put("供应商简称", supplierInfo.getNickName());
            map.put("供应商类别", supplierInfo.getSupplierType());
            map.put("联系人", supplierInfo.getContacts());
            map.put("联系电话", supplierInfo.getTelphone());
            map.put("创建者", supplierInfo.getCreateBy());
            map.put("更新者", supplierInfo.getUpdateBy());
            map.put("创建日期", supplierInfo.getCreateTime());
            map.put("更新时间", supplierInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}