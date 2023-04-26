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

import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.LogisticsInshopsDto;
import me.zhengjie.support.DMZTOSupport;
import me.zhengjie.support.GZTOSupport;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.support.sf.SFGJSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.LogisticsInfoRepository;
import me.zhengjie.service.dto.LogisticsInfoDto;
import me.zhengjie.service.dto.LogisticsInfoQueryCriteria;
import me.zhengjie.service.mapstruct.LogisticsInfoMapper;
import me.zhengjie.utils.constant.PlatformConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
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
* @author luob
* @date 2021-12-02
**/
@CacheConfig(cacheNames = "logistics")
@Service
@RequiredArgsConstructor
public class LogisticsInfoServiceImpl implements LogisticsInfoService {

    private final LogisticsInfoRepository logisticsInfoRepository;
    private final LogisticsInfoMapper logisticsInfoMapper;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private LogisticsInshopsService logisticsInshopsService;

    @Autowired
    private LogisticsUnarrivePlaceService logisticsUnarrivePlaceService;

    @Autowired
    private EMSService emsService;

    @Autowired
    private JDService jdService;

    @Autowired
    private GZTOSupport gztoSupport;

    @Autowired
    private ConfigService configService;

    @Autowired
    private SFGJSupport sfgjSupport;

    @Autowired
    private DMZTOSupport dmztoSupport;

    /**
     * 选择物流发货
     * @param order
     * @throws Exception
     */
    @Override
    public synchronized void getLogistics(CrossBorderOrder order) throws Exception {
        if (StringUtils.isNotEmpty(order.getLogisticsNo()))
            return;
        if (order.getSupplierId() != null)
            getMail(order);
        LogisticsInshops logisticsInshops = logisticsInshopsService.queryByShopId(order.getShopId());
        LogisticsInfo logisticsInfo;
        if (StringUtil.equals("DY",order.getPlatformCode())){
            if (logisticsInshops == null) {
                Config config = configService.queryByK("DEFAULT_LOGISTICS");// 默认快递
                if (config != null) {
                    logisticsInfo = queryByCode(config.getV());
                }else {
                    logisticsInfo = queryByCode("GZTO");
                }
                if (logisticsInfo == null)
                    throw new BadRequestException("中通快递未配置");
            }else {
                logisticsInfo = queryById(logisticsInshops.getLogisticsId());
            }
        }else {
            logisticsInfo=queryByCode("GZTO");
        }
        order.setSupplierId(logisticsInfo.getId());
        order.setLogisticsCode(logisticsInfo.getCode());
        order.setLogisticsName(logisticsInfo.getName());
        getMail(order);
    }

    /**
     * 根据给定的快递获取单号
     * @param order
     * @param supplierId
     */
    @Override
    public void getLogisticsByLogis(CrossBorderOrder order, Long supplierId) throws Exception {
        LogisticsInfo logisticsInfo = queryById(supplierId);
        order.setSupplierId(logisticsInfo.getId());
        order.setLogisticsCode(logisticsInfo.getCode());
        order.setLogisticsName(logisticsInfo.getName());
        getMail(order);
    }


    /**
     * 跨境单获取运单号
     * @param order
     */
    @Override
    public void getMail(CrossBorderOrder order) throws Exception {
        if (StringUtils.isBlank(order.getLogisticsCode())) {
            LogisticsInfo logisticsInfo = queryById(order.getSupplierId());
            order.setLogisticsCode(logisticsInfo.getCode());
        }
        if (StringUtils.equals(order.getLogisticsCode(), "GZTO")) {
            try {
                gztoSupport.getMail(order, null);
//                List<LogisticsUnarrivePlace> unarrivePlaces =  logisticsUnarrivePlaceService.queryByCityAndDistrictAndLogisticsId(order.getCity(), order.getDistrict(), order.getSupplierId());
//
//                // 当发现获取到的三段码里面有停发，则转顺丰
//                if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()) &&
//                        (StringUtil.contains(order.getAddMark(), "停发") || CollectionUtils.isNotEmpty(unarrivePlaces))) {
//                    order.setLogisticsNo("");
//                    order.setAddMark("");
//                    LogisticsInfo logisticsInfo = queryByCode("GSF");
//                    getLogisticsByLogis(order, logisticsInfo.getId());
//
//                }

            }catch (Exception e) {
//                if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()) && !StringUtils.equals("1", order.getFourPl())) {
//                    LogisticsInshops logisticsInshops = logisticsInshopsService.queryByShopId(order.getShopId());
//                    if (logisticsInshops == null) {
//                        // 只有本身用中通的才去尝试获取顺丰
//                        LogisticsInfo logisticsInfo = queryByCode("GSF");
//                        getLogisticsByLogis(order, logisticsInfo.getId());
//                    }else {
//                        throw e;
//                    }
//                }else {
//                    throw e;
//                }
                throw e;
            }
        }else if (StringUtils.equals(order.getLogisticsCode(), "JD")) {
//            jdService.getOrderNumber(order); //取运单号
//            jdService.getDecinfo(order); //运单申报
            throw new BadRequestException("快递未配置：" + order.getLogisticsCode());
        }else if (StringUtils.equals(order.getLogisticsCode(), "EMS")) {
//            emsService.getOrderNumber(order);
//            emsService.getDecinfo(order);
            throw new BadRequestException("快递未配置：" + order.getLogisticsCode());
        }else if (StringUtils.equals(order.getLogisticsCode(), "GSF")) {
            try {
                sfgjSupport.getMail(order);
            }catch (Exception e) {
                // 顺丰不能获取就获取中通
//                if (!StringUtils.equals("1", order.getFourPl())) {
//                    LogisticsInshops logisticsInshops = logisticsInshopsService.queryByShopId(order.getShopId());
//                    if (logisticsInshops != null && logisticsInshops.getTurnDefault()) {
//                        LogisticsInfo logisticsInfo = queryByCode("GZTO");
//                        getLogisticsByLogis(order, logisticsInfo.getId());
//                    }
//                }else {
//                    throw e;
//                }
                throw e;
            }
        }else {
            throw new BadRequestException("快递未配置：" + order.getLogisticsCode());
        }
    }

    // 国内订单获取运单号
    @Override
    public void getDMLogistics(DomesticOrder order) throws Exception {
        if (StringUtils.isNotEmpty(order.getLogisticsNo()))
            return;
        LogisticsInfo logisticsInfo = queryByCode("DMZTO");// 默认中通
        if (logisticsInfo == null)
            throw new BadRequestException("快递未配置");
        order.setSupplierId(logisticsInfo.getId());
        dmztoSupport.getMailNo(order);
    }

