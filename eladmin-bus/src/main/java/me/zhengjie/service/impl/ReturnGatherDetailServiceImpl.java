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

import me.zhengjie.domain.ReturnGatherDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ReturnGatherDetailRepository;
import me.zhengjie.service.ReturnGatherDetailService;
import me.zhengjie.service.dto.ReturnGatherDetailDto;
import me.zhengjie.service.dto.ReturnGatherDetailQueryCriteria;
import me.zhengjie.service.mapstruct.ReturnGatherDetailMapper;
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
* @date 2022-04-06
**/
@Service
@RequiredArgsConstructor
public class ReturnGatherDetailServiceImpl implements ReturnGatherDetailService {

    private final ReturnGatherDetailRepository returnGatherDetailRepository;
    private final ReturnGatherDetailMapper returnGatherDetailMapper;

    @Override
    public Map<String,Object> queryAll(ReturnGatherDetailQueryCriteria criteria, Pageable pageable){
        Page<ReturnGatherDetail> page = returnGatherDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(returnGatherDetailMapper::toDto));
    }

    @Override
    public List<ReturnGatherDetailDto> queryAll(ReturnGatherDetailQueryCriteria criteria){
        return returnGatherDetailMapper.toDto(returnGatherDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ReturnGatherDetailDto findById(Long id) {
        ReturnGatherDetail returnGatherDetail = returnGatherDetailRepository.findById(id).orElseGet(ReturnGatherDetail::new);
        ValidationUtil.isNull(returnGatherDetail.getId(),"ReturnGatherDetail","id",id);
        return returnGatherDetailMapper.toDto(returnGatherDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReturnGatherDetailDto create(ReturnGatherDetail resources) {
        return returnGatherDetailMapper.toDto(returnGatherDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ReturnGatherDetail resources) {
        ReturnGatherDetail returnGatherDetail = returnGatherDetailRepository.findById(resources.getId()).orElseGet(ReturnGatherDetail::new);
        ValidationUtil.isNull( returnGatherDetail.getId(),"ReturnGatherDetail","id",resources.getId());
        returnGatherDetail.copy(resources);
        returnGatherDetailRepository.save(returnGatherDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            returnGatherDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ReturnGatherDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ReturnGatherDetailDto returnGatherDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("提总单ID", returnGatherDetail.getGatherId());
            map.put("提总单号", returnGatherDetail.getGatherNo());
            map.put("货号", returnGatherDetail.getGoodsNo());
            map.put("数量", returnGatherDetail.getQty());
            map.put("名称", returnGatherDetail.getGoodsName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saves(List<ReturnGatherDetail> detailList) {
        returnGatherDetailRepository.saveAll(detailList);
    }

    @Override
    public List<ReturnGatherDetail> queryByGatherId(Long gatherId) {
        return returnGatherDetailRepository.findByGatherId(gatherId);
    }
}