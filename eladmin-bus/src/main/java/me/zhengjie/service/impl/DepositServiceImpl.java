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

import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.Deposit;
import me.zhengjie.domain.DepositLog;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.DepositLogService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DepositRepository;
import me.zhengjie.service.DepositService;
import me.zhengjie.service.dto.DepositDto;
import me.zhengjie.service.dto.DepositQueryCriteria;
import me.zhengjie.service.mapstruct.DepositMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
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
* @author luob
* @date 2021-11-13
**/
@Service
@RequiredArgsConstructor
public class DepositServiceImpl implements DepositService {

    private final DepositRepository depositRepository;
    private final DepositMapper depositMapper;

    @Autowired
    private DepositLogService depositLogService;

    @Override
    public Map<String,Object> queryAll(DepositQueryCriteria criteria, Pageable pageable){
        Page<Deposit> page = depositRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(depositMapper::toDto));
    }

    @Override
    public List<DepositDto> queryAll(DepositQueryCriteria criteria){
        return depositMapper.toDto(depositRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DepositDto findById(Long id) {
        Deposit deposit = depositRepository.findById(id).orElseGet(Deposit::new);
        ValidationUtil.isNull(deposit.getId(),"Deposit","id",id);
        return depositMapper.toDto(deposit);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepositDto create(Deposit resources) {
        Deposit deposit = queryByShopId(resources.getShopId());
        if (deposit != null)
            throw new BadRequestException("店铺已存在");
        DepositDto depositDto = depositMapper.toDto(depositRepository.save(resources));
        DepositLog log = new DepositLog(
                depositDto.getId(),
                "1",
                resources.getAmount(),
                depositDto.getAmount(),
                SecurityUtils.getCurrentUsername(),
                new Timestamp(System.currentTimeMillis()),
                null
        );
        depositLogService.create(log);
        return depositDto;
    }
    @Override
    public Deposit queryByShopId(Long shopId) {
        return depositRepository.findByShopId(shopId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Deposit resources) {
        Deposit deposit = depositRepository.findById(resources.getId()).orElseGet(Deposit::new);
        ValidationUtil.isNull( deposit.getId(),"Deposit","id",resources.getId());
        deposit.copy(resources);
        depositRepository.save(deposit);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            depositRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DepositDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DepositDto deposit : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户ID", deposit.getCustomersId());
            map.put("店铺ID", deposit.getShopId());
            map.put("金额", deposit.getAmount());
            map.put("创建人", deposit.getCreateBy());
            map.put("创建时间", deposit.getCreateTime());
            map.put("更新者", deposit.getUpdateBy());
            map.put("更新时间", deposit.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 保证金额变动
     * @param order
     */
    @Override
    public void change(CrossBorderOrder order) {
        try{
            Deposit deposit = queryByShopId(order.getShopId());
            if (deposit != null) {
                change(deposit.getId(), "2", new BigDecimal(order.getTaxAmount()), order.getOrderNo());
            }
        }catch (Exception e) {
            // 为保证正常业务不出问题，无论出什么异常都catch并不做处理
            e.printStackTrace();
        }
    }

    /**
     * 保证金金额变动
     * @param depositId
     * @param type
     * @param changeAmount
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public synchronized void change(Long depositId, String type, BigDecimal changeAmount, String orderNo) {
        if (StringUtils.equals("2", type) || StringUtils.equals("4", type)) {
            // 扣减
            depositRepository.subAmount(depositId, changeAmount);
        }else if (StringUtils.equals("1", type) || StringUtils.equals("3", type)) {
            depositRepository.addAmount(depositId, changeAmount);
        }else {
            throw new BadRequestException("无对应可修改类型：" + type);
        }
        DepositDto depositDto = findById(depositId);
        DepositLog log = new DepositLog(
                depositId,
                type,
                changeAmount,
                depositDto.getAmount(),
                StringUtils.isEmpty(orderNo)?SecurityUtils.getCurrentUsername():"SYSTEM",
                new Timestamp(System.currentTimeMillis()),
                orderNo
        );
        depositLogService.create(log);
    }


}