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

import me.zhengjie.domain.WmsStockLog;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WmsStockLogRepository;
import me.zhengjie.service.WmsStockLogService;
import me.zhengjie.service.dto.WmsStockLogDto;
import me.zhengjie.service.dto.WmsStockLogQueryCriteria;
import me.zhengjie.service.mapstruct.WmsStockLogMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QueryHelp;

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
* @author 王淼
* @date 2020-12-21
**/
@Service
@RequiredArgsConstructor
public class WmsStockLogServiceImpl implements WmsStockLogService {

    private final WmsStockLogRepository wmsStockLogRepository;
    private final WmsStockLogMapper wmsStockLogMapper;

    @Override
    public Map<String,Object> queryAll(WmsStockLogQueryCriteria criteria, Pageable pageable){
        Page<WmsStockLog> page = wmsStockLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wmsStockLogMapper::toDto));
    }

    @Override
    public List<WmsStockLogDto> queryAll(WmsStockLogQueryCriteria criteria){
        return wmsStockLogMapper.toDto(wmsStockLogRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WmsStockLogDto findById(Long id) {
        WmsStockLog wmsStockLog = wmsStockLogRepository.findById(id).orElseGet(WmsStockLog::new);
        ValidationUtil.isNull(wmsStockLog.getId(),"WmsStockLog","id",id);
        return wmsStockLogMapper.toDto(wmsStockLog);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsStockLogDto create(WmsStockLog resources) {
        return wmsStockLogMapper.toDto(wmsStockLogRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WmsStockLog resources) {
        WmsStockLog wmsStockLog = wmsStockLogRepository.findById(resources.getId()).orElseGet(WmsStockLog::new);
        ValidationUtil.isNull( wmsStockLog.getId(),"WmsStockLog","id",resources.getId());
        wmsStockLog.copy(resources);
        wmsStockLogRepository.save(wmsStockLog);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            wmsStockLogRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WmsStockLogDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WmsStockLogDto wmsStockLog : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("出入库单号", wmsStockLog.getOrderSn());
            map.put("库存操作，0入库，1出库", wmsStockLog.getType());
            map.put("请求报文", wmsStockLog.getRequest());
            map.put("响应报文", wmsStockLog.getResponse());
            map.put("备注", wmsStockLog.getRemark());
            map.put("操作状态", wmsStockLog.getStatusText());
            map.put("操作时间", wmsStockLog.getOperationTime());
            map.put("操作人", wmsStockLog.getOperationUser());
            map.put("操作状态码", wmsStockLog.getStatus());
            map.put("是否成功", wmsStockLog.getIsSuccess());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createFail(String orderSn, String inOrOut, String status, String msg,String optUser) {
        WmsStockLog wmsStockLog=new WmsStockLog();
        wmsStockLog.setStatus(status);
        wmsStockLog.setOrderSn(orderSn);
        wmsStockLog.setRemark(msg);
        wmsStockLog.setIsSuccess("0");
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType(inOrOut);
        wmsStockLog.setOperationUser(optUser);
        create(wmsStockLog);
    }

    @Override
    public void createSucc(String orderSn, String inOrOut, String status, String optUser) {
        WmsStockLog wmsStockLog=new WmsStockLog();
        wmsStockLog.setStatus(status);
        wmsStockLog.setOrderSn(orderSn);
        wmsStockLog.setIsSuccess("1");
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType(inOrOut);
        wmsStockLog.setOperationUser(optUser);
        create(wmsStockLog);
    }
}
