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

import cn.hutool.core.util.IdUtil;
import me.zhengjie.domain.CustomerInfo;
import me.zhengjie.domain.CustomerKey;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.dto.CustomerInfoDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomerKeyRepository;
import me.zhengjie.service.CustomerKeyService;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.service.dto.CustomerKeyQueryCriteria;
import me.zhengjie.service.mapstruct.CustomerKeyMapper;
import org.springframework.data.domain.Example;
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
* @date 2021-04-05
**/
@Service
@RequiredArgsConstructor
public class CustomerKeyServiceImpl implements CustomerKeyService {

    private final CustomerKeyRepository customerKeyRepository;
    private final CustomerKeyMapper customerKeyMapper;
    private final CustomerInfoService customerInfoService;

    @Override
    public Map<String,Object> queryAll(CustomerKeyQueryCriteria criteria, Pageable pageable){
        Page<CustomerKey> page = customerKeyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customerKeyMapper::toDto));
    }

    @Override
    public List<CustomerKeyDto> queryAll(CustomerKeyQueryCriteria criteria){
        return customerKeyMapper.toDto(customerKeyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomerKeyDto findById(Long id) {
        CustomerKey customerKey = customerKeyRepository.findById(id).orElseGet(CustomerKey::new);
        ValidationUtil.isNull(customerKey.getId(),"CustomerKey","id",id);
        return customerKeyMapper.toDto(customerKey);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerKeyDto create(CustomerKey resources) {
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setSignKey(IdUtil.simpleUUID());
        resources.setSignType("DES");
        resources.setCreateUserId(SecurityUtils.getCurrentUserId());
        return customerKeyMapper.toDto(customerKeyRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerKey resources) {
        CustomerKey customerKey = customerKeyRepository.findById(resources.getId()).orElseGet(CustomerKey::new);
        ValidationUtil.isNull( customerKey.getId(),"CustomerKey","id",resources.getId());
        customerKey.copy(resources);
        customerKeyRepository.save(customerKey);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customerKeyRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomerKeyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomerKeyDto customerKey : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户id", customerKey.getCustomerId());
            map.put("加密方式", customerKey.getSignType());
            map.put("秘钥", customerKey.getSignKey());
            map.put("创建人id", customerKey.getCreateUserId());
            map.put("创建时间", customerKey.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CustomerKeyDto findByCustCode(String customersCode) {
        CustomerKey customerKey=new CustomerKey();
        customerKey.setCode(customersCode);
        Optional<CustomerKey> opt = customerKeyRepository.findOne(Example.of(customerKey));
        if (opt.isPresent()){
            return customerKeyMapper.toDto(opt.get());
        }
        return null;
    }

    @Override
    public CustomerKey findByCustId(Long custId) {
        return customerKeyRepository.findByCustomerId(custId);
    }
}
