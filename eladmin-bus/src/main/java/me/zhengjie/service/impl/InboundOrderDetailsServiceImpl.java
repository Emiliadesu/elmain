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

import me.zhengjie.domain.InboundOrderDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.InboundOrderDetailsRepository;
import me.zhengjie.service.InboundOrderDetailsService;
import me.zhengjie.service.dto.InboundOrderDetailsDto;
import me.zhengjie.service.dto.InboundOrderDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.InboundOrderDetailsMapper;
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
* @date 2021-05-13
**/
@Service
@RequiredArgsConstructor
public class InboundOrderDetailsServiceImpl implements InboundOrderDetailsService {

    private final InboundOrderDetailsRepository inboundOrderDetailsRepository;
    private final InboundOrderDetailsMapper inboundOrderDetailsMapper;

    @Override
    public Map<String,Object> queryAll(InboundOrderDetailsQueryCriteria criteria, Pageable pageable){
        Page<InboundOrderDetails> page = inboundOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(inboundOrderDetailsMapper::toDto));
    }

    @Override
    public List<InboundOrderDetailsDto> queryAll(InboundOrderDetailsQueryCriteria criteria){
        return inboundOrderDetailsMapper.toDto(inboundOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public InboundOrderDetailsDto findById(Long id) {
        InboundOrderDetails inboundOrderDetails = inboundOrderDetailsRepository.findById(id).orElseGet(InboundOrderDetails::new);
        ValidationUtil.isNull(inboundOrderDetails.getId(),"InboundOrderDetails","id",id);
        return inboundOrderDetailsMapper.toDto(inboundOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundOrderDetailsDto create(InboundOrderDetails resources) {
        return inboundOrderDetailsMapper.toDto(inboundOrderDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InboundOrderDetails resources) {
        InboundOrderDetails inboundOrderDetails = inboundOrderDetailsRepository.findById(resources.getId()).orElseGet(InboundOrderDetails::new);
        ValidationUtil.isNull( inboundOrderDetails.getId(),"InboundOrderDetails","id",resources.getId());
        inboundOrderDetails.copy(resources);
        inboundOrderDetailsRepository.save(inboundOrderDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            inboundOrderDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InboundOrderDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundOrderDetailsDto inboundOrderDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库单号", inboundOrderDetails.getOrderNo());
            map.put("货品ID", inboundOrderDetails.getGoodsId());
            map.put("货品编码", inboundOrderDetails.getGoodsCode());
            map.put("海关货号", inboundOrderDetails.getGoodsNo());
            map.put("条码", inboundOrderDetails.getBarCode());
            map.put("商品名称", inboundOrderDetails.getGoodsName());
            map.put("商品行", inboundOrderDetails.getGoodsLineNo());
            map.put("预期到货数量", inboundOrderDetails.getExpectNum());
            map.put("实际收货数量", inboundOrderDetails.getTakeNum());
            map.put("短少数量", inboundOrderDetails.getLackNum());
            map.put("正品数量", inboundOrderDetails.getNormalNum());
            map.put("残品数量", inboundOrderDetails.getDamagedNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saveBatch(List<InboundOrderDetails> details) {
        inboundOrderDetailsRepository.saveAll(details);
    }

    @Override
    public List<InboundOrderDetails> queryByOrderId(Long id) {
        InboundOrderDetailsQueryCriteria criteria = new InboundOrderDetailsQueryCriteria();
        criteria.setOrderId(id);
        return inboundOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }
}