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

import me.zhengjie.domain.HezhuDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.HezhuDetailsRepository;
import me.zhengjie.service.HezhuDetailsService;
import me.zhengjie.service.dto.HezhuDetailsDto;
import me.zhengjie.service.dto.HezhuDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.HezhuDetailsMapper;
import org.springframework.data.domain.Example;
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
* @date 2021-08-26
**/
@Service
@RequiredArgsConstructor
public class HezhuDetailsServiceImpl implements HezhuDetailsService {

    private final HezhuDetailsRepository hezhuDetailsRepository;
    private final HezhuDetailsMapper hezhuDetailsMapper;

    @Override
    public Map<String,Object> queryAll(HezhuDetailsQueryCriteria criteria, Pageable pageable){
        Page<HezhuDetails> page = hezhuDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(hezhuDetailsMapper::toDto));
    }

    @Override
    public List<HezhuDetailsDto> queryAll(HezhuDetailsQueryCriteria criteria){
        return hezhuDetailsMapper.toDto(hezhuDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HezhuDetailsDto findById(Long id) {
        HezhuDetails hezhuDetails = hezhuDetailsRepository.findById(id).orElseGet(HezhuDetails::new);
        ValidationUtil.isNull(hezhuDetails.getId(),"HezhuDetails","id",id);
        return hezhuDetailsMapper.toDto(hezhuDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HezhuDetailsDto create(HezhuDetails resources) {
        return hezhuDetailsMapper.toDto(hezhuDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HezhuDetails resources) {
        HezhuDetails hezhuDetails = hezhuDetailsRepository.findById(resources.getId()).orElseGet(HezhuDetails::new);
        ValidationUtil.isNull( hezhuDetails.getId(),"HezhuDetails","id",resources.getId());
        hezhuDetails.copy(resources);
        hezhuDetailsRepository.save(hezhuDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            hezhuDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HezhuDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HezhuDetailsDto hezhuDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("清关ID", hezhuDetails.getOrderId());
            map.put("单据编号", hezhuDetails.getOrderNo());
            map.put("序号", hezhuDetails.getSeqNo());
            map.put("货品ID", hezhuDetails.getGoodsId());
            map.put("货品编码", hezhuDetails.getGoodsCode());
            map.put("海关货号", hezhuDetails.getGoodsNo());
            map.put("外部货号", hezhuDetails.getOuterGoodsNo());
            map.put("HS编码", hezhuDetails.getHsCode());
            map.put("商品名称", hezhuDetails.getGoodsName());
            map.put("备案序号", hezhuDetails.getRecordNo());
            map.put("净重（千克）", hezhuDetails.getNetWeight());
            map.put("毛重（千克）", hezhuDetails.getGrossWeight());
            map.put("法一单位", hezhuDetails.getLegalUnit());
            map.put("法一单位代码", hezhuDetails.getLegalUnitCode());
            map.put("法一数量", hezhuDetails.getLegalNum());
            map.put("法二单位", hezhuDetails.getSecondUnit());
            map.put("法二单位代码", hezhuDetails.getSecondUnitCode());
            map.put("法二数量", hezhuDetails.getSecondNum());
            map.put("数量", hezhuDetails.getQty());
            map.put("计量单位", hezhuDetails.getUnit());
            map.put("商品单价", hezhuDetails.getPrice());
            map.put("商品总价", hezhuDetails.getTotalPrice());
            map.put("规格型号", hezhuDetails.getProperty());
            map.put("币种", hezhuDetails.getCurrency());
            map.put("原产国", hezhuDetails.getMakeCountry());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createBatch(List<HezhuDetails> saveDetails) {
        hezhuDetailsRepository.saveAll(saveDetails);
    }

    @Override
    public List<HezhuDetails> queryByHezhuId(Long hezhuId) {
        HezhuDetails wrapper=new HezhuDetails();
        wrapper.setOrderId(hezhuId);
        return hezhuDetailsRepository.findAll(Example.of(wrapper));
    }
}
