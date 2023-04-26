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
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.FluxUtils;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WmsInstockRepository;
import me.zhengjie.service.dto.WmsInstockDto;
import me.zhengjie.service.dto.WmsInstockQueryCriteria;
import me.zhengjie.service.mapstruct.WmsInstockMapper;
import me.zhengjie.utils.constant.MsgType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2020-12-08
**/
@Service
@RequiredArgsConstructor
@Slf4j
public class WmsInstockServiceImpl implements WmsInstockService {

    private final WmsInstockRepository wmsInstockRepository;
    private final WmsInstockMapper wmsInstockMapper;
    private final WmsStockLogService wmsStockLogService;
    @Autowired
    private WmsInstockItemService wmsInstockItemService;
    private final ThirdOrderLogService thirdOrderLogService;
    @Autowired
    private StockInTollyService stockInTollyService;
    private final SkuMapService skuMapService;
    private final ShopInfoService shopInfoService;
    private final ClearCompanyInfoService clearCompanyInfoService;
    @Autowired
    private StockAttrService stockAttrService;
    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Autowired
    private TrsDetailService trsDetailService;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Override
    public Map<String,Object> queryAll(WmsInstockQueryCriteria criteria, Pageable pageable){
        Page<WmsInstock> page = wmsInstockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wmsInstockMapper::toDto));
    }

    @Override
    public List<WmsInstockDto> queryAll(WmsInstockQueryCriteria criteria){
        return wmsInstockMapper.toDto(wmsInstockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WmsInstockDto findById(Long id) {
        WmsInstock wmsInstock = wmsInstockRepository.findById(id).orElseGet(WmsInstock::new);
        ValidationUtil.isNull(wmsInstock.getId(),"WmsInstock","id",id);
        return wmsInstockMapper.toDto(wmsInstock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsInstockDto create(WmsInstock resources) {
        if (StringUtil.isEmpty(resources.getWarehouseId())){
            resources.setWarehouseId("3302461510");
        }
        resources.setCreateCustomer(SecurityUtils.getCurrentUserId()+"");
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setInStatus("-00");//该状态是只有主表，子表还没有数据进行添加
        resources.setStatusTime(System.currentTimeMillis());
        resources.setSyncComplete(true);
        return wmsInstockMapper.toDto(wmsInstockRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(WmsInstock resources) {
        WmsInstock wmsInstock = wmsInstockRepository.findById(resources.getId()).orElseGet(WmsInstock::new);
        ValidationUtil.isNull( wmsInstock.getId(),"WmsInstock","id",resources.getId());
        wmsInstock.copy(resources);
        wmsInstockRepository.save(wmsInstock);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            wmsInstockRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WmsInstockDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WmsInstockDto wmsInstock : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("入库单号，商家自行生成的唯一单号", wmsInstock.getInOrderSn());
            map.put("单据类型，0:采购入库 1:调拨入库 2:销退入库", wmsInstock.getInOrderType());
            map.put("原单号(销退入库需填写)", wmsInstock.getOriginalNo());
            map.put("报关单号", wmsInstock.getDeclareNo());
            map.put("报检单号", wmsInstock.getInspectNo());
            map.put("预期到货时间(yyyy-MM-dd HH:mm:ss)", wmsInstock.getExpectArriveTime());
            map.put("备注", wmsInstock.getRemark());
            map.put("理货维度：0:件1:箱2:拖。暂时默认件", wmsInstock.getTallyWay());
            map.put("创建时间", wmsInstock.getCreateTime());
            map.put("创建商家", wmsInstock.getCreateCustomer());
            map.put("入库通知单状态", wmsInstock.getInStatus());
            map.put("状态最后变化时间戳", wmsInstock.getStatusTime());
            map.put("收货完成时间", wmsInstock.getGoodsUpper());
            map.put("实际到货时间", wmsInstock.getActualArriveTime());
            map.put("同步流程是否完成", wmsInstock.getSyncComplete());
            map.put("附件链接", wmsInstock.getFileLinks());
            map.put(" channel",  wmsInstock.getChannel());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void inStockPushFlux() {
        List<WmsInstock> wmsInstockList = queryByStatus("02");
        if (CollectionUtils.isNotEmpty(wmsInstockList)) {
            for (WmsInstock wmsInstock : wmsInstockList) {
                ThirdOrderLog thirdOrderLog=new ThirdOrderLog();
                try {
                    ShopInfo shopInfo=shopInfoService.findById(wmsInstock.getShopId());
                    ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
                    shopInfo.setClearCompanyInfo(clearCompanyInfo);
                    wmsInstock.setShopInfo(shopInfo);
                    List<WmsInstockItem>wmsInstockItemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
                    wmsInstock.setItemList(wmsInstockItemList);
                    FluxUtils.pushWmsInstock(wmsInstock,thirdOrderLog);
                } catch (Exception e) {
                    WmsStockLog wmsStockLog = new WmsStockLog();
                    wmsStockLog.setStatus("05F");
                    wmsStockLog.setOrderSn(wmsInstock.getInOrderSn());
                    wmsStockLog.setIsSuccess("0");
                    wmsStockLog.setType("0");
                    wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
                    wmsStockLog.setOperationUser("SYS");
                    wmsStockLog.setRemark(e.getMessage());
                    wmsInstock.setInStatus("05F");
                    wmsInstock.setStatusTime(System.currentTimeMillis());
                    update(wmsInstock);
                    wmsStockLogService.create(wmsStockLog);
                    thirdOrderLogService.create(thirdOrderLog);
                    continue;
                }
                wmsInstock.setInStatus("05");
                wmsInstock.setStatusTime(System.currentTimeMillis());
                update(wmsInstock);
                WmsStockLog wmsStockLog = new WmsStockLog();
                wmsStockLog.setStatus("05");
                wmsStockLog.setOrderSn(wmsInstock.getInOrderSn());
                wmsStockLog.setIsSuccess("1");
                wmsStockLog.setType("0");
                wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
                wmsStockLog.setOperationUser("SYS");
                wmsStockLog.setRemark("推送富勒成功");
                wmsStockLogService.create(wmsStockLog);
                thirdOrderLogService.create(thirdOrderLog);
            }
        }
    }

    @Override
    public List<WmsInstock> queryByStatus(String status) {
        WmsInstock wmsInstockConditions = new WmsInstock();
        wmsInstockConditions.setInStatus(status);
        return wmsInstockRepository.findAll(Example.of(wmsInstockConditions));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addAsn(String decData, String customersCode) {
        log.debug("卓志入库单下发请求报文:"+decData);
        WmsInstock data = JSONObject.parseObject(decData, WmsInstock.class);
        if (data != null) {
            if (StringUtil.isEmpty(data.getInOrderSn()))
                throw new BadRequestException("inOrderSn为空");
            if (StringUtil.isEmpty(data.getInOrderType()))
                throw new BadRequestException("inOrderType为空");
            if (!(StringUtil.equals(data.getInOrderType(), "0")
                    || StringUtil.equals(data.getInOrderType(), "1")
                    || StringUtil.equals(data.getInOrderType(), "2")
                    || StringUtil.equals(data.getInOrderType(), "4")))
                throw new BadRequestException("inOrderType仅支持0,1,2,4");
            if (StringUtil.isEmpty(data.getExpectArriveTime())&&!data.getInOrderType().equals("2"))
                throw new BadRequestException("expectArriveTime为空");
            if (CollectionUtils.isEmpty(data.getItemList()))
                throw new BadRequestException("skuDetails为空");
            if (StringUtil.equals(data.getInOrderType(), "2") && StringUtil.isEmpty(data.getOriginalNo()))
                throw new BadRequestException("入库单为销退入库时originalNo不能为空");
            if (StringUtil.isEmpty(data.getMerchantId())){
                throw new BadRequestException("merchantId为空");
            }
            if (StringUtil.isEmpty(data.getWarehouseId())){
                throw new BadRequestException("warehouseId为空");
            }
            if (StringUtil.isEmpty(data.getTenantCode())){
                throw new BadRequestException("tenantCode为空");
            }
            WmsInstock wmsInstock = findByOrderSn(data.getInOrderSn());
            if (wmsInstock != null) {
                throw new BadRequestException("已有相同的入库通知单号了");
            }
            data.setCreateCustomer(customersCode);
            data.setCreateTime(new Timestamp(System.currentTimeMillis()));
            data.setInStatus("00");
            data.setStatusTime(System.currentTimeMillis());
            data.setSyncComplete(false);
            ShopInfo shopInfo=shopInfoService.queryByShopCode(data.getMerchantId());
            if (shopInfo==null)
                throw new BadRequestException("merchantId找不到该商家");
            data.setShopId(shopInfo.getId());
            wmsInstockRepository.save(data);
            List<WmsInstockItem> itemList = data.getItemList();
            int i = 0;
            for (WmsInstockItem item : itemList) {
                i++;
                if (StringUtil.isEmpty(item.getSkuNo()))
                    throw new BadRequestException("第" + i + "个的skuNo为空");
                if (StringUtil.isEmpty(item.getGoodsLineNo()))
                    throw new BadRequestException("第" + i + "个的goodsLineNo为空");
                if (StringUtil.isEmpty(item.getBarCode()))
                    throw new BadRequestException("第" + i + "个的barCode为空");
                if (StringUtil.isEmpty(item.getGoodsName()))
                    throw new BadRequestException("第" + i + "个的goodsName为空");
                if (item.getQty() == null)
                    throw new BadRequestException("第" + i + "个的qty为空");
                if (StringUtil.isEmpty(item.getProductId()))
                    throw new BadRequestException("第" + i + "个的productId为空");
                if (item.getNeedSn()==null){
                    item.setNeedSn(0);
                }
                item.setInId(data.getId());
                BaseSku baseSku=baseSkuService.queryByCode(item.getProductId());
                if (baseSku==null)
                    throw new BadRequestException("找不到对应的productId,仓库需要先得到productId与海关货号的关系");
                item.setGoodsNo(baseSku.getGoodsNo());
                item.setBarCode(baseSku.getBarCode());
                wmsInstockItemService.create(item);
            }
            WmsStockLog wmsStockLog = new WmsStockLog();
            wmsStockLog.setOrderSn(data.getInOrderSn());
            wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
            wmsStockLog.setOperationUser(customersCode);
            wmsStockLog.setRequest(decData);
            wmsStockLog.setStatusText("入库通知单下发");
            wmsStockLog.setStatus("00");
            wmsStockLog.setRemark("操作成功");
            wmsStockLog.setType("0");
            wmsStockLog.setIsSuccess("1");
            wmsStockLogService.create(wmsStockLog);
            try {
                cbOrderProducer.send(MsgType.ZHUOZHI_IN_SYNCSTATUS,data.getId().toString(),data.getPoNo());
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        throw new BadRequestException("data解析失败");
    }

    @Override
    public boolean cancel(String orderSn, String customersCode,String tenantCode, String reason) {
        WmsInstock wmsInstock = findByOrderSn(orderSn,tenantCode);
        if (wmsInstock == null) {
            throw new BadRequestException("入库通知单不存在");
        }if (StringUtil.equals(wmsInstock.getInStatus(),"-1")){
            //不在创建、已推送富勒、到货状态的不支持取消，需联系仓库进行手动取消
            return true;
        }

        wmsInstock.setInStatus("01");//已取消
        wmsInstock.setStatusTime(System.currentTimeMillis());
        List<StockInTolly>stockInTollyList=stockInTollyService.queryByOrderSn(wmsInstock);
        if (CollectionUtils.isNotEmpty(stockInTollyList)){
            for (StockInTolly stockInTolly : stockInTollyList) {
                if (!StringUtil.equals("35",stockInTolly.getStatus())){
                    stockInTolly.setStatus("01");
                    stockInTollyService.update(stockInTolly);
                }
            }
        }
        WmsStockLog wmsStockLog=new WmsStockLog();
        wmsStockLog.setOrderSn(wmsInstock.getInOrderSn());
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setOperationUser(customersCode);
        wmsStockLog.setStatusText("入库通知单取消");
        wmsStockLog.setStatus("01");
        wmsStockLog.setRemark("操作成功");
        wmsStockLog.setType("0");
        wmsStockLog.setIsSuccess("1");
        wmsStockLogService.create(wmsStockLog);
        wmsInstockRepository.save(wmsInstock);
        return true;
    }

    @Override
    public WmsInstock findByOrderSn(String inOrderSn) {
        WmsInstock wmsInstock=new WmsInstock();
        wmsInstock.setInOrderSn(inOrderSn);
        Optional<WmsInstock> wmsInOpt = wmsInstockRepository.findOne(Example.of(wmsInstock));
        /*if (wmsInOpt.isPresent())
            return wmsInOpt.get();
        return null;
        等同于wmsInOpt.orElse(null)
        */
        return wmsInOpt.orElse(null);
    }

    @Override
    public WmsInstockDto queryByIdDto(Long id) {
        return wmsInstockMapper.toDto(wmsInstockRepository.findById(id).orElseGet(WmsInstock::new));
    }

    public WmsInstock queryById(Long id){
        return wmsInstockRepository.findById(id).orElse(null);
    }

    @Override
    public void uploadArriveTime(WmsInstock wmsInstock) {
        wmsInstock=wmsInstockRepository.findById(wmsInstock.getId()).orElse(null);
        if (wmsInstock==null)
            throw new BadRequestException("数据不存在");
        wmsInstock.setInStatus("10");
        wmsInstock.setStatusTime(System.currentTimeMillis());
        update(wmsInstock);
        wmsStockLogService.createSucc(wmsInstock.getInOrderSn(),"0",wmsInstock.getInStatus(),SecurityUtils.getCurrentUsername());
        syncStatus(new Long[]{wmsInstock.getId()});
    }

    @Override
    public void updateStatus(WmsInstock wmsInstock) {
        String targetStatus=wmsInstock.getInStatus();
        wmsInstock=wmsInstockRepository.findById(wmsInstock.getId()).orElse(null);
        if (wmsInstock==null)
            throw new BadRequestException("入库通知单不存在");
            //验收
        StockInTolly stockInTolly=stockInTollyService.queryByInId(wmsInstock.getId());
        if (stockInTolly==null)throw new BadRequestException("理货单据不存在");
        List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
        for (WmsInstockItem item : itemList) {
            item.setWmsInstock(wmsInstock);
        }
        List<TrsDetail>transDetails=trsDetailService.queryAllByInStockId(stockInTolly.getId());
        stockInTolly.setTrsDetailList(transDetails);
        wmsInstock.setItemList(itemList);
        stockInTolly.setWmsInstock(wmsInstock);
        stockInTolly.setAsnStatus(targetStatus);
        if (StringUtil.equals("50",targetStatus)){
            stockInTolly.setRecheckBy(SecurityUtils.getCurrentUsername());
            stockInTolly.setRecheckTime(DateUtils.now());
            List<Map<String,String>>mapList=new ArrayList<>();
            for (TrsDetail trsDetail : stockInTolly.getTrsDetailList()) {
                if (StringUtil.isNotBlank(trsDetail.getBatchNo()))
                    continue;
                if (CollectionUtil.isNotEmpty(mapList)){
                    //准备迭代已经获取过的批次号，避免过多的重复请求
                    for (Map<String, String> map : mapList) {
                        String sku=map.get("sku");
                        String expireDate=map.get("expireDate");
                        String inOrderSn=map.get("inOrderSn");
                        String avaOrDef=map.get("avaOrDef");
                        if (StringUtil.equals(sku,trsDetail.getSkuNo())
                                && StringUtil.equals(expireDate,trsDetail.getExpiredTime())
                                && StringUtil.equals(inOrderSn,trsDetail.getPoNo())
                                && StringUtil.equals(avaOrDef,trsDetail.getIsDamaged())){
                            String lotNum=map.get("lotNum");
                            trsDetail.setBatchNo(lotNum);
                            break;
                        }
                    }
                }
                if (StringUtil.isNotBlank(trsDetail.getBatchNo()))
                    continue;//如果经历过上面的迭代行为还没有获取到批次号，则说明需要请求数据
                CustomerKeyDto customerKeyDto=customerKeyService.findByCustCode("3302461510");
                FluxUtils.getWmsBatchNo(trsDetail,customerKeyDto);
                if (StringUtil.isBlank(trsDetail.getBatchNo()))
                    throw new BadRequestException("富勒并没有该sku的批次号");
                Map<String,String>map=new HashMap<>();
                map.put("sku",trsDetail.getSkuNo());
                map.put("expireDate",trsDetail.getExpiredTime());
                map.put("inOrderSn",trsDetail.getPoNo());
                map.put("avaOrDef",trsDetail.getIsDamaged());
                map.put("lotNum",trsDetail.getBatchNo());
                mapList.add(map);
            }
        }else if (StringUtil.equals("60",targetStatus)){
            stockInTolly.setPutawayBy(SecurityUtils.getCurrentUsername());
            stockInTolly.setPutawayTime(DateUtils.now());
        }
        stockInTollyService.update(stockInTolly);//存储批次号
        WmsStockLog wmsStockLog=new WmsStockLog();
        zhuozhiSupport.stockInTolly(stockInTolly,wmsStockLog);
        if (StringUtil.equals("0",wmsStockLog.getIsSuccess())){
            try {
                wmsStockLogService.create(wmsStockLog);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            return;
        }
        /**记录库存相关属性*/
        /** 由定时器获取最新批次号记录*/
        /*if (StringUtil.equals(targetStatus,"60")){
            for (TrsDetail detail : stockInTolly.getTrsDetailList()) {
                StockAttr stockAttr=new StockAttr();
                stockAttr.setBookNo(wmsInstock.getAccountNumber());
                stockAttr.setInOrderSn(wmsInstock.getPoNo());
                stockAttr.setSubType("0102");
                stockAttr.setSuperviseCode(wmsInstock.getSuperviseCode());
                stockAttr.setWmsBatchNo(detail.getBatchNo());
                stockAttr.setCustomerBatchNo(detail.getCustomerBatchNo());
                if (!stockAttrService.exists(stockAttr)){
                    stockAttr.setUpdateTime(new Timestamp(System.currentTimeMillis()));
                    stockAttrService.create(stockAttr);
                }
            }
        }*/
        wmsStockLog.setStatus(targetStatus);
        stockInTolly.setStatus(targetStatus);
        wmsInstock.setStatusTime(System.currentTimeMillis());
        wmsInstock.setInStatus(targetStatus);
        stockInTollyService.update(stockInTolly);
        update(wmsInstock);
        try {
            wmsStockLogService.create(wmsStockLog);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        wmsStockLog=new WmsStockLog();
        zhuozhiSupport.noticeDocStatus(wmsInstock,null,wmsStockLog);
    }

    @Override
    public void syncStatus(Long[] ids) {
        for (Long id : ids) {
            WmsInstock wmsInstock=wmsInstockRepository.findById(id).orElse(null);
            if (wmsInstock==null)
                continue;
            WmsStockLog wmsStockLog=new WmsStockLog();
            try {
                zhuozhiSupport.noticeDocStatus(wmsInstock,null,wmsStockLog);
            }catch (Exception e){
                e.printStackTrace();
                try {
                    wmsStockLogService.create(wmsStockLog);
                }catch (Exception ex){
                    ex.printStackTrace();
                    log.error(e.getMessage(),e);
                }
                continue;
            }
            if (StringUtil.equals(wmsInstock.getInStatus(),"00")) {
                wmsInstock.setInStatus("02");//已接单
                wmsStockLog.setStatus("02");
            }
            else if (StringUtil.equals(wmsInstock.getInStatus(),"10")) {
                wmsInstock.setInStatus("11");//已回传到货
                wmsStockLog.setStatus("11");
            }
            wmsInstockRepository.save(wmsInstock);
            try {
                wmsStockLogService.create(wmsStockLog);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public void getAsnNo() {
        List<WmsInstock>wmsInstockList=wmsInstockRepository.getAllEmptyAsnNo();
        if (CollectionUtil.isEmpty(wmsInstockList))
            return;
        for (WmsInstock wmsInstock : wmsInstockList) {
            ShopInfo shopInfo=shopInfoService.findById(wmsInstock.getShopId());
            ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
            shopInfo.setClearCompanyInfo(clearCompanyInfo);
            CustomerKeyDto customerKeyDto=customerKeyService.findByCustCode(clearCompanyInfo.getCustomsCode());
            String asnNo=FluxUtils.getFluxAsnNo(wmsInstock.getPoNo(),customerKeyDto);
            if (!StringUtil.equals("-",asnNo)){
                wmsInstock.setAsnNo(asnNo);
                update(wmsInstock);
            }
        }
    }

    /**
     * 定时器任务
     */
    @Override
    public void inTallyPush() {
        List<WmsInstock>wmsInstockList=queryByStatus("20");
        if (CollectionUtil.isEmpty(wmsInstockList))
            return;
        for (WmsInstock wmsInstock : wmsInstockList) {
            stockInTollyService.tallyPush(new Long[]{wmsInstock.getId()});
        }
    }

    @Override
    public WmsInstock queryByPoNo(String poNo) {
        WmsInstock wmsInstock=new WmsInstock();
        wmsInstock.setPoNo(poNo);
        return wmsInstockRepository.findOne(Example.of(wmsInstock)).orElse(null);
    }

    @Override
    public void checkInTally() {
        List<WmsInstock> wmsInstockList=queryByStatus("40");
        if (CollectionUtil.isEmpty(wmsInstockList))
            return;
        for (WmsInstock wmsInstock : wmsInstockList) {
            try {
                StockInTolly stockInTolly=stockInTollyService.queryByInId(wmsInstock.getId());
                if (stockInTolly==null) continue;
                List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
                for (WmsInstockItem item : itemList) {
                    item.setWmsInstock(wmsInstock);
                }
                List<TrsDetail>transDetails=trsDetailService.queryAllByInStockId(stockInTolly.getId());
                stockInTolly.setTrsDetailList(transDetails);
                wmsInstock.setItemList(itemList);
                stockInTolly.setWmsInstock(wmsInstock);
                stockInTolly.setAsnStatus("50");
                stockInTolly.setRecheckBy("SYSTEM TIMER");
                stockInTolly.setRecheckTime(DateUtils.now());
                List<Map<String,String>>mapList=new ArrayList<>();
                for (TrsDetail trsDetail : stockInTolly.getTrsDetailList()) {
                    if (StringUtil.isNotBlank(trsDetail.getBatchNo()))
                        continue;
                    if (CollectionUtil.isNotEmpty(mapList)){
                        //准备迭代已经获取过的批次号，避免过多的重复请求
                        for (Map<String, String> map : mapList) {
                            String sku=map.get("sku");
                            String expireDate=map.get("expireDate");
                            String inOrderSn=map.get("inOrderSn");
                            String avaOrDef=map.get("avaOrDef");
                            if (StringUtil.equals(sku,trsDetail.getSkuNo())
                                    && StringUtil.equals(expireDate,trsDetail.getExpiredTime())
                                    && StringUtil.equals(inOrderSn,trsDetail.getPoNo())
                                    && StringUtil.equals(avaOrDef,trsDetail.getIsDamaged())){
                                String lotNum=map.get("lotNum");
                                trsDetail.setBatchNo(lotNum);
                                break;
                            }
                        }
                    }
                    if (StringUtil.isNotBlank(trsDetail.getBatchNo()))
                        continue;//如果经历过上面的迭代行为还没有获取到批次号，则说明需要请求数据
                    CustomerKeyDto customerKeyDto=customerKeyService.findByCustCode("3302461510");
                    FluxUtils.getWmsBatchNo(trsDetail,customerKeyDto);
                    if (StringUtil.isBlank(trsDetail.getBatchNo()))
                        throw new BadRequestException("富勒并没有该sku的批次号");
                    Map<String,String>map=new HashMap<>();
                    map.put("sku",trsDetail.getSkuNo());
                    map.put("expireDate",trsDetail.getExpiredTime());
                    map.put("inOrderSn",trsDetail.getPoNo());
                    map.put("avaOrDef",trsDetail.getIsDamaged());
                    map.put("lotNum",trsDetail.getBatchNo());
                    mapList.add(map);
                }
                stockInTollyService.update(stockInTolly);//存储批次号
                WmsStockLog wmsStockLog=new WmsStockLog();
                zhuozhiSupport.stockInTolly(stockInTolly,wmsStockLog);
                if (StringUtil.equals("0",wmsStockLog.getIsSuccess())){
                    try {
                        wmsStockLogService.create(wmsStockLog);
                    }catch (Exception e){
                        log.error(e.getMessage(),e);
                    }
                    return;
                }
                /**记录库存相关属性*/
                /** 由定时器获取最新批次号记录*/
                wmsStockLog.setStatus("50");
                stockInTolly.setStatus("50");
                wmsInstock.setStatusTime(System.currentTimeMillis());
                wmsInstock.setInStatus("50");
                stockInTollyService.update(stockInTolly);
                update(wmsInstock);
                try {
                    wmsStockLogService.create(wmsStockLog);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                wmsStockLog=new WmsStockLog();
                zhuozhiSupport.noticeDocStatus(wmsInstock,null,wmsStockLog);
            }catch (Exception e){
                log.error("定时器自动验收入库失败",e);
            }
        }
    }

    @Override
    public void inStock() {
        List<WmsInstock>wmsInStockList=queryByStatus("50");
        if (CollectionUtil.isEmpty(wmsInStockList))
            return;
        for (WmsInstock wmsInstock : wmsInStockList) {
            StockInTolly stockInTolly=stockInTollyService.queryByInId(wmsInstock.getId());
            if (stockInTolly==null)throw new BadRequestException("理货单据不存在");
            List<WmsInstockItem>itemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
            for (WmsInstockItem item : itemList) {
                item.setWmsInstock(wmsInstock);
            }
            List<TrsDetail>transDetails=trsDetailService.queryAllByInStockId(stockInTolly.getId());
            stockInTolly.setTrsDetailList(transDetails);
            wmsInstock.setItemList(itemList);
            stockInTolly.setWmsInstock(wmsInstock);
            stockInTolly.setAsnStatus("60");
            stockInTolly.setPutawayBy("SYSTEM TIMER");
            stockInTolly.setPutawayTime(DateUtils.now());
            stockInTollyService.update(stockInTolly);
            WmsStockLog wmsStockLog=new WmsStockLog();
            zhuozhiSupport.stockInTolly(stockInTolly,wmsStockLog);
            if (StringUtil.equals("0",wmsStockLog.getIsSuccess())){
                try {
                    wmsStockLogService.create(wmsStockLog);
                }catch (Exception e){
                    log.error(e.getMessage(),e);
                }
                return;
            }
            /**记录库存相关属性*/
            /** 由定时器获取最新批次号记录*/
            wmsStockLog.setStatus("60");
            stockInTolly.setStatus("60");
            wmsInstock.setStatusTime(System.currentTimeMillis());
            wmsInstock.setInStatus("60");
            stockInTollyService.update(stockInTolly);
            update(wmsInstock);
            try {
                wmsStockLogService.create(wmsStockLog);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            wmsStockLog=new WmsStockLog();
            zhuozhiSupport.noticeDocStatus(wmsInstock,null,wmsStockLog);
        }
    }

    private WmsInstock findByOrderSn(String inOrderSn,String tenantCode) {
        WmsInstock wmsInstock=new WmsInstock();
        wmsInstock.setInOrderSn(inOrderSn);
        Optional<WmsInstock> wmsInOpt = wmsInstockRepository.findOne(Example.of(wmsInstock));
        /*if (wmsInOpt.isPresent())
            return wmsInOpt.get();
        return null;
        等同于wmsInOpt.orElse(null)
        */
        return wmsInOpt.orElse(null);
    }
}
