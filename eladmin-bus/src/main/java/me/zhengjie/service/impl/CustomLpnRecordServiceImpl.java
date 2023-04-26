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

import me.zhengjie.domain.CustomLpnRecord;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.CustomLpnRecordRepository;
import me.zhengjie.service.CustomLpnRecordService;
import me.zhengjie.service.dto.CustomLpnRecordDto;
import me.zhengjie.service.dto.CustomLpnRecordQueryCriteria;
import me.zhengjie.service.mapstruct.CustomLpnRecordMapper;
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
* @date 2021-06-23
**/
@Service
@RequiredArgsConstructor
public class CustomLpnRecordServiceImpl implements CustomLpnRecordService {

    private final CustomLpnRecordRepository customLpnRecordRepository;
    private final CustomLpnRecordMapper customLpnRecordMapper;

    @Override
    public Map<String,Object> queryAll(CustomLpnRecordQueryCriteria criteria, Pageable pageable){
        Page<CustomLpnRecord> page = customLpnRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(customLpnRecordMapper::toDto));
    }

    @Override
    public List<CustomLpnRecordDto> queryAll(CustomLpnRecordQueryCriteria criteria){
        return customLpnRecordMapper.toDto(customLpnRecordRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public CustomLpnRecordDto findById(Long id) {
        CustomLpnRecord customLpnRecord = customLpnRecordRepository.findById(id).orElseGet(CustomLpnRecord::new);
        ValidationUtil.isNull(customLpnRecord.getId(),"CustomLpnRecord","id",id);
        return customLpnRecordMapper.toDto(customLpnRecord);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomLpnRecordDto create(CustomLpnRecord resources) {
        if(customLpnRecordRepository.findByLpnCode(resources.getLpnCode()) != null){
            throw new EntityExistException(CustomLpnRecord.class,"lpn_code",resources.getLpnCode());
        }
        return customLpnRecordMapper.toDto(customLpnRecordRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(CustomLpnRecord resources) {
        CustomLpnRecord customLpnRecord = customLpnRecordRepository.findById(resources.getId()).orElseGet(CustomLpnRecord::new);
        ValidationUtil.isNull( customLpnRecord.getId(),"CustomLpnRecord","id",resources.getId());
        CustomLpnRecord customLpnRecord1 = customLpnRecordRepository.findByLpnCode(resources.getLpnCode());
        if(customLpnRecord1 != null && !customLpnRecord1.getId().equals(customLpnRecord.getId())){
            throw new EntityExistException(CustomLpnRecord.class,"lpn_code",resources.getLpnCode());
        }
        customLpnRecord.copy(resources);
        customLpnRecordRepository.save(customLpnRecord);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            customLpnRecordRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<CustomLpnRecordDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CustomLpnRecordDto customLpnRecord : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库的po单号", customLpnRecord.getPoNo());
            map.put("收货托盘号", customLpnRecord.getLpnCode());
            map.put("录入人", customLpnRecord.getCreateUser());
            map.put("录入时间", customLpnRecord.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
