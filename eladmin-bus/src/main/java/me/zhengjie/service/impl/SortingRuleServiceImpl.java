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

import me.zhengjie.domain.SortingRule;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SortingRuleRepository;
import me.zhengjie.service.SortingRuleService;
import me.zhengjie.service.dto.SortingRuleDto;
import me.zhengjie.service.dto.SortingRuleQueryCriteria;
import me.zhengjie.service.mapstruct.SortingRuleMapper;
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
public class SortingRuleServiceImpl implements SortingRuleService {

    private final SortingRuleRepository sortingRuleRepository;
    private final SortingRuleMapper sortingRuleMapper;

    @Override
    public Map<String,Object> queryAll(SortingRuleQueryCriteria criteria, Pageable pageable){
        Page<SortingRule> page = sortingRuleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(sortingRuleMapper::toDto));
    }

    @Override
    public List<SortingRuleDto> queryAll(SortingRuleQueryCriteria criteria){
        return sortingRuleMapper.toDto(sortingRuleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SortingRuleDto findById(Long id) {
        SortingRule sortingRule = sortingRuleRepository.findById(id).orElseGet(SortingRule::new);
        ValidationUtil.isNull(sortingRule.getId(),"SortingRule","id",id);
        return sortingRuleMapper.toDto(sortingRule);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortingRuleDto create(SortingRule resources) {
        return sortingRuleMapper.toDto(sortingRuleRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SortingRule resources) {
        SortingRule sortingRule = sortingRuleRepository.findById(resources.getId()).orElseGet(SortingRule::new);
        ValidationUtil.isNull( sortingRule.getId(),"SortingRule","id",resources.getId());
        sortingRule.copy(resources);
        sortingRuleRepository.save(sortingRule);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            sortingRuleRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SortingRuleDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SortingRuleDto sortingRule : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("规则名称", sortingRule.getRuleName());
            map.put("规则代码", sortingRule.getRuleCode());
            map.put("规则代码1(跨境购)", sortingRule.getRuleCode1());
            map.put("规则代码2(菜鸟)", sortingRule.getRuleCode2());
            map.put("状态", sortingRule.getStatus());
            map.put("创建人", sortingRule.getCreateBy());
            map.put("创建时间", sortingRule.getCreateTime());
            map.put("更新者", sortingRule.getUpdateBy());
            map.put("更新时间", sortingRule.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<SortingRule> queryList() {
        SortingRuleQueryCriteria criteria = new SortingRuleQueryCriteria();
        criteria.setStatus("1");
        List<SortingRule> all = sortingRuleRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return all;
    }
}