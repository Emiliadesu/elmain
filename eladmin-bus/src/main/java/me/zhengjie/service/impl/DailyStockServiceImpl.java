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

import cn.hutool.core.date.DatePattern;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.aliyun.openservices.ons.api.Action;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.DailyStock;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.CustomerInfoService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.CustomerInfoDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DailyStockRepository;
import me.zhengjie.service.DailyStockService;
import me.zhengjie.service.dto.DailyStockDto;
import me.zhengjie.service.dto.DailyStockQueryCriteria;
import me.zhengjie.service.mapstruct.DailyStockMapper;
import me.zhengjie.utils.constant.PlatformConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-07-06
**/
@Service
@RequiredArgsConstructor
@Slf4j
public class DailyStockServiceImpl implements DailyStockService {

    private final DailyStockRepository dailyStockRepository;
    private final DailyStockMapper dailyStockMapper;

    @Autowired
    private WmsSupport wmsSupport;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CustomerInfoService customerInfoService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private RedisUtils redisUtils;

    private final String DS_HANDLE = "DS_HANDLE";

    @Override
    public Map<String,Object> queryAll(DailyStockQueryCriteria criteria, Pageable pageable){
        Page<DailyStock> page = dailyStockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dailyStockMapper::toDto));
    }

    @Override
    public List<DailyStockDto> queryAll(DailyStockQueryCriteria criteria){
        return dailyStockMapper.toDto(dailyStockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DailyStockDto findById(Long id) {
        DailyStock dailyStock = dailyStockRepository.findById(id).orElseGet(DailyStock::new);
        ValidationUtil.isNull(dailyStock.getId(),"DailyStock","id",id);
        return dailyStockMapper.toDto(dailyStock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DailyStockDto create(DailyStock resources) {
        return dailyStockMapper.toDto(dailyStockRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DailyStock resources) {
        DailyStock dailyStock = dailyStockRepository.findById(resources.getId()).orElseGet(DailyStock::new);
        ValidationUtil.isNull( dailyStock.getId(),"DailyStock","id",resources.getId());
        dailyStock.copy(resources);
        dailyStockRepository.save(dailyStock);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dailyStockRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DailyStockDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DailyStockDto dailyStock : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("客户", dailyStock.getCustomersName());
            map.put("店铺", dailyStock.getShopName());
            map.put("时间", dailyStock.getDayTime());
            map.put("WMS货主代码", dailyStock.getWmsCustomersCode());
            map.put("货号", dailyStock.getGoodsNo());
            map.put("商品编码", dailyStock.getGoodsCode());
            map.put("条码", dailyStock.getBarCode());
            map.put("名称", dailyStock.getGoodsName());
            map.put("WMS商品名称", dailyStock.getWmsGoodsName());
            map.put("库位", dailyStock.getLocation());
            map.put("批次号", dailyStock.getBatchNo());
            map.put("商品状态", dailyStock.getStockStatus());
            map.put("生产日期", dailyStock.getProdTime());
            map.put("失效日期", dailyStock.getExpTime());
            map.put("入库日期", dailyStock.getInTime());
            map.put("库存数量", dailyStock.getQty());
            map.put("WMS保存时间", dailyStock.getWmsTime());
            map.put("ERP保存时间", dailyStock.getCreateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void pullStock(String dayTime) {
        String redisKey = String.valueOf(redisUtils.get(DS_HANDLE));
        // 2.如果存在就直接消费成功，如果不存在就继续消费下去(这个方案会导致补偿业务异常补偿方案失效，先观察一段时间)
        if (StringUtils.isNotBlank(redisKey)
                && !"null".equals(redisKey)) {
            log.info("拉取任务处理中，不再重复处理：{}", redisKey );
            throw new BadRequestException("拉取任务处理中，不再重复处理：" + redisKey);
        }
        // 保存redis缓存
        redisUtils.set(DS_HANDLE, StringUtils.getLocalIp());
        try {
            if (StringUtil.isBlank(dayTime)) {
                dayTime = DateUtils.format(DateUtils.offsetDay(new Date(), -1), DatePattern.PURE_DATE_FORMAT);
            }
            Integer count = queryCountByDayTime(dayTime);
            if (count == null || count.intValue() != 0) {
                redisUtils.del(DS_HANDLE);
                throw new BadRequestException("已存在该日期记录，要重新拉取请删除当天记录：" + dayTime);
            }

            JSONObject result = wmsSupport.queryDailyStock(dayTime, 1, 50000);
//            BigDecimal totalPage = totalCount.divide(new BigDecimal(pageSize), 0, BigDecimal.ROUND_UP);
//            for (int i = 1; i <= totalPage.intValue(); i++) {
//                JSONObject jsonObject = wmsSupport.queryDailyStock(dayTime, i, pageSize);
                JSONArray records = result.getJSONArray("records");
                List<DailyStock> saves = new ArrayList<>();
                for (int j = 0; j < records.size(); j++) {
                    String goodsNo = records.getJSONObject(j).getStr("sku");
                    DailyStock dailyStock = new DailyStock();
                    dailyStock.setGoodsNo(goodsNo);
                    BaseSku baseSku = baseSkuService.queryByGoodsNo(goodsNo);
                    if (baseSku != null) {
                        dailyStock.setCustomersId(baseSku.getCustomersId());
                        dailyStock.setShopId(baseSku.getShopId());
                        CustomerInfoDto customerInfoDto = customerInfoService.queryById(baseSku.getCustomersId());
                        ShopInfoDto shopInfoDto = shopInfoService.queryById(baseSku.getShopId());
                        dailyStock.setCustomersName(customerInfoDto.getCustNickName());
                        dailyStock.setShopName(shopInfoDto.getName());
                        dailyStock.setGoodsId(baseSku.getId());
                        dailyStock.setGoodsCode(baseSku.getGoodsCode());
                        dailyStock.setGoodsName(baseSku.getGoodsName());
                        dailyStock.setBarCode(baseSku.getBarCode());
                    }else {
                        dailyStock.setGoodsName(records.getJSONObject(j).getStr("descrC"));
                        dailyStock.setBarCode(records.getJSONObject(j).getStr("alternateSKU1"));
                    }
                    dailyStock.setDayTime(records.getJSONObject(j).getStr("other"));
                    dailyStock.setWmsCustomersCode(records.getJSONObject(j).getStr("customerid"));
                    dailyStock.setBatchNo(records.getJSONObject(j).getStr("lotnum"));
                    dailyStock.setLocation(records.getJSONObject(j).getStr("locationid"));
                    if (StringUtils.equals(baseSku.getPlatformCode(), PlatformConstant.DY)
                            && StringUtils.equals(baseSku.getWarehouseCode(), "4PLFLBBC01")) {
                        dailyStock.setQty(0);
                    }else {
                        dailyStock.setQty(records.getJSONObject(j).getInt("qty"));
                    }

                    dailyStock.setWmsGoodsName(records.getJSONObject(j).getStr("descrC"));
                    dailyStock.setLocation(records.getJSONObject(j).getStr("locationid"));
                    dailyStock.setStockStatus(records.getJSONObject(j).getStr("lotstatus"));
                    dailyStock.setProdTime(records.getJSONObject(j).getStr("prodtime"));
                    dailyStock.setExpTime(records.getJSONObject(j).getStr("exptime"));
                    dailyStock.setInTime(records.getJSONObject(j).getStr("intime"));
                    dailyStock.setWmsTime(new Timestamp(records.getJSONObject(j).getLong("addtime")));
                    saves.add(dailyStock);
                }
                dailyStockRepository.saveAll(saves);
//            }
            redisUtils.del(DS_HANDLE);
        }catch (Exception e) {
            e.printStackTrace();
            redisUtils.del(DS_HANDLE);
            throw e;
        }
    }

    @Transactional
    @Override
    public void deleteByDayTime(String dayTime) {
        dailyStockRepository.deleteByDayTime(dayTime);
    }

    @Override
    public Integer queryCountByDayTime(String dayTime) {
        return dailyStockRepository.queryCountByDayTime(dayTime);
    }

    @Override
    public void clearKey() {
        redisUtils.del(DS_HANDLE);
    }

    @Override
    public List<DailyStock> queryByShopIdAndDayTime(Long shopInfoId, String dayTime) {
        List<Map<String,Object>>maps=dailyStockRepository.queryAllByShopIdAndDayTime(shopInfoId,dayTime);
        return com.alibaba.fastjson.JSONArray.parseArray(com.alibaba.fastjson.JSONObject.toJSONString(maps)).toJavaList(DailyStock.class);
    }
}