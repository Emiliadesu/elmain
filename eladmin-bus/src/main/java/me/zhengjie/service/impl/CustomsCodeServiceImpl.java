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

import me.zhengjie.domain.CustomsCode;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomsCodeRepository;
import me.zhengjie.service.CustomsCodeService;
import me.zhengjie.service.dto.CustomsCodeDto;
import me.zhengjie.service.dto.CustomsCodeQueryCriteria;
import me.zhengjie.service.mapstruct.CustomsCodeMapper;
import me.zhengjie.utils.constant.CustomsCodeConstant;
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
* @author luob
* @date 2021-08-21
**/
@Service
@RequiredArgsConstructor
public class CustomsCodeServiceImpl implements CustomsCodeService {

    private final CustomsCodeRepository customsCodeRepository;
    private final CustomsCodeMapper customsCodeMapper;

    @Override
    public Map<String,Object> queryAll(CustomsCodeQueryCriteria criteria, Pageable pageable){
        Page<CustomsCode> page = customsCodeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customsCodeMapper::toDto));
    }

    @Override
    public List<CustomsCodeDto> queryAll(CustomsCodeQueryCriteria criteria){
        return customsCodeMapper.toDto(customsCodeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomsCodeDto findById(Long id) {
        CustomsCode customsCode = customsCodeRepository.findById(id).orElseGet(CustomsCode::new);
        ValidationUtil.isNull(customsCode.getId(),"CustomsCode","id",id);
        return customsCodeMapper.toDto(customsCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomsCodeDto create(CustomsCode resources) {
        return customsCodeMapper.toDto(customsCodeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomsCode resources) {
        CustomsCode customsCode = customsCodeRepository.findById(resources.getId()).orElseGet(CustomsCode::new);
        ValidationUtil.isNull( customsCode.getId(),"CustomsCode","id",resources.getId());
        customsCode.copy(resources);
        customsCodeRepository.save(customsCode);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customsCodeRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomsCodeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomsCodeDto customsCode : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("类型 ", customsCode.getType());
            map.put("代码", customsCode.getCode());
            map.put("描述", customsCode.getDes());
            map.put("创建人", customsCode.getCreateBy());
            map.put("创建时间", customsCode.getCreateTime());
            map.put("更新人", customsCode.getUpdateBy());
            map.put("更新时间", customsCode.getUpdateTime());
            map.put("类型描述", customsCode.getTypeDes());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public CustomsCode queryByTypeAndDes(String type, String des) {
        return customsCodeRepository.findByTypeAndDes(type, des);
    }

    @Override
    public Map<String, List<CustomsCode>> queryCustomsCode() {
        List<CustomsCode> unit = queryByType(CustomsCodeConstant.UNIT);
        List<CustomsCode> country = queryByType(CustomsCodeConstant.COUNTRY);
        List<CustomsCode> currency = queryByType(CustomsCodeConstant.CURRENCY);
        List<CustomsCode> trans = queryByType(CustomsCodeConstant.TRANS);
        Map<String, List<CustomsCode>> result= new HashMap<>();
        result.put(CustomsCodeConstant.UNIT, unit);
        result.put(CustomsCodeConstant.COUNTRY, country);
        result.put(CustomsCodeConstant.CURRENCY, currency);
        result.put(CustomsCodeConstant.TRANS, trans);
        return result;
    }

    @Override
    public List<CustomsCode> queryByType(String type) {
        return customsCodeRepository.findByType(type);
    }

    @Override
    public CustomsCode queryByTypeAndCode(String type, String code) {
        return customsCodeRepository.findByTypeAndCode(type, code);
    }
}