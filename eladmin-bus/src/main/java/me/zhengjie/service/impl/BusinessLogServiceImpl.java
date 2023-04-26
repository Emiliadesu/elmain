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

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.BusinessLog;
import me.zhengjie.mq.BusLogProducer;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.BusinessLogRepository;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.dto.BusinessLogDto;
import me.zhengjie.service.dto.BusinessLogQueryCriteria;
import me.zhengjie.service.mapstruct.BusinessLogMapper;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
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
* @date 2022-01-11
**/
@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessLogServiceImpl implements BusinessLogService {

    private final BusinessLogRepository businessLogRepository;
    private final BusinessLogMapper businessLogMapper;

    @Autowired
    private BusLogProducer busLogProducer;

    @Override
    public Map<String,Object> queryAll(BusinessLogQueryCriteria criteria, Pageable pageable){
        Page<BusinessLog> page = businessLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(businessLogMapper::toDto));
    }

    @Override
    public List<BusinessLogDto> queryAll(BusinessLogQueryCriteria criteria){
        return businessLogMapper.toDto(businessLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public BusinessLogDto findById(Long id) {
        BusinessLog businessLog = businessLogRepository.findById(id).orElseGet(BusinessLog::new);
        ValidationUtil.isNull(businessLog.getId(),"BusinessLog","id",id);
        return businessLogMapper.toDto(businessLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BusinessLogDto create(BusinessLog resources) {
        return businessLogMapper.toDto(businessLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(BusinessLog resources) {
        BusinessLog businessLog = businessLogRepository.findById(resources.getId()).orElseGet(BusinessLog::new);
        ValidationUtil.isNull( businessLog.getId(),"BusinessLog","id",resources.getId());
        businessLog.copy(resources);
        businessLogRepository.save(businessLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            businessLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<BusinessLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BusinessLogDto businessLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("类型", businessLog.getType());
            map.put("方向", businessLog.getDirection());
            map.put("描述", businessLog.getDescription());
            map.put("关键字", businessLog.getKeyWord());
            map.put("请求地址", businessLog.getReqUrl());
            map.put("请求IP", businessLog.getRequestIp());
            map.put("请求参数", businessLog.getReqParams());
            map.put("返回参数", businessLog.getResParams());
            map.put("处理机器", businessLog.getHost());
            map.put("花费时间", businessLog.getTime());
            map.put("处理时间", businessLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 从消息保存日志
     * @param body
     */
    @Override
    public void createLog(String body) {
        BusinessLog log = JSON.parseObject(body, BusinessLog.class);
        create(log);
    }

    @Override
    public void saveLog(BusTypeEnum busTypeEnum, String url, String keyWord, String reqParams, String resParams, long time) {
        try {
            BusinessLog log = new BusinessLog();
            log.setType(busTypeEnum.getType());
            log.setDirection(busTypeEnum.getDirection());
            log.setDescription(busTypeEnum.getDescription());
            log.setKeyWord(keyWord);
            log.setReqUrl(url);
            String ip = StringUtils.getLocalIp();
            log.setRequestIp(ip);
            log.setReqParams(reqParams);
            log.setResParams(resParams);
            log.setHost(ip);
            log.setTime(time);
            log.setCreateTime(new Timestamp(System.currentTimeMillis()));
            if (busLogProducer.getProducer() != null) {
                busLogProducer.send(
                        "BUS_LOG",
                        JSON.toJSONString(log),
                        keyWord
                );
            }else {
                create(log);
            }
        }catch (Exception e) {
            log.error("保存业务日志出错：{}", e.getMessage());
        }

    }

    @Override
    public void saveLog(BusTypeEnum busTypeEnum, String method, String url, String keyWord, String reqParams, String resParams, long time) {
        try {
            BusinessLog log = new BusinessLog();
            log.setType(busTypeEnum.getType());
            log.setDirection(busTypeEnum.getDirection());
            log.setDescription(method);
            log.setKeyWord(keyWord);
            log.setReqUrl(url);
            String ip = StringUtils.getLocalIp();
            log.setRequestIp(ip);
            log.setReqParams(reqParams);
            log.setResParams(resParams);
            log.setHost(ip);
            log.setTime(time);
            log.setCreateTime(new Timestamp(System.currentTimeMillis()));
            if (busLogProducer.getProducer() != null) {
                busLogProducer.send(
                        "BUS_LOG",
                        JSON.toJSONString(log),
                        keyWord
                );
            }else {
                create(log);
            }
        }catch (Exception e) {
            log.error("保存业务日志出错：{}", e.getMessage());
        }
    }
}