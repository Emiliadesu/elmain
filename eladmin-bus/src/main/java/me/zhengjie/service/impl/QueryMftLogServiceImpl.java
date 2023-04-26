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

import me.zhengjie.domain.QueryMftLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.QueryMftLogRepository;
import me.zhengjie.service.QueryMftLogService;
import me.zhengjie.service.dto.QueryMftLogDto;
import me.zhengjie.service.dto.QueryMftLogQueryCriteria;
import me.zhengjie.service.mapstruct.QueryMftLogMapper;
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
* @date 2021-04-03
**/
@Service
@RequiredArgsConstructor
public class QueryMftLogServiceImpl implements QueryMftLogService {

    private final QueryMftLogRepository QueryMftLogRepository;
    private final QueryMftLogMapper QueryMftLogMapper;

    @Override
    public Map<String,Object> queryAll(QueryMftLogQueryCriteria criteria, Pageable pageable){
        Page<QueryMftLog> page = QueryMftLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(QueryMftLogMapper::toDto));
    }

    @Override
    public List<QueryMftLogDto> queryAll(QueryMftLogQueryCriteria criteria){
        return QueryMftLogMapper.toDto(QueryMftLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public QueryMftLogDto findById(Long id) {
        QueryMftLog QueryMftLog = QueryMftLogRepository.findById(id).orElseGet(QueryMftLog::new);
        ValidationUtil.isNull(QueryMftLog.getId(),"QueryMftLog","id",id);
        return QueryMftLogMapper.toDto(QueryMftLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public QueryMftLogDto create(QueryMftLog resources) {
        return QueryMftLogMapper.toDto(QueryMftLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(QueryMftLog resources) {
        QueryMftLog QueryMftLog = QueryMftLogRepository.findById(resources.getId()).orElseGet(QueryMftLog::new);
        ValidationUtil.isNull( QueryMftLog.getId(),"QueryMftLog","id",resources.getId());
        QueryMftLog.copy(resources);
        QueryMftLogRepository.save(QueryMftLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            QueryMftLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<QueryMftLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QueryMftLogDto QueryMftLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("开始时间", QueryMftLog.getStartTime());
            map.put("结束时间", QueryMftLog.getEndTime());
            map.put("页面", QueryMftLog.getPageNo());
            map.put("页大小", QueryMftLog.getPageSize());
            map.put("是否有下一页", QueryMftLog.getNextPage());
            map.put("结果", QueryMftLog.getResult());
            map.put("返回报文", QueryMftLog.getResMsg());
            map.put("执行机器", QueryMftLog.getHost());
            map.put("创建时间", QueryMftLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public QueryMftLog getPullTime() {
        return QueryMftLogRepository.getPullTime();
    }
}