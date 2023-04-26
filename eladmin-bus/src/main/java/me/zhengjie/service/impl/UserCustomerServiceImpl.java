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

import cn.hutool.json.JSONArray;
import me.zhengjie.domain.BRole;
import me.zhengjie.domain.UserCustomer;
import me.zhengjie.service.BRoleService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.UserCustomerRepository;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.UserCustomerDto;
import me.zhengjie.service.dto.UserCustomerQueryCriteria;
import me.zhengjie.service.mapstruct.UserCustomerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-04-05
**/
@Service
@RequiredArgsConstructor
public class UserCustomerServiceImpl implements UserCustomerService {

    private final UserCustomerRepository userCustomerRepository;
    private final UserCustomerMapper userCustomerMapper;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private BRoleService broleService;

    @Override
    public Map<String,Object> queryAll(UserCustomerQueryCriteria criteria, Pageable pageable){
        Page<UserCustomer> page = userCustomerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(userCustomerMapper::toDto));
    }

    @Override
    public List<UserCustomerDto> queryAll(UserCustomerQueryCriteria criteria){
        return userCustomerMapper.toDto(userCustomerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public UserCustomerDto findById(Long id) {
        UserCustomer userCustomer = userCustomerRepository.findById(id).orElseGet(UserCustomer::new);
        ValidationUtil.isNull(userCustomer.getId(),"UserCustomer","id",id);
        return userCustomerMapper.toDto(userCustomer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserCustomerDto create(UserCustomer resources) {
        return userCustomerMapper.toDto(userCustomerRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(UserCustomer resources) {
        UserCustomer userCustomer = userCustomerRepository.findById(resources.getId()).orElseGet(UserCustomer::new);
        ValidationUtil.isNull( userCustomer.getId(),"UserCustomer","id",resources.getId());
        userCustomer.copy(resources);
        userCustomerRepository.save(userCustomer);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            userCustomerRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<UserCustomerDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (UserCustomerDto userCustomer : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("用户ID", userCustomer.getUserId());
            map.put("客户ID", userCustomer.getCustomerId());
            map.put("店铺ID", userCustomer.getShopId());
            map.put("创建人", userCustomer.getCreateBy());
            map.put("创建时间", userCustomer.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<UserCustomer> queryByUserId(Long userId) {
        UserCustomerQueryCriteria criteria = new UserCustomerQueryCriteria();
        criteria.setUserId(userId);
        List<UserCustomer> all = userCustomerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return all;
    }

    @Override
    public List<Long> queryShops(Long userId) {
        // 查询用户的角色，只有是“客户”的角色才需要权限控制
        BRole role = broleService.findByUserIdAndName(userId, "客户");
        if (role == null)
            return null;
        UserCustomerQueryCriteria criteria = new UserCustomerQueryCriteria();
        criteria.setUserId(userId);
        List<UserCustomer> all = userCustomerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        List<Long> ids = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(all)) {
            for (UserCustomer userCustomer : all) {
                ids.add(userCustomer.getShopId());
            }
        }else {
            ids.add(Long.valueOf(0));
        }
        return ids;
    }

    @Override
    public List<Long> queryCustomers(Long userId) {
        UserCustomerQueryCriteria criteria = new UserCustomerQueryCriteria();
        criteria.setUserId(userId);
        List<UserCustomer> all = userCustomerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        List<Long> ids = new ArrayList<>();
        List<Long> shopIds = queryShops(userId);
        if (CollectionUtils.isEmpty(shopIds)) {
            // admin权限
            return ids;
        }
        if (CollectionUtils.isNotEmpty(all)) {
            for (UserCustomer userCustomer : all) {
                ids.add(userCustomer.getCustomerId());
            }
        }else {
            ids.add(Long.valueOf(0));
        }
        return ids;
    }

    @Transactional
    @Override
    public void save(JSONArray shopIds, Long userId) {
        // 先全部删除
        List<Long> ids = new ArrayList<>();
        UserCustomerQueryCriteria criteria = new UserCustomerQueryCriteria();
        criteria.setUserId(userId);
        List<UserCustomerDto> userCustomerDtos = queryAll(criteria);
        for (UserCustomerDto userCustomerDto : userCustomerDtos) {
            ids.add(userCustomerDto.getId());
        }
        deleteAll(ids.toArray(new Long[ids.size()]));
        List<UserCustomer> list = new ArrayList<>();
        for (int i = 0; i < shopIds.size(); i++) {
            UserCustomer userCustomer = new UserCustomer();
            ShopInfoDto shopInfoDto = shopInfoService.queryById(Long.valueOf(shopIds.getLong(i)));
            userCustomer.setUserId(userId);
            userCustomer.setShopId(shopInfoDto.getId());
            userCustomer.setCustomerId(shopInfoDto.getCustId());
            list.add(userCustomer);
        }
        userCustomerRepository.saveAll(list);
    }

    @Override
    public UserCustomer queryByShopId(Long shopId, Long userId) {
        UserCustomer userCustomer = new  UserCustomer();
        userCustomer.setShopId(shopId);
        userCustomer.setUserId(userId);
        Example<UserCustomer> example = Example.of(userCustomer);
        Optional<UserCustomer> one = userCustomerRepository.findOne(example);
        if (one.isPresent())
            return one.get();
        else
            return null;
    }

}