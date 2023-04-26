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

import me.zhengjie.domain.LogisticsInshops;
import me.zhengjie.utils.ValidationUtil;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LogisticsInshopsRepository;
import me.zhengjie.service.LogisticsInshopsService;
import me.zhengjie.service.dto.LogisticsInshopsDto;
import me.zhengjie.service.dto.LogisticsInshopsQueryCriteria;
import me.zhengjie.service.mapstruct.LogisticsInshopsMapper;
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
* @author leningzhou
* @date 2021-12-03
**/
@Service
@RequiredArgsConstructor
public class LogisticsInshopsServiceImpl implements LogisticsInshopsService {

    private final LogisticsInshopsRepository logisticsInshopsRepository;
    private final LogisticsInshopsMapper logisticsInshopsMapper;

    @Override
    public Map<String,Object> queryAll(LogisticsInshopsQueryCriteria criteria, Pageable pageable){
        Page<LogisticsInshops> page = logisticsInshopsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(logisticsInshopsMapper::toDto));
    }

    @Override
    public LogisticsInshops queryByCustId(Long custId) {
        return logisticsInshopsRepository.findByCustomersId(custId);
    }

    @Override
    public LogisticsInshops queryByShopId(Long shopId) {
        return logisticsInshopsRepository.findByShopId(shopId);
    }

    @Override
    public List<LogisticsInshopsDto> queryAll(LogisticsInshopsQueryCriteria criteria){
        return logisticsInshopsMapper.toDto(logisticsInshopsRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LogisticsInshopsDto findById(Long id) {
        LogisticsInshops logisticsInshops = logisticsInshopsRepository.findById(id).orElseGet(LogisticsInshops::new);
        ValidationUtil.isNull(logisticsInshops.getId(),"LogisticsInshops","id",id);
        return logisticsInshopsMapper.toDto(logisticsInshops);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LogisticsInshopsDto create(LogisticsInshops resources) {
        return logisticsInshopsMapper.toDto(logisticsInshopsRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LogisticsInshops resources) {
        LogisticsInshops logisticsInshops = logisticsInshopsRepository.findById(resources.getId()).orElseGet(LogisticsInshops::new);
        ValidationUtil.isNull( logisticsInshops.getId(),"LogisticsInshops","id",resources.getId());
        logisticsInshops.copy(resources);
        logisticsInshopsRepository.save(logisticsInshops);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            logisticsInshopsRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<LogisticsInshopsDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LogisticsInshopsDto logisticsInshops : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("物流公司", logisticsInshops.getLogisticsName());
            map.put("物流代码", logisticsInshops.getLogisticsCode());
            map.put("客户ID", logisticsInshops.getCustomersId());
            map.put("店铺ID",logisticsInshops.getShopId());
            map.put("店铺名", logisticsInshops.getName());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


}