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

import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.AddValueInfo;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.AddValueInfoRepository;
import me.zhengjie.service.AddValueInfoService;
import me.zhengjie.service.dto.AddValueInfoDto;
import me.zhengjie.service.dto.AddValueInfoQueryCriteria;
import me.zhengjie.service.mapstruct.AddValueInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author AddValueInfo
* @date 2021-08-05
**/
@Service
@RequiredArgsConstructor
public class AddValueInfoServiceImpl implements AddValueInfoService {

    private final AddValueInfoRepository addValueInfoRepository;
    private final AddValueInfoMapper addValueInfoMapper;

    @Override
    public Map<String,Object> queryAll(AddValueInfoQueryCriteria criteria, Pageable pageable){
        Page<AddValueInfo> page = addValueInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(addValueInfoMapper::toDto));
    }

    @Override
    public List<AddValueInfoDto> queryAll(AddValueInfoQueryCriteria criteria){
        return addValueInfoMapper.toDto(addValueInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public AddValueInfoDto findById(Long id) {
        AddValueInfo addValueInfo = addValueInfoRepository.findById(id).orElseGet(AddValueInfo::new);
        ValidationUtil.isNull(addValueInfo.getId(),"AddValueInfo","id",id);
        return addValueInfoMapper.toDto(addValueInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddValueInfoDto create(AddValueInfo resources) {
        return addValueInfoMapper.toDto(addValueInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AddValueInfo resources) {
        AddValueInfo addValueInfo = addValueInfoRepository.findById(resources.getId()).orElseGet(AddValueInfo::new);
        ValidationUtil.isNull( addValueInfo.getId(),"AddValueInfo","id",resources.getId());
        addValueInfo.copy(resources);
        addValueInfoRepository.save(addValueInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            addValueInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AddValueInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AddValueInfoDto addValueInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("增值编码", addValueInfo.getAddCode());
            map.put("增值名称", addValueInfo.getAddName());
            map.put("说明", addValueInfo.getRemake());
            map.put("创建人", addValueInfo.getCreateBy());
            map.put("创建时间", addValueInfo.getCreateTime());
            map.put("更新人", addValueInfo.getUpdateBy());
            map.put("更新时间", addValueInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    /*
    接口下发的实现
     */
    @Override
    public AddValueInfo addValue(String decData, Long customerId) {
        JSONObject data = JSONObject.parseObject(decData);
        String addCode = data.getString("addCode");
        if (StringUtils.isEmpty(addCode))
            throw new BadRequestException("addCode必须有");
        AddValueInfo addValueInfo=new AddValueInfo();
        addValueInfo.setAddCode(addCode);
        String addName = data.getString("addName");
        if (StringUtils.isEmpty(addName))
            throw new BadRequestException("addName必须有");
        addValueInfo.setAddName(addName);
        String remake = data.getString("remake");
        addValueInfo.setRemake(remake);
        create(addValueInfo);
        return addValueInfo;
    }

    @Override
    public AddValueInfo queryByAddCode(String addCode) {
        return addValueInfoRepository.findByAddCode(addCode);
    }
}