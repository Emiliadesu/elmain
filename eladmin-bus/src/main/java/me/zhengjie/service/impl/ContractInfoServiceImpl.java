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

import me.zhengjie.domain.ContractInfo;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ContractInfoRepository;
import me.zhengjie.service.ContractInfoService;
import me.zhengjie.service.dto.ContractInfoDto;
import me.zhengjie.service.dto.ContractInfoQueryCriteria;
import me.zhengjie.service.mapstruct.ContractInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author leningzhou
* @date 2022-03-02
**/
@Service
@RequiredArgsConstructor
public class ContractInfoServiceImpl implements ContractInfoService {

    private final ContractInfoRepository contractInfoRepository;
    private final ContractInfoMapper contractInfoMapper;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Override
    public Map<String,Object> queryAll(ContractInfoQueryCriteria criteria, Pageable pageable){
        Page<ContractInfo> page = contractInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(contractInfoMapper::toDto));
    }

    @Override
    public List<ContractInfoDto> queryAll(ContractInfoQueryCriteria criteria){
        return contractInfoMapper.toDto(contractInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ContractInfoDto findById(Long id) {
        ContractInfo contractInfo = contractInfoRepository.findById(id).orElseGet(ContractInfo::new);
        ValidationUtil.isNull(contractInfo.getId(),"ContractInfo","id",id);
        return contractInfoMapper.toDto(contractInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ContractInfoDto create(ContractInfo resources) {
        resources.setCreateBy("SYSTEM");
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return contractInfoMapper.toDto(contractInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ContractInfo resources) {
        ContractInfo contractInfo = contractInfoRepository.findById(resources.getId()).orElseGet(ContractInfo::new);
        ValidationUtil.isNull( contractInfo.getId(),"ContractInfo","id",resources.getId());
        contractInfo.copy(resources);
        contractInfoRepository.save(contractInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            contractInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ContractInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ContractInfoDto contractInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户ID", customerInfoService.queryById(contractInfo.getCustomersId()).getCustNickName());
            map.put("店铺ID", shopInfoService.queryById(contractInfo.getShopId()).getName());
            map.put("是否签署合同", contractInfo.getIsSign());
            map.put("合同开始日期", contractInfo.getOpenTime());
            map.put("合同结束日期", contractInfo.getEndTime());
            map.put("合同文件存储url", contractInfo.getContractUrl());
            map.put("创建人", contractInfo.getCreateBy());
            map.put("创建时间", contractInfo.getCreateTime());
            map.put("修改人", contractInfo.getUpdateBy());
            map.put("修改时间", contractInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}