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

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.PddCloudPrintLog;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PddCloudPrintLogRepository;
import me.zhengjie.service.PddCloudPrintLogService;
import me.zhengjie.service.dto.PddCloudPrintLogDto;
import me.zhengjie.service.dto.PddCloudPrintLogQueryCriteria;
import me.zhengjie.service.mapstruct.PddCloudPrintLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
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
* @author wangmiao
* @date 2022-08-08
**/
@Service
@RequiredArgsConstructor
@Slf4j
public class PddCloudPrintLogServiceImpl implements PddCloudPrintLogService {

    private final PddCloudPrintLogRepository pddCloudPrintLogRepository;
    private final PddCloudPrintLogMapper pddCloudPrintLogMapper;

    @Override
    public Map<String,Object> queryAll(PddCloudPrintLogQueryCriteria criteria, Pageable pageable){
        Page<PddCloudPrintLog> page = pddCloudPrintLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(pddCloudPrintLogMapper::toDto));
    }

    @Override
    public List<PddCloudPrintLogDto> queryAll(PddCloudPrintLogQueryCriteria criteria){
        return pddCloudPrintLogMapper.toDto(pddCloudPrintLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PddCloudPrintLogDto findById(Long id) {
        PddCloudPrintLog pddCloudPrintLog = pddCloudPrintLogRepository.findById(id).orElseGet(PddCloudPrintLog::new);
        ValidationUtil.isNull(pddCloudPrintLog.getId(),"PddCloudPrintLog","id",id);
        return pddCloudPrintLogMapper.toDto(pddCloudPrintLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PddCloudPrintLogDto create(PddCloudPrintLog resources) {
        return pddCloudPrintLogMapper.toDto(pddCloudPrintLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PddCloudPrintLog resources) {
        PddCloudPrintLog pddCloudPrintLog = pddCloudPrintLogRepository.findById(resources.getId()).orElseGet(PddCloudPrintLog::new);
        ValidationUtil.isNull( pddCloudPrintLog.getId(),"PddCloudPrintLog","id",resources.getId());
        pddCloudPrintLog.copy(resources);
        pddCloudPrintLogRepository.save(pddCloudPrintLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            pddCloudPrintLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PddCloudPrintLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PddCloudPrintLogDto pddCloudPrintLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("运单号", pddCloudPrintLog.getMailNo());
            map.put("打印人", pddCloudPrintLog.getPrintOperator());
            map.put("打印时间", pddCloudPrintLog.getPrintTime());
            map.put("当时打印次数", pddCloudPrintLog.getPrintCount());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void saveLogByMq(String body) {
        try {
            PddCloudPrintLog printLog= JSONObject.parseObject(body,PddCloudPrintLog.class);
            List<PddCloudPrintLog>logList=pddCloudPrintLogRepository.findAllByMailNo(printLog.getMailNo());
            printLog.setPrintCount((CollectionUtils.isEmpty(logList)?0:logList.size())+1);
            create(printLog);
        }catch (Exception e){
            log.error("保存拼多多云打印日志失败！打印日志"+body,e);
        }
    }
}