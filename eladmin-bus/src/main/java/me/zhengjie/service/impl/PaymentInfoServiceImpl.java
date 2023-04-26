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

import me.zhengjie.domain.PaymentInfo;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PaymentInfoRepository;
import me.zhengjie.service.PaymentInfoService;
import me.zhengjie.service.dto.PaymentInfoDto;
import me.zhengjie.service.dto.PaymentInfoQueryCriteria;
import me.zhengjie.service.mapstruct.PaymentInfoMapper;
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
* @date 2022-04-26
**/
@Service
@RequiredArgsConstructor
public class PaymentInfoServiceImpl implements PaymentInfoService {

    private final PaymentInfoRepository paymentInfoRepository;
    private final PaymentInfoMapper paymentInfoMapper;

    @Override
    public Map<String,Object> queryAll(PaymentInfoQueryCriteria criteria, Pageable pageable){
        Page<PaymentInfo> page = paymentInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(paymentInfoMapper::toDto));
    }

    @Override
    public List<PaymentInfoDto> queryAll(PaymentInfoQueryCriteria criteria){
        return paymentInfoMapper.toDto(paymentInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PaymentInfoDto findById(Long id) {
        PaymentInfo paymentInfo = paymentInfoRepository.findById(id).orElseGet(PaymentInfo::new);
        ValidationUtil.isNull(paymentInfo.getId(),"PaymentInfo","id",id);
        return paymentInfoMapper.toDto(paymentInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PaymentInfoDto create(PaymentInfo resources) {
        resources.setCreateBy(SecurityUtils.getCurrentUserId()+":"+SecurityUtils.getCurrentUsername());
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setUpdateBy(resources.getCreateBy());
        resources.setUpdateTime(resources.getCreateTime());
        return paymentInfoMapper.toDto(paymentInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PaymentInfo resources) {
        PaymentInfo paymentInfo = paymentInfoRepository.findById(resources.getId()).orElseGet(PaymentInfo::new);
        ValidationUtil.isNull( paymentInfo.getId(),"PaymentInfo","id",resources.getId());
        paymentInfo.copy(resources);
        paymentInfoRepository.save(paymentInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            paymentInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PaymentInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PaymentInfoDto paymentInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("支付机构企业海关备案名	", paymentInfo.getPayName());
            map.put("支付机构企业海关备案代码", paymentInfo.getPayCustomerCode());
            map.put("海关支付代码", paymentInfo.getPayCode());
            map.put("创建人", paymentInfo.getCreateBy());
            map.put("创建时间", paymentInfo.getCreateTime());
            map.put("修改人", paymentInfo.getUpdateBy());
            map.put("修改时间", paymentInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PaymentInfo queryByCustomerCode(String customsCode) {
        return paymentInfoRepository.findByPayCustomerCode(customsCode);
    }
}