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

import me.zhengjie.domain.CustomerComplain;
import me.zhengjie.domain.CustomerComplainItem;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CustomerComplainItemService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomerComplainRepository;
import me.zhengjie.service.CustomerComplainService;
import me.zhengjie.service.dto.CustomerComplainDto;
import me.zhengjie.service.dto.CustomerComplainQueryCriteria;
import me.zhengjie.service.mapstruct.CustomerComplainMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
* @date 2021-12-15
**/
@Service
@RequiredArgsConstructor
public class CustomerComplainServiceImpl implements CustomerComplainService {

    private final CustomerComplainRepository customerComplainRepository;
    private final CustomerComplainMapper customerComplainMapper;

    @Autowired
    private CustomerComplainItemService customerComplainItemService;

    @Override
    public Map<String,Object> queryAll(CustomerComplainQueryCriteria criteria, Pageable pageable){
        Page<CustomerComplain> page = customerComplainRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customerComplainMapper::toDto));
    }

    @Override
    public List<CustomerComplainDto> queryAll(CustomerComplainQueryCriteria criteria){
        return customerComplainMapper.toDto(customerComplainRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomerComplainDto findById(Long id) {
        CustomerComplain customerComplain = customerComplainRepository.findById(id).orElseGet(CustomerComplain::new);
        ValidationUtil.isNull(customerComplain.getId(),"CustomerComplain","id",id);
        return customerComplainMapper.toDto(customerComplain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerComplainDto create(CustomerComplain resources) {
        if (CollectionUtils.isEmpty(resources.getItemList()))
            throw new BadRequestException("提交数据没有明细");
        //获取用户信息
        String username = SecurityUtils.getCurrentUsername();
        //获取系统时间
        Timestamp date = new Timestamp(System.currentTimeMillis());
        resources.setCreateUser(username);
        resources.setCreateTime(date);
        //新增 修改人时间与创建人一样
        resources.setModifyUser(username);
        resources.setModifyTime(date);
        CustomerComplain custComplain=customerComplainRepository.save(resources);
        for (CustomerComplainItem item : custComplain.getItemList()) {
            item.setComplainId(custComplain.getId());
            customerComplainItemService.create(item);
        }
        return customerComplainMapper.toDto(custComplain);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomerComplain resources) {
        CustomerComplain customerComplain = customerComplainRepository.findById(resources.getId()).orElseGet(CustomerComplain::new);
        ValidationUtil.isNull( customerComplain.getId(),"CustomerComplain","id",resources.getId());
        customerComplain.copy(resources);
        customerComplainRepository.save(customerComplain);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customerComplainRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomerComplainDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomerComplainDto customerComplain : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("运单号", customerComplain.getMailNo());
            map.put("订单号", customerComplain.getOrderNo());
            map.put("客诉类型", customerComplain.getComplainType());
            map.put("责任方", customerComplain.getResponsibleParty());
            map.put("责任人", customerComplain.getResponsibleName());
            map.put("处理方式", customerComplain.getProcessWay());
            map.put("赔偿金额", customerComplain.getProcessPrice());
            map.put("退货单号", customerComplain.getReturnNo());
            map.put("补发单号", customerComplain.getReissuedNo());
            map.put("备注", customerComplain.getRemark());
            map.put("登记人", customerComplain.getCreateUser());
            map.put("登记时间", customerComplain.getCreateTime());
            map.put("修改人", customerComplain.getModifyUser());
            map.put("修改时间", customerComplain.getModifyTime());
            map.put("订单平台", customerComplain.getPlatId());
            map.put("客户", customerComplain.getCustomersId());
            map.put("店铺", customerComplain.getShopId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CustomerComplain getCustComplainByIdDto(Long id) {
        CustomerComplain comp=customerComplainRepository.findById(id).orElse(null);
        if (comp==null)
            throw new BadRequestException("找不到id为"+id+"的数据");
        List<CustomerComplainItem>itemList=customerComplainItemService.findByCompId(id);
        comp.setItemList(itemList);
        return comp;
    }
}
