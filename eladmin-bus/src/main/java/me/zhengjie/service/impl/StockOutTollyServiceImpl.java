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

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.AsnHeaderDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockOutTollyRepository;
import me.zhengjie.service.dto.StockOutTollyDto;
import me.zhengjie.service.dto.StockOutTollyQueryCriteria;
import me.zhengjie.service.mapstruct.StockOutTollyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
* @author wangm
* @date 2021-03-28
**/
@Service
@RequiredArgsConstructor
public class StockOutTollyServiceImpl implements StockOutTollyService {

    private final StockOutTollyRepository stockOutTollyRepository;
    private final StockOutTollyMapper stockOutTollyMapper;
    private final WmsStockLogService wmsStockLogService;

    @Autowired
    private WmsOutstockItemService wmsOutstockItemService;

    @Autowired
    private CartonHeadersService cartonHeadersService;

    @Autowired
    private CartonDetailService cartonDetailService;

    @Autowired
    private AsnHeaderService asnHeaderService;

    @Autowired
    private LoadHeaderService loadHeaderService;

    @Autowired
    private WmsSupport wmsSupport;

    @Override
    public Map<String,Object> queryAll(StockOutTollyQueryCriteria criteria, Pageable pageable){
        Page<StockOutTolly> page = stockOutTollyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(stockOutTollyMapper::toDto));
    }

    @Override
    public List<StockOutTollyDto> queryAll(StockOutTollyQueryCriteria criteria){
        return stockOutTollyMapper.toDto(stockOutTollyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockOutTollyDto findById(Long id) {
        StockOutTolly stockOutTolly = stockOutTollyRepository.findById(id).orElseGet(StockOutTolly::new);
        ValidationUtil.isNull(stockOutTolly.getId(),"StockOutTolly","id",id);
        List<WmsOutstockItem>outstockItemList=wmsOutstockItemService.queryByOutId(stockOutTolly.getWmsOutstock().getId());
        stockOutTolly.getWmsOutstock().setItemList(outstockItemList);
        AsnHeader asnHeader=asnHeaderService.findByOutId(stockOutTolly.getWmsOutstock().getId());
        stockOutTolly.setAsnHeader(asnHeader);
        return stockOutTollyMapper.toDto(stockOutTolly);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockOutTollyDto create(StockOutTolly resources) {
        return stockOutTollyMapper.toDto(stockOutTollyRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockOutTolly resources) {
        StockOutTolly stockOutTolly = stockOutTollyRepository.findById(resources.getId()).orElseGet(StockOutTolly::new);
        ValidationUtil.isNull( stockOutTolly.getId(),"StockOutTolly","id",resources.getId());
        stockOutTolly.copy(resources);
        stockOutTollyRepository.save(stockOutTolly);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockOutTollyRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockOutTollyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockOutTollyDto stockOutTolly : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库单号", stockOutTolly.getWmsOutstock().getOutOrderSn());
            map.put("理货单号", stockOutTolly.getTallyOrderSn());
            map.put("理货品种数", stockOutTolly.getSkuNum());
            map.put("理货总件数", stockOutTolly.getTotalNum());
            map.put("当前理货次数", stockOutTolly.getCurrentNum());
            map.put("理货开始时间", stockOutTolly.getStartTime());
            map.put("理货完成时间", stockOutTolly.getEndTime());
            map.put("出库包装方式 (0:散箱 1:拖)", stockOutTolly.getPackWay());
            map.put("出库包装数量", stockOutTolly.getPackNum());
            map.put("托盘高度", stockOutTolly.getTrayHeight());
            map.put("总重量g", stockOutTolly.getTotalWeight());
            map.put("是否已通知,0否1是", stockOutTolly.getStatus());
            map.put(" reason",  stockOutTolly.getReason());
            map.put("理论重量", stockOutTolly.getGrossWeight());
            map.put("租户编码", stockOutTolly.getTenantCode());
            map.put("仓库id", stockOutTolly.getWarehouseId());
            map.put("申报单号", stockOutTolly.getParentDoCode());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public StockOutTolly findByOutId(Long outOrderSnId) {
        StockOutTolly stockOutTolly=new StockOutTolly();
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setId(outOrderSnId);
        stockOutTolly.setWmsOutstock(wmsOutstock);
        return stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElse(null);
    }
    @Override
    public StockOutTolly findByOutOrderSn(WmsOutstock wmsOutstock) {
        StockOutTolly stockOutTolly=new StockOutTolly();
        stockOutTolly.setWmsOutstock(wmsOutstock);
        return stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElse(null);
    }

    @Override
    public boolean auditTally(String decData,WmsOutstock wmsOutstock, String customersCode) {
        JSONObject data=JSONObject.parseObject(decData);
        String docId=data.getString("docId");
        StockOutTolly stockOutTolly=new StockOutTolly();
        stockOutTolly.setWmsOutstock(wmsOutstock);
        Optional<StockOutTolly> optionalStockOutTolly = stockOutTollyRepository.findOne(Example.of(stockOutTolly));
        stockOutTolly=optionalStockOutTolly.orElse(null);
        if (stockOutTolly==null)
            throw new BadRequestException("单据ID"+docId+"不存在");
        String busType=data.getString("businessType");
        String optType=data.getString("operateType");
        String warehouseId=data.getString("warehouseId");
        if (StringUtil.equals("22",busType)){
            if (StringUtil.equals(optType,"0")){
                stockOutTolly.getWmsOutstock().setEnableStock("1");
            }else {
                stockOutTolly.getWmsOutstock().setOutStatus("31");
                stockOutTolly.setStatus("31");
            }
            stockOutTollyRepository.save(stockOutTolly);
        }else
            throw new BadRequestException("不支持的业务类型");
        return true;
    }

    @Override
    public StockOutTollyDto tallyReg(WmsOutstock wmsOutstock) {
        StockOutTolly stockOutTolly=new StockOutTolly();
        List<WmsOutstockItem>wmsOutstockItemList=wmsOutstockItemService.queryByOutId(wmsOutstock.getId());
        if (CollectionUtil.isEmpty(wmsOutstockItemList))
            throw new BadRequestException("没有通知单明细");
        wmsOutstock.setItemList(wmsOutstockItemList);
        stockOutTolly.setWmsOutstock(wmsOutstock);
        stockOutTolly=stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElse(null);
        if (stockOutTolly==null){
            stockOutTolly=new StockOutTolly();
            wmsOutstock.setOutStatus("05");
            wmsOutstock.setStatusTime(System.currentTimeMillis());
            stockOutTolly.setWmsOutstock(wmsOutstock);
            stockOutTolly.setTallyOrderSn("TL"+System.currentTimeMillis());
            stockOutTolly.setSkuNum(wmsOutstock.getItemList().size());
            stockOutTolly.setTotalNum(0);
            stockOutTolly.setStockOutTollyItems(new ArrayList<>());
            for (WmsOutstockItem wmsOutstockItem : wmsOutstock.getItemList()) {
                stockOutTolly.setTotalNum(stockOutTolly.getTotalNum()+wmsOutstockItem.getQty());
                StockOutTollyItem item=new StockOutTollyItem();
                item.setStockOutTolly(stockOutTolly);
                item.setGoodsNo(wmsOutstockItem.getGoodsNo());
                item.setSkuNo(wmsOutstockItem.getSkuNo());
                item.setBarcode(wmsOutstockItem.getBarCode());
                item.setPreTallyNum(wmsOutstockItem.getQty());
                item.setTrayNo("-");
                item.setTrayMaterial("塑胶");
                item.setTraySize("-");
                item.setTallyNum(0);
                item.setAvaliableNum(0);
                item.setDefectNum(0);
                item.setNumPerBox(0);
                item.setNeedSn(wmsOutstockItem.getNeedSn());
                stockOutTolly.getStockOutTollyItems().add(item);
            }
            stockOutTolly.setCurrentNum(1);
            stockOutTolly.setStartTime(DateUtils.now());
            stockOutTolly.setStatus("05");
            stockOutTolly.setTenantCode(wmsOutstock.getTenantCode());
            stockOutTolly.setWarehouseId(wmsOutstock.getWarehouseId());
            stockOutTolly.setGrossWeight("0");
            stockOutTolly=stockOutTollyRepository.save(stockOutTolly);
            AsnHeader asnHeader=new AsnHeader();
            asnHeader.setWarehouseId(wmsOutstock.getWarehouseId());
            asnHeader.setTenantCode(wmsOutstock.getTenantCode());
            asnHeader.setWmsOutstock(wmsOutstock);
            asnHeaderService.create(asnHeader);
            stockOutTolly.setAsnHeader(asnHeader);
        }else {
            stockOutTolly.setStatus("05");
            stockOutTolly.setStartTime(DateUtils.now());
            stockOutTolly.setEndTime("");
            stockOutTolly.setCurrentNum(stockOutTolly.getCurrentNum()+1);
            stockOutTolly.getWmsOutstock().setOutStatus("05");
            stockOutTolly.getWmsOutstock().setStatusTime(System.currentTimeMillis());
            AsnHeader asnHeader=asnHeaderService.findByOutId(wmsOutstock.getId());
            stockOutTolly.setAsnHeader(asnHeader);
            stockOutTollyRepository.save(stockOutTolly);
        }
        wmsStockLogService.createSucc(wmsOutstock.getOutOrderSn(),"1",wmsOutstock.getOutStatus(),SecurityUtils.getCurrentUsername());
        return stockOutTollyMapper.toDto(stockOutTolly);
    }

    @Override
    public void tallyEnd(Long id) {
        StockOutTolly stockOutTolly=stockOutTollyRepository.findById(id).orElse(null);
        if (stockOutTolly==null)
            throw new BadRequestException("无理货数据");
        if (!StringUtil.equals(stockOutTolly.getWmsOutstock().getOutStatus(),"05"))
            throw new BadRequestException("非法操作");
        if (CollectionUtils.isEmpty(stockOutTolly.getDoLots()))
            throw new BadRequestException("批次信息为空");
        stockOutTolly.setStatus("20");
        stockOutTolly.getWmsOutstock().setOutStatus("11");
        stockOutTolly.getWmsOutstock().setStatusTime(System.currentTimeMillis());
        stockOutTolly.setEndTime(DateUtils.now());
        //=======>>根据托盘明细生成包裹信息
        AsnHeader asnHeader=asnHeaderService.findByOutId(stockOutTolly.getWmsOutstock().getId());
        Map<String, BigDecimal>totalWeightMap=new HashMap<>();
        Map<String, Set<String>>lpnMap=new HashMap<>();
        Map<String,Integer>skuQtyMap=new HashMap<>();
        //这里计算出库所有托盘总重以及以容器材质为组计算各种容器的托盘数量
        for (AsnDetail asnDetail : asnHeader.getAsnDetails()) {
            BigDecimal weight=totalWeightMap.get(asnDetail.getMaterials());
            if (weight==null){
                totalWeightMap.put(asnDetail.getMaterials(),asnDetail.getTotalSkuWeight());
            }else {
                weight=weight.add(asnDetail.getTotalSkuWeight());
                totalWeightMap.put(asnDetail.getMaterials(),weight);
            }
            Set<String>lpnSet=lpnMap.get(asnDetail.getMaterials());
            if (lpnSet==null){
                lpnSet=new TreeSet<>();
                lpnSet.add(asnDetail.getLpn());
                lpnMap.put(asnDetail.getMaterials(),lpnSet);
            }else {
                lpnSet.add(asnDetail.getLpn());
            }
        }
        //这里是生成包裹明细，需要以商品编码为组，计算每个商品编码的出库数量
        for (DoLot doLot : stockOutTolly.getDoLots()) {
            /**
             * Integer qty=skuQtyMap.get(doLot.getProductId());
             * if (qty==null)
             *      skuQtyMap.put(doLot.getProductId(),doLot.getTransactionQty());
             * else
             *      skuQtyMap.put(doLot.getProductId(),qty+doLot.getTransactionQty());
             */
            skuQtyMap.merge(doLot.getProductId(), doLot.getTransactionQty(), Integer::sum);
        }
        //包裹表头

        CartonHeaders cartonHeaders=cartonHeaders=cartonHeadersService.queryByCartonNo(stockOutTolly.getWmsOutstock().getOutOrderSn());
        if (cartonHeaders!=null){
            //如果有包裹信息，则是复理
            cartonHeaders.setIsFirst("0");
        }else {
            cartonHeaders=new CartonHeaders();
            cartonHeaders.setCartonNo(stockOutTolly.getWmsOutstock().getOutOrderSn());
            cartonHeaders.setIsFirst("1");
            cartonHeaders.setStockOutTolly(stockOutTolly);
        }
        cartonHeaders.setPackagedBy(SecurityUtils.getCurrentUsername());
        cartonHeaders.setPackageTime(new Timestamp(System.currentTimeMillis()));
        BigDecimal totalWeight=BigDecimal.ZERO;
        for (Map.Entry<String, BigDecimal> entry : totalWeightMap.entrySet()) {
            totalWeight=totalWeight.add(entry.getValue());
        }
        StringBuilder mat=new StringBuilder();
        int i=0;
        for (Map.Entry<String, Set<String>> entry : lpnMap.entrySet()) {
            mat.append(entry.getKey()).append(":").append(entry.getValue().size());
            i++;
            if (i<lpnMap.size())
                mat.append(",");
        }
        cartonHeaders.setActualGrossWeight(totalWeight);
        cartonHeaders.setMaterials(mat.toString());
        if (CollectionUtil.isNotEmpty(cartonHeaders.getCartonDetails())){
            //复理时要清除已有的包裹明细
            Long[]detailIds=new Long[cartonHeaders.getCartonDetails().size()];
            for (int j = 0; j < cartonHeaders.getCartonDetails().size(); j++) {
                detailIds[j]=cartonHeaders.getCartonDetails().get(j).getId();
            }
            cartonDetailService.deleteAll(detailIds);
        }
        List<CartonDetail>cartonDetailList=new ArrayList<>();
        for (Map.Entry<String, Integer> entry : skuQtyMap.entrySet()) {
            CartonDetail cartonDetail=new CartonDetail();
            cartonDetail.setProductId(entry.getKey());
            cartonDetail.setCartonHeader(cartonHeaders);
            cartonDetail.setNum(entry.getValue());
            cartonDetailList.add(cartonDetail);
        }
        cartonHeaders.setCartonDetails(cartonDetailList);
        if (cartonHeaders.getId()!=null)
            cartonHeadersService.update(cartonHeaders);
        else
            cartonHeadersService.create(cartonHeaders);
        //<<=======包裹信息生成完成
        //=======>>根据托盘明细生成预装载单
        LoadHeader loadHeader=loadHeaderService.findByOutId(stockOutTolly.getWmsOutstock().getId());
        if (loadHeader==null){
            loadHeader=new LoadHeader();
            loadHeader.setWmsOutstock(stockOutTolly.getWmsOutstock());
            loadHeader.setWarehouseId(stockOutTolly.getWarehouseId());
            loadHeader.setVechileNo("浙888888");
            loadHeader.setTenantCode(stockOutTolly.getTenantCode());
            List<LoadDetail>loadDetailList=new ArrayList<>();
            for (Map.Entry<String, Set<String>> entry : lpnMap.entrySet()) {
                for (String lpn : entry.getValue()) {
                    LoadDetail loadDetail=new LoadDetail();
                    loadDetail.setDoNo(stockOutTolly.getWmsOutstock().getSoNo());
                    loadDetail.setLoadHeader(loadHeader);
                    loadDetail.setLpn(lpn);
                    loadDetailList.add(loadDetail);
                }
            }
            loadHeader.setLoadDetails(loadDetailList);
            loadHeader.setLoadNo("load"+ DateUtils.format(new Date(),"yyyyMMddHHmmssSSS"));
            loadHeaderService.create(loadHeader);
            stockOutTolly.getWmsOutstock().setLoadNo(loadHeader.getLoadNo());
        }
        //<<=======预装载单生成完成
        //=======>>根据通知单明细修改理货单明细的商品行号
        List<WmsOutstockItem>itemList=wmsOutstockItemService.queryByOutId(stockOutTolly.getWmsOutstock().getId());
        for (int j = 0; j < itemList.size(); j++) {
            WmsOutstockItem wmsOutstockItem=itemList.get(j);
            for (DoLot doLot : stockOutTolly.getDoLots()) {
                if (StringUtil.equals(doLot.getDocItemId(),(j+1)+"")){
                    doLot.setDocItemId(wmsOutstockItem.getGoodsLineNo());
                }
            }
            //修改富勒的
            JSONObject object=new JSONObject();
            object.put("soNo",stockOutTolly.getWmsOutstock().getFluxOrderNo());
            object.put("oldLineNo",j+1);
            object.put("newLineNo",wmsOutstockItem.getGoodsLineNo());
            wmsSupport.updateDocGoodLineNo(object.toString());
        }
        //<<=======商品行号修改完成
        update(stockOutTolly);
        wmsStockLogService.createSucc(stockOutTolly.getWmsOutstock().getOutOrderSn(),"1",stockOutTolly.getWmsOutstock().getOutStatus(),SecurityUtils.getCurrentUsername());
    }

    @Override
    public StockOutTollyDto queryByOutIdDto(Long outId) {
        StockOutTolly stockOutTolly=new StockOutTolly();
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setId(outId);
        stockOutTolly.setWmsOutstock(wmsOutstock);
        return stockOutTollyMapper.toDto(stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElseGet(StockOutTolly::new));
    }

    @Override
    public void updateStatusByOutId(Long outId, String targetStatus) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setId(outId);
        StockOutTolly stockOutTolly=new StockOutTolly();
        stockOutTolly.setWmsOutstock(wmsOutstock);
        stockOutTolly=stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElse(null);
        if (stockOutTolly==null){
            throw new BadRequestException("出库通知单不存在");
        }
        stockOutTolly.setStatus(targetStatus);
        stockOutTolly.getWmsOutstock().setOutStatus(targetStatus);
        stockOutTolly.getWmsOutstock().setStatusTime(System.currentTimeMillis());
        update(stockOutTolly);
        wmsStockLogService.createSucc(stockOutTolly.getWmsOutstock().getOutOrderSn(),"1",stockOutTolly.getWmsOutstock().getOutStatus(),SecurityUtils.getCurrentUsername());
    }

    @Override
    public StockOutTolly queryById(Long id) {
        return stockOutTollyRepository.findById(id).orElse(null);
    }

    @Override
    public StockOutTolly queryByOutId(Long outId) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setId(outId);
        StockOutTolly stockOutTolly=new StockOutTolly();
        stockOutTolly.setWmsOutstock(wmsOutstock);
        return stockOutTollyRepository.findOne(Example.of(stockOutTolly)).orElse(null);
    }

    @Override
    public Map<String, Object> checkDiff(Long id) {
        StockOutTolly stockOutTolly=queryById(id);
        AsnHeader asnHeader = asnHeaderService.findByOutId(stockOutTolly.getWmsOutstock().getId());
        List<WmsOutstockItem>wmsOutstockItemList = wmsOutstockItemService.queryByOutId(stockOutTolly.getWmsOutstock().getId());
        if (CollectionUtils.isEmpty(asnHeader.getAsnDetails()))
            throw new BadRequestException("请先填充理货数据");
        Map<String,Integer>preSumMap = new HashMap<>();
        Map<String,String>productIdGoodsNoMap = new HashMap<>();
        for (WmsOutstockItem item : wmsOutstockItemList) {
            productIdGoodsNoMap.put(item.getGoodsLineNo(),item.getGoodsNo());
            Integer sum = preSumMap.get(item.getGoodsLineNo());
            if (sum == null)
                sum = 0;
            sum += item.getQty();
            preSumMap.put(item.getGoodsLineNo(),sum);
        }
        Map<String,Integer>tallySumMap = new HashMap<>();
        for (AsnDetail asnDetail : asnHeader.getAsnDetails()) {
            Integer sum = tallySumMap.get(asnDetail.getDocItemId());
            if (sum == null)
                sum = 0;
            sum += asnDetail.getQty();
            tallySumMap.put(asnDetail.getDocItemId(),sum);
        }
        Map<String,Object>retData = new HashMap<>();
        StringBuilder builder=new StringBuilder();
        boolean isNonDiff = true;
        for (Map.Entry<String, Integer> entry : preSumMap.entrySet()) {
            Integer tallySum = tallySumMap.getOrDefault(entry.getKey(),0);
            if (!tallySum.equals(entry.getValue())){
                isNonDiff = false;
                String result = (entry.getValue() - tallySum > 0?"差异":"超收") +  (Math.abs(entry.getValue()-tallySum));
                builder.append(productIdGoodsNoMap.get(entry.getKey())).append(":").append(result).append("<br/>");
            }
        }
        retData.put("isNonDiff",isNonDiff);
        retData.put("remark",builder);
        return retData;
    }
}
