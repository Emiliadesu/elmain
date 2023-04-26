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

import me.zhengjie.domain.DepositLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DepositLogRepository;
import me.zhengjie.service.DepositLogService;
import me.zhengjie.service.dto.DepositLogDto;
import me.zhengjie.service.dto.DepositLogQueryCriteria;
import me.zhengjie.service.mapstruct.DepositLogMapper;
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
* @date 2021-11-13
**/
@Service
@RequiredArgsConstructor
public class DepositLogServiceImpl implements DepositLogService {

    private final DepositLogRepository depositLogRepository;
    private final DepositLogMapper depositLogMapper;

    @Override
    public Map<String,Object> queryAll(DepositLogQueryCriteria criteria, Pageable pageable){
        Page<DepositLog> page = depositLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(depositLogMapper::toDto));
    }

    @Override
    public List<DepositLogDto> queryAll(DepositLogQueryCriteria criteria){
        return depositLogMapper.toDto(depositLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DepositLogDto findById(Long id) {
        DepositLog depositLog = depositLogRepository.findById(id).orElseGet(DepositLog::new);
        ValidationUtil.isNull(depositLog.getId(),"DepositLog","id",id);
        return depositLogMapper.toDto(depositLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DepositLogDto create(DepositLog resources) {
        return depositLogMapper.toDto(depositLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DepositLog resources) {
        DepositLog depositLog = depositLogRepository.findById(resources.getId()).orElseGet(DepositLog::new);
        ValidationUtil.isNull( depositLog.getId(),"DepositLog","id",resources.getId());
        depositLog.copy(resources);
        depositLogRepository.save(depositLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            depositLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DepositLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DepositLogDto depositLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("保证金ID", depositLog.getDepositId());
            map.put("变动类型", depositLog.getType());
            map.put("金额", depositLog.getChangeAmount());
            map.put("当前金额", depositLog.getCurrentAmount());
            map.put("创建人", depositLog.getCreateBy());
            map.put("创建时间", depositLog.getCreateTime());
            map.put("关联订单", depositLog.getOrderNo());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}