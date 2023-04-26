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

import me.zhengjie.domain.SortingLineChuteCode;
import me.zhengjie.service.SortingLineService;
import me.zhengjie.service.SortingRuleService;
import me.zhengjie.service.dto.SortingRuleDto;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SortingLineChuteCodeRepository;
import me.zhengjie.service.SortingLineChuteCodeService;
import me.zhengjie.service.dto.SortingLineChuteCodeDto;
import me.zhengjie.service.dto.SortingLineChuteCodeQueryCriteria;
import me.zhengjie.service.mapstruct.SortingLineChuteCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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
@CacheConfig(cacheNames = "chuteCode")
@Service
@RequiredArgsConstructor
public class SortingLineChuteCodeServiceImpl implements SortingLineChuteCodeService {

    private final SortingLineChuteCodeRepository sortingLineChuteCodeRepository;
    private final SortingLineChuteCodeMapper sortingLineChuteCodeMapper;

    @Autowired
    private SortingRuleService sortingRuleService;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(SortingLineChuteCodeQueryCriteria criteria, Pageable pageable){
        Page<SortingLineChuteCode> page = sortingLineChuteCodeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(sortingLineChuteCodeMapper::toDto));
    }

    @Override
    public List<SortingLineChuteCodeDto> queryAll(SortingLineChuteCodeQueryCriteria criteria){
        return sortingLineChuteCodeMapper.toDto(sortingLineChuteCodeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SortingLineChuteCodeDto findById(Long id) {
        SortingLineChuteCode sortingLineChuteCode = sortingLineChuteCodeRepository.findById(id).orElseGet(SortingLineChuteCode::new);
        ValidationUtil.isNull(sortingLineChuteCode.getId(),"SortingLineChuteCode","id",id);
        return sortingLineChuteCodeMapper.toDto(sortingLineChuteCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SortingLineChuteCodeDto create(SortingLineChuteCode resources) {
        SortingRuleDto sortingRuleDto = sortingRuleService.findById(resources.getRuleId());
        resources.setRuleName(sortingRuleDto.getRuleName());
        resources.setRuleCode(sortingRuleDto.getRuleCode());
        resources.setRuleCode1(sortingRuleDto.getRuleCode1());
        resources.setRuleCode2(sortingRuleDto.getRuleCode2());
        redisUtils.del("chuteCode::userCode1:" + resources.getUserId() + resources.getRuleCode1());
        redisUtils.del("chuteCode::userCode2:" + resources.getUserId() + resources.getRuleCode2());
        return sortingLineChuteCodeMapper.toDto(sortingLineChuteCodeRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SortingLineChuteCode resources) {
        SortingLineChuteCode sortingLineChuteCode = sortingLineChuteCodeRepository.findById(resources.getId()).orElseGet(SortingLineChuteCode::new);
        ValidationUtil.isNull( sortingLineChuteCode.getId(),"SortingLineChuteCode","id",resources.getId());
        sortingLineChuteCode.copy(resources);
        sortingLineChuteCodeRepository.save(sortingLineChuteCode);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            SortingLineChuteCodeDto sortingLineChuteCodeDto = findById(id);
            redisUtils.del("chuteCode::userCode1:" + sortingLineChuteCodeDto.getUserId() + sortingLineChuteCodeDto.getRuleCode1());
            redisUtils.del("chuteCode::userCode2:" + sortingLineChuteCodeDto.getUserId() + sortingLineChuteCodeDto.getRuleCode2());
            sortingLineChuteCodeRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SortingLineChuteCodeDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SortingLineChuteCodeDto sortingLineChuteCode : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("分拣线ID", sortingLineChuteCode.getLineId());
            map.put("格口代码", sortingLineChuteCode.getChuteCode());
            map.put("格口名称", sortingLineChuteCode.getChuteName());
            map.put("状态", sortingLineChuteCode.getStatus());
            map.put("创建人", sortingLineChuteCode.getCreateBy());
            map.put("创建时间", sortingLineChuteCode.getCreateTime());
            map.put("更新者", sortingLineChuteCode.getUpdateBy());
            map.put("更新时间", sortingLineChuteCode.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<SortingLineChuteCode> queryByLineId(Long lineId) {
        SortingLineChuteCodeQueryCriteria criteria = new SortingLineChuteCodeQueryCriteria();
        criteria.setLineId(lineId);
        List<SortingLineChuteCode> all = sortingLineChuteCodeRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder));
        return all;
    }

    @Cacheable(key = "'userCode1:' + #p0 + #p1")
    @Override
    public SortingLineChuteCode queryByUserIdAndRuleCode1(String userId, String ruleCode1) {
        return sortingLineChuteCodeRepository.findByUserIdAndRuleCode1(userId, ruleCode1);
    }

    @Cacheable(key = "'userCode2:' + #p0 + #p1")
    @Override
    public SortingLineChuteCode queryByUserIdAndRuleCode2(String userId, String ruleCode2) {
        return sortingLineChuteCodeRepository.findByUserIdAndRuleCode2(userId, ruleCode2);
    }

    @Cacheable(key = "'userCode3:' + #p0 + #p1")
    @Override
    public SortingLineChuteCode queryByUserIdAndRuleCode(String userId, String ruleCode) {
        return sortingLineChuteCodeRepository.findByUserIdAndRuleCode(userId, ruleCode);
    }

    @Cacheable(key = "'userCode4:' + #p0 + #p1")
    @Override
    public SortingLineChuteCode queryByUserIdAndChuteCode(String userId, String chuteCode) {
        return sortingLineChuteCodeRepository.findByUserIdAndChuteCode(userId, chuteCode);
    }
}