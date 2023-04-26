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

import cn.hutool.core.collection.CollectionUtil;
import me.zhengjie.domain.CustomsTariff;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomsTariffRepository;
import me.zhengjie.service.CustomsTariffService;
import me.zhengjie.service.dto.CustomsTariffDto;
import me.zhengjie.service.dto.CustomsTariffQueryCriteria;
import me.zhengjie.service.mapstruct.CustomsTariffMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2020-10-20
**/
@Service
@RequiredArgsConstructor
public class CustomsTariffServiceImpl implements CustomsTariffService {

    private final CustomsTariffRepository customsTariffRepository;
    private final CustomsTariffMapper customsTariffMapper;

    @Override
    public Map<String,Object> queryAll(CustomsTariffQueryCriteria criteria, Pageable pageable){
        Page<CustomsTariff> page = customsTariffRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customsTariffMapper::toDto));
    }

    @Override
    public List<CustomsTariffDto> queryAll(CustomsTariffQueryCriteria criteria){
        return customsTariffMapper.toDto(customsTariffRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomsTariffDto findById(Long id) {
        CustomsTariff customsTariff = customsTariffRepository.findById(id).orElseGet(CustomsTariff::new);
        ValidationUtil.isNull(customsTariff.getId(),"CustomsTariff","id",id);
        return customsTariffMapper.toDto(customsTariff);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomsTariffDto create(CustomsTariff resources) {
        return customsTariffMapper.toDto(customsTariffRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomsTariff resources) {
        CustomsTariff customsTariff = customsTariffRepository.findById(resources.getId()).orElseGet(CustomsTariff::new);
        ValidationUtil.isNull( customsTariff.getId(),"CustomsTariff","id",resources.getId());
        customsTariff.copy(resources);
        customsTariffRepository.save(customsTariff);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customsTariffRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomsTariffDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomsTariffDto customsTariff : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("HS编码", customsTariff.getHsCode());
            map.put("货品名称", customsTariff.getName());
            map.put("普通税率", customsTariff.getNormalTariff());
            map.put("优惠税率", customsTariff.getDiscountTariff());
            map.put("备注", customsTariff.getRemark());
            map.put("出口税率", customsTariff.getExportTariff());
            map.put("消费税率", customsTariff.getConsumptTariff());
            map.put("增值税率", customsTariff.getValueAddTariff());
            map.put("第一法定单位", customsTariff.getFirstUnit());
            map.put("第二法定单位", customsTariff.getSecondUnit());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CustomsTariff queryByHs(String hsCode) {
        CustomsTariff customsTariff = new  CustomsTariff();
        customsTariff.setHsCode(hsCode);
        Example<CustomsTariff> example = Example.of(customsTariff);
        Optional<CustomsTariff> one = customsTariffRepository.findOne(example);
        return one.isPresent() ? one.get() : null;
    }

    @Override
    public CustomsTariff queryByHsCode(String hsCode) {
        CustomsTariff customsTariff=new CustomsTariff();
        customsTariff.setHsCode(hsCode);
        List<CustomsTariff>list=customsTariffRepository.findAll(Example.of(customsTariff));
        return CollectionUtil.isEmpty(list)?null:list.get(0);
    }
}
