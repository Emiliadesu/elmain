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

import me.zhengjie.domain.OutofPlanDetail;
import me.zhengjie.domain.StockInTolly;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.OutofPlanDetailRepository;
import me.zhengjie.service.OutofPlanDetailService;
import me.zhengjie.service.dto.OutofPlanDetailDto;
import me.zhengjie.service.dto.OutofPlanDetailQueryCriteria;
import me.zhengjie.service.mapstruct.OutofPlanDetailMapper;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;
import org.springframework.web.multipart.MultipartFile;

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
* @date 2021-03-23
**/
@Service
@RequiredArgsConstructor
public class OutofPlanDetailServiceImpl implements OutofPlanDetailService {

    private final OutofPlanDetailRepository outofPlanDetailRepository;
    private final OutofPlanDetailMapper outofPlanDetailMapper;

    @Override
    public Map<String,Object> queryAll(OutofPlanDetailQueryCriteria criteria, Pageable pageable){
        Page<OutofPlanDetail> page = outofPlanDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(outofPlanDetailMapper::toDto));
    }

    @Override
    public List<OutofPlanDetailDto> queryAll(OutofPlanDetailQueryCriteria criteria){
        return outofPlanDetailMapper.toDto(outofPlanDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public OutofPlanDetailDto findById(Long id) {
        OutofPlanDetail outofPlanDetail = outofPlanDetailRepository.findById(id).orElseGet(OutofPlanDetail::new);
        ValidationUtil.isNull(outofPlanDetail.getId(),"OutofPlanDetail","id",id);
        return outofPlanDetailMapper.toDto(outofPlanDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OutofPlanDetailDto create(OutofPlanDetail resources) {
        return outofPlanDetailMapper.toDto(outofPlanDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(OutofPlanDetail resources) {
        OutofPlanDetail outofPlanDetail = outofPlanDetailRepository.findById(resources.getId()).orElseGet(OutofPlanDetail::new);
        ValidationUtil.isNull( outofPlanDetail.getId(),"OutofPlanDetail","id",resources.getId());
        outofPlanDetail.copy(resources);
        outofPlanDetailRepository.save(outofPlanDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            outofPlanDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<OutofPlanDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (OutofPlanDetailDto outofPlanDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商品不在通知单的条码", outofPlanDetail.getEan13());
            map.put("商品名", outofPlanDetail.getProductName());
            map.put("收货数量", outofPlanDetail.getReceiveQty());
            map.put("图片地址", outofPlanDetail.getPicUrl());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void uploadfile(Long id, MultipartFile file) {

    }

    @Override
    public OutofPlanDetail queryOne(OutofPlanDetail outofPlanDetail) {
        return outofPlanDetailRepository.findOne(Example.of(outofPlanDetail)).orElse(null);
    }

    @Override
    public List<OutofPlanDetail> queryAllByInTallyId(Long inTallyId) {
        OutofPlanDetail outofPlanDetail=new OutofPlanDetail();
        StockInTolly stockInTolly=new StockInTolly();
        stockInTolly.setId(inTallyId);
        outofPlanDetail.setStockInTolly(stockInTolly);
        return outofPlanDetailRepository.findAll(Example.of(outofPlanDetail));
    }
}
