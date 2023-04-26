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

import me.zhengjie.domain.TransDetails;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.TransDetailsRepository;
import me.zhengjie.service.TransDetailsService;
import me.zhengjie.service.dto.TransDetailsDto;
import me.zhengjie.service.dto.TransDetailsQueryCriteria;
import me.zhengjie.service.mapstruct.TransDetailsMapper;
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
* @date 2021-09-04
**/
@Service
@RequiredArgsConstructor
public class TransDetailsServiceImpl implements TransDetailsService {

    private final TransDetailsRepository transDetailsRepository;
    private final TransDetailsMapper transDetailsMapper;

    @Override
    public Map<String,Object> queryAll(TransDetailsQueryCriteria criteria, Pageable pageable){
        Page<TransDetails> page = transDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(transDetailsMapper::toDto));
    }

    @Override
    public List<TransDetailsDto> queryAll(TransDetailsQueryCriteria criteria){
        return transDetailsMapper.toDto(transDetailsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public TransDetailsDto findById(Long id) {
        TransDetails transDetails = transDetailsRepository.findById(id).orElseGet(TransDetails::new);
        ValidationUtil.isNull(transDetails.getId(),"TransDetails","id",id);
        return transDetailsMapper.toDto(transDetails);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransDetailsDto create(TransDetails resources) {
        return transDetailsMapper.toDto(transDetailsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TransDetails resources) {
        TransDetails transDetails = transDetailsRepository.findById(resources.getId()).orElseGet(TransDetails::new);
        ValidationUtil.isNull( transDetails.getId(),"TransDetails","id",resources.getId());
        transDetails.copy(resources);
        transDetailsRepository.save(transDetails);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            transDetailsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TransDetailsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TransDetailsDto transDetails : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("清关ID", transDetails.getOrderId());
            map.put("箱号", transDetails.getContainerNo());
            map.put("箱型", transDetails.getContainerType());
            map.put("排车方", transDetails.getPlanCarType());
            map.put("车牌", transDetails.getPlateNo());
            map.put("车型", transDetails.getCarType());
            map.put("是否拼车", transDetails.getShareFlag());
            map.put("打包方式", transDetails.getPackWay());
            map.put("打包数量", transDetails.getPackNum());
            map.put("更新者", transDetails.getUpdateBy());
            map.put("更新时间", transDetails.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<TransDetails> queryByOrderId(Long orderId) {
        return transDetailsRepository.findByOrderId(orderId);
    }
}