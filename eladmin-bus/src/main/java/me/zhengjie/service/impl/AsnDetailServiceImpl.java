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
import me.zhengjie.service.*;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.AsnDetailRepository;
import me.zhengjie.service.dto.AsnDetailDto;
import me.zhengjie.service.dto.AsnDetailQueryCriteria;
import me.zhengjie.service.mapstruct.AsnDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author wangm
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-03-28
 **/
@Service
@RequiredArgsConstructor
public class AsnDetailServiceImpl implements AsnDetailService {

    private final AsnDetailRepository asnDetailRepository;
    private final AsnDetailMapper asnDetailMapper;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private StockOutTollyService stockOutTollyService;

    @Autowired
    private AsnHeaderService asnHeaderService;

    @Autowired
    private StackStockRecordService stackStockRecordService;

    @Override
    public Map<String, Object> queryAll(AsnDetailQueryCriteria criteria, Pageable pageable) {
        Page<AsnDetail> page = asnDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(asnDetailMapper::toDto));
    }

    @Override
    public List<AsnDetailDto> queryAll(AsnDetailQueryCriteria criteria) {
        return asnDetailMapper.toDto(asnDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public AsnDetailDto findById(Long id) {
        AsnDetail asnDetail = asnDetailRepository.findById(id).orElseGet(AsnDetail::new);
        ValidationUtil.isNull(asnDetail.getId(), "AsnDetail", "id", id);
        return asnDetailMapper.toDto(asnDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AsnDetailDto create(AsnDetail resources) {
        BaseSku baseSku = baseSkuService.queryByCode(resources.getProductCode());
        if (baseSku == null)
            throw new BadRequestException("根据goodsCode找不到对应的商品信息");
        resources.setLpnWeight(BigDecimal.ZERO);
        return asnDetailMapper.toDto(asnDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(AsnDetail resources) {
        AsnDetail asnDetail = asnDetailRepository.findById(resources.getId()).orElseGet(AsnDetail::new);
        ValidationUtil.isNull(asnDetail.getId(), "AsnDetail", "id", resources.getId());
        asnDetail.copy(resources);
        asnDetailRepository.save(asnDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            asnDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<AsnDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (AsnDetailDto asnDetail : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("产品id", asnDetail.getProductCode());
            map.put("原始订单明细id", asnDetail.getDocItemId());
            map.put("主单位数量", asnDetail.getQty());
            map.put("箱数", asnDetail.getCaseQty());
            map.put("托盘号", asnDetail.getLpn());
            map.put("商品总重", asnDetail.getTotalSkuWeight());
            map.put("托盘自重", asnDetail.getLpnWeight());
            map.put("托盘长度", asnDetail.getLpnLength());
            map.put("托盘宽度", asnDetail.getLpnWidth());
            map.put("托盘高度", asnDetail.getLpnHeight());
            map.put("wms批次号", asnDetail.getLotNo());
            map.put("客户批次号", asnDetail.getCustomerBatchNo());
            map.put("生产日期", asnDetail.getProductionTime());
            map.put("失效日期", asnDetail.getExpireTime());
            map.put("入仓时间", asnDetail.getWarehouseTime());
            map.put("入仓单号", asnDetail.getPoNo());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadAsnD(List<Map<String, Object>> maps, Long outId) {
        if (CollectionUtils.isEmpty(maps))
            throw new BadRequestException("数据为空");
        StockOutTolly stockOutTolly = stockOutTollyService.queryById(outId);
        if (stockOutTolly == null)
            throw new BadRequestException("理货数据不存在");
        AsnHeader asnHeader = asnHeaderService.findByOutId(stockOutTolly.getWmsOutstock().getId());
        AsnHeader asnHeaderCopy=new AsnHeader();
        asnHeaderCopy.setId(asnHeader.getId());
        int i = 0;
        for (Map<String, Object> map : maps) {
            i++;
            try {
                AsnDetail asnDetail = checkData(map, stockOutTolly, asnHeaderCopy);
                create(asnDetail);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BadRequestException("第" + (i+1) + "行：" + e.getMessage());
            }
        }
    }

    private AsnDetail checkData(Map<String, Object> map, StockOutTolly stockOutTolly, AsnHeader asnHeader) {
        Object skuNo = map.getOrDefault("货号", null);
        Object packNum = map.getOrDefault("箱数", null);
        Object qty = map.getOrDefault("主数量", null);
        Object lpn = map.getOrDefault("托盘号", null);
        Object weight = map.getOrDefault("带托重", null);
        Object lpnLength = map.getOrDefault("托盘长", null);
        Object lpnWidth = map.getOrDefault("托盘宽", null);
        Object lpnHeight = map.getOrDefault("托盘高", null);
        Object material = map.getOrDefault("包装材质", null);
        Object customerBatchNo = map.getOrDefault("客户批次号", null);
        Object expiredDate = map.getOrDefault("失效日期", null);
        if (skuNo == null || StringUtil.isBlank(skuNo.toString()))
            throw new BadRequestException("货号为空");
        if (packNum == null)
            throw new BadRequestException("箱数为空");
        if (qty == null)
            throw new BadRequestException("主数量为空");
        if (lpn == null)
            throw new BadRequestException("托盘号为空");
        if (weight == null)
            throw new BadRequestException("带托重为空");
        if (lpnLength == null)
            throw new BadRequestException("托盘长为空");
        if (lpnWidth == null)
            throw new BadRequestException("托盘宽为空");
        if (lpnHeight == null)
            throw new BadRequestException("托盘高为空");
        if (material == null)
            throw new BadRequestException("包装材质为空");
        if (expiredDate == null)
            throw new BadRequestException("失效日期为空");
        if (expiredDate.toString().length()>8)
            throw new BadRequestException("效期格式不对,应为年年年年月月日日");
        if (CollectionUtils.isEmpty(stockOutTolly.getDoLots()))
            throw new BadRequestException("请先生成批次信息");
        AsnDetail detail = new AsnDetail();
        BaseSku baseSku = baseSkuService.queryByGoodsNo(skuNo.toString());
        if (baseSku==null){
            baseSku = baseSkuService.queryByOutGoodsNo(skuNo.toString());
            if (baseSku==null){
                baseSku = baseSkuService.queryByBarcode(skuNo.toString());
                if (baseSku==null)
                    throw new BadRequestException("sku找不到信息");
            }
        }
        detail.setExpireTime(new Timestamp(DateUtils.parse(expiredDate+"","yyyyMMdd").getTime()));
        detail.setProductCode(baseSku.getGoodsCode());
        detail.setLpn(lpn.toString());
        detail.setQty(Integer.parseInt(qty+""));
        detail.setCaseQty(Integer.parseInt(packNum + ""));
        detail.setTotalSkuWeight(new BigDecimal(weight + ""));
        detail.setLpnLength(new BigDecimal(lpnLength + ""));
        detail.setLpnWidth(new BigDecimal(lpnWidth + ""));
        detail.setLpnHeight(new BigDecimal(lpnHeight + ""));
        detail.setMaterials(material.toString());
        detail.setAsnHeader(asnHeader);
        for (DoLot doLot : stockOutTolly.getDoLots()) {
            if (StringUtil.isNotBlank(customerBatchNo==null?null:customerBatchNo+"")) {
                if (StringUtil.equals(doLot.getProductId(), baseSku.getGoodsCode())
                        && doLot.getExpireTime()!=null && detail.getExpireTime()!=null
                        && doLot.getExpireTime().getTime() == detail.getExpireTime().getTime()
                        && StringUtil.equalsIgnoreCase(doLot.getCustomerBatchNo(), customerBatchNo + "")) {
                    detail.setDocItemId(doLot.getDocItemId());
                    detail.setLotNo(doLot.getLotNo());
                    detail.setProductionTime(new Timestamp(detail.getExpireTime().getTime() - baseSku.getLifecycle() * 24 * 3600 * 1000));
                    detail.setWarehouseTime(doLot.getWarehouseTime());
                    detail.setPoNo(doLot.getPoCode());
                    detail.setCustomerBatchNo(doLot.getCustomerBatchNo());
                    break;
                }
            }else {
                if (StringUtil.equals(doLot.getProductId(), baseSku.getGoodsCode())
                        && doLot.getExpireTime()!=null && detail.getExpireTime()!=null
                        && doLot.getExpireTime().getTime() == detail.getExpireTime().getTime()) {
                    detail.setDocItemId(doLot.getDocItemId());
                    detail.setLotNo(doLot.getLotNo());
                    detail.setProductionTime(new Timestamp(detail.getExpireTime().getTime() - baseSku.getLifecycle() * 24 * 3600 * 1000));
                    detail.setWarehouseTime(doLot.getWarehouseTime());
                    detail.setPoNo(doLot.getPoCode());
                    detail.setCustomerBatchNo(doLot.getCustomerBatchNo());
                    break;
                }
            }
        }
            if (StringUtil.isBlank(detail.getDocItemId()))
                throw new BadRequestException("wms批次号" + detail.getLotNo() + "没有匹配上商品行");
            return detail;
        }
    }
