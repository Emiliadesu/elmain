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

import me.zhengjie.domain.LoadHeader;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LoadHeaderRepository;
import me.zhengjie.service.LoadHeaderService;
import me.zhengjie.service.dto.LoadHeaderDto;
import me.zhengjie.service.dto.LoadHeaderQueryCriteria;
import me.zhengjie.service.mapstruct.LoadHeaderMapper;
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
* @date 2021-04-01
**/
@Service
@RequiredArgsConstructor
public class LoadHeaderServiceImpl implements LoadHeaderService {

    private final LoadHeaderRepository loadHeaderRepository;
    private final LoadHeaderMapper loadHeaderMapper;

    @Override
    public Map<String,Object> queryAll(LoadHeaderQueryCriteria criteria, Pageable pageable){
        Page<LoadHeader> page = loadHeaderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(loadHeaderMapper::toDto));
    }

    @Override
    public List<LoadHeaderDto> queryAll(LoadHeaderQueryCriteria criteria){
        return loadHeaderMapper.toDto(loadHeaderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LoadHeaderDto findById(Long id) {
        LoadHeader loadHeader = loadHeaderRepository.findById(id).orElseGet(LoadHeader::new);
        ValidationUtil.isNull(loadHeader.getId(),"LoadHeader","id",id);
        return loadHeaderMapper.toDto(loadHeader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoadHeaderDto create(LoadHeader resources) {
        return loadHeaderMapper.toDto(loadHeaderRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LoadHeader resources) {
        LoadHeader loadHeader = loadHeaderRepository.findById(resources.getId()).orElseGet(LoadHeader::new);
        ValidationUtil.isNull( loadHeader.getId(),"LoadHeader","id",resources.getId());
        loadHeader.copy(resources);
        loadHeaderRepository.save(loadHeader);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            loadHeaderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<LoadHeaderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LoadHeaderDto loadHeader : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("租户编码", loadHeader.getTenantCode());
            map.put("装载单号", loadHeader.getLoadNo());
            map.put("车牌号", loadHeader.getVechileNo());
            map.put("仓库编码", loadHeader.getWarehouseId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public LoadHeader findByOutId(Long id) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setId(id);
        LoadHeader loadHeader=new LoadHeader();
        loadHeader.setWmsOutstock(wmsOutstock);
        return loadHeaderRepository.findOne(Example.of(loadHeader)).orElse(null);
    }
}
