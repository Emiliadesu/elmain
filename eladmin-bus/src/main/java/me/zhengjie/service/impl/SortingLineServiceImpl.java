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

import me.zhengjie.domain.SortingLine;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SortingLineRepository;
import me.zhengjie.service.SortingLineService;
import me.zhengjie.service.dto.SortingLineDto;
import me.zhengjie.service.dto.SortingLineQueryCriteria;
import me.zhengjie.service.mapstruct.SortingLineMapper;
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
* @date 2021-10-04
**/
@Service
@RequiredArgsConstructor
public class SortingLineServiceImpl implements SortingLineService {

    private final SortingLineRepository sortingLineRepository;
    private final SortingLineMapper sortingLineMapper;

    @Override
    public Map<String,Object> queryAll(SortingLineQueryCriteria criteria, Pageable pageable){
        Page<SortingLine> page = sortingLineRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(sortingLineMapper::toDto));
    }

    @Override
    public List<SortingLineDto> queryAll(SortingLineQueryCriteria criteria){
        return sortingLineMapper.toDto(sortingLineRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SortingLineDto findById(Long id) {
        SortingLine sortingLine = sortingLineRepository.findById(id).orElseGet(SortingLine::new);
        ValidationUtil.isNull(sortingLine.getId(),"SortingLine","id",id);
        return sortingLineMapper.toDto(sortingLine);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortingLineDto create(SortingLine resources) {
        return sortingLineMapper.toDto(sortingLineRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SortingLine resources) {
        SortingLine sortingLine = sortingLineRepository.findById(resources.getId()).orElseGet(SortingLine::new);
        ValidationUtil.isNull( sortingLine.getId(),"SortingLine","id",resources.getId());
        sortingLine.copy(resources);
        sortingLineRepository.save(sortingLine);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            sortingLineRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SortingLineDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SortingLineDto sortingLine : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("分拣线名称", sortingLine.getLineName());
            map.put("分拣线代码", sortingLine.getLineCode());
            map.put("绑定区域", sortingLine.getArea());
            map.put("绑定用户ID", sortingLine.getUserId());
            map.put("状态", sortingLine.getStatus());
            map.put("创建人", sortingLine.getCreateBy());
            map.put("创建时间", sortingLine.getCreateTime());
            map.put("更新者", sortingLine.getUpdateBy());
            map.put("更新时间", sortingLine.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}