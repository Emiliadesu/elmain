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

import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.domain.LogisticsInfo;
import me.zhengjie.domain.LogisticsUnarrivePlace;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.LogisticsInfoService;
import me.zhengjie.service.dto.CrossBorderOrderDetailsQueryCriteria;
import me.zhengjie.service.dto.OrderReturnDetailsQueryCriteria;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LogisticsUnarrivePlaceRepository;
import me.zhengjie.service.LogisticsUnarrivePlaceService;
import me.zhengjie.service.dto.LogisticsUnarrivePlaceDto;
import me.zhengjie.service.dto.LogisticsUnarrivePlaceQueryCriteria;
import me.zhengjie.service.mapstruct.LogisticsUnarrivePlaceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
* @author leningzhou
* @date 2021-12-03
**/
@Service
@RequiredArgsConstructor
public class LogisticsUnarrivePlaceServiceImpl implements LogisticsUnarrivePlaceService {

    private final LogisticsUnarrivePlaceRepository logisticsUnarrivePlaceRepository;
    private final LogisticsUnarrivePlaceMapper logisticsUnarrivePlaceMapper;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Override
    public Map<String,Object> queryAll(LogisticsUnarrivePlaceQueryCriteria criteria, Pageable pageable){
        Page<LogisticsUnarrivePlace> page = logisticsUnarrivePlaceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(logisticsUnarrivePlaceMapper::toDto));
    }


    /**
     * 根据快递Id查询不可达区域
     * @param LogisticsId
     * @return
     */
    @Override
    public List<LogisticsUnarrivePlace> queryByLogisticsId(Long LogisticsId) {
        return logisticsUnarrivePlaceRepository.findByLogisticsId(LogisticsId);
    }

    @Override
    public List<LogisticsUnarrivePlace> queryById(Long id) {
        LogisticsUnarrivePlaceQueryCriteria criteria = new LogisticsUnarrivePlaceQueryCriteria();
        criteria.setLogisticsId(id);
        return logisticsUnarrivePlaceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    public List<LogisticsUnarrivePlace> queryByLogisticsCode(String logisticsCode) {
        LogisticsUnarrivePlaceQueryCriteria criteria = new LogisticsUnarrivePlaceQueryCriteria();
        criteria.setLogisticsCode(logisticsCode);
        return logisticsUnarrivePlaceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadLogistics(List<Map<String, Object>> list) {
        if (CollectionUtils.isEmpty(list))
            throw new BadRequestException("数据为空");
        List<LogisticsUnarrivePlace> logisticsUnarrivePlaces = new ArrayList<>();
        for (Map<String, Object> map : list) {
            int sNo = Integer.valueOf(map.get("序号").toString());
            String logisticsCode = map.get("物流代码") != null ?map.get("物流代码").toString() : null;
            String province = map.get("省") != null ?map.get("省").toString() : null;
            String city = map.get("市") != null ?map.get("市").toString() : null;
            String district = map.get("区") != null ?map.get("区").toString() : null;
            String reason = map.get("原因") != null ?map.get("原因").toString() : null;

            if (StringUtils.isEmpty(logisticsCode))
                throw new BadRequestException("第" + sNo + "行，logisticsCode不能为空");
            if (StringUtils.isEmpty(province))
                throw new BadRequestException("第" + sNo + "行，province不能为空");
            if (StringUtils.isEmpty(city))
                throw new BadRequestException("第" + sNo + "行，city不能为空");
            if (StringUtils.isEmpty(district))
                throw new BadRequestException("第" + sNo + "行，district不能为空");


            LogisticsInfo logisticsInfo = logisticsInfoService.queryByCode(logisticsCode);
            if (logisticsInfo == null)
                throw new BadRequestException("第" + sNo + "行，查询不到物流代码");

            LogisticsUnarrivePlace logisticsUnarrivePlace = new LogisticsUnarrivePlace();
            logisticsUnarrivePlace.setLogisticsId(logisticsInfo.getId());
            logisticsUnarrivePlace.setLogisticsName(logisticsInfo.getName());
            logisticsUnarrivePlace.setLogisticsCode(logisticsCode);

            logisticsUnarrivePlace.setProvince(province);
            logisticsUnarrivePlace.setCity(city);
            logisticsUnarrivePlace.setDistrict(district);
            logisticsUnarrivePlace.setReason(reason);
            logisticsUnarrivePlaces.add(logisticsUnarrivePlace);
        }
        logisticsUnarrivePlaceRepository.saveAll(logisticsUnarrivePlaces);
    }

    @Override
    public LogisticsUnarrivePlace queryByLogisticsName(String logisticsName){
        return logisticsUnarrivePlaceRepository.findByLogisticsName(logisticsName);
    }

    @Override
    public LogisticsUnarrivePlace queryUnarrivePlaceByLId(Long logisticsId, String province, String city,String district) {
        try {
            return logisticsUnarrivePlaceRepository.findUnarrivePlaceByLId(logisticsId, province, city,district);
        }catch (Exception e){
            System.out.println(e.getMessage());
            throw new BadRequestException(e.getMessage());
        }

    }

    @Override
    public List<LogisticsUnarrivePlace> queryByCityAndDistrictAndLogisticsId(String city, String district, Long logisticsId) {
        return logisticsUnarrivePlaceRepository.queryByCityAndDistrictAndLogisticsId(city, district, logisticsId);
    }

    @Override
    public List<LogisticsUnarrivePlaceDto> queryAll(LogisticsUnarrivePlaceQueryCriteria criteria){
        return logisticsUnarrivePlaceMapper.toDto(logisticsUnarrivePlaceRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LogisticsUnarrivePlaceDto findById(Long id) {
        LogisticsUnarrivePlace logisticsUnarrivePlace = logisticsUnarrivePlaceRepository.findById(id).orElseGet(LogisticsUnarrivePlace::new);
        ValidationUtil.isNull(logisticsUnarrivePlace.getId(),"LogisticsUnarrivePlace","id",id);
        return logisticsUnarrivePlaceMapper.toDto(logisticsUnarrivePlace);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LogisticsUnarrivePlaceDto create(LogisticsUnarrivePlace resources) {
        return logisticsUnarrivePlaceMapper.toDto(logisticsUnarrivePlaceRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LogisticsUnarrivePlace resources) {
        LogisticsUnarrivePlace logisticsUnarrivePlace = logisticsUnarrivePlaceRepository.findById(resources.getId()).orElseGet(LogisticsUnarrivePlace::new);
        ValidationUtil.isNull( logisticsUnarrivePlace.getId(),"LogisticsUnarrivePlace","id",resources.getId());
        logisticsUnarrivePlace.copy(resources);
        logisticsUnarrivePlaceRepository.save(logisticsUnarrivePlace);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            logisticsUnarrivePlaceRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<LogisticsUnarrivePlaceDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LogisticsUnarrivePlaceDto logisticsUnarrivePlace : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("序号",logisticsUnarrivePlace.getId());
//            map.put("物流ID",logisticsUnarrivePlace.getLogisticsId());
            map.put("物流公司", logisticsUnarrivePlace.getLogisticsName());
            map.put("物流代码", logisticsUnarrivePlace.getLogisticsCode());
            map.put("省", logisticsUnarrivePlace.getProvince());
            map.put("市", logisticsUnarrivePlace.getCity());
            map.put("区", logisticsUnarrivePlace.getDistrict());
            map.put("原因",logisticsUnarrivePlace.getReason());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

}