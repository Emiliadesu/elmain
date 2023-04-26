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

import me.zhengjie.annotation.type.ReqLogType;
import me.zhengjie.domain.RequestLog;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.RequestLogRepository;
import me.zhengjie.service.RequestLogService;
import me.zhengjie.service.dto.RequestLogDto;
import me.zhengjie.service.dto.RequestLogQueryCriteria;
import me.zhengjie.service.mapstruct.RequestLogMapper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-04-28
**/
@Service
@RequiredArgsConstructor
public class RequestLogServiceImpl implements RequestLogService {

    private final RequestLogRepository requestLogRepository;
    private final RequestLogMapper requestLogMapper;

    @Override
    public Map<String,Object> queryAll(RequestLogQueryCriteria criteria, Pageable pageable){
        Page<RequestLog> page = requestLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(requestLogMapper::toDto));
    }

    @Override
    public List<RequestLogDto> queryAll(RequestLogQueryCriteria criteria){
        return requestLogMapper.toDto(requestLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public RequestLogDto findById(Long id) {
        RequestLog requestLog = requestLogRepository.findById(id).orElseGet(RequestLog::new);
        ValidationUtil.isNull(requestLog.getId(),"RequestLog","id",id);
        return requestLogMapper.toDto(requestLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RequestLogDto create(RequestLog resources) {
        return requestLogMapper.toDto(requestLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(RequestLog resources) {
        RequestLog requestLog = requestLogRepository.findById(resources.getId()).orElseGet(RequestLog::new);
        ValidationUtil.isNull( requestLog.getId(),"RequestLog","id",resources.getId());
        requestLog.copy(resources);
        requestLogRepository.save(requestLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            requestLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<RequestLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (RequestLogDto requestLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("业务类型", requestLog.getBusType());
            map.put("业务类型描述", requestLog.getBusTypeName());
            map.put("业务单号", requestLog.getBusNo());
            map.put("请求地址", requestLog.getReqUrl());
            map.put("请求报文", requestLog.getReqMsg());
            map.put("返回报文", requestLog.getResMsg());
            map.put("执行机器", requestLog.getHost());
            map.put("请求耗时", requestLog.getCostTime());
            map.put("创建人", requestLog.getCreateBy());
            map.put("创建时间", requestLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void save(ProceedingJoinPoint joinPoint, RequestLog log) throws Throwable {
        //参数值
        List<Object> argValues = new ArrayList<>(Arrays.asList(joinPoint.getArgs()));
        // 所有使用此切面的方法必须要按照第一个参数url,第二个参数实际请求报文定义
        ReqLogType reqLogType = (ReqLogType) argValues.get(0);
        String url = String.valueOf(argValues.get(1));
        String params = String.valueOf(argValues.get(2));
        String proceed = String.valueOf(joinPoint.proceed());// 返回值

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        log.setBusType(reqLogType.getCode());
        log.setBusTypeName(reqLogType.getDescription());
        log.setReqUrl(url);
        log.setReqMsg(params);
        log.setResMsg(proceed);
        log.setCreateBy("SYSTEM");
        log.setCreateTime(new Timestamp(System.currentTimeMillis()));
        log.setHost(StringUtils.getLocalIp());
        requestLogRepository.save(log);
    }
}