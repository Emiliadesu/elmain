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

import me.zhengjie.domain.TransLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.TransLogRepository;
import me.zhengjie.service.TransLogService;
import me.zhengjie.service.dto.TransLogDto;
import me.zhengjie.service.dto.TransLogQueryCriteria;
import me.zhengjie.service.mapstruct.TransLogMapper;
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
* @date 2021-09-04
**/
@Service
@RequiredArgsConstructor
public class TransLogServiceImpl implements TransLogService {

    private final TransLogRepository transLogRepository;
    private final TransLogMapper transLogMapper;

    @Override
    public Map<String,Object> queryAll(TransLogQueryCriteria criteria, Pageable pageable){
        Page<TransLog> page = transLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(transLogMapper::toDto));
    }

    @Override
    public List<TransLogDto> queryAll(TransLogQueryCriteria criteria){
        return transLogMapper.toDto(transLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public TransLogDto findById(Long id) {
        TransLog transLog = transLogRepository.findById(id).orElseGet(TransLog::new);
        ValidationUtil.isNull(transLog.getId(),"TransLog","id",id);
        return transLogMapper.toDto(transLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransLogDto create(TransLog resources) {
        return transLogMapper.toDto(transLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TransLog resources) {
        TransLog transLog = transLogRepository.findById(resources.getId()).orElseGet(TransLog::new);
        ValidationUtil.isNull( transLog.getId(),"TransLog","id",resources.getId());
        transLog.copy(resources);
        transLogRepository.save(transLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            transLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TransLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TransLogDto transLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", transLog.getOrderId());
            map.put("订单号", transLog.getOrderNo());
            map.put("状态", transLog.getStatus());
            map.put("操作节点", transLog.getOptNode());
            map.put("请求报文", transLog.getReqMsg());
            map.put("返回报文", transLog.getResMsg());
            map.put("关键字", transLog.getKeyWord());
            map.put("执行机器", transLog.getHost());
            map.put("是否成功", transLog.getSuccess());
            map.put("描述", transLog.getMsg());
            map.put("创建人", transLog.getCreateBy());
            map.put("创建时间", transLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}