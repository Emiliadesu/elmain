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

import me.zhengjie.domain.CustomerInfo;
import me.zhengjie.domain.ShopInfo;
import me.zhengjie.domain.UserCustomer;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomerInfoRepository;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.dto.CustomerInfoDto;
import me.zhengjie.service.dto.CustomerInfoQueryCriteria;
import me.zhengjie.service.mapstruct.CustomerInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-02-27
**/
@CacheConfig(cacheNames = "customer")
@Service
@RequiredArgsConstructor
public class CustomerInfoServiceImpl implements CustomerInfoService {

    private final CustomerInfoRepository customerInfoRepository;
    private final CustomerInfoMapper customerInfoMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private UserCustomerService userCustomerService;

    private final RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(CustomerInfoQueryCriteria criteria, Pageable pageable){
        Page<CustomerInfo> page = customerInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customerInfoMapper::toDto));
    }

    @Override
    public List<CustomerInfoDto> queryAll(CustomerInfoQueryCriteria criteria){
        return customerInfoMapper.toDto(customerInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomerInfoDto findById(Long id) {
        CustomerInfo customerInfo = customerInfoRepository.findById(id).orElseGet(CustomerInfo::new);
        ValidationUtil.isNull(customerInfo.getId(),"CustomerInfo","id",id);
        return customerInfoMapper.toDto(customerInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerInfoDto create(CustomerInfo resources) {
        resources.setCreateUserId(SecurityUtils.getCurrentUserId());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        return customerInfoMapper.toDto(customerInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerInfo resources) {
        CustomerInfo customerInfo = customerInfoRepository.findById(resources.getId()).orElseGet(CustomerInfo::new);
        ValidationUtil.isNull( customerInfo.getId(),"CustomerInfo","id",resources.getId());
        customerInfo.copy(resources);
        customerInfoRepository.save(customerInfo);
        redisUtils.del("customer::id:" + customerInfo.getId());
        redisUtils.del("customer::name:" + customerInfo.getCustName());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customerInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomerInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomerInfoDto customerInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户名", customerInfo.getCustName());
            map.put("客户别名", customerInfo.getCustNickName());
            map.put("客户联系人姓名", customerInfo.getContacts());
            map.put("客户联系电话", customerInfo.getTelphone());
            map.put("客户地址", customerInfo.getAddress());
            map.put("添加时间", customerInfo.getCreateTime());
            map.put("添加人id", customerInfo.getCreateUserId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<CustomerInfoDto> queryAllCustom(CustomerInfoQueryCriteria criteria) {
        List<CustomerInfo>customerInfoList=customerInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        for (CustomerInfo customerInfo : customerInfoList) {
            customerInfo.setCreateUserId(null);
            customerInfo.setCreateTime(null);
            customerInfo.setContacts(null);
            customerInfo.setAddress(null);
            customerInfo.setTelphone(null);
        }
        return customerInfoMapper.toDto(customerInfoList);
    }

    @Cacheable(key = "'name:' + #p0")
    @Override
    public CustomerInfo queryByName(String cusName) {
        return customerInfoRepository.queryByCustName(cusName);
    }

    @Cacheable(key = "'id:' + #p0")
    @Override
    public CustomerInfoDto queryById(Long customerId) {
        return findById(customerId);
    }

    @Override
    public List<CustomerInfo> queryCustomerShop(Long customerId) {
        CustomerInfoQueryCriteria customerInfoQueryCriteria = new CustomerInfoQueryCriteria();
        if (customerId != null) {
            List<Long> ids = new ArrayList<>();
            ids.add(customerId);
            customerInfoQueryCriteria.setId(ids);
        }
        List<CustomerInfo> customerInfoList = customerInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,customerInfoQueryCriteria,criteriaBuilder));
        Iterator<CustomerInfo> iterator = customerInfoList.iterator();
        while (iterator.hasNext()) {
            CustomerInfo customerInfo = iterator.next();
            List<ShopInfo> shopInfos = shopInfoService.queryByCusId(customerInfo.getId());
            if (CollectionUtils.isEmpty(shopInfos)) {
                iterator.remove();
            }else {
                customerInfo.setChildren(shopInfos);
            }
        }
        return customerInfoList;
    }

    @Override
    public List<CustomerInfo> queryUserCusShop() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<UserCustomer> userCustomers = userCustomerService.queryByUserId(currentUserId);
        return null;
    }

    @Override
    public List<CustomerInfo> queryCurrentUserCustomer() {
        Long currentUserId = SecurityUtils.getCurrentUserId();
        List<Long> customerIds = userCustomerService.queryCustomers(currentUserId);
        CustomerInfoQueryCriteria criteria = new CustomerInfoQueryCriteria();
        criteria.setId(customerIds);
        List<CustomerInfo> customerInfoList = customerInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        return customerInfoList;
    }

}
