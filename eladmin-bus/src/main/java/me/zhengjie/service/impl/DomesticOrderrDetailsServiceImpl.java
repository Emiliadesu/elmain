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

import me.zhengjie.domain.DomesticOrderDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DomesticOrderrDetailsRepository;
import me.zhengjie.service.DomesticOrderrDetailsService;
import me.zhengjie.service.dto.DomesticOrderrDetailsDto;
import me.zhengjie.service.dto.DomesticOrderrDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.DomesticOrderrDetailsMapper;
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
* @date 2022-04-11
**/
@Service
@RequiredArgsConstructor
public class DomesticOrderrDetailsServiceImpl implements DomesticOrderrDetailsService {

    private final DomesticOrderrDetailsRepository domesticOrderrDetailsRepository;
    private final DomesticOrderrDetailsMapper domesticOrderrDetailsMapper;

    @Override
    public Map<String,Object> queryAll(DomesticOrderrDetailsQueryCriteria criteria, Pageable pageable){
        Page<DomesticOrderDetails> page = domesticOrderrDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(domesticOrderrDetailsMapper::toDto));
    }

    @Override
    public List<DomesticOrderrDetailsDto> queryAll(DomesticOrderrDetailsQueryCriteria criteria){
        return domesticOrderrDetailsMapper.toDto(domesticOrderrDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DomesticOrderrDetailsDto findById(Long id) {
        DomesticOrderDetails domesticOrderDetails = domesticOrderrDetailsRepository.findById(id).orElseGet(DomesticOrderDetails::new);
        ValidationUtil.isNull(domesticOrderDetails.getId(),"DomesticOrderrDetails","id",id);
        return domesticOrderrDetailsMapper.toDto(domesticOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomesticOrderrDetailsDto create(DomesticOrderDetails resources) {
        return domesticOrderrDetailsMapper.toDto(domesticOrderrDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DomesticOrderDetails resources) {
        DomesticOrderDetails domesticOrderDetails = domesticOrderrDetailsRepository.findById(resources.getId()).orElseGet(DomesticOrderDetails::new);
        ValidationUtil.isNull( domesticOrderDetails.getId(),"DomesticOrderrDetails","id",resources.getId());
        domesticOrderDetails.copy(resources);
        domesticOrderrDetailsRepository.save(domesticOrderDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            domesticOrderrDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DomesticOrderrDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DomesticOrderrDetailsDto domesticOrderrDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", domesticOrderrDetails.getOrderId());
            map.put("订单号", domesticOrderrDetails.getOrderNo());
            map.put("货品ID", domesticOrderrDetails.getGoodsId());
            map.put("货品编码", domesticOrderrDetails.getGoodsCode());
            map.put("海关货号", domesticOrderrDetails.getGoodsNo());
            map.put("前端商品名称", domesticOrderrDetails.getFontGoodsName());
            map.put("商品名称", domesticOrderrDetails.getGoodsName());
            map.put("数量", domesticOrderrDetails.getQty());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createAll(List<DomesticOrderDetails> items) {
        domesticOrderrDetailsRepository.saveAll(items);
    }

    @Override
    public List<DomesticOrderDetails> queryByOrderId(Long orderId) {
        return domesticOrderrDetailsRepository.findByOrderId(orderId);
    }
}