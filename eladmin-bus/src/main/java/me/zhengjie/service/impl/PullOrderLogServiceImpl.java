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

import me.zhengjie.domain.PullOrderLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PullOrderLogRepository;
import me.zhengjie.service.PullOrderLogService;
import me.zhengjie.service.dto.PullOrderLogDto;
import me.zhengjie.service.dto.PullOrderLogQueryCriteria;
import me.zhengjie.service.mapstruct.PullOrderLogMapper;
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
* @date 2021-04-01
**/
@Service
@RequiredArgsConstructor
public class PullOrderLogServiceImpl implements PullOrderLogService {

    private final PullOrderLogRepository pullOrderLogRepository;
    private final PullOrderLogMapper pullOrderLogMapper;

    @Override
    public Map<String,Object> queryAll(PullOrderLogQueryCriteria criteria, Pageable pageable){
        Page<PullOrderLog> page = pullOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(pullOrderLogMapper::toDto));
    }

    @Override
    public List<PullOrderLogDto> queryAll(PullOrderLogQueryCriteria criteria){
        return pullOrderLogMapper.toDto(pullOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PullOrderLogDto findById(Long id) {
        PullOrderLog pullOrderLog = pullOrderLogRepository.findById(id).orElseGet(PullOrderLog::new);
        ValidationUtil.isNull(pullOrderLog.getId(),"PullOrderLog","id",id);
        return pullOrderLogMapper.toDto(pullOrderLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PullOrderLogDto create(PullOrderLog resources) {
        return pullOrderLogMapper.toDto(pullOrderLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PullOrderLog resources) {
        PullOrderLog pullOrderLog = pullOrderLogRepository.findById(resources.getId()).orElseGet(PullOrderLog::new);
        ValidationUtil.isNull( pullOrderLog.getId(),"PullOrderLog","id",resources.getId());
        pullOrderLog.copy(resources);
        pullOrderLogRepository.save(pullOrderLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            pullOrderLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PullOrderLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PullOrderLogDto pullOrderLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("店铺ID", pullOrderLog.getShopId());
            map.put("开始时间", pullOrderLog.getStartTime());
            map.put("结束时间", pullOrderLog.getEndTime());
            map.put("订单号", pullOrderLog.getPageNo());
            map.put("操作节点", pullOrderLog.getPageSize());
            map.put("返回报文", pullOrderLog.getResMsg());
            map.put("执行机器", pullOrderLog.getHost());
            map.put("创建时间", pullOrderLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public PullOrderLog getPullTime(Long shopId) {
        return pullOrderLogRepository.getPullTime(shopId);
    }
}
