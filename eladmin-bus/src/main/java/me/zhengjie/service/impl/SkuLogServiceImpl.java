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

import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.SkuLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.SkuLogRepository;
import me.zhengjie.service.SkuLogService;
import me.zhengjie.service.dto.SkuLogDto;
import me.zhengjie.service.dto.SkuLogQueryCriteria;
import me.zhengjie.service.mapstruct.SkuLogMapper;
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
* @date 2021-04-21
**/
@Service
@RequiredArgsConstructor
public class SkuLogServiceImpl implements SkuLogService {

    private final SkuLogRepository skuLogRepository;
    private final SkuLogMapper skuLogMapper;

    @Override
    public Map<String,Object> queryAll(SkuLogQueryCriteria criteria, Pageable pageable){
        Page<SkuLog> page = skuLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(skuLogMapper::toDto));
    }

    @Override
    public List<SkuLogDto> queryAll(SkuLogQueryCriteria criteria){
        return skuLogMapper.toDto(skuLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public SkuLogDto findById(Long id) {
        SkuLog skuLog = skuLogRepository.findById(id).orElseGet(SkuLog::new);
        ValidationUtil.isNull(skuLog.getId(),"SkuLog","id",id);
        return skuLogMapper.toDto(skuLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SkuLogDto create(SkuLog resources) {
        return skuLogMapper.toDto(skuLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SkuLog resources) {
        SkuLog skuLog = skuLogRepository.findById(resources.getId()).orElseGet(SkuLog::new);
        ValidationUtil.isNull( skuLog.getId(),"SkuLog","id",resources.getId());
        skuLog.copy(resources);
        skuLogRepository.save(skuLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            skuLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<SkuLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (SkuLogDto skuLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", skuLog.getSkuId());
            map.put("操作节点", skuLog.getOptNode());
            map.put("请求报文", skuLog.getReqMsg());
            map.put("返回报文", skuLog.getResMsg());
            map.put("执行机器", skuLog.getHost());
            map.put("是否成功", skuLog.getSuccess());
            map.put("描述", skuLog.getMsg());
            map.put("创建人", skuLog.getCreateBy());
            map.put("创建时间", skuLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saveAll(List<SkuLog> logs) {
        skuLogRepository.saveAll(logs);
    }
}