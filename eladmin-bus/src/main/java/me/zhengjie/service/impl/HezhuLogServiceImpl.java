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

import me.zhengjie.domain.HezhuLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.HezhuLogRepository;
import me.zhengjie.service.HezhuLogService;
import me.zhengjie.service.dto.HezhuLogDto;
import me.zhengjie.service.dto.HezhuLogQueryCriteria;
import me.zhengjie.service.mapstruct.HezhuLogMapper;
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
* @date 2021-08-26
**/
@Service
@RequiredArgsConstructor
public class HezhuLogServiceImpl implements HezhuLogService {

    private final HezhuLogRepository hezhuLogRepository;
    private final HezhuLogMapper hezhuLogMapper;

    @Override
    public Map<String,Object> queryAll(HezhuLogQueryCriteria criteria, Pageable pageable){
        Page<HezhuLog> page = hezhuLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(hezhuLogMapper::toDto));
    }

    @Override
    public List<HezhuLogDto> queryAll(HezhuLogQueryCriteria criteria){
        return hezhuLogMapper.toDto(hezhuLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public HezhuLogDto findById(Long id) {
        HezhuLog hezhuLog = hezhuLogRepository.findById(id).orElseGet(HezhuLog::new);
        ValidationUtil.isNull(hezhuLog.getId(),"HezhuLog","id",id);
        return hezhuLogMapper.toDto(hezhuLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HezhuLogDto create(HezhuLog resources) {
        return hezhuLogMapper.toDto(hezhuLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(HezhuLog resources) {
        HezhuLog hezhuLog = hezhuLogRepository.findById(resources.getId()).orElseGet(HezhuLog::new);
        ValidationUtil.isNull( hezhuLog.getId(),"HezhuLog","id",resources.getId());
        hezhuLog.copy(resources);
        hezhuLogRepository.save(hezhuLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            hezhuLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<HezhuLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (HezhuLogDto hezhuLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("订单ID", hezhuLog.getOrderId());
            map.put("订单号", hezhuLog.getOrderNo());
            map.put("状态", hezhuLog.getStatus());
            map.put("操作节点", hezhuLog.getOptNode());
            map.put("请求报文", hezhuLog.getReqMsg());
            map.put("返回报文", hezhuLog.getResMsg());
            map.put("关键字", hezhuLog.getKeyWord());
            map.put("执行机器", hezhuLog.getHost());
            map.put("是否成功", hezhuLog.getSuccess());
            map.put("描述", hezhuLog.getMsg());
            map.put("创建人", hezhuLog.getCreateBy());
            map.put("创建时间", hezhuLog.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}