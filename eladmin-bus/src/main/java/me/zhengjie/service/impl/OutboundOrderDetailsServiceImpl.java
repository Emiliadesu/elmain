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

import me.zhengjie.domain.OrderReturn;
import me.zhengjie.domain.OrderReturnDetails;
import me.zhengjie.domain.OutboundOrderDetails;
import me.zhengjie.repository.OrderReturnDetailsRepository;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OutboundOrderDetailsRepository;
import me.zhengjie.service.OutboundOrderDetailsService;
import me.zhengjie.service.dto.OutboundOrderDetailsDto;
import me.zhengjie.service.dto.OutboundOrderDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.OutboundOrderDetailsMapper;
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
 * @author luob
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-07-13
 **/
@Service
@RequiredArgsConstructor
public class OutboundOrderDetailsServiceImpl implements OutboundOrderDetailsService {

    private final OutboundOrderDetailsRepository OutboundOrderDetailsRepository;
    private final OutboundOrderDetailsMapper OutboundOrderDetailsMapper;

    @Override
    public Map<String, Object> queryAll(OutboundOrderDetailsQueryCriteria criteria, Pageable pageable) {
        Page<OutboundOrderDetails> page = OutboundOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(OutboundOrderDetailsMapper::toDto));
    }

    @Override
    public List<OutboundOrderDetailsDto> queryAll(OutboundOrderDetailsQueryCriteria criteria) {
        return OutboundOrderDetailsMapper.toDto(OutboundOrderDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public OutboundOrderDetailsDto findById(Long id) {
        OutboundOrderDetails OutboundOrderDetails = OutboundOrderDetailsRepository.findById(id).orElseGet(OutboundOrderDetails::new);
        ValidationUtil.isNull(OutboundOrderDetails.getId(), "OutboundOrderDetails", "id", id);
        return OutboundOrderDetailsMapper.toDto(OutboundOrderDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutboundOrderDetailsDto create(OutboundOrderDetails resources) {
        return OutboundOrderDetailsMapper.toDto(OutboundOrderDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OutboundOrderDetails resources) {
        OutboundOrderDetails OutboundOrderDetails = OutboundOrderDetailsRepository.findById(resources.getId()).orElseGet(OutboundOrderDetails::new);
        ValidationUtil.isNull(OutboundOrderDetails.getId(), "OutboundOrderDetails", "id", resources.getId());
        OutboundOrderDetails.copy(resources);
        OutboundOrderDetailsRepository.save(OutboundOrderDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            OutboundOrderDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OutboundOrderDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OutboundOrderDetailsDto OutboundOrderDetails : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("订单ID", OutboundOrderDetails.getOrderId());
            map.put("入库单号", OutboundOrderDetails.getOrderNo());
            map.put("货品ID", OutboundOrderDetails.getGoodsId());
            map.put("货品编码", OutboundOrderDetails.getGoodsCode());
            map.put("海关货号", OutboundOrderDetails.getGoodsNo());
            map.put("条码", OutboundOrderDetails.getBarCode());
            map.put("商品名称", OutboundOrderDetails.getGoodsName());
            map.put("商品行", OutboundOrderDetails.getGoodsLineNo());
            map.put("预期发货数量", OutboundOrderDetails.getExpectNum());
            map.put("实际发货数量", OutboundOrderDetails.getDeliverNum());
            map.put("短少数量", OutboundOrderDetails.getLackNum());
            map.put("正品数量", OutboundOrderDetails.getNormalNum());
            map.put("残品数量", OutboundOrderDetails.getDamagedNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<OutboundOrderDetails> findbyOutId(Long outId) {
        OutboundOrderDetails details = new OutboundOrderDetails();
        details.setOrderId(outId);
        return OutboundOrderDetailsRepository.findAll(Example.of(details));
    }

    @Override
    public void saveBatch(List<OutboundOrderDetails> details) {
        OutboundOrderDetailsRepository.saveAll(details);
    }

}
