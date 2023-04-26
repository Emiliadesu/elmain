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

import me.zhengjie.domain.CompanyInfo;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CompanyInfoRepository;
import me.zhengjie.service.CompanyInfoService;
import me.zhengjie.service.dto.CompanyInfoDto;
import me.zhengjie.service.dto.CompanyInfoQueryCriteria;
import me.zhengjie.service.mapstruct.CompanyInfoMapper;
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
* @date 2020-10-09
**/
@Service
@RequiredArgsConstructor
public class CompanyInfoServiceImpl implements CompanyInfoService {

    private final CompanyInfoRepository CompanyInfoRepository;
    private final CompanyInfoMapper CompanyInfoMapper;

    @Override
    public Map<String,Object> queryAll(CompanyInfoQueryCriteria criteria, Pageable pageable){
        Page<CompanyInfo> page = CompanyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(CompanyInfoMapper::toDto));
    }

    @Override
    public List<CompanyInfoDto> queryAll(CompanyInfoQueryCriteria criteria){
        return CompanyInfoMapper.toDto(CompanyInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CompanyInfoDto findById(Long companyId) {
        CompanyInfo CompanyInfo = CompanyInfoRepository.findById(companyId).orElseGet(CompanyInfo::new);
        ValidationUtil.isNull(CompanyInfo.getCompanyId(),"CompanyInfo","companyId",companyId);
        return CompanyInfoMapper.toDto(CompanyInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CompanyInfoDto create(CompanyInfo resources) {
        return CompanyInfoMapper.toDto(CompanyInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CompanyInfo resources) {
        CompanyInfo CompanyInfo = CompanyInfoRepository.findById(resources.getCompanyId()).orElseGet(CompanyInfo::new);
        ValidationUtil.isNull( CompanyInfo.getCompanyId(),"CompanyInfo","id",resources.getCompanyId());
        CompanyInfo.copy(resources);
        CompanyInfoRepository.save(CompanyInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long companyId : ids) {
            CompanyInfoRepository.deleteById(companyId);
        }
    }

    @Override
    public void download(List<CompanyInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CompanyInfoDto CompanyInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("商家名称", CompanyInfo.getCompanyName());
            map.put("业务类别", CompanyInfo.getBusType());
            map.put("进仓库区", CompanyInfo.getInArea());
            map.put("更新者", CompanyInfo.getUpdateBy());
            map.put("更新时间", CompanyInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
