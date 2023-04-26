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

import me.zhengjie.domain.DomesticOrderLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DomesticOrderLogRepository;
import me.zhengjie.service.DomesticOrderLogService;
import me.zhengjie.service.dto.DomesticOrderLogDto;
import me.zhengjie.service.dto.DomesticOrderLogQueryCriteria;
import me.zhengjie.service.mapstruct.DomesticOrderLogMapper;
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
* @date 2022-04-11
**/
@Service
@RequiredArgsConstructor
public class DomesticOrderLogServiceImpl implements DomesticOrderLogService {

    private final DomesticOrderLogRepository DomesticOrderLogRepository;
    private final DomesticOrderLogMapper DomesticOrderLogMapper;

    @Override
    public Map<String,Object> queryAll(DomesticOrderLogQueryCriteria criteria, Pageable pageable){
        Page<DomesticOrderLog> page = DomesticOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(DomesticOrderLogMapper::toDto));
    }

    @Override
    public List<DomesticOrderLogDto> queryAll(DomesticOrderLogQueryCriteria criteria){
        return DomesticOrderLogMapper.toDto(DomesticOrderLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DomesticOrderLogDto findById(Long id) {
        DomesticOrderLog DomesticOrderLog = DomesticOrderLogRepository.findById(id).orElseGet(DomesticOrderLog::new);
        ValidationUtil.isNull(DomesticOrderLog.getId(),"DomesticOrderLog","id",id);
        return DomesticOrderLogMapper.toDto(DomesticOrderLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DomesticOrderLogDto create(DomesticOrderLog resources) {
        return DomesticOrderLogMapper.toDto(DomesticOrderLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DomesticOrderLog resources) {
        DomesticOrderLog DomesticOrderLog = DomesticOrderLogRepository.findById(resources.getId()).orElseGet(DomesticOrderLog::new);
        ValidationUtil.isNull( DomesticOrderLog.getId(),"DomesticOrderLog","id",resources.getId());
        DomesticOrderLog.copy(resources);
        DomesticOrderLogRepository.save(DomesticOrderLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            DomesticOrderLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DomesticOrderLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DomesticOrderLogDto DomesticOrderLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", DomesticOrderLog.getOrderId());
            map.put("订单号", DomesticOrderLog.getOrderNo());
            map.put("操作节点", DomesticOrderLog.getOptNode());
            map.put("请求报文", DomesticOrderLog.getReqMsg());
            map.put("返回报文", DomesticOrderLog.getResMsg());
            map.put("关键字", DomesticOrderLog.getKeyWord());
            map.put("执行机器", DomesticOrderLog.getHost());
            map.put("是否成功", DomesticOrderLog.getSuccess());
            map.put("描述", DomesticOrderLog.getMsg());
            map.put("花费时间", DomesticOrderLog.getCostTime());
            map.put("创建人", DomesticOrderLog.getCreateBy());
            map.put("创建时间", DomesticOrderLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}