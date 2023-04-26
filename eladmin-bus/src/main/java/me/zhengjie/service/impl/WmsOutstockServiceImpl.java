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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mq.CBOrderProducer;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.FluxUtils;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.WmsOutstockRepository;
import me.zhengjie.service.dto.WmsOutstockDto;
import me.zhengjie.service.dto.WmsOutstockQueryCriteria;
import me.zhengjie.service.mapstruct.WmsOutstockMapper;
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
 * @date 2020-12-04
 **/
@Service
@RequiredArgsConstructor
@Slf4j
public class WmsOutstockServiceImpl implements WmsOutstockService {

    private final WmsOutstockRepository wmsOutstockRepository;
    private final WmsOutstockMapper wmsOutstockMapper;

    @Autowired
    private WmsStockLogService wmsStockLogService;

    @Autowired
    private ThirdOrderLogService thirdOrderLogService;

    @Autowired
    private AsnHeaderService asnHeaderService;

    @Autowired
    private LoadHeaderService loadHeaderService;

    @Autowired
    private StockOutTollyService stockOutTollyService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private WmsOutstockItemService wmsOutstockItemService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsInstockService wmsInstockService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Autowired
    private CBOrderProducer cbOrderProducer;

    @Autowired
    private WmsSupport wmsSupport;

    @Override
    public Map<String,Object> queryAll(WmsOutstockQueryCriteria criteria, Pageable pageable){
        Page<WmsOutstock> page = wmsOutstockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(wmsOutstockMapper::toDto));
    }

    @Override
    public List<WmsOutstockDto> queryAll(WmsOutstockQueryCriteria criteria){
        return wmsOutstockMapper.toDto(wmsOutstockRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public WmsOutstockDto findById(Long id) {
        WmsOutstock wmsOutstock = wmsOutstockRepository.findById(id).orElseGet(WmsOutstock::new);
        ValidationUtil.isNull(wmsOutstock.getId(),"WmsOutstock","id",id);
        return wmsOutstockMapper.toDto(wmsOutstock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public WmsOutstockDto create(WmsOutstock resources) {
        resources.setOutStatus("-00");
        resources.setCreateCustomer(SecurityUtils.getCurrentUserId()+"");
        resources.setCreateTime(new Timestamp(System.currentTimeMillis()));
        resources.setStatusTime(System.currentTimeMillis());
        resources.setSyncComplete(true);
        return wmsOutstockMapper.toDto(wmsOutstockRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(WmsOutstock resources) {
        WmsOutstock wmsOutstock = wmsOutstockRepository.findById(resources.getId()).orElseGet(WmsOutstock::new);
        ValidationUtil.isNull( wmsOutstock.getId(),"WmsOutstock","id",resources.getId());
        wmsOutstock.copy(resources);
        return wmsOutstockRepository.save(wmsOutstock)!=null;
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            wmsOutstockRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<WmsOutstockDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (WmsOutstockDto wmsOutstock : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("出库单号", wmsOutstock.getOutOrderSn());
            map.put("单据类型", wmsOutstock.getOutOrderType());
            map.put("出库联系人", wmsOutstock.getReceiver());
            map.put("出库联系电话", wmsOutstock.getPhone());
            map.put("配送地址", wmsOutstock.getAddress());
            map.put("运输方式", wmsOutstock.getTransportWay());
            map.put("托盘要求", wmsOutstock.getPallet());
            map.put("打托高度", wmsOutstock.getPalletizedHeight());
            map.put("预期理货完成时间(yyyy-MM-dd HH:mm:ss)", wmsOutstock.getExpectTallyTime());
            map.put("预期出库时间", wmsOutstock.getExpectShipTime());
            map.put("理货维度", wmsOutstock.getTallyWay());
            map.put("备注", wmsOutstock.getRemark());
            map.put("附件地址(理货要求等)", wmsOutstock.getFileLink());
            map.put("创建时间", wmsOutstock.getCreateTime());
            map.put("创建商家", wmsOutstock.getCreateCustomer());
            map.put("出库通知单状态", wmsOutstock.getOutStatus());
            map.put("出库时间", wmsOutstock.getGoodsUpper());
            map.put("状态最后变化时间戳", wmsOutstock.getStatusTime());
            map.put("同步流程是否完成", wmsOutstock.getSyncComplete());
            map.put("渠道", wmsOutstock.getChannel());
            map.put("入库理货单号-调拨单完成之前必须有", wmsOutstock.getInTallySn());
            map.put("出库理货单号", wmsOutstock.getOutTallySn());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void outStockPushFlux() {
        List<WmsOutstock> wmsOutstockList = queryByStatus("01");
        if (CollectionUtils.isNotEmpty(wmsOutstockList)) {
            for (WmsOutstock wmsOutstock : wmsOutstockList) {
                ThirdOrderLog thirdOrderLog=new ThirdOrderLog();
                try {
                    ShopInfo shopInfo=shopInfoService.findById(wmsOutstock.getShopId());
                    ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
                    shopInfo.setClearCompanyInfo(clearCompanyInfo);
                    wmsOutstock.setShopInfo(shopInfo);
                    List<WmsOutstockItem>itemList=wmsOutstockItemService.queryByOutId(wmsOutstock.getId());
                    if (CollectionUtil.isEmpty(itemList))
                        throw new BadRequestException("找不到通知单明细");
                    wmsOutstock.setItemList(itemList);
                    FluxUtils.pushWmsOutStock(wmsOutstock,thirdOrderLog);
                } catch (Exception e) {
                    WmsStockLog wmsStockLog = new WmsStockLog();
                    wmsStockLog.setStatus("02F");
                    wmsStockLog.setOrderSn(wmsOutstock.getOutOrderSn());
                    wmsOutstock.setOutStatus("02F");
                    wmsOutstock.setStatusTime(System.currentTimeMillis());
                    wmsStockLog.setIsSuccess("0");
                    wmsStockLog.setType("0");
                    wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
                    wmsStockLog.setOperationUser("SYS");
                    wmsStockLog.setRemark(e.getMessage());
                    wmsStockLogService.create(wmsStockLog);
                    update(wmsOutstock);
                    thirdOrderLogService.create(thirdOrderLog);
                    continue;
                }
                wmsOutstock.setOutStatus("02");
                wmsOutstock.setStatusTime(System.currentTimeMillis());
                update(wmsOutstock);
                WmsStockLog wmsStockLog = new WmsStockLog();
                wmsStockLog.setStatus("02");
                wmsStockLog.setOrderSn(wmsOutstock.getOutOrderSn());
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
    public List<WmsOutstock> queryByStatus(String status) {
        WmsOutstock wmsOutstockConditions = new WmsOutstock();
        wmsOutstockConditions.setOutStatus(status);
        return wmsOutstockRepository.findAll(Example.of(wmsOutstockConditions));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addOutBound(String decData, String customersCode) {
        //商家推送到仓库的2b出库通知
        WmsOutstock data = JSONObject.parseObject(decData, WmsOutstock.class);
        if (data != null) {
            if (StringUtil.isEmpty(data.getOutOrderSn())) {
                throw new BadRequestException("outOrderSn为空");
            }
            if (StringUtil.isEmpty(data.getOutOrderType())) {
                throw new BadRequestException("outOrderType为空");
            }
            if (StringUtil.isEmpty(data.getChannel()))
                data.setChannel("default");
            if (StringUtil.isEmpty(data.getMerchantId()))
                throw new BadRequestException("mechantId为空");
            if (StringUtil.isEmpty(data.getWarehouseId()))
                throw new BadRequestException("warehouseId为空");
            if (StringUtil.isEmpty(data.getTenantCode()))
                throw new BadRequestException("tenantCode为空");
            /*if (StringUtil.isEmpty(data.getReceiver())){
                throw new BadRequestException("receiver为空");
            }
            if (StringUtil.isEmpty(data.getPhone())){
                throw new BadRequestException("phone为空");
            }
            if (StringUtil.isEmpty(data.getAddress())){
                throw new BadRequestException("address为空");
            }*/
            if (CollectionUtils.isEmpty(data.getItemList())) {
                throw new BadRequestException("skuDetails为空");
            }
            WmsOutstock wmsOutstock=findByOutOrderSn(data.getOutOrderSn());
            if (wmsOutstock!=null){
                throw new BadRequestException("已有相同出库通知单号");
            }
            data.setSoNo(data.getOutOrderSn());
            ShopInfo shopInfo=shopInfoService.queryByShopCode(data.getMerchantId());
            if (shopInfo!=null){
                data.setShopInfo(shopInfo);
                data.setShopId(shopInfo.getId());
            }
            List<WmsOutstockItem> itemList = data.getItemList();
            int i = 0;
            data.setCreateCustomer(customersCode);
            data.setCreateTime(new Timestamp(System.currentTimeMillis()));
            data.setOutStatus("00");
            data.setStatusTime(System.currentTimeMillis());
            data.setSyncComplete(false);
            data.setFluxOrderNo("-");
            wmsOutstockRepository.save(data);
            for (WmsOutstockItem item : itemList) {
                i++;
                if (StringUtil.isEmpty(item.getSkuNo())) {
                    throw new BadRequestException("第" + i + "个的skuNo为空");
                }
                if (StringUtil.isEmpty(item.getGoodsLineNo())) {
                    throw new BadRequestException("第" + i + "个的goodsLineNo为空");
                }
                if (StringUtil.isEmpty(item.getBarCode())) {
                    throw new BadRequestException("第" + i + "个的barCode为空");
                }
                if (StringUtil.isEmpty(item.getGoodsName())) {
                    throw new BadRequestException("第" + i + "个的goodsName为空");
                }
                if (item.getQty() == null) {
                    throw new BadRequestException("第" + i + "个的qty为空");
                }
                if (item.getNondefectiveNum() == null) {
                    throw new BadRequestException("第" + i + "个的nondefectiveNum为空");
                }
                if (item.getDamageNum() == null) {
                    throw new BadRequestException("第" + i + "个的damageNum为空");
                }
                //item.setWmsOutstock(data);
                item.setOutId(data.getId());
                //查询商品
                BaseSku baseSku=baseSkuService.queryByOutGoodsNo(item.getSkuNo());
                if (baseSku==null)
                    item.setGoodsNo(item.getSkuNo());
                else {
                    item.setProductId(baseSku.getGoodsCode());
                    item.setGoodsNo(baseSku.getGoodsNo());
                    item.setBarCode(baseSku.getBarCode());
                }
                wmsOutstockItemService.create(item);
            }
            WmsStockLog wmsStockLog = new WmsStockLog();
            wmsStockLog.setOrderSn(data.getOutOrderSn());
            wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
            wmsStockLog.setOperationUser(customersCode);
            wmsStockLog.setRequest(decData);
            wmsStockLog.setStatusText("出库通知单下发");
            wmsStockLog.setStatus("00");
            wmsStockLog.setRemark("操作成功");
            wmsStockLog.setType("1");
            wmsStockLog.setIsSuccess("1");
            wmsStockLogService.create(wmsStockLog);
            try {
                cbOrderProducer.send(MsgType.ZHUOZHI_OUT_SYNCSTATUS,data.getId().toString(),data.getSoNo());
            }catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        throw new BadRequestException("data解析失败");
    }

    @Override
    public WmsOutstock findByOutOrderSn(String outStockSn) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setOutOrderSn(outStockSn);
        Optional<WmsOutstock> wmsOutOpt = wmsOutstockRepository.findOne(Example.of(wmsOutstock));
        /*if (wmsOutOpt.isPresent())
            return wmsOutOpt.get();
        return null;
        等效于下面语句
        */
        return wmsOutOpt.orElse(null);
    }
    private WmsOutstock findByOutOrderSn(String outStockSn,String tenantCode) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setOutOrderSn(outStockSn);
        wmsOutstock.setTenantCode(tenantCode);
        Optional<WmsOutstock> wmsOutOpt = wmsOutstockRepository.findOne(Example.of(wmsOutstock));
        /*if (wmsOutOpt.isPresent())
            return wmsOutOpt.get();
        return null;
        等效于下面语句
        */
        return wmsOutOpt.orElse(null);
    }

    @Override
    public boolean cancel(String orderSn, String customersCode,String tenantCode, String reason) {
        WmsOutstock wmsOutstock = findByOutOrderSn(orderSn,tenantCode);
        if (wmsOutstock == null) {
            throw new BadRequestException("出库通知单不存在");
        }
        if (StringUtil.equals(wmsOutstock.getOutStatus(),"-1")){
            //不在创建、已推送富勒、到货状态的不支持取消，需联系仓库进行手动取消
            return true;
        }
        if (!(StringUtil.equals(wmsOutstock.getOutStatus(),"00")||
                StringUtil.equals(wmsOutstock.getOutStatus(),"01")||
                StringUtil.equals(wmsOutstock.getOutStatus(),"01F"))){
            //不在创建、已推送富勒、到货状态的不支持取消，需联系仓库进行手动取消
            //需要调用富勒取消订单接口或者通知取消
        }
        StockOutTolly stockOutTolly = stockOutTollyService.findByOutOrderSn(wmsOutstock);
        if (stockOutTolly!=null){
            stockOutTolly.setStatus("-1");
            stockOutTollyService.update(stockOutTolly);
        }
        wmsOutstock.setOutStatus("-1");
        boolean flag = update(wmsOutstock);
        WmsStockLog wmsStockLog = new WmsStockLog();
        wmsStockLog.setType("1");
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setOperationUser(customersCode);
        wmsStockLog.setOrderSn(wmsOutstock.getOutOrderSn());
        if (flag) {
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setRemark("取消成功");
            wmsStockLog.setStatus("-1");
            wmsStockLog.setStatusText("出库通知单取消");
            wmsStockLogService.create(wmsStockLog);
            return true;
        }
        wmsStockLog.setIsSuccess("0");
        wmsStockLog.setRemark("取消失败");
        wmsStockLog.setStatus("-1F");
        wmsStockLog.setStatusText("出库通知单取消失败");
        wmsStockLogService.create(wmsStockLog);
        return false;
    }

    @Override
    public WmsOutstockDto queryByIdDto(Long id) {
        WmsOutstock wmsOutstock=wmsOutstockRepository.findById(id).orElseGet(WmsOutstock::new);
        if (wmsOutstock.getId()!=null){
            wmsOutstock.setItemList(wmsOutstockItemService.queryByOutId(wmsOutstock.getId()));
        }
        return wmsOutstockMapper.toDto(wmsOutstock);
    }

    @Override
    public WmsOutstock queryById(Long id) {
        return wmsOutstockRepository.findById(id).orElse(null);
    }

    @Override
    public void pushLpnStock(Long id) {
        WmsOutstock wmsOutstock=wmsOutstockRepository.findById(id).orElse(null);
        if (wmsOutstock==null){
            throw new BadRequestException("找不到出库通知单");
        }
        StockOutTolly stockOutTolly=stockOutTollyService.findByOutId(id);
        WmsStockLog wmsStockLog=new WmsStockLog();
        try {
            zhuozhiSupport.stockOutTally(stockOutTolly,wmsStockLog);
        }catch (Exception e){
            try {
                wmsStockLogService.create(wmsStockLog);
            }catch (Exception e2){
                log.error(e2.getMessage(),e2);
            }
            throw new BadRequestException(e.getMessage());
        }
        try {
            wmsStockLogService.create(wmsStockLog);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if (StringUtil.equals("1",wmsStockLog.getIsSuccess())){
            AsnHeader asnHeader=asnHeaderService.findByOutId(id);
            if (asnHeader==null){
                throw new BadRequestException("没有托盘信息");
            }
            wmsStockLog=new WmsStockLog();
            try {
                zhuozhiSupport.tpUpload(asnHeader,wmsStockLog);
            }catch (Exception e){
                try {
                    wmsStockLogService.createFail(stockOutTolly.getWmsOutstock().getOutOrderSn(),"1","13F",e.getMessage(),"System");
                }catch (Exception e2){
                    log.error(e2.getMessage(),e2);
                }
                return;
            }
        }
        stockOutTolly.setStatus(wmsStockLog.getStatus());
        stockOutTolly.getWmsOutstock().setOutStatus(wmsStockLog.getStatus());
        stockOutTolly.getWmsOutstock().setStatusTime(System.currentTimeMillis());
        stockOutTollyService.update(stockOutTolly);
        try {
            wmsStockLogService.create(wmsStockLog);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void pushPreLoad(Long id) {
        WmsOutstock wmsOutstock=wmsOutstockRepository.findById(id).orElse(null);
        if (wmsOutstock==null){
            throw new BadRequestException("找不到出库通知单");
        }
        LoadHeader loadHeader=loadHeaderService.findByOutId(id);
        if (loadHeader==null){
            throw new BadRequestException("没有预装载单信息");
        }
        WmsStockLog wmsStockLog=new WmsStockLog();
        zhuozhiSupport.preLoad(loadHeader,wmsStockLog);
        if (StringUtil.equals(wmsStockLog.getIsSuccess(),"0")){
            throw new BadRequestException("推送预装载单失败："+wmsStockLog.getRemark());
        }
        StockOutTolly stockOutTolly=stockOutTollyService.findByOutId(id);
        stockOutTolly.setStatus("30");
        stockOutTolly.getWmsOutstock().setOutStatus("30");
        stockOutTolly.getWmsOutstock().setStatusTime(System.currentTimeMillis());
        stockOutTollyService.update(stockOutTolly);
        try {
            wmsStockLogService.create(wmsStockLog);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }
    @Override
    public void syncStatus(Long[] ids) {
        for (Long id : ids) {
            WmsOutstock wmsOutstock=wmsOutstockRepository.findById(id).orElse(null);
            if (wmsOutstock==null)
                continue;
            WmsStockLog wmsStockLog=new WmsStockLog();
            try {
                zhuozhiSupport.noticeDocStatus(null,wmsOutstock,wmsStockLog);
            }catch (Exception e){
                e.printStackTrace();
                continue;
            }
            if (StringUtil.equals(wmsOutstock.getOutStatus(),"00")) {
                wmsOutstock.setOutStatus("01");//已接单
                wmsStockLog.setStatus("01");
            }
            update(wmsOutstock);
            try {
                wmsStockLogService.create(wmsStockLog);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public List<Map<String, String>> getOrderSns() {
        WmsOutstock wrapper=new WmsOutstock();
        wrapper.setOutStatus("30");
        List<WmsOutstock>wmsOutstockList=wmsOutstockRepository.findAll(Example.of(wrapper));
        if (CollectionUtil.isEmpty(wmsOutstockList))
            return new ArrayList<>();
        List<Map<String, String>>retList=new ArrayList<>();
        for (WmsOutstock wmsOutstock : wmsOutstockList) {
            Map<String,String>map=new HashMap<>();
            map.put("outOrderSn",wmsOutstock.getOutOrderSn());
            map.put("loadNo",wmsOutstock.getLoadNo());
            retList.add(map);
        }
        return retList;
    }

    @Override
    public void outStock(long id) {
        WmsOutstock wmsOutstock=wmsOutstockRepository.findById(id).orElse(null);
        if (wmsOutstock==null)
            throw new BadRequestException("数据不存在");
        if (!StringUtil.equals(wmsOutstock.getOutStatus(),"35"))
            throw new BadRequestException("车辆信息没有上传");
        if (!StringUtil.equals("1",wmsOutstock.getEnableStock())){
            throw new BadRequestException("卓志没有下发允许出库通知");
        }
        try {
            wmsSupport.deliver(wmsOutstock.getFluxOrderNo());
        } catch (Exception e) {
            e.printStackTrace();
            throw new BadRequestException(e.getMessage());
        }
        wmsOutstock.setOutStatus("40");
        WmsStockLog wmsStockLog=new WmsStockLog();
        try {
            zhuozhiSupport.noticeDocStatus(null,wmsOutstock,wmsStockLog);
        }catch (Exception e){
            throw new BadRequestException(e.getMessage());
        }
        update(wmsOutstock);
        wmsStockLog.setStatus("40");
        try {
            wmsStockLogService.create(wmsStockLog);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    @Override
    public void getFluxSoNo() {
        WmsOutstock wrapper =new WmsOutstock();
        wrapper.setFluxOrderNo("-");
        List<WmsOutstock>list=wmsOutstockRepository.findAll(Example.of(wrapper));
        if (CollectionUtil.isEmpty(list))
            return;
        for (WmsOutstock wmsOutstock : list) {
            if (wmsOutstock.getShopId()==null) continue;
            ShopInfo shopInfo=shopInfoService.findById(wmsOutstock.getShopId());
            ClearCompanyInfo clearCompanyInfo=clearCompanyInfoService.findById(shopInfo.getServiceId());
            shopInfo.setClearCompanyInfo(clearCompanyInfo);
            CustomerKeyDto customerKeyDto=customerKeyService.findByCustCode(clearCompanyInfo.getCustomsCode());
            String fluxSoNo=FluxUtils.getFluxSoNo(wmsOutstock.getOutOrderSn(),customerKeyDto);
            wmsOutstock.setFluxOrderNo(fluxSoNo);
            update(wmsOutstock);
        }
    }

    /**
     * 出库时根据分配的库存批次信息获取PO单号
     * @param resp
     */
    @Override
    public void getPoNo(JSONObject resp) {
        if (resp.getBoolean("success")&&resp.getJSONObject("data")!=null){
            JSONArray array= resp.getJSONObject("data").getJSONArray("list");
            if (CollectionUtils.isEmpty(array))
                return;
            for (int i = 0; i < array.size(); i++) {
                String inOrderSn=array.getJSONObject(i).getString("lotAtt04");
                WmsInstock wmsInstock=wmsInstockService.findByOrderSn(inOrderSn);
                array.getJSONObject(i).put("lotAtt04",wmsInstock.getPoNo());
            }
        }
    }

    @Override
    public WmsOutstock queryByLoadNo(String loadNo) {
        WmsOutstock wmsOutstock=new WmsOutstock();
        wmsOutstock.setLoadNo(loadNo);
        return wmsOutstockRepository.findOne(Example.of(wmsOutstock)).orElse(null);
    }
}
