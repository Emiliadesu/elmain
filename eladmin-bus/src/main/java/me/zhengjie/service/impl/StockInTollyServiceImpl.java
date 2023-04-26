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
import me.zhengjie.service.*;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.StockInTollyRepository;
import me.zhengjie.service.dto.StockInTollyDto;
import me.zhengjie.service.dto.StockInTollyQueryCriteria;
import me.zhengjie.service.mapstruct.StockInTollyMapper;
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
 * @author wangm
 * @website https://el-admin.vip
 * @description 服务实现
 * @date 2021-03-23
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class StockInTollyServiceImpl implements StockInTollyService {

    private final StockInTollyRepository stockInTollyRepository;
    private final StockInTollyMapper stockInTollyMapper;
    @Autowired
    private WmsInstockService wmsInstockService;
    @Autowired
    private WmsInstockItemService wmsInstockItemService;
    @Autowired
    private StockInTollyItemService stockInTollyItemService;
    @Autowired
    private WmsStockLogService wmsStockLogService;
    @Autowired
    private TrsDetailService trsDetailService;

    @Autowired
    private OutofPlanDetailService outofPlanDetailService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Override
    public Map<String, Object> queryAll(StockInTollyQueryCriteria criteria, Pageable pageable) {
        Page<StockInTolly> page = stockInTollyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(stockInTollyMapper::toDto));
    }

    @Override
    public List<StockInTollyDto> queryAll(StockInTollyQueryCriteria criteria) {
        return stockInTollyMapper.toDto(stockInTollyRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder)));
    }

    @Override
    @Transactional
    public StockInTollyDto findByIdDto(Long id) {
        StockInTolly stockInTolly = stockInTollyRepository.findById(id).orElseGet(StockInTolly::new);
        ValidationUtil.isNull(stockInTolly.getId(), "StockInTolly", "id", id);
        WmsInstock wmsInstock = wmsInstockService.queryById(stockInTolly.getInOrderId());
        List<WmsInstockItem> itemList = wmsInstockItemService.queryAllByInId(wmsInstock.getId());
        for (WmsInstockItem item : itemList) {
            item.setWmsInstock(wmsInstock);
        }
        wmsInstock.setItemList(itemList);
        stockInTolly.setWmsInstock(wmsInstock);
        return stockInTollyMapper.toDto(stockInTolly);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public StockInTollyDto create(StockInTolly resources) {
        return stockInTollyMapper.toDto(stockInTollyRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(StockInTolly resources) {
        StockInTolly stockInTolly = stockInTollyRepository.findById(resources.getId()).orElseGet(StockInTolly::new);
        ValidationUtil.isNull(stockInTolly.getId(), "StockInTolly", "id", resources.getId());
        stockInTolly.copy(resources);
        stockInTollyRepository.save(stockInTolly);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            stockInTollyRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<StockInTollyDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (StockInTollyDto stockInTolly : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            //map.put("入库单号id", stockInTolly.getWmsInstock().getInOrderSn());
            map.put("理货单号", stockInTolly.getTallyOrderSn());
            map.put("理货品种数", stockInTolly.getSkuNum());
            map.put("理货总件数", stockInTolly.getTotalNum());
            map.put("当前理货次数", stockInTolly.getCurrentNum());
            map.put("理货开始时间", stockInTolly.getStartTime());
            map.put("理货完成时间", stockInTolly.getEndTime());
            map.put("理货单状态", stockInTolly.getStatus());
            map.put(" fileName", stockInTolly.getFileName());
            map.put(" reason", stockInTolly.getReason());
            map.put("租户编码", stockInTolly.getTenantCode());
            map.put("仓库编码", stockInTolly.getWarehouseId());
            map.put("ASN单状态", stockInTolly.getAsnStatus());
            map.put("ASN单号", stockInTolly.getAsnNo());
            map.put("审核时间", stockInTolly.getShpdDate());
            map.put("验收时间", stockInTolly.getRecheckTime());
            map.put("收货时间", stockInTolly.getFinishReceiptTime());
            map.put("审核人", stockInTolly.getVerifyBy());
            map.put("验收人", stockInTolly.getRecheckBy());
            map.put("收货人", stockInTolly.getReceiveBy());
            map.put("上架时间", stockInTolly.getPutawayTime());
            map.put("上架人", stockInTolly.getPutawayBy());
            map.put("ASN单号类型", stockInTolly.getAsnType());
            map.put(" grfId", stockInTolly.getGrfId());
            map.put("验收单号", stockInTolly.getRecheckNo());
            map.put(" sourceSys", stockInTolly.getSourceSys());
            map.put("托盘数", stockInTolly.getLpnQty());
            map.put("到货时间", stockInTolly.getArriveTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<StockInTolly> queryByInOrderSnId(Long wmsInstockId) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(wmsInstockId);
        return stockInTollyRepository.findAll(Example.of(stockInTolly));
    }

    @Override
    public List<StockInTolly> queryByOrderSn(WmsInstock wmsInstock) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(wmsInstock.getId());
        return stockInTollyRepository.findAll(Example.of(stockInTolly));
    }

    @Override
    public boolean auditTally(cn.hutool.json.JSONObject data, WmsInstock wmsInstock, String customersCode) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(wmsInstock.getId());
        Optional<StockInTolly> optional = stockInTollyRepository.findOne(Example.of(stockInTolly));
        stockInTolly = optional.orElse(null);
        if (stockInTolly == null)
            throw new BadRequestException("通知单" + wmsInstock.getInOrderSn() + "理货单据不存在");
        String businessType = data.getStr("businessType");
        Integer operateType = data.getInt("operateType");
        String remark = data.getStr("operateRemark");
        if (StringUtil.equals(businessType, "10")) {
            //理货审核处理
            if (!StringUtil.equals(wmsInstock.getInStatus(), "35"))
                throw new BadRequestException("入库通知单" + wmsInstock.getInOrderSn() + "并非出于理货待审核状态");
            if (operateType == 0) {
                stockInTolly.setStatus("40");
                wmsInstock.setInStatus("40");
            } else if (operateType == 1) {
                stockInTolly.setStatus("41");
                wmsInstock.setInStatus("41");
            } else
                throw new BadRequestException("只支持0同意、1拒绝");
            wmsInstock.setStatusTime(System.currentTimeMillis());
            stockInTollyRepository.save(stockInTolly);
            wmsInstockService.update(wmsInstock);
            WmsStockLog wmsStockLog = new WmsStockLog();
            wmsStockLog.setOrderSn(wmsInstock.getInOrderSn());
            wmsStockLog.setOperationUser("Sys API");
            wmsStockLog.setType("0");
            wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
            wmsStockLog.setStatus(wmsInstock.getInStatus());
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setRequest(data.toString());
            wmsStockLog.setRemark(remark);
            wmsStockLogService.create(wmsStockLog);
        } else if (StringUtil.equals(businessType, "12")) {
            //入库
            if (!StringUtil.equals(stockInTolly.getStatus(), "51"))
                throw new BadRequestException("入库通知单" + wmsInstock.getInOrderSn() + "并非出于已验收状态");
            if (operateType == 0) {
                stockInTolly.setStatus("55");
                wmsInstock.setInStatus("55");
            } else if (operateType == 1) {
                stockInTolly.setStatus("56");
                wmsInstock.setInStatus("56");
            } else
                throw new BadRequestException("只支持0同意、1拒绝");
            wmsInstock.setStatusTime(System.currentTimeMillis());
            stockInTollyRepository.save(stockInTolly);
            wmsInstockService.update(wmsInstock);
            WmsStockLog wmsStockLog = new WmsStockLog();
            wmsStockLog.setOrderSn(wmsInstock.getInOrderSn());
            wmsStockLog.setOperationUser("Sys API");
            wmsStockLog.setType("0");
            wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
            wmsStockLog.setStatus(wmsInstock.getInStatus());
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setRequest(data.toString());
            wmsStockLogService.create(wmsStockLog);
        } else
            throw new BadRequestException("业务类型businessType只支持10理货、12上架");
        return true;
    }

    @Override
    public StockInTollyDto tallyReg(WmsInstock wmsInstock) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(wmsInstock.getId());
        stockInTolly = stockInTollyRepository.findOne(Example.of(stockInTolly)).orElse(null);
        if (stockInTolly == null) {
            stockInTolly = new StockInTolly();
            wmsInstock.setInStatus("15");
            wmsInstock.setStatusTime(System.currentTimeMillis());
            stockInTolly.setInOrderId(wmsInstock.getId());
            stockInTolly.setTallyOrderSn("TL" + System.currentTimeMillis());
            stockInTolly.setSkuNum(wmsInstock.getItemList().size());
            stockInTolly.setTotalNum(0);
            stockInTolly.setCurrentNum(1);
            stockInTolly.setStartTime(DateUtils.now());
            stockInTolly.setStatus("15");
            stockInTolly.setTenantCode(wmsInstock.getTenantCode());
            stockInTolly.setWarehouseId(wmsInstock.getWarehouseId());
            stockInTolly.setAsnStatus("15");
            /*String asnNo=FluxUtils.queryASNOrderBySnNo(wmsInstock.getInOrderSn());
            stockInTolly.setAsnNo(asnNo);*/
            stockInTolly.setAsnNo(wmsInstock.getInOrderSn());
            stockInTolly = stockInTollyRepository.save(stockInTolly);
            wmsInstockService.update(wmsInstock);

        } else {
            stockInTolly.setAsnStatus("15");
            stockInTolly.setStatus("15");
            stockInTolly.setStartTime(DateUtils.now());
            stockInTolly.setEndTime("");
            stockInTolly.setCurrentNum(stockInTolly.getCurrentNum() + 1);
            wmsInstock.setInStatus("15");
            wmsInstock.setStatusTime(System.currentTimeMillis());
            stockInTollyRepository.save(stockInTolly);
            wmsInstockService.update(wmsInstock);
        }
        wmsStockLogService.createSucc(wmsInstock.getInOrderSn(), "0", wmsInstock.getInStatus(), SecurityUtils.getCurrentUsername());
        return stockInTollyMapper.toDto(stockInTolly);
    }

    @Override
    public StockInTollyDto queryByInIdDto(Long inId) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(inId);
        stockInTolly = stockInTollyRepository.findOne(Example.of(stockInTolly)).orElseGet(StockInTolly::new);
        return stockInTollyMapper.toDto(stockInTolly);
    }

    @Override
    public StockInTolly queryByInId(Long inId) {
        StockInTolly stockInTolly = new StockInTolly();
        stockInTolly.setInOrderId(inId);
        stockInTolly = stockInTollyRepository.findOne(Example.of(stockInTolly)).orElse(null);
        return stockInTolly;
    }

    @Override
    public void tallyEnd(Long id) {
        StockInTolly stockInTolly = stockInTollyRepository.findById(id).orElse(null);
        if (stockInTolly == null)
            throw new BadRequestException("无理货数据");
        WmsInstock wmsInstock = wmsInstockService.queryById(stockInTolly.getInOrderId());
        //根据收货交易明细来计算出理货数量
        //begin
        /*List<StockInTollyItem>itemList=stockInTollyItemService.queryByStockId(stockInTolly.getId());
        stockInTolly.setTotalNum(0);
        if (CollectionUtil.isEmpty(itemList)){
            //第一次理货完成
            List<WmsInstockItem>wmsInstockItemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
            if (CollectionUtil.isEmpty(wmsInstockItemList))
                throw new BadRequestException("通知单空明细");
            for (WmsInstockItem item : wmsInstockItemList) {
                TrsDetail trsDetail=new TrsDetail();
                trsDetail.setStockInTolly(stockInTolly);
                trsDetail.setDocLineId(item.getGoodsLineNo());
                List<TrsDetail>trsDetailList=trsDetailService.queryList(trsDetail);
                if (CollectionUtil.isEmpty(trsDetailList))
                    throw new BadRequestException("理货明细为空");
                StockInTollyItem stockInTollyItem=new StockInTollyItem();
                stockInTollyItem.setInTallyId(stockInTolly.getId());
                stockInTollyItem.setPreTallyNum(item.getQty());
                stockInTollyItem.setProductId(item.getProductId());
                stockInTollyItem.setMerchantId(wmsInstock.getMerchantId());
                stockInTollyItem.setPoNo(wmsInstock.getPoNo());
                stockInTollyItem.setNeedSn(item.getNeedSn());
                stockInTollyItem.setGoodsLine(item.getGoodsLineNo());
                stockInTollyItem.setQtyReceived(0);
                for (TrsDetail detail : trsDetailList) {
                    stockInTolly.setTotalNum(stockInTolly.getTotalNum()+detail.getTransactionQty());
                    stockInTollyItem.setQtyReceived(stockInTollyItem.getQtyReceived()+detail.getTransactionQty());
                }
                stockInTollyItemService.create(stockInTollyItem);
            }
        }else {
            //非第一次理货完成，需修改
            for (StockInTollyItem stockInTollyItem : itemList) {
                List<WmsInstockItem>wmsInstockItemList=wmsInstockItemService.queryAllByInId(wmsInstock.getId());
                if (CollectionUtil.isEmpty(wmsInstockItemList))
                    throw new BadRequestException("通知单空明细");
                for (WmsInstockItem item : wmsInstockItemList) {
                    TrsDetail trsDetail = new TrsDetail();
                    trsDetail.setStockInTolly(stockInTolly);
                    trsDetail.setDocLineId(item.getGoodsLineNo());
                    List<TrsDetail> trsDetailList = trsDetailService.queryList(trsDetail);
                    if (CollectionUtil.isEmpty(trsDetailList))
                        throw new BadRequestException("理货明细为空");
                    stockInTollyItem.setQtyReceived(0);
                    for (TrsDetail detail : trsDetailList) {
                        stockInTolly.setTotalNum(stockInTolly.getTotalNum()+detail.getTransactionQty());
                        stockInTollyItem.setQtyReceived(stockInTollyItem.getQtyReceived() + detail.getTransactionQty());
                    }
                    stockInTollyItemService.update(stockInTollyItem);
                }
            }
        }*/
        //end
        if (!StringUtil.equals(wmsInstock.getInStatus(), "15"))
            throw new BadRequestException("非理货中状态");
        stockInTolly.setStatus("20");
        wmsInstock.setInStatus("20");
        wmsInstock.setStatusTime(System.currentTimeMillis());
        stockInTolly.setEndTime(DateUtils.now());
        stockInTolly.setReceiveBy(SecurityUtils.getCurrentUsername());
        stockInTolly.setFinishReceiptTime(DateUtils.now());
        stockInTolly.setAsnStatus("40");
        update(stockInTolly);
        wmsInstockService.update(wmsInstock);
        wmsStockLogService.createSucc(wmsInstock.getInOrderSn(), "0", wmsInstock.getInStatus(), SecurityUtils.getCurrentUsername());
    }

    @Override
    public void tallyPush(Long[] inIds) {
        for (Long inId : inIds) {
            StockInTolly stockInTolly = new StockInTolly();
            stockInTolly.setInOrderId(inId);
            stockInTolly = stockInTollyRepository.findOne(Example.of(stockInTolly)).orElse(null);
            if (stockInTolly == null)
                continue;

            WmsInstock wmsInstock = wmsInstockService.queryById(inId);
            stockInTolly.setWmsInstock(wmsInstock);
            List<StockInTollyItem> itemList = stockInTollyItemService.queryByStockId(stockInTolly.getId());
            stockInTolly.setItems(itemList);
            WmsStockLog wmsStockLog = new WmsStockLog();
            List<TrsDetail>trsDetailList=trsDetailService.queryAllByInStockId(stockInTolly.getId());
            stockInTolly.setTrsDetailList(trsDetailList);
            List<OutofPlanDetail>outofPlanDetails=outofPlanDetailService.queryAllByInTallyId(stockInTolly.getId());
            stockInTolly.setOutofPlanDetails(outofPlanDetails);
            zhuozhiSupport.stockInTolly(stockInTolly, wmsStockLog);
            if (StringUtil.equals(wmsStockLog.getIsSuccess(), "0")) {
                try {
                    wmsStockLogService.create(wmsStockLog);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
                throw new BadRequestException("推送失败：" + wmsStockLog.getRemark());
            }
            wmsStockLog.setStatus("35");
            stockInTolly.setStatus("35");
            wmsInstock.setInStatus("35");
            stockInTollyRepository.save(stockInTolly);
            wmsInstockService.update(wmsInstock);
            try {
                wmsStockLogService.create(wmsStockLog);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Override
    public StockInTolly findById(Long id) {
        return stockInTollyRepository.findById(id).orElse(null);
    }
}
