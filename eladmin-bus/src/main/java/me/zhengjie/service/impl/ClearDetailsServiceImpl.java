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

import me.zhengjie.domain.ClearDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ClearDetailsRepository;
import me.zhengjie.service.ClearDetailsService;
import me.zhengjie.service.dto.ClearDetailsDto;
import me.zhengjie.service.dto.ClearDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.ClearDetailsMapper;
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
* @date 2021-08-10
**/
@Service
@RequiredArgsConstructor
public class ClearDetailsServiceImpl implements ClearDetailsService {

    private final ClearDetailsRepository ClearDetailsRepository;
    private final ClearDetailsMapper ClearDetailsMapper;

    @Override
    public Map<String,Object> queryAll(ClearDetailsQueryCriteria criteria, Pageable pageable){
        Page<ClearDetails> page = ClearDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(ClearDetailsMapper::toDto));
    }

    @Override
    public List<ClearDetailsDto> queryAll(ClearDetailsQueryCriteria criteria){
        return ClearDetailsMapper.toDto(ClearDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ClearDetailsDto findById(Long id) {
        ClearDetails ClearDetails = ClearDetailsRepository.findById(id).orElseGet(ClearDetails::new);
        ValidationUtil.isNull(ClearDetails.getId(),"ClearDetails","id",id);
        return ClearDetailsMapper.toDto(ClearDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClearDetailsDto create(ClearDetails resources) {
        return ClearDetailsMapper.toDto(ClearDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClearDetails resources) {
        ClearDetails ClearDetails = ClearDetailsRepository.findById(resources.getId()).orElseGet(ClearDetails::new);
        ValidationUtil.isNull( ClearDetails.getId(),"ClearDetails","id",resources.getId());
        ClearDetails.copy(resources);
        ClearDetailsRepository.save(ClearDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            ClearDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ClearDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClearDetailsDto ClearDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("清关ID", ClearDetails.getClearId());
            map.put("单据编号", ClearDetails.getClearNo());
            map.put("序号", ClearDetails.getSeqNo());
            map.put("货品ID", ClearDetails.getGoodsId());
            map.put("货品编码", ClearDetails.getGoodsCode());
            map.put("海关货号", ClearDetails.getGoodsNo());
            map.put("HS编码", ClearDetails.getHsCode());
            map.put("商品名称", ClearDetails.getGoodsName());
            map.put("净重（千克）", ClearDetails.getNetWeight());
            map.put("毛重（千克）", ClearDetails.getGrossWeight());
            map.put("法一单位", ClearDetails.getLegalUnit());
            map.put("法一单位代码", ClearDetails.getLegalUnitCode());
            map.put("法一数量", ClearDetails.getLegalNum());
            map.put("法二单位", ClearDetails.getSecondUnit());
            map.put("法二单位代码", ClearDetails.getSecondUnitCode());
            map.put("法二数量", ClearDetails.getSecondNum());
            map.put("数量", ClearDetails.getQty());
            map.put("计量单位", ClearDetails.getUnit());
            map.put("商品单价", ClearDetails.getPrice());
            map.put("商品总价", ClearDetails.getTotalPrice());
            map.put("规格型号", ClearDetails.getProperty());
            map.put("币种", ClearDetails.getCurrency());
            map.put("原产国", ClearDetails.getMakeCountry());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saveBatch(List<ClearDetails> detailsSave) {
        ClearDetailsRepository.saveAll(detailsSave);
    }

    @Override
    public List<ClearDetails> queryByClearId(Long clearId) {
        return ClearDetailsRepository.findByClearId(clearId);
    }
}