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

import me.zhengjie.domain.PackCheck;
import me.zhengjie.domain.PackCheckDetails;
import me.zhengjie.service.PackCheckDetailsService;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PackCheckRepository;
import me.zhengjie.service.PackCheckService;
import me.zhengjie.service.dto.PackCheckDto;
import me.zhengjie.service.dto.PackCheckQueryCriteria;
import me.zhengjie.service.mapstruct.PackCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
* @date 2021-07-22
**/
@Service
@RequiredArgsConstructor
public class PackCheckServiceImpl implements PackCheckService {

    private final PackCheckRepository packCheckRepository;
    private final PackCheckMapper packCheckMapper;

    @Autowired
    private PackCheckDetailsService packCheckDetailsService;

    @Override
    public Map<String,Object> queryAll(PackCheckQueryCriteria criteria, Pageable pageable){
        Page<PackCheck> page = packCheckRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(packCheckMapper::toDto));
    }

    @Override
    public List<PackCheckDto> queryAll(PackCheckQueryCriteria criteria){
        return packCheckMapper.toDto(packCheckRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PackCheckDto findById(Long id) {
        PackCheck packCheck = packCheckRepository.findById(id).orElseGet(PackCheck::new);
        ValidationUtil.isNull(packCheck.getId(),"PackCheck","id",id);
        return packCheckMapper.toDto(packCheck);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PackCheckDto create(PackCheck resources) {
        return packCheckMapper.toDto(packCheckRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PackCheck resources) {
        PackCheck packCheck = packCheckRepository.findById(resources.getId()).orElseGet(PackCheck::new);
        ValidationUtil.isNull( packCheck.getId(),"PackCheck","id",resources.getId());
        packCheck.copy(resources);
        packCheckRepository.save(packCheck);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            packCheckRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PackCheckDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PackCheckDto packCheck : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", packCheck.getOrderId());
            map.put("订单号", packCheck.getOrderNo());
            map.put("运单号", packCheck.getLogisticsNo());
            map.put("说明", packCheck.getRemark());
            map.put("创建人", packCheck.getCreateBy());
            map.put("创建时间", packCheck.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PackCheck queryByOrderId(Long orderId) {
        return packCheckRepository.findByOrderId(orderId);
    }

    @Override
    public PackCheck queryByLogisticsNo(String mailNo) {
        return packCheckRepository.findByLogisticsNo(mailNo);
    }

    @Override
    public PackCheck queryByIdWithDetails(Long id) {
        PackCheck packCheck = packCheckRepository.findById(id).orElseGet(PackCheck::new);
        List<PackCheckDetails> list = packCheckDetailsService.queryByCheckId(packCheck.getId());
        packCheck.setItemList(list);
        return packCheck;
    }
}