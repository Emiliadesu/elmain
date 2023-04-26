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

import me.zhengjie.domain.InboundOrder;
import me.zhengjie.domain.InboundTally;
import me.zhengjie.domain.InboundTallyDetails;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.InboundTallyDetailsService;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.InboundTallyRepository;
import me.zhengjie.service.InboundTallyService;
import me.zhengjie.service.dto.InboundTallyDto;
import me.zhengjie.service.dto.InboundTallyQueryCriteria;
import me.zhengjie.service.mapstruct.InboundTallyMapper;
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
* @date 2021-06-16
**/
@Service
@RequiredArgsConstructor
public class InboundTallyServiceImpl implements InboundTallyService {

    private final InboundTallyRepository inboundTallyRepository;
    private final InboundTallyMapper inboundTallyMapper;

    @Autowired
    private InboundTallyDetailsService inboundTallyDetailsService;

    @Override
    public Map<String,Object> queryAll(InboundTallyQueryCriteria criteria, Pageable pageable){
        Page<InboundTally> page = inboundTallyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(inboundTallyMapper::toDto));
    }

    @Override
    public List<InboundTallyDto> queryAll(InboundTallyQueryCriteria criteria){
        return inboundTallyMapper.toDto(inboundTallyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public InboundTallyDto findById(Long id) {
        InboundTally inboundTally = inboundTallyRepository.findById(id).orElseGet(InboundTally::new);
        ValidationUtil.isNull(inboundTally.getId(),"InboundTally","id",id);
        return inboundTallyMapper.toDto(inboundTally);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public InboundTallyDto create(InboundTally resources) {
        if(inboundTallyRepository.findByOrderNo(resources.getOrderNo()) != null){
            throw new EntityExistException(InboundTally.class,"order_no",resources.getOrderNo());
        }
        return inboundTallyMapper.toDto(inboundTallyRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(InboundTally resources) {
        InboundTally inboundTally = inboundTallyRepository.findById(resources.getId()).orElseGet(InboundTally::new);
        ValidationUtil.isNull( inboundTally.getId(),"InboundTally","id",resources.getId());
        inboundTally.copy(resources);
        inboundTallyRepository.save(inboundTally);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            inboundTallyRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<InboundTallyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (InboundTallyDto inboundTally : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库单ID", inboundTally.getOrderId());
            map.put("入库单号", inboundTally.getOrderNo());
            map.put("理货单号", inboundTally.getTallyNo());
            map.put("理货状态", inboundTally.getStatus());
            map.put("托数", inboundTally.getPalletNum());
            map.put("箱数", inboundTally.getBoxNum());
            map.put("预期SKU数", inboundTally.getExpectSkuNum());
            map.put("理货SKU数", inboundTally.getTallySkuNum());
            map.put("预期总件数", inboundTally.getExpectTotalNum());
            map.put("理货总件数", inboundTally.getTallyTotalNum());
            map.put("理货正品件数", inboundTally.getTallyNormalNum());
            map.put("理货残品件数", inboundTally.getTallyDamagedNum());
            map.put("理货开始时间", inboundTally.getTallyStartTime());
            map.put("理货结束时间", inboundTally.getTallyEndTime());
            map.put("创建人", inboundTally.getCreateBy());
            map.put("创建时间", inboundTally.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public InboundTally queryByOrderNo(String orderNo) {
        InboundTally tally = inboundTallyRepository.findByOrderNo(orderNo);
        return tally;
    }

    @Override
    public InboundTally queryByTallyNo(String tallyNo) {
        return inboundTallyRepository.findByTallyNo(tallyNo);
    }

}