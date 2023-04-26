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

import me.zhengjie.domain.OrderTaxAccount;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OrderTaxAccountRepository;
import me.zhengjie.service.OrderTaxAccountService;
import me.zhengjie.service.dto.OrderTaxAccountDto;
import me.zhengjie.service.dto.OrderTaxAccountQueryCriteria;
import me.zhengjie.service.mapstruct.OrderTaxAccountMapper;
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
* @date 2021-12-11
**/
@Service
@RequiredArgsConstructor
public class OrderTaxAccountServiceImpl implements OrderTaxAccountService {

    private final OrderTaxAccountRepository orderTaxAccountRepository;
    private final OrderTaxAccountMapper orderTaxAccountMapper;

    @Override
    public Map<String,Object> queryAll(OrderTaxAccountQueryCriteria criteria, Pageable pageable){
        Page<OrderTaxAccount> page = orderTaxAccountRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(orderTaxAccountMapper::toDto));
    }

    @Override
    public List<OrderTaxAccountDto> queryAll(OrderTaxAccountQueryCriteria criteria){
        return orderTaxAccountMapper.toDto(orderTaxAccountRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OrderTaxAccountDto findById(Long id) {
        OrderTaxAccount orderTaxAccount = orderTaxAccountRepository.findById(id).orElseGet(OrderTaxAccount::new);
        ValidationUtil.isNull(orderTaxAccount.getId(),"OrderTaxAccount","id",id);
        return orderTaxAccountMapper.toDto(orderTaxAccount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderTaxAccountDto create(OrderTaxAccount resources) {
        return orderTaxAccountMapper.toDto(orderTaxAccountRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OrderTaxAccount resources) {
        OrderTaxAccount orderTaxAccount = orderTaxAccountRepository.findById(resources.getId()).orElseGet(OrderTaxAccount::new);
        ValidationUtil.isNull( orderTaxAccount.getId(),"OrderTaxAccount","id",resources.getId());
        orderTaxAccount.copy(resources);
        orderTaxAccountRepository.save(orderTaxAccount);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            orderTaxAccountRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OrderTaxAccountDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderTaxAccountDto orderTaxAccount : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单号", orderTaxAccount.getOrderNo());
            map.put("LP单号", orderTaxAccount.getLpNo());
            map.put("运单号", orderTaxAccount.getMailNo());
            map.put("总署清单编号", orderTaxAccount.getInvtNo());
            map.put("增值税", orderTaxAccount.getAddTax());
            map.put("消费税", orderTaxAccount.getConsumptionTax());
            map.put("总税", orderTaxAccount.getTaxAmount());
            map.put("订单出库时间", orderTaxAccount.getOrderTime());
            map.put("创建时间", orderTaxAccount.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createTaxs(List<OrderTaxAccount> orderTaxAccounts) {
        orderTaxAccountRepository.saveAll(orderTaxAccounts);
    }

    @Override
    public void updateInvtNo(List<OrderTaxAccount> orderTaxAccounts) {
        orderTaxAccountRepository.saveAll(orderTaxAccounts);
    }

    private OrderTaxAccount queryByMailNo(String mailNo) {
        return orderTaxAccountRepository.findByMailNo(mailNo);
    }
}