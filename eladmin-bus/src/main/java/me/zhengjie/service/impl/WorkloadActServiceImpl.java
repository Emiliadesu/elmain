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

import me.zhengjie.domain.WorkloadAct;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WorkloadActRepository;
import me.zhengjie.service.WorkloadActService;
import me.zhengjie.service.dto.WorkloadActDto;
import me.zhengjie.service.dto.WorkloadActQueryCriteria;
import me.zhengjie.service.mapstruct.WorkloadActMapper;
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
* @author KC
* @date 2021-01-13
**/
@Service
@RequiredArgsConstructor
public class WorkloadActServiceImpl implements WorkloadActService {

    private final WorkloadActRepository workloadActRepository;
    private final WorkloadActMapper workloadActMapper;

    @Override
    public Map<String,Object> queryAll(WorkloadActQueryCriteria criteria, Pageable pageable){
        Page<WorkloadAct> page = workloadActRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(workloadActMapper::toDto));
    }

    @Override
    public List<WorkloadActDto> queryAll(WorkloadActQueryCriteria criteria){
        return workloadActMapper.toDto(workloadActRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WorkloadActDto findById(Long id) {
        WorkloadAct workloadAct = workloadActRepository.findById(id).orElseGet(WorkloadAct::new);
        ValidationUtil.isNull(workloadAct.getId(),"WorkloadAct","id",id);
        return workloadActMapper.toDto(workloadAct);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WorkloadActDto create(WorkloadAct resources) {
        return workloadActMapper.toDto(workloadActRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WorkloadAct resources) {
        WorkloadAct workloadAct = workloadActRepository.findById(resources.getId()).orElseGet(WorkloadAct::new);
        ValidationUtil.isNull( workloadAct.getId(),"WorkloadAct","id",resources.getId());
        workloadAct.copy(resources);
        workloadActRepository.save(workloadAct);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            workloadActRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WorkloadActDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WorkloadActDto workloadAct : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("单号", workloadAct.getOrderSn());
            map.put("工作类型", workloadAct.getWorkType());
            map.put("员工账号", workloadAct.getUserId());
            map.put("新增时间", workloadAct.getAddTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}