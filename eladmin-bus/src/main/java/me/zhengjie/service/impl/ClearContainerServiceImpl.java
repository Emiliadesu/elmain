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

import me.zhengjie.domain.ClearContainer;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ClearContainerRepository;
import me.zhengjie.service.ClearContainerService;
import me.zhengjie.service.dto.ClearContainerDto;
import me.zhengjie.service.dto.ClearContainerQueryCriteria;
import me.zhengjie.service.mapstruct.ClearContainerMapper;
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
* @date 2021-03-23
**/
@Service
@RequiredArgsConstructor
public class ClearContainerServiceImpl implements ClearContainerService {

    private final ClearContainerRepository clearContainerRepository;
    private final ClearContainerMapper clearContainerMapper;

    @Override
    public Map<String,Object> queryAll(ClearContainerQueryCriteria criteria, Pageable pageable){
        Page<ClearContainer> page = clearContainerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(clearContainerMapper::toDto));
    }

    @Override
    public List<ClearContainerDto> queryAll(ClearContainerQueryCriteria criteria){
        return clearContainerMapper.toDto(clearContainerRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ClearContainerDto findById(Long id) {
        ClearContainer clearContainer = clearContainerRepository.findById(id).orElseGet(ClearContainer::new);
        ValidationUtil.isNull(clearContainer.getId(),"ClearContainer","id",id);
        return clearContainerMapper.toDto(clearContainer);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClearContainerDto create(ClearContainer resources) {
        return clearContainerMapper.toDto(clearContainerRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClearContainer resources) {
        ClearContainer clearContainer = clearContainerRepository.findById(resources.getId()).orElseGet(ClearContainer::new);
        ValidationUtil.isNull( clearContainer.getId(),"ClearContainer","id",resources.getId());
        clearContainer.copy(resources);
        clearContainerRepository.save(clearContainer);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            clearContainerRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<ClearContainerDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClearContainerDto clearContainer : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("清关ID", clearContainer.getClearId());
            map.put("更新者", clearContainer.getUpdateBy());
            map.put("更新时间", clearContainer.getUpdateTime());
            map.put("箱号", clearContainer.getContainerNo());
            map.put("箱型", clearContainer.getContainerType());
            map.put("打包方式", clearContainer.getPackWay());
            map.put("打包数量", clearContainer.getPackNum());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<ClearContainer> queryByClearId(Long clearId) {
        return clearContainerRepository.findByClearId(clearId);
    }
}