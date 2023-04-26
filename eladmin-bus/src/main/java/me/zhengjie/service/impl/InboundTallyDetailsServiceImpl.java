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

import me.zhengjie.domain.InboundTallyDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.InboundTallyDetailsRepository;
import me.zhengjie.service.InboundTallyDetailsService;
import me.zhengjie.service.dto.InboundTallyDetailsDto;
import me.zhengjie.service.dto.InboundTallyDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.InboundTallyDetailsMapper;
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
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-06-16
**/
@Service
@RequiredArgsConstructor
public class InboundTallyDetailsServiceImpl implements InboundTallyDetailsService {

    private final InboundTallyDetailsRepository inboundTallyDetailsRepository;
    private final InboundTallyDetailsMapper inboundTallyDetailsMapper;

    @Override
    public Map<String,Object> queryAll(InboundTallyDetailsQueryCriteria criteria, Pageable pageable){
        Page<InboundTallyDetails> page = inboundTallyDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(inboundTallyDetailsMapper::toDto));
    }

    @Override
    public List<InboundTallyDetailsDto> queryAll(InboundTallyDetailsQueryCriteria criteria){
        return inboundTallyDetailsMapper.toDto(inboundTallyDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public InboundTallyDetailsDto findById(Long id) {
        InboundTallyDetails inboundTallyDetails = inboundTallyDetailsRepository.findById(id).orElseGet(InboundTallyDetails::new);
        ValidationUtil.isNull(inboundTallyDetails.getId(),"InboundTallyDetails","id",id);
        return inboundTallyDetailsMapper.toDto(inboundTallyDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundTallyDetailsDto create(InboundTallyDetails resources) {
        return inboundTallyDetailsMapper.toDto(inboundTallyDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InboundTallyDetails resources) {
        InboundTallyDetails inboundTallyDetails = inboundTallyDetailsRepository.findById(resources.getId()).orElseGet(InboundTallyDetails::new);
        ValidationUtil.isNull( inboundTallyDetails.getId(),"InboundTallyDetails","id",resources.getId());
        inboundTallyDetails.copy(resources);
        inboundTallyDetailsRepository.save(inboundTallyDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            inboundTallyDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InboundTallyDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundTallyDetailsDto inboundTallyDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", inboundTallyDetails.getTallyId());
            map.put("理货单号", inboundTallyDetails.getTallyNo());
            map.put("货品ID", inboundTallyDetails.getGoodsId());
            map.put("货品编码", inboundTallyDetails.getGoodsCode());
            map.put("海关货号", inboundTallyDetails.getGoodsNo());
            map.put("条码", inboundTallyDetails.getBarCode());
            map.put("商品名称", inboundTallyDetails.getGoodsName());
            map.put("货品属性", inboundTallyDetails.getGoodsQuality());
            map.put("理货数量", inboundTallyDetails.getTallyNum());
            map.put("生产日期", inboundTallyDetails.getProductDate());
            map.put("失效日期", inboundTallyDetails.getExpiryDate());
            map.put("生产批次", inboundTallyDetails.getBatchNo());
            map.put("图片地址", inboundTallyDetails.getPicUrl());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<InboundTallyDetails> queryByTallyNo(@NotBlank String tallyNo) {
        return inboundTallyDetailsRepository.findByTallyNo(tallyNo);
    }

    @Override
    public List<InboundTallyDetails> queryByTallyIdAndGoodsId(Long tallyId, Long goodsId) {
        return inboundTallyDetailsRepository.findByTallyIdAndGoodsId(tallyId, goodsId);
    }
}