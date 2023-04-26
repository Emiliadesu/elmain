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

import me.zhengjie.domain.DyStockTransform;
import me.zhengjie.domain.DyStockTransformDetail;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyStockTransformDetailRepository;
import me.zhengjie.service.DyStockTransformDetailService;
import me.zhengjie.service.dto.DyStockTransformDetailDto;
import me.zhengjie.service.dto.DyStockTransformDetailQueryCriteria;
import me.zhengjie.service.mapstruct.DyStockTransformDetailMapper;
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
public class DyStockTransformDetailServiceImpl implements DyStockTransformDetailService {

    private final DyStockTransformDetailRepository dyStockTransformDetailRepository;
    private final DyStockTransformDetailMapper dyStockTransformDetailMapper;

    @Override
    public Map<String,Object> queryAll(DyStockTransformDetailQueryCriteria criteria, Pageable pageable){
        Page<DyStockTransformDetail> page = dyStockTransformDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyStockTransformDetailMapper::toDto));
    }

    @Override
    public List<DyStockTransformDetailDto> queryAll(DyStockTransformDetailQueryCriteria criteria){
        return dyStockTransformDetailMapper.toDto(dyStockTransformDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyStockTransformDetailDto findById(Long id) {
        DyStockTransformDetail dyStockTransformDetail = dyStockTransformDetailRepository.findById(id).orElseGet(DyStockTransformDetail::new);
        ValidationUtil.isNull(dyStockTransformDetail.getId(),"DyStockTransformDetail","id",id);
        return dyStockTransformDetailMapper.toDto(dyStockTransformDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyStockTransformDetailDto create(DyStockTransformDetail resources) {
        return dyStockTransformDetailMapper.toDto(dyStockTransformDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyStockTransformDetail resources) {
        DyStockTransformDetail dyStockTransformDetail = dyStockTransformDetailRepository.findById(resources.getId()).orElseGet(DyStockTransformDetail::new);
        ValidationUtil.isNull( dyStockTransformDetail.getId(),"DyStockTransformDetail","id",resources.getId());
        dyStockTransformDetail.copy(resources);
        dyStockTransformDetailRepository.save(dyStockTransformDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyStockTransformDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyStockTransformDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyStockTransformDetailDto dyStockTransformDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("库存变动表头id", dyStockTransformDetail.getMainId());
            map.put("货号", dyStockTransformDetail.getGoodsNo());
            map.put("调整前质量等级", dyStockTransformDetail.getFromGrade());
            map.put("调整前质量等级", dyStockTransformDetail.getToGrade());
            map.put("数量", dyStockTransformDetail.getQuantity());
            map.put("具体原因", dyStockTransformDetail.getReasonMsg());
            map.put("创建人", dyStockTransformDetail.getCreateUserId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<DyStockTransformDetail> queryByMainId(Long mainId) {
        DyStockTransformDetail detail=new DyStockTransformDetail();
        detail.setMainId(mainId);
        return dyStockTransformDetailRepository.findAll(Example.of(detail));
    }

    @Override
    public List<DyStockTransformDetailDto> queryByMainIdDto(Long mainId) {
        return dyStockTransformDetailMapper.toDto(queryByMainId(mainId));
    }

    @Override
    public List<DyStockTransformDetail> queryByBatchNoAndLocation(String batchNo, String location,String goodsNo) {
        return dyStockTransformDetailRepository.findAllByWmsBatchAndLocationAndGoodsNoOrderByMainIdDesc(batchNo,location,goodsNo);
    }
}
