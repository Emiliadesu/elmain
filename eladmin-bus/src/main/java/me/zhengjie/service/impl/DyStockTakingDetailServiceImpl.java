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

import me.zhengjie.domain.DyStockTakingDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyStockTakingDetailRepository;
import me.zhengjie.service.DyStockTakingDetailService;
import me.zhengjie.service.dto.DyStockTakingDetailDto;
import me.zhengjie.service.dto.DyStockTakingDetailQueryCriteria;
import me.zhengjie.service.mapstruct.DyStockTakingDetailMapper;
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
* @author wangm
* @date 2021-09-26
**/
@Service
@RequiredArgsConstructor
public class DyStockTakingDetailServiceImpl implements DyStockTakingDetailService {

    private final DyStockTakingDetailRepository dyStockTakingDetailRepository;
    private final DyStockTakingDetailMapper dyStockTakingDetailMapper;

    @Override
    public Map<String,Object> queryAll(DyStockTakingDetailQueryCriteria criteria, Pageable pageable){
        Page<DyStockTakingDetail> page = dyStockTakingDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyStockTakingDetailMapper::toDto));
    }

    @Override
    public List<DyStockTakingDetailDto> queryAll(DyStockTakingDetailQueryCriteria criteria){
        return dyStockTakingDetailMapper.toDto(dyStockTakingDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyStockTakingDetailDto findById(Long id) {
        DyStockTakingDetail dyStockTakingDetail = dyStockTakingDetailRepository.findById(id).orElseGet(DyStockTakingDetail::new);
        ValidationUtil.isNull(dyStockTakingDetail.getId(),"DyStockTakingDetail","id",id);
        return dyStockTakingDetailMapper.toDto(dyStockTakingDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyStockTakingDetailDto create(DyStockTakingDetail resources) {
        return dyStockTakingDetailMapper.toDto(dyStockTakingDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyStockTakingDetail resources) {
        DyStockTakingDetail dyStockTakingDetail = dyStockTakingDetailRepository.findById(resources.getId()).orElseGet(DyStockTakingDetail::new);
        ValidationUtil.isNull( dyStockTakingDetail.getId(),"DyStockTakingDetail","id",resources.getId());
        dyStockTakingDetail.copy(resources);
        dyStockTakingDetailRepository.save(dyStockTakingDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyStockTakingDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyStockTakingDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyStockTakingDetailDto dyStockTakingDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("盘点主id", dyStockTakingDetail.getTakingId());
            map.put("货号", dyStockTakingDetail.getGoodsNo());
            map.put("质量等级", dyStockTakingDetail.getQualityGrade());
            map.put("数量", dyStockTakingDetail.getQuantity());
            map.put("盘点原因", dyStockTakingDetail.getReasonCode());
            map.put("具体原因", dyStockTakingDetail.getReasonMsg());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<DyStockTakingDetail> queryByTalkingId(Long talkingId) {
        DyStockTakingDetail detail=new DyStockTakingDetail();
        detail.setTakingId(talkingId);
        return dyStockTakingDetailRepository.findAll(Example.of(detail));
    }

    @Override
    public List<DyStockTakingDetailDto> queryByTalkingIdDto(Long talkingId) {
        return dyStockTakingDetailMapper.toDto(queryByTalkingId(talkingId));
    }
}