    @Override
    public Map<String,Object> queryAll(LogisticsInfoQueryCriteria criteria, Pageable pageable){
        Page<LogisticsInfo> page = logisticsInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(logisticsInfoMapper::toDto));
    }

    @Override
    public List<LogisticsInfoDto> queryAll(LogisticsInfoQueryCriteria criteria){
        return logisticsInfoMapper.toDto(logisticsInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public LogisticsInfoDto findById(Long id) {
        LogisticsInfo logisticsInfo = logisticsInfoRepository.findById(id).orElseGet(LogisticsInfo::new);
        ValidationUtil.isNull(logisticsInfo.getId(),"LogisticsInfo","id",id);
        return logisticsInfoMapper.toDto(logisticsInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LogisticsInfoDto create(LogisticsInfo resources) {
        if(logisticsInfoRepository.findByName(resources.getName()) != null){
            throw new EntityExistException(LogisticsInfo.class,"name",resources.getName());
        }
        if(logisticsInfoRepository.findByCode(resources.getCode()) != null){
            throw new EntityExistException(LogisticsInfo.class,"code",resources.getCode());
        }
        if(logisticsInfoRepository.findByKjgCode(resources.getKjgCode()) != null){
            throw new EntityExistException(LogisticsInfo.class,"kjg_code",resources.getKjgCode());
        }
        redisUtils.del("logistics::id:" + resources.getId());
        redisUtils.del("logistics::code:" + resources.getCode());
        redisUtils.del("logistics::carrierCode:" + resources.getDefault01());
        return logisticsInfoMapper.toDto(logisticsInfoRepository.save(resources));
    }

    @Cacheable(key = "'id:' + #p0")
    @Override
    public LogisticsInfo queryById(Long id) {
        if (id == null)
            return null;
        return logisticsInfoRepository.findById(id).orElseGet(LogisticsInfo::new);
    }

    @Cacheable(key = "'carrierCode:' + #p0")
    @Override
    public LogisticsInfo queryByDefault01(String carrierCode) {
        return logisticsInfoRepository.findByDefault01(carrierCode);
    }

    @Cacheable(key = "'kjgCode:' + #p0")
    @Override
    public LogisticsInfo queryByKjgCode(String kjgCode) {
        return logisticsInfoRepository.findByKjgCode(kjgCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(LogisticsInfo resources) {
        LogisticsInfo logisticsInfo = logisticsInfoRepository.findById(resources.getId()).orElseGet(LogisticsInfo::new);
        ValidationUtil.isNull( logisticsInfo.getId(),"LogisticsInfo","id",resources.getId());
        LogisticsInfo logisticsInfo1 = null;
        logisticsInfo1 = logisticsInfoRepository.findByName(resources.getName());
        if(logisticsInfo1 != null && !logisticsInfo1.getId().equals(logisticsInfo.getId())){
            throw new EntityExistException(LogisticsInfo.class,"name",resources.getName());
        }
        logisticsInfo1 = logisticsInfoRepository.findByCode(resources.getCode());
        if(logisticsInfo1 != null && !logisticsInfo1.getId().equals(logisticsInfo.getId())){
            throw new EntityExistException(LogisticsInfo.class,"code",resources.getCode());
        }
        logisticsInfo1 = logisticsInfoRepository.findByKjgCode(resources.getKjgCode());
        if(logisticsInfo1 != null && !logisticsInfo1.getId().equals(logisticsInfo.getId())){
            throw new EntityExistException(LogisticsInfo.class,"kjg_code",resources.getKjgCode());
        }
        logisticsInfo.copy(resources);
        logisticsInfoRepository.save(logisticsInfo);
        redisUtils.del("logistics::id:" + resources.getId());
        redisUtils.del("logistics::code:" + resources.getCode());
        redisUtils.del("logistics::carrierCode:" + resources.getDefault01());
        redisUtils.del("logistics::kjgCode:" + resources.getKjgCode());
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            logisticsInfoRepository.deleteById(id);
            redisUtils.del("logistics::id:" + id);
        }
    }

    @Override
    public void download(List<LogisticsInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (LogisticsInfoDto logisticsInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("名称", logisticsInfo.getName());
            map.put("代码", logisticsInfo.getCode());
            map.put("海关备案名称", logisticsInfo.getCustomsName());
            map.put("海关备案代码", logisticsInfo.getCustomsCode());
            map.put("跨境购代码", logisticsInfo.getKjgCode());
            map.put("默认字段1(抖音代码)", logisticsInfo.getDefault01());
            map.put("默认字段2(拼多多代码)", logisticsInfo.getDefault02());
            map.put("默认字段3", logisticsInfo.getDefault03());
            map.put("默认字段4", logisticsInfo.getDefault04());
            map.put("默认字段5", logisticsInfo.getDefault05());
            map.put("创建人", logisticsInfo.getCreateBy());
            map.put("创建时间", logisticsInfo.getCreateTime());
            map.put("更新者", logisticsInfo.getUpdateBy());
            map.put("更新时间", logisticsInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }


    @Cacheable(key = "'code:' + #p0")
    @Override
    public LogisticsInfo queryByCode(String code) {
        return logisticsInfoRepository.findByCode(code);
    }
}