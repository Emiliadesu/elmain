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

import me.zhengjie.domain.AsnHeader;
import me.zhengjie.domain.LoadDetail;
import me.zhengjie.domain.LoadHeader;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LoadDetailRepository;
import me.zhengjie.service.LoadDetailService;
import me.zhengjie.service.dto.LoadDetailDto;
import me.zhengjie.service.dto.LoadDetailQueryCriteria;
import me.zhengjie.service.mapstruct.LoadDetailMapper;
import org.springframework.data.domain.Example;
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
* @author wangm
* @date 2021-04-01
**/
@Service
@RequiredArgsConstructor
public class LoadDetailServiceImpl implements LoadDetailService {

    private final LoadDetailRepository loadDetailRepository;
    private final LoadDetailMapper loadDetailMapper;

    @Override
    public Map<String,Object> queryAll(LoadDetailQueryCriteria criteria, Pageable pageable){
        Page<LoadDetail> page = loadDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(loadDetailMapper::toDto));
    }

    @Override
    public List<LoadDetailDto> queryAll(LoadDetailQueryCriteria criteria){
        return loadDetailMapper.toDto(loadDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LoadDetailDto findById(Long id) {
        LoadDetail loadDetail = loadDetailRepository.findById(id).orElseGet(LoadDetail::new);
        ValidationUtil.isNull(loadDetail.getId(),"LoadDetail","id",id);
        return loadDetailMapper.toDto(loadDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoadDetailDto create(LoadDetail resources) {
        return loadDetailMapper.toDto(loadDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LoadDetail resources) {
        LoadDetail loadDetail = loadDetailRepository.findById(resources.getId()).orElseGet(LoadDetail::new);
        ValidationUtil.isNull( loadDetail.getId(),"LoadDetail","id",resources.getId());
        loadDetail.copy(resources);
        loadDetailRepository.save(loadDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            loadDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<LoadDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LoadDetailDto loadDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("预装载单号", loadDetail.getLoadHeader().getLoadNo());
            map.put("do编号", loadDetail.getDoNo());
            map.put("托盘号", loadDetail.getLpn());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void uploadLpn(List<Map<String, Object>> maps, Long loadHeadId, String doNo) {
        if (CollectionUtils.isEmpty(maps))
            throw new BadRequestException("数据为空");
        int i=0;
        LoadHeader loadHeader=new LoadHeader();
        loadHeader.setId(loadHeadId);
        for (Map<String, Object> map : maps) {
            i++;
            try {
                LoadDetail detail = checkData(map,loadHeader,doNo);
                LoadDetail exist=loadDetailRepository.findOne(Example.of(detail)).orElse(null);
                if (exist == null) {
                    create(detail);
                }
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException("第"+i+"行："+e.getMessage());
            }
        }
    }

    private LoadDetail checkData(Map<String, Object> map, LoadHeader loadHeader, String doNo) {
        Object lpn=map.get("托盘号");
        if (lpn==null|| StringUtil.isBlank(lpn.toString()))
            throw new BadRequestException("托盘号为空");
        LoadDetail loadDetail=new LoadDetail();
        loadDetail.setDoNo(doNo);
        loadDetail.setLoadHeader(loadHeader);
        loadDetail.setLpn(lpn.toString());
        return loadDetail;
    }
}
