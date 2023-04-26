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
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.fuliPre.BaseSkuPackInfo;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.TrsDetailRepository;
import me.zhengjie.service.dto.TrsDetailDto;
import me.zhengjie.service.dto.TrsDetailQueryCriteria;
import me.zhengjie.service.mapstruct.TrsDetailMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author wangm
* @date 2021-03-23
**/
@Service
@RequiredArgsConstructor
public class TrsDetailServiceImpl implements TrsDetailService {

    private final TrsDetailRepository trsDetailRepository;
    private final TrsDetailMapper trsDetailMapper;

    @Autowired
    private CustomLpnRecordService customLpnRecordService;

    @Autowired
    private StockInTollyService stockInTollyService;

    @Autowired
    private OutofPlanDetailService outofPlanDetailService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private WmsInstockItemService wmsInstockItemService;

    @Autowired
    private WmsSupport wmsSupport;

    @Override
    public Map<String,Object> queryAll(TrsDetailQueryCriteria criteria, Pageable pageable){
        Page<TrsDetail> page = trsDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(trsDetailMapper::toDto));
    }

    @Override
    public List<TrsDetailDto> queryAll(TrsDetailQueryCriteria criteria){
        return trsDetailMapper.toDto(trsDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public TrsDetailDto findById(Long id) {
        TrsDetail trsDetail = trsDetailRepository.findById(id).orElseGet(TrsDetail::new);
        ValidationUtil.isNull(trsDetail.getId(),"TrsDetail","id",id);
        return trsDetailMapper.toDto(trsDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TrsDetailDto create(TrsDetail resources) {
        resources.setWarehouseTime(DateUtils.now());
        resources.setLotNo(resources.getPoNo());
        StockInTolly stockInTolly=stockInTollyService.findById(resources.getStockInTolly().getId());
        WmsInstock wmsInstock=wmsInstockService.queryById(stockInTolly.getInOrderId());
        List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
        resources.setMerchantId(wmsInstock.getMerchantId());
        resources.setVirtualMerchantId(wmsInstock.getMerchantId());
        resources.setStockBusinessType(wmsInstock.getInOrderType());
        resources.setConsignorId(wmsInstock.getMerchantId());
        resources.setFundProviderId(wmsInstock.getMerchantId());
        resources.setPoNo(wmsInstock.getPoNo());
        resources.setLotNo(wmsInstock.getPoNo());
        if (StringUtil.equals(resources.getLpn(),"-")){
            String lpnCode=spawnLpn();
            resources.setLpn(lpnCode);
            CustomLpnRecord record=new CustomLpnRecord();
            record.setPoNo(stockInTolly.getWmsInstock().getPoNo());
            record.setLpnCode(lpnCode);
            record.setCreateUser(SecurityUtils.getCurrentUsername());
            record.setCreateTime(new Timestamp(System.currentTimeMillis()));
            customLpnRecordService.create(record);
        }
        BaseSku baseSku = baseSkuService.queryByOutGoodsNo(resources.getSkuNo());
        if (baseSku==null)
            throw new BadRequestException("商品基本信息不存在");
        if (baseSku.getSaleW()==null||baseSku.getSaleL()==null||baseSku.getSaleH()==null){
            BaseSkuPackInfo skuPackInfo = wmsSupport.getBaseSkuPackInfo(baseSku.getGoodsNo());
            if ((skuPackInfo.getWidth()==null||BigDecimalUtils.eq(skuPackInfo.getWidth(),BigDecimal.ZERO))
                    &&(skuPackInfo.getPackWidth()==null||BigDecimalUtils.eq(skuPackInfo.getPackWidth(),BigDecimal.ZERO)))
                throw new BadRequestException("商品基本信息的长宽高数据为空");
            if ((skuPackInfo.getWeight()==null||BigDecimalUtils.eq(skuPackInfo.getWeight(),BigDecimal.ZERO))
                    &&(skuPackInfo.getPackWeight()==null||BigDecimalUtils.eq(skuPackInfo.getPackWeight(),BigDecimal.ZERO)))
                throw new BadRequestException("商品基本信息的毛重和箱重数据为空");
            if (skuPackInfo.getPackNum()==null||skuPackInfo.getPackNum()==0)
                throw new BadRequestException("商品基本信息的箱规为空");
            if (skuPackInfo.getWidth()==null||BigDecimalUtils.eq(skuPackInfo.getWidth(),BigDecimal.ZERO)) {
                baseSku.setSaleW(skuPackInfo.getPackWidth());
                baseSku.setPackW(skuPackInfo.getPackWidth());
                baseSku.setSaleL(skuPackInfo.getPackLength());
                baseSku.setPackL(skuPackInfo.getPackLength());
                baseSku.setSaleH(skuPackInfo.getPackHeight());
                baseSku.setPackH(skuPackInfo.getPackHeight());
                baseSku.setGrossWeight(skuPackInfo.getWeight());
                baseSku.setPackWeight(skuPackInfo.getPackWeight());
                baseSku.setPackNum(skuPackInfo.getPackNum());
            }else if (skuPackInfo.getPackWidth()==null||BigDecimalUtils.eq(skuPackInfo.getPackWidth(),BigDecimal.ZERO)){
                baseSku.setSaleW(skuPackInfo.getWidth());
                baseSku.setPackW(skuPackInfo.getWidth());
                baseSku.setSaleL(skuPackInfo.getLength());
                baseSku.setPackL(skuPackInfo.getLength());
                baseSku.setSaleH(skuPackInfo.getHeight());
                baseSku.setPackH(skuPackInfo.getHeight());
                baseSku.setGrossWeight(skuPackInfo.getWeight());
                baseSku.setPackWeight(skuPackInfo.getPackWeight());
                baseSku.setPackNum(skuPackInfo.getPackNum());
            }else {
                baseSku.setSaleW(skuPackInfo.getWidth());
                baseSku.setPackW(skuPackInfo.getPackWidth());
                baseSku.setSaleL(skuPackInfo.getLength());
                baseSku.setPackL(skuPackInfo.getPackLength());
                baseSku.setSaleH(skuPackInfo.getHeight());
                baseSku.setPackH(skuPackInfo.getPackHeight());
                baseSku.setGrossWeight(skuPackInfo.getWeight());
                baseSku.setPackWeight(skuPackInfo.getPackWeight());
                baseSku.setPackNum(skuPackInfo.getPackNum());
            }
            baseSkuService.update(baseSku);
        }
        resources.setGoodsNo(baseSku.getGoodsNo());
        resources.setBarCode(baseSku.getBarCode());
        if (StringUtil.equals(wmsInstock.getTallyWay(),"1")){
            //按件理货
            if (BigDecimalUtils.le(baseSku.getSaleW(), BigDecimal.ZERO)
                    ||BigDecimalUtils.le(baseSku.getSaleL(), BigDecimal.ZERO)
                    ||BigDecimalUtils.le(baseSku.getSaleH(), BigDecimal.ZERO))
                throw new BadRequestException("商品基本信息的长宽高数据小于等于0");
            resources.setWidth(baseSku.getSaleW().toString());
            resources.setHeight(baseSku.getSaleH().toString());
            resources.setLength(baseSku.getSaleL().toString());
            resources.setVolume(baseSku.getSaleVolume()==null
                    ?baseSku.getSaleW().multiply(baseSku.getSaleH()).multiply(baseSku.getSaleL()).toString()
                    :baseSku.getSaleVolume().toString());
        }else if (StringUtil.equals("2",wmsInstock.getTallyWay())){
            //按箱理货
            if (BigDecimalUtils.le(baseSku.getPackW(), BigDecimal.ZERO)
                    ||BigDecimalUtils.le(baseSku.getPackL(), BigDecimal.ZERO)
                    ||BigDecimalUtils.le(baseSku.getPackH(), BigDecimal.ZERO))
                throw new BadRequestException("商品基本信息的外箱长宽高数据小于等于0");
            resources.setWidth(baseSku.getPackW().toString());
            resources.setHeight(baseSku.getPackH().toString());
            resources.setLength(baseSku.getPackL().toString());
            resources.setVolume(baseSku.getPackVolume()==null
                    ?baseSku.getPackW().multiply(baseSku.getPackL()).multiply(baseSku.getPackH()).toString()
                    :baseSku.getPackVolume().toString());
        }else {
            //其他 按托理货，商品基本信息的外箱长宽高和内件的长宽高信息至少有一种不能为空
            if (BigDecimalUtils.ge(baseSku.getSaleL(),BigDecimal.ZERO)
                    &&BigDecimalUtils.ge(baseSku.getSaleW(),BigDecimal.ZERO)
                    &&BigDecimalUtils.ge(baseSku.getSaleH(),BigDecimal.ZERO)){
                resources.setWidth(baseSku.getSaleW().toString());
                resources.setHeight(baseSku.getSaleH().toString());
                resources.setLength(baseSku.getSaleL().toString());
                resources.setVolume(baseSku.getSaleVolume()==null
                        ?baseSku.getSaleW().multiply(baseSku.getSaleH()).multiply(baseSku.getSaleL()).toString()
                        :baseSku.getSaleVolume().toString());
            }else if (BigDecimalUtils.ge(baseSku.getPackL(),BigDecimal.ZERO)
                    &&BigDecimalUtils.ge(baseSku.getPackW(),BigDecimal.ZERO)
                    &&BigDecimalUtils.ge(baseSku.getPackH(),BigDecimal.ZERO)){
                resources.setWidth(baseSku.getPackW().toString());
                resources.setHeight(baseSku.getPackH().toString());
                resources.setLength(baseSku.getPackL().toString());
                resources.setVolume(baseSku.getPackVolume()==null
                        ?baseSku.getPackW().multiply(baseSku.getPackL()).multiply(baseSku.getPackH()).toString()
                        :baseSku.getPackVolume().toString());
            }else {
                throw new BadRequestException("商品基本信息的外箱或者内件的长宽高数据小于等于0");
            }
        }

        resources.setGrossWeight(baseSku.getGrossWeight()==null?"0":baseSku.getGrossWeight().toString());
        //如果生产日期是空白的，需要根据失效日期和保质期推算生产日期
        if (StringUtil.isNotBlank(resources.getExpiredTime())&&StringUtil.isBlank(resources.getProductionTime())){
            Date expiredDate=DateUtils.parse(resources.getExpiredTime(),"yyyyMMdd");
            //失效日期毫秒数-保质期毫秒数
            Date productionDate=new Date(expiredDate.getTime()-baseSku.getLifecycle()*24L*3600L*1000L);
            resources.setProductionTime(DateUtils.format(productionDate,"yyyyMMdd"));
        }
        for (WmsInstockItem item : itemList) {
            if (StringUtil.equals(item.getSkuNo(),resources.getSkuNo())){
                if (StringUtil.isNotBlank(item.getCustomerBatchNo())&&StringUtil.isNotBlank(resources.getCustomerBatchNo())){
                    if (StringUtil.equals(resources.getCustomerBatchNo(),item.getCustomerBatchNo())){
                        resources.setDocLineId(item.getGoodsLineNo());
                        resources.setProductId(item.getProductId());
                        break;
                    }
                }else {
                    resources.setDocLineId(item.getGoodsLineNo());
                    resources.setProductId(item.getProductId());
                    break;
                }
            }
        }
        if (StringUtil.isBlank(resources.getDocLineId())){
            //超收
            resources.setDocLineId("OOP"+System.currentTimeMillis());
            resources.setProductId(baseSku.getGoodsCode());
            OutofPlanDetail outofPlanDetail=new OutofPlanDetail();
            outofPlanDetail.setEan13(resources.getSkuNo());
            outofPlanDetail.setStockInTolly(resources.getStockInTolly());
            OutofPlanDetail exist=outofPlanDetailService.queryOne(outofPlanDetail);
            if (exist!=null){
                exist.setReceiveQty((resources.getTransactionQty()+Integer.parseInt(exist.getReceiveQty()))+"");
                outofPlanDetailService.update(exist);
            }else {
                outofPlanDetail.setReceiveQty(resources.getTransactionQty() + "");
                outofPlanDetail.setProductName(baseSku.getGoodsName());
                outofPlanDetail.setPicUrl("-");
                outofPlanDetailService.create(outofPlanDetail);
            }
        }
        return trsDetailMapper.toDto(trsDetailRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TrsDetail resources) {
        TrsDetail trsDetail = trsDetailRepository.findById(resources.getId()).orElseGet(TrsDetail::new);
        ValidationUtil.isNull( trsDetail.getId(),"TrsDetail","id",resources.getId());
        trsDetail.copy(resources);
        trsDetailRepository.save(trsDetail);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            trsDetailRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TrsDetailDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TrsDetailDto trsDetail : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("理货单id", trsDetail.getStockInTolly().getTallyOrderSn());
            map.put("理货详情的id", trsDetail.getDocLineId());
            map.put("交易数量", trsDetail.getTransactionQty());
            map.put("交易类型", trsDetail.getTransactionType());
            map.put("商品编码", trsDetail.getProductId());
            map.put("产品长度", trsDetail.getLength());
            map.put("产品宽度", trsDetail.getWidth());
            map.put("产品高度", trsDetail.getHeight());
            map.put("产品净重", trsDetail.getNetWeight());
            map.put("产品毛重", trsDetail.getGrossWeight());
            map.put("产品体积", trsDetail.getVolume());
            map.put("供应商", trsDetail.getSupplierId());
            map.put("商家id", trsDetail.getMerchantId());
            map.put("源批次号", trsDetail.getLotNo());
            map.put("制造商id", trsDetail.getManufactureId());
            map.put("合同号", trsDetail.getPoNo());
            map.put("采购进价", trsDetail.getPurchasePrice());
            map.put("生产日期", trsDetail.getProductionTime());
            map.put("入库时间", trsDetail.getWarehouseTime());
            map.put("失效日期", trsDetail.getExpiredTime());
            map.put("账册编号", trsDetail.getAccountBookId());
            map.put("货主企业编码", trsDetail.getFundProviderId());
            map.put("提单号大", trsDetail.getBlNo1());
            map.put("提单号小", trsDetail.getBlNo2());
            map.put("店铺id", trsDetail.getShopId());
            map.put("是否溯源", trsDetail.getIsTraceSrc());
            map.put("货主企业编码", trsDetail.getConsignorId());
            map.put("wms批次号", trsDetail.getBatchNo());
            map.put("是否坏品", trsDetail.getIsDamaged());
            map.put("客户批次号", trsDetail.getCustomerBatchNo());
            map.put("虚拟货主", trsDetail.getVirtualMerchantId());
            map.put("细分类型", trsDetail.getSubType());
            map.put("单位", trsDetail.getUom());
            map.put("收货托盘号", trsDetail.getLpn());
            map.put("业务类型", trsDetail.getStockBusinessType());
            map.put("收货单位", trsDetail.getInbUom());
            map.put("收货单位数量", trsDetail.getInbUomQty());
            map.put("监管代码", trsDetail.getLot26());
            map.put("账册", trsDetail.getLot27());
            map.put("生产批次号", trsDetail.getLot28());
            map.put(" lot29",  trsDetail.getLot29());
            map.put(" lot30",  trsDetail.getLot30());
            map.put(" lot31",  trsDetail.getLot31());
            map.put(" lot32",  trsDetail.getLot32());
            map.put(" lot33",  trsDetail.getLot33());
            map.put(" lot34",  trsDetail.getLot34());
            map.put(" lot35",  trsDetail.getLot35());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public TrsDetail queryOne(TrsDetail trsDetail) {
        return trsDetailRepository.findOne(Example.of(trsDetail)).orElse(null);
    }

    @Override
    public List<TrsDetail> queryList(TrsDetail trsDetail) {
        return trsDetailRepository.findAll(Example.of(trsDetail));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadTrsD(List<Map<String, Object>> maps, Long stockId) {
        if (CollectionUtils.isEmpty(maps))
            throw new BadRequestException("数据为空");
        List<TrsDetail> trsDetailList = new ArrayList<>();
        StockInTolly stockInTolly=stockInTollyService.findById(stockId);
        if (stockInTolly==null)
            throw new BadRequestException("理货数据不存在");
        WmsInstock wmsInstock=wmsInstockService.queryById(stockInTolly.getInOrderId());
        List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
        wmsInstock.setItemList(itemList);
        stockInTolly.setWmsInstock(wmsInstock);
        int i=0;
        for (Map<String, Object> map : maps) {
            i++;
            try {
                TrsDetail trsDetail = checkData(map,stockInTolly);
                if (trsDetailList.indexOf(trsDetail)!=-1){
                    TrsDetail exist=trsDetailList.get(trsDetailList.indexOf(trsDetail));
                    exist.setTransactionQty(exist.getTransactionQty()+trsDetail.getTransactionQty());
                }else
                    trsDetailList.add(trsDetail);
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException("第"+i+"行："+e.getMessage());
            }
        }
        trsDetailRepository.saveAll(trsDetailList);
    }

    @Override
    public List<TrsDetail> queryAllByInStockId(Long inTallyId) {
        TrsDetail trsDetail=new TrsDetail();
        StockInTolly stockInTolly=new StockInTolly();
        stockInTolly.setId(inTallyId);
        trsDetail.setStockInTolly(stockInTolly);
        return trsDetailRepository.findAll(Example.of(trsDetail));
    }

    @Override
    public void downloadBatchNoLpn(HttpServletResponse response) throws IOException{
        List<Map<String, Object>> list = new ArrayList<>();
        List<TrsDetail>detailList=trsDetailRepository.findByLpnNotNull();
        if (CollectionUtils.isNotEmpty(detailList)){
            for (TrsDetail trsDetail : detailList) {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("货号", trsDetail.getGoodsNo());
                map.put("客户批次号", trsDetail.getCustomerBatchNo());
                map.put("托盘号",trsDetail.getLpn());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    private TrsDetail checkData(Map<String, Object> map,StockInTolly stockInTolly) {
        String isDamageGoods=map.get("好坏品")!=null?map.get("好坏品")+"":null;
        String skuNo=map.get("货号")!=null?map.get("货号")+"":null;
        Integer tallyNum=map.get("理货数量")!=null?Integer.parseInt(map.get("理货数量")+""):null;
        String customerBatch=map.get("客户批次号")!=null?map.get("客户批次号")+"":null;
        String lpnCode=map.get("托盘号")!=null?map.get("托盘号")+"":null;
        String productionTime=map.get("生产日期")!=null?map.get("生产日期")+"":null;
        String expiredTime=map.get("失效日期")!=null?map.get("失效日期")+"":null;
        if (StringUtil.isBlank(isDamageGoods))
            throw new BadRequestException("好坏品为空");
        if (StringUtil.isBlank(skuNo))
            throw new BadRequestException("货号为空");
        if (tallyNum==null||tallyNum<=0)
            throw new BadRequestException("理货数量不能小于等于0");
        if (StringUtil.isBlank(customerBatch))
            throw new BadRequestException("客户批次号为空");
        if (expiredTime!=null&&expiredTime.length()!=8)
            throw new BadRequestException("失效日期请按照年年年年月月日日格式");
        TrsDetail trsDetail = new TrsDetail();
        if (StringUtil.equals("残品",isDamageGoods)){
            trsDetail.setIsDamaged("1");
        }else if (StringUtil.equals("良品",isDamageGoods)){
            trsDetail.setIsDamaged("0");
        }
        if (StringUtil.equals(lpnCode,"-")){
            lpnCode=spawnLpn();
            CustomLpnRecord record=new CustomLpnRecord();
            record.setPoNo(stockInTolly.getWmsInstock().getPoNo());
            record.setLpnCode(lpnCode);
            record.setCreateUser(SecurityUtils.getCurrentUsername());
            record.setCreateTime(new Timestamp(System.currentTimeMillis()));
            customLpnRecordService.create(record);
        }
        trsDetail.setWarehouseTime(DateUtils.now());
        trsDetail.setSkuNo(skuNo);
        BaseSku baseSku=baseSkuService.queryByOutGoodsNo(skuNo);
        if (baseSku==null)
            throw new BadRequestException("货号"+skuNo+"的商品基本信息不存在");
        if (baseSku.getSaleW()==null||baseSku.getSaleL()==null||baseSku.getSaleH()==null)
            throw new BadRequestException("货号"+skuNo+"长宽高为空");
        if (BigDecimalUtils.eq(BigDecimal.ZERO,baseSku.getSaleW())
                ||BigDecimalUtils.eq(BigDecimal.ZERO,baseSku.getSaleL())
                ||BigDecimalUtils.eq(BigDecimal.ZERO,baseSku.getSaleH()))
            throw new BadRequestException("货号"+skuNo+"长宽高为0");
        trsDetail.setGoodsNo(baseSku.getGoodsNo());
        trsDetail.setWidth(baseSku.getSaleW()+"");
        trsDetail.setHeight(baseSku.getSaleH()+"");
        trsDetail.setLength(baseSku.getSaleL()+"");
        trsDetail.setVolume(baseSku.getSaleVolume()==null?
                baseSku.getSaleW()
                        .multiply(baseSku.getSaleL())
                        .multiply(baseSku.getSaleH()).divide(BigDecimal.ONE,4, RoundingMode.HALF_UP)+"":baseSku.getSaleVolume()+"");
        trsDetail.setGrossWeight(baseSku.getGrossWeight()==null?"0":baseSku.getGrossWeight()+"");
        //匹配通知单的商品，得到商品行和商品编码，如果遍历通知单商品后商品编码和商品行仍然为空，则判为超收
        for (WmsInstockItem item : stockInTolly.getWmsInstock().getItemList()) {
            if (StringUtil.equals(item.getSkuNo(),skuNo)){
                trsDetail.setDocLineId(item.getGoodsLineNo());
                trsDetail.setProductId(item.getProductId());
                break;
            }
        }
        trsDetail.setTransactionQty(tallyNum);
        if (StringUtil.isBlank(trsDetail.getProductId())){
            OutofPlanDetail outofPlanDetail=new OutofPlanDetail();
            outofPlanDetail.setEan13(skuNo);
            outofPlanDetail.setStockInTolly(stockInTolly);
            OutofPlanDetail exist=outofPlanDetailService.queryOne(outofPlanDetail);
            trsDetail.setDocLineId("OOP"+System.currentTimeMillis());
            trsDetail.setProductId(baseSku.getGoodsCode());
            if (exist!=null){
                exist.setReceiveQty((trsDetail.getTransactionQty()+Integer.parseInt(exist.getReceiveQty()))+"");
                outofPlanDetailService.update(exist);
            }else {
                outofPlanDetail.setEan13(skuNo);
                outofPlanDetail.setStockInTolly(stockInTolly);
                outofPlanDetail.setReceiveQty(tallyNum+"");
                outofPlanDetail.setProductName(baseSku.getGoodsName());
                outofPlanDetail.setPicUrl("-");
                outofPlanDetailService.create(outofPlanDetail);
            }
        }
        trsDetail.setCustomerBatchNo(customerBatch);
        trsDetail.setLpn(lpnCode);
        if (StringUtil.isBlank(productionTime)&&StringUtil.isNotBlank(expiredTime)){
            trsDetail.setExpiredTime(expiredTime);
            if (baseSku.getLifecycle()==null)
                throw new BadRequestException("货号"+skuNo+"的保质期没有维护");
            Date expiredDate=DateUtils.parse(expiredTime,"yyyyMMdd");
            //失效日期毫秒数-保质期毫秒数
            Date productionDate=new Date(expiredDate.getTime()-baseSku.getLifecycle()*24L*3600L*1000L);
            trsDetail.setProductionTime(DateUtils.format(productionDate,"yyyyMMdd"));
        }else if (StringUtil.isNotBlank(productionTime)&&StringUtil.isBlank(expiredTime)){
            if (baseSku.getLifecycle()==null)
                throw new BadRequestException("货号"+skuNo+"的保质期没有维护");
            Date productionDate=DateUtils.parse(productionTime,"yyyyMMdd");
            //失效日期毫秒数-保质期毫秒数
            Date expireDate=new Date(productionDate.getTime()+baseSku.getLifecycle()*24L*3600L*1000L);
            trsDetail.setExpiredTime(DateUtils.format(expireDate,"yyyyMMdd"));
            trsDetail.setProductionTime(productionTime);
        }else if (StringUtil.isNotBlank(productionTime)&&StringUtil.isNotBlank(expiredTime)){
            trsDetail.setProductionTime(productionTime);
            trsDetail.setExpiredTime(expiredTime);
        }else
            throw new BadRequestException("生产日期和失效日期不能都为空");

        trsDetail.setStockInTolly(stockInTolly);
        trsDetail.setMerchantId(stockInTolly.getWmsInstock().getMerchantId());
        trsDetail.setVirtualMerchantId(stockInTolly.getWmsInstock().getMerchantId());
        trsDetail.setStockBusinessType(stockInTolly.getWmsInstock().getInOrderType());
        trsDetail.setConsignorId(stockInTolly.getWmsInstock().getMerchantId());
        trsDetail.setFundProviderId(stockInTolly.getWmsInstock().getMerchantId());
        trsDetail.setPoNo(stockInTolly.getWmsInstock().getPoNo());
        trsDetail.setLotNo(stockInTolly.getWmsInstock().getPoNo());
        return trsDetail;
    }
    private String spawnLpn(){
        StringBuilder builder=new StringBuilder();
        for (int i = 0; i < 20; i++) {
            Random random=new Random();
            builder.append(random.nextInt(10));
        }
        return builder.toString();
    }
}
