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

import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.domain.CompanyInfo;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.ClearCompanyInfoRepository;
import me.zhengjie.service.ClearCompanyInfoService;
import me.zhengjie.service.dto.ClearCompanyInfoDto;
import me.zhengjie.service.dto.ClearCompanyInfoQueryCriteria;
import me.zhengjie.service.mapstruct.ClearCompanyInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2020-10-09
**/
@CacheConfig(cacheNames = "clearCompanyInfo")
@Service
@RequiredArgsConstructor
public class ClearCompanyInfoServiceImpl implements ClearCompanyInfoService {

    private final ClearCompanyInfoRepository ClearCompanyInfoRepository;
    private final ClearCompanyInfoMapper ClearCompanyInfoMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Override
    public Map<String,Object> queryAll(ClearCompanyInfoQueryCriteria criteria, Pageable pageable){
        Page<ClearCompanyInfo> page = ClearCompanyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(ClearCompanyInfoMapper::toDto));
    }

    @Override
    public List<ClearCompanyInfoDto> queryAll(ClearCompanyInfoQueryCriteria criteria){
        return ClearCompanyInfoMapper.toDto(ClearCompanyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public ClearCompanyInfoDto findByIdDto(Long clearCompanyId) {
        ClearCompanyInfo ClearCompanyInfo = ClearCompanyInfoRepository.findById(clearCompanyId).orElseGet(ClearCompanyInfo::new);
        ValidationUtil.isNull(ClearCompanyInfo.getClearCompanyId(),"ClearCompanyInfo","clearCompanyId",clearCompanyId);
        return ClearCompanyInfoMapper.toDto(ClearCompanyInfo);
    }

    @Cacheable(key = "'id:' + #p0")
    @Override
    @Transactional
    public ClearCompanyInfo findById(Long clearCompanyId) {
        ClearCompanyInfo ClearCompanyInfo = ClearCompanyInfoRepository.findById(clearCompanyId).orElseGet(ClearCompanyInfo::new);
        ValidationUtil.isNull(ClearCompanyInfo.getClearCompanyId(),"ClearCompanyInfo","clearCompanyId",clearCompanyId);
        return ClearCompanyInfo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ClearCompanyInfoDto create(ClearCompanyInfo resources) {
        return ClearCompanyInfoMapper.toDto(ClearCompanyInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(ClearCompanyInfo resources) {
        ClearCompanyInfo ClearCompanyInfo = ClearCompanyInfoRepository.findById(resources.getClearCompanyId()).orElseGet(ClearCompanyInfo::new);
        ValidationUtil.isNull( ClearCompanyInfo.getClearCompanyId(),"ClearCompanyInfo","id",resources.getClearCompanyId());
        ClearCompanyInfo.copy(resources);
        ClearCompanyInfoRepository.save(ClearCompanyInfo);
        redisUtils.del("clearCompanyInfo::id:" + ClearCompanyInfo.getClearCompanyId());
        redisUtils.del("clearCompanyInfo::code:" + ClearCompanyInfo.getCustomsCode());
    }

    @Override
    @Cacheable(key = "'code:' + #p0")
    public ClearCompanyInfo queryByCustomsCode(String customsCode) {
        return ClearCompanyInfoRepository.findByCustomsCode(customsCode);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long clearCompanyId : ids) {
            ClearCompanyInfoRepository.deleteById(clearCompanyId);
        }
    }

    @Override
    public void download(List<ClearCompanyInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (ClearCompanyInfoDto ClearCompanyInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商家名称", ClearCompanyInfo.getClearCompanyName());
            map.put("业务类别", ClearCompanyInfo.getKjgUser());
            map.put("进仓库区", ClearCompanyInfo.getKjgKey());
            map.put("更新者", ClearCompanyInfo.getUpdateBy());
            map.put("更新时间", ClearCompanyInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<ClearCompanyInfoDto> queryInClearCompIds(List<String> clearCompIds) {
        ClearCompanyInfo clComp;
        List<ClearCompanyInfoDto>reList=new ArrayList<>();
        for (String clearCompId : clearCompIds) {
            clComp=new ClearCompanyInfo();
            clComp.setClearCompanyId(Long.parseLong(clearCompId));
            Optional<ClearCompanyInfo> op=ClearCompanyInfoRepository.findOne(Example.of(clComp));
            op.ifPresent(clearCompanyInfo -> reList.add(ClearCompanyInfoMapper.toDto(clearCompanyInfo)));
        }
        return reList;
    }

    /*@Override
    public List<ClearCompanyInfoDto> queryByCompanyId(Long companyId) {
        ClearCompanyInfo clComp=new ClearCompanyInfo();
        CompanyInfo companyInfo=new CompanyInfo();
        companyInfo.setCompanyId(companyId);
        clComp.setCompany(companyInfo);
        return ClearCompanyInfoMapper.toDto(ClearCompanyInfoRepository.findAll(Example.of(clComp)));
    }*/

    @Override
    public List<ClearCompanyInfoDto> queryAllClearComp(ClearCompanyInfoQueryCriteria criteria) {
        List<ClearCompanyInfo>clearList=ClearCompanyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
        for (ClearCompanyInfo clearCompanyInfo : clearList) {
            clearCompanyInfo.setKjgKey(null);
            clearCompanyInfo.setKjgUser(null);
        }
        return ClearCompanyInfoMapper.toDto(clearList);
    }


}
