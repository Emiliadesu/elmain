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
import me.zhengjie.domain.AddValueOrder;
import me.zhengjie.domain.AddValueOrderDetails;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.AddValueOrderService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.AddValueOrderDetailsRepository;
import me.zhengjie.service.AddValueOrderDetailsService;
import me.zhengjie.service.dto.AddValueOrderDetailsDto;
import me.zhengjie.service.dto.AddValueOrderDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.AddValueOrderDetailsMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @author AddValueOrderDetails
* @date 2021-08-05
**/
@Service
@RequiredArgsConstructor
public class AddValueOrderDetailsServiceImpl implements AddValueOrderDetailsService {

    private final AddValueOrderDetailsRepository addValueOrderDetailsRepository;
    private final AddValueOrderDetailsMapper addValueOrderDetailsMapper;

    @Override
    public Map<String,Object> queryAll(AddValueOrderDetailsQueryCriteria criteria, Pageable pageable){
        Page<AddValueOrderDetails> page = addValueOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(addValueOrderDetailsMapper::toDto));
    }

    @Override
    public List<AddValueOrderDetailsDto> queryAll(AddValueOrderDetailsQueryCriteria criteria){
        return addValueOrderDetailsMapper.toDto(addValueOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public AddValueOrderDetailsDto findById(Long id) {
        AddValueOrderDetails addValueOrderDetails = addValueOrderDetailsRepository.findById(id).orElseGet(AddValueOrderDetails::new);
        ValidationUtil.isNull(addValueOrderDetails.getId(),"AddValueOrderDetails","id",id);
        return addValueOrderDetailsMapper.toDto(addValueOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AddValueOrderDetailsDto create(AddValueOrderDetails resources) {
        return addValueOrderDetailsMapper.toDto(addValueOrderDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AddValueOrderDetails resources) {
        AddValueOrderDetails addValueOrderDetails = addValueOrderDetailsRepository.findById(resources.getId()).orElseGet(AddValueOrderDetails::new);
        ValidationUtil.isNull( addValueOrderDetails.getId(),"AddValueOrderDetails","id",resources.getId());
        addValueOrderDetails.copy(resources);
        addValueOrderDetailsRepository.save(addValueOrderDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            addValueOrderDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AddValueOrderDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AddValueOrderDetailsDto addValueOrderDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("上游明细ID", addValueOrderDetails.getOrderId());
            map.put("要求加工数量", addValueOrderDetails.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Override
    public void createBatch(List<AddValueOrderDetails> saveDetails) {
        addValueOrderDetailsRepository.saveAll(saveDetails);
    }

    @Override
    public List<AddValueOrderDetails> queryByOrderId(Long orderId) {
        return addValueOrderDetailsRepository.findByOrderId(orderId);
    }
}