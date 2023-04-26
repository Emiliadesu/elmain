package me.zhengjie.support.zhuozhi;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.ClearOptLogService;
import me.zhengjie.service.HezhuLogService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.TransLogService;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.*;
import me.zhengjie.utils.enums.ClearInfoStatusEnum;
import me.zhengjie.utils.enums.ClearTransStatusEnum;
import me.zhengjie.utils.enums.HeZhuInfoStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

@Slf4j
@Service
public class ZhuozhiSupport {

    @Value("${zhuozhi.app-key}")
    public String appKey;

    @Value("${zhuozhi.url-pms}")
    private String urlPms;

    @Autowired
    private ClearOptLogService clearOptLogService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private HezhuLogService hezhuLogService;

    @Autowired
    private TransLogService transLogService;

    // 回推清关状态
    public void noticeClearStatus(ClearInfo clearInfo) {
        JSONObject body = new JSONObject();
        body.put("orderCode", clearInfo.getClearNo());
        body.put("tradeType", clearInfo.getTradeType());
        body.put("tenantCode", "zhuozhi");
        body.put("warehouseCode", "8020");
        body.put("cmcCode", clearInfo.getEntryNo());
        body.put("releaseTime", DateUtils.now());

        body.put("type", "20");
        if (StringUtils.equals(ClearInfoStatusEnum.STATUS_CLEAR_START.getCode(), clearInfo.getStatus())) {
            body.put("status", "30");
        }else if(StringUtils.equals(ClearInfoStatusEnum.STATUS_CLEAR_PASS.getCode(), clearInfo.getStatus())) {
            body.put("status", "40");
        }else if(StringUtils.equals(ClearInfoStatusEnum.STATUS_DRAFT_PASS.getCode(), clearInfo.getStatus())) {
            body.put("status", "80");
        }else if(StringUtils.equals(ClearInfoStatusEnum.STATUS_DONE.getCode(), clearInfo.getStatus())) {
            body.put("status", "90");
        }else {
            // 其他节点不回传
            return;
        }
        log.info("卓志清关回推请求：{}", body.toJSONString());
        String resp= HttpRequest.post( urlPms + "/rest/flWms/rewrite/declareStatus").
                header("token", JwtUtils.createToken(appKey, body.toJSONString())).
                body(body.toJSONString()).
                execute().
                body();
        log.info("卓志清关回推返回：{}", resp);
        JSONObject response=JSONObject.parseObject(resp);
        ClearOptLog log = new ClearOptLog(
                clearInfo.getId(),
                clearInfo.getStatus(),
                new Timestamp(System.currentTimeMillis()),
                body.toJSONString(),
                resp
        );
        clearOptLogService.create(log);
        int status = response.getIntValue("status");
        if (status == 1){
            return;
        }else {
            throw new BadRequestException(response.getString("message"));
        }
    }

    // 回推运输信息
    public void noticeTransStatus(TransInfo transInfo) {
        JSONObject body = new JSONObject();
        body.put("orderCode", transInfo.getClearNo());
        body.put("tradeType", transInfo.getTradeType());
        body.put("tenantCode", "zhuozhi");
        body.put("warehouseCode", "8020");
        body.put("releaseTime", DateUtils.now());
        body.put("type", "65");
        if (ClearTransStatusEnum.STATUS_215.getCode().intValue() == transInfo.getStatus().intValue()) {
            body.put("status", "30");
        }else if(ClearTransStatusEnum.STATUS_230.getCode().intValue() == transInfo.getStatus().intValue()) {
            body.put("status", "90");
        }else {
            // 其他节点不回传
            return;
        }
        JSONArray vehicleList = new JSONArray();
        List<TransDetails> transDetailsList = transInfo.getDetails();
        for (int i = 0; i < transDetailsList.size(); i++) {
            JSONObject vehicle = new JSONObject();
            vehicle.put("seqNo", i+1);
            vehicle.put("plateNo", transDetailsList.get(i).getPlateNo());
            vehicle.put("carType", transDetailsList.get(i).getCarType());
            vehicle.put("shareFlag", transDetailsList.get(i).getShareFlag());
            vehicleList.add(vehicle);
        }
        body.put("vehicleList", vehicleList);
        log.info("卓志运输回推请求：{}", body.toJSONString());
        String resp= HttpRequest.post( urlPms + "/rest/flWms/rewrite/declareStatus").
                header("token", JwtUtils.createToken(appKey, body.toJSONString())).
                body(body.toJSONString()).
                execute().
                body();
        log.info("卓志运输回推返回：{}", resp);
        JSONObject response=JSONObject.parseObject(resp);
        TransLog transLog = new TransLog(
                transInfo.getId(),
                transInfo.getOrderNo(),
                transInfo.getStatus().toString(),
                body.toJSONString(),
                resp
        );
        transLogService.create(transLog);
        int status = response.getIntValue("status");
        if (status == 1){
            return;
        }else {
            throw new BadRequestException(response.getString("message"));
        }
    }

    // 回推核注单状态
    public void noticeHeZhuStatus(HezhuInfo hezhuInfo) {
        JSONObject body = new JSONObject();
        body.put("orderCode", hezhuInfo.getClearNo());
        body.put("tradeType", hezhuInfo.getTradeType());
        body.put("tenantCode", "zhuozhi");
        body.put("warehouseCode", "8020");
        body.put("cmcCode", hezhuInfo.getQdCode());
        body.put("releaseTime", DateUtils.now());

        body.put("type", "10");
        if (HeZhuInfoStatusEnum.STATUS_815.getCode().intValue() ==  hezhuInfo.getStatus()) {
            body.put("status", "30");
        }else if(HeZhuInfoStatusEnum.STATUS_820.getCode().intValue() ==  hezhuInfo.getStatus()) {
            body.put("status", "40");
        }else if(HeZhuInfoStatusEnum.STATUS_830.getCode().intValue() ==  hezhuInfo.getStatus()) {
            body.put("status", "90");
        }else {
            // 其他节点不回传
            return;
        }
        log.info("卓志核注回推请求：{}", body.toJSONString());
        String resp= HttpRequest.post( urlPms + "/rest/flWms/rewrite/declareStatus").
                header("token", JwtUtils.createToken(appKey, body.toJSONString())).
                body(body.toJSONString()).
                execute().
                body();
        log.info("卓志核注回推返回：{}", resp);
        JSONObject response=JSONObject.parseObject(resp);
        HezhuLog hezhuLog = new HezhuLog(
                hezhuInfo.getId(),
                hezhuInfo.getOrderNo(),
                hezhuInfo.getStatus().toString(),
                body.toJSONString(),
                resp
        );
        hezhuLogService.create(hezhuLog);
        int status = response.getIntValue("status");
        if (status == 1){
            return;
        }else {
            throw new BadRequestException(response.getString("message"));
        }
    }

    public void noticeAddValue(AddValueOrder addValueOrder) {
        JSONObject body = new JSONObject();
        body.put("cuicode", "zhuozhi");
        body.put("addServiceNo", addValueOrder.getOutOrderNo());
        body.put("warehouseCode", "8020");
        ShopInfoDto shopInfoDto = shopInfoService.queryById(addValueOrder.getShopId());
        body.put("merchantId", shopInfoDto.getCode());
        JSONArray cabAddServiceDetailList = new JSONArray();
        JSONObject cabAddServiceDetail = new JSONObject();
        cabAddServiceDetail.put("contractsinvtcode", addValueOrder.getAddCode());
        cabAddServiceDetail.put("chargeqty", addValueOrder.getFinishQty());
        cabAddServiceDetailList.add(cabAddServiceDetail);
        body.put("cabAddServiceDetailList", cabAddServiceDetailList);

        JSONArray cabAddServiceMaterialList = new JSONArray();
        JSONObject cabAddServiceMaterial = new JSONObject();
        cabAddServiceMaterial.put("materialCode", "FL012");
        cabAddServiceMaterial.put("materialNum", "5");
        cabAddServiceMaterialList.add(cabAddServiceMaterial);
        body.put("cabAddServiceMaterialList", cabAddServiceMaterialList);
        log.info("卓志增值服务回推请求：{}", body.toJSONString());
        String resp= HttpRequest.post( urlPms + "/rest/flWms/rewrite/backWriteVasOrder").
                header("token", JwtUtils.createToken(appKey, body.toJSONString())).
                body(body.toJSONString()).
                execute().
                body();
        JSONObject response=JSONObject.parseObject(resp);
        log.info("卓志增值服务回推返回：{}", resp);
        int status = response.getIntValue("status");
        if (status == 1){
            return;
        }else {
            throw new BadRequestException(response.getString("message"));
        }

    }
    /**
     * 入库理货报告
     */
    public void stockInTolly(StockInTolly stockTolly, WmsStockLog wmsStockLog) {
        JSONObject body = new JSONObject();
        body.put("tenantCode",stockTolly.getTenantCode());
        body.put("id",stockTolly.getId()+"");
        body.put("warehouseId",stockTolly.getWmsInstock().getWarehouseId());
        body.put("asnStatus",stockTolly.getAsnStatus());
        body.put("asnNo",stockTolly.getAsnNo());
        switch (stockTolly.getAsnStatus()){
            case "40":
                body.put("receiveBy", stockTolly.getReceiveBy());
                body.put("finishReceiptTime",stockTolly.getFinishReceiptTime());
                break;
            case "50":
                body.put("recheckBy",stockTolly.getRecheckBy());
                body.put("recheckTime",stockTolly.getRecheckTime());
                break;
            case "60":
                body.put("putawayBy",stockTolly.getPutawayBy());
                body.put("putawayTime",stockTolly.getPutawayTime());
                break;
            case "99":
                body.put("verifyBy",stockTolly.getVerifyBy());
                body.put("shpdDate",stockTolly.getShpdDate());
                break;
        }
        JSONArray asnDetailList=new JSONArray();
        if (stockTolly.getItems()!=null){
            for (StockInTollyItem item : stockTolly.getItems()) {
                JSONObject asnDetail=new JSONObject();
                asnDetail.put("id",item.getId()+"");
                asnDetail.put("qtyReceived",item.getQtyReceived());
                asnDetail.put("merchantId",stockTolly.getWmsInstock().getMerchantId());
                if (item.getPreTallyNum()>item.getQtyReceived()){
                    //有差异
                    JSONArray reasonDetails=new JSONArray();
                    for (ReasonDetail detail : item.getReasonDetails()) {
                        JSONObject reasonDetail=new JSONObject();
                        reasonDetail.put("reasonCode",detail.getReasonCode());
                        reasonDetail.put("reasonDescr",detail.getReasonDescr());
                        reasonDetail.put("num",detail.getNum());
                        reasonDetails.add(reasonDetail);
                    }
                    asnDetail.put("reasonDetails",reasonDetails);
                }
                asnDetailList.add(asnDetail);
            }
        }
        body.put("asnDetailList",asnDetailList);
        if (StringUtil.equals("40",stockTolly.getAsnStatus())||StringUtil.equals("50",stockTolly.getAsnStatus())){
            JSONArray trsDetailList=new JSONArray();
            for (TrsDetail detail : stockTolly.getTrsDetailList()) {
                JSONObject trsDetail=new JSONObject();
                trsDetail.put("id",detail.getId()+"");
                trsDetail.put("docLineId",detail.getDocLineId());
                trsDetail.put("transactionQty",detail.getTransactionQty());
                trsDetail.put("productId",detail.getProductId());
                trsDetail.put("length",detail.getLength());
                trsDetail.put("width",detail.getWidth());
                trsDetail.put("height",detail.getHeight());
                trsDetail.put("grossWeight",detail.getGrossWeight());
                trsDetail.put("volume",detail.getVolume());
                trsDetail.put("merchantId",detail.getMerchantId());
                trsDetail.put("lotNo",detail.getLotNo());
                if (StringUtil.isNotBlank(detail.getProductionTime()))
                    trsDetail.put("productionTime",DateUtils.formatDateTime(DateUtils.parse(detail.getProductionTime(),"yyyyMMdd")));
                trsDetail.put("warehouseTime",detail.getWarehouseTime());
                if (StringUtil.isNotBlank(detail.getExpiredTime()))
                    trsDetail.put("expiredTime",DateUtils.formatDateTime(DateUtils.parse(detail.getExpiredTime(),"yyyyMMdd")));
                trsDetail.put("fundProviderId",detail.getFundProviderId());
                trsDetail.put("consignorId",detail.getConsignorId());
                trsDetail.put("batchNo",detail.getBatchNo());
                trsDetail.put("isDamaged",detail.getIsDamaged());
                trsDetail.put("virtualMerchantId",detail.getVirtualMerchantId());
                trsDetail.put("stockBusinessType",detail.getStockBusinessType());
                trsDetail.put("customerBatchNo",detail.getCustomerBatchNo());
                trsDetail.put("lpn",detail.getLpn());
                trsDetailList.add(trsDetail);
            }
            body.put("trsDetailList",trsDetailList);
        }else {
            body.put("trsDetailList",new JSONArray());
        }
        if (CollectionUtil.isNotEmpty(stockTolly.getOutofPlanDetails())){
            JSONArray outofPlanDetailList=new JSONArray();
            for (OutofPlanDetail detail : stockTolly.getOutofPlanDetails()) {
                JSONObject outofPlanDetail=new JSONObject();
                outofPlanDetail.put("ean13",detail.getEan13());
                outofPlanDetail.put("productName",detail.getProductName());
                outofPlanDetail.put("receiveQty",detail.getReceiveQty());
                outofPlanDetail.put("picUrl",detail.getPicUrl());
                outofPlanDetailList.add(outofPlanDetail);
            }
            body.put("outofPlanDetailList",outofPlanDetailList);
        }
        wmsStockLog.setOrderSn(stockTolly.getWmsInstock().getInOrderSn());
        wmsStockLog.setType("0");
        wmsStockLog.setRequest(body.toJSONString());
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/tallyResult").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(body.toJSONString()).execute().body();
        if (StringUtil.indexOf(resp,"{")!=0)
            throw new BadRequestException("不是JSON字符串:"+resp);
        wmsStockLog.setResponse(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if ("success".equalsIgnoreCase(response.getString("flag"))){
            wmsStockLog.setIsSuccess("1");
        }else {
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setRemark(response.getString("message"));
        }
    }

    public void stockOutTally(StockOutTolly stockOutTolly,WmsStockLog wmsStockLog) {
        JSONObject body = new JSONObject();
        body.put("doNo",stockOutTolly.getWmsOutstock().getOutOrderSn());
        body.put("grossWeight",stockOutTolly.getGrossWeight());
        body.put("id",stockOutTolly.getId()+"");
        body.put("parentDoCode",stockOutTolly.getParentDoCode());
        body.put("tenantCode",stockOutTolly.getTenantCode());
        body.put("warehouseId",stockOutTolly.getWarehouseId());
        JSONArray cartonHeaders=new JSONArray();
        for (CartonHeaders header : stockOutTolly.getCartonHeaders()) {
            JSONObject cartonHeader=new JSONObject();
            cartonHeader.put("actualGrossWeight",header.getActualGrossWeight());
            JSONArray cartonDetails=new JSONArray();
            for (CartonDetail detail : header.getCartonDetails()) {
                JSONObject cartonDetail=new JSONObject();
                cartonDetail.put("cartonHeaderId",header.getId()+"");
                cartonDetail.put("id",detail.getId()+"");
                cartonDetail.put("num",detail.getNum());
                cartonDetail.put("productId",detail.getProductId());
                cartonDetails.add(cartonDetail);
            }
            cartonHeader.put("cartonDetails",cartonDetails);
            cartonHeader.put("cartonNo",header.getCartonNo());
            cartonHeader.put("doNo",stockOutTolly.getWmsOutstock().getOutOrderSn());
            cartonHeader.put("id",stockOutTolly.getId()+"");
            cartonHeader.put("isFirst",header.getIsFirst());
            cartonHeader.put("materials",header.getMaterials());
            cartonHeader.put("packageTime",DateUtils.formatDateTime(header.getPackageTime()));
            cartonHeader.put("packagedBy",header.getPackagedBy());
            cartonHeaders.add(cartonHeader);
        }
        body.put("cartonHeaders",cartonHeaders);
        JSONArray doLots=new JSONArray();
        for (DoLot doLot : stockOutTolly.getDoLots()) {
            JSONObject dolotJs=new JSONObject();
            dolotJs.put("areaNo",doLot.getAreaNo());
            dolotJs.put("batchNo",doLot.getBatchNo());
            dolotJs.put("consignorId", doLot.getConsignorId());
            dolotJs.put("docItemId",doLot.getDocItemId());
            dolotJs.put("customerBatchNo",doLot.getCustomerBatchNo());
            if (doLot.getExpireTime()!=null)
                dolotJs.put("expireTime",DateUtils.formatDate(doLot.getExpireTime()));
            dolotJs.put("fundProviderId",doLot.getFundProviderId());
            dolotJs.put("isDamaged",doLot.getIsDamaged());
            dolotJs.put("lotNo",doLot.getLotNo());
            dolotJs.put("merchantId",doLot.getMerchantId());
            dolotJs.put("poCode", doLot.getPoCode());
            dolotJs.put("productId",doLot.getProductId());
            if (doLot.getProductionTime()!=null)
                dolotJs.put("productionTime",DateUtils.formatDate(doLot.getProductionTime()));
            dolotJs.put("shopId", doLot.getShopId());
            dolotJs.put("transactionQty",doLot.getTransactionQty());
            dolotJs.put("warehouseId",doLot.getWarehouseId());
            dolotJs.put("warehouseTime",DateUtils.formatDate(doLot.getWarehouseTime()));
            doLots.add(dolotJs);
        }
        body.put("doLots",doLots);
        String bodyStr=body.toJSONString();
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType("1");
        wmsStockLog.setOperationUser(SecurityUtils.getCurrentUsername());
        wmsStockLog.setOrderSn(stockOutTolly.getWmsOutstock().getOutOrderSn());
        wmsStockLog.setRequest(bodyStr);
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/outboundGoods").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(bodyStr).execute().body();
        wmsStockLog.setResponse(resp);
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if ("success".equalsIgnoreCase(response.getString("flag"))){
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setStatus("12");
        }else {
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setStatus("12F");
            wmsStockLog.setRemark(response.getString("message"));
            throw new BadRequestException(wmsStockLog.getRemark());
        }
    }

    /**
     * 托盘信息回传
     * @param asnHeader
     */
    public void tpUpload(AsnHeader asnHeader,WmsStockLog wmsStockLog){
        JSONObject body=new JSONObject();
        body.put("id",asnHeader.getId()+"");
        body.put("doNo",asnHeader.getWmsOutstock().getOutOrderSn());
        body.put("tenantCode",asnHeader.getTenantCode());
        body.put("warehouseId",asnHeader.getWarehouseId());
        JSONArray detailList=new JSONArray();
        for (AsnDetail detail : asnHeader.getAsnDetails()) {
            JSONObject asnDetail=new JSONObject();
            asnDetail.put("productCode",detail.getProductCode());
            asnDetail.put("docItemId",detail.getDocItemId());
            asnDetail.put("qty",detail.getQty());
            asnDetail.put("caseQty",detail.getCaseQty());
            asnDetail.put("lpn",detail.getLpn());
            asnDetail.put("totalSkuWeight",detail.getTotalSkuWeight());
            asnDetail.put("lpnWeight",detail.getLpnWeight());
            asnDetail.put("lpnLength",detail.getLpnLength());
            asnDetail.put("lpnWidth",detail.getLpnWidth());
            asnDetail.put("lpnHeight",detail.getLpnHeight());
            asnDetail.put("lotNo",detail.getLotNo());
            asnDetail.put("customerBatchNo",detail.getCustomerBatchNo());
            asnDetail.put("productionTime",DateUtils.formatDate(detail.getProductionTime()));
            if (detail.getExpireTime()!=null){
                asnDetail.put("expireTime",DateUtils.formatDate(detail.getExpireTime()));
            }
            asnDetail.put("warehouseTime",DateUtils.formatDateTime(detail.getWarehouseTime()));
            asnDetail.put("poNo",detail.getPoNo());
            detailList.add(asnDetail);
        }
        body.put("detailList",detailList);
        String bodyStr=body.toJSONString();
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType("1");
        wmsStockLog.setOperationUser(SecurityUtils.getCurrentUsername());
        wmsStockLog.setOrderSn(asnHeader.getWmsOutstock().getOutOrderSn());
        wmsStockLog.setRequest(bodyStr);
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/lpnBatch").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(bodyStr).execute().body();
        wmsStockLog.setResponse(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if ("success".equalsIgnoreCase(response.getString("flag"))){
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setStatus("14");
        }else {
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setStatus("14F");
            wmsStockLog.setRemark(response.getString("message"));
        }
    }
    /**
     * 预装载单信息回传
     */
    public void preLoad(LoadHeader header,WmsStockLog wmsStockLog){
        JSONObject body=new JSONObject();
        body.put("id",header.getId()+"");
        body.put("tenantCode",header.getTenantCode());
        body.put("loadNo",header.getLoadNo());
        body.put("vechileNo",header.getVechileNo());
        body.put("warehouseId",header.getWarehouseId());
        JSONArray loadDetails=new JSONArray();
        for (LoadDetail detail : header.getLoadDetails()) {
            JSONObject loadDetail=new JSONObject();
            loadDetail.put("doNo",detail.getDoNo());
            loadDetail.put("lpn",detail.getLpn());
            loadDetails.add(loadDetail);
        }
        body.put("loadDetails",loadDetails);
        JSONArray cartonDetails =new JSONArray();
        for (CartonDetail detail : header.getCartonDetails()) {
            JSONObject cartonDetail=new JSONObject();
            cartonDetail.put("cartonNo",detail.getCartonHeader().getCartonNo());
            cartonDetails.add(cartonDetail);
        }
        body.put("cartonDetails",cartonDetails);
        String bodyStr=body.toJSONString();
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType("1");
        wmsStockLog.setOperationUser(SecurityUtils.getCurrentUsername());
        wmsStockLog.setOrderSn(header.getWmsOutstock().getOutOrderSn());
        wmsStockLog.setRequest(bodyStr);
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/preload").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(bodyStr).execute().body();
        System.out.println(resp);
        wmsStockLog.setResponse(resp);
        JSONObject response=JSONObject.parseObject(resp);
        if ("success".equalsIgnoreCase(response.getString("flag"))){
            wmsStockLog.setIsSuccess("1");
            wmsStockLog.setStatus("15");
        }else {
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setStatus("15F");
            wmsStockLog.setRemark(response.getString("message"));
        }
    }

    /**
     * 托盘车辆数回传
     * @param header
     */
    public void tpVehInfoUpload(VehicleHeader header){
        JSONObject body=new JSONObject();
        body.put("id",header.getId()+"");
        body.put("tenantCode",header.getTenantCode());
        body.put("loadNo",header.getLoadNo());
        body.put("loadType",header.getLoadType());
        body.put("entruckType",header.getEntruckType());
        body.put("warehouseId",header.getWarehouseId());
        if ("1".equals(header.getEntruckType())){
            body.put("lpnQty", header.getLpnQty());
            body.put("buildLpnQty",header.getBuildLpnQty());
            body.put("splitLpnQty",header.getSplitLpnQty());
        }
        String []doNos=header.getLoadDetails().split(",");
        JSONArray loadDetails=new JSONArray();
        for (String doNo : doNos) {
            JSONObject loadDetail=new JSONObject();
            loadDetail.put("doNo",doNo);
            loadDetails.add(loadDetail);
        }
        body.put("loadDetails",loadDetails);
        JSONArray vehDetails=new JSONArray();
        for (VehicleDetail detail : header.getVehicleDetails()) {
            JSONObject vehDetail=new JSONObject();
            vehDetail.put("vechileNo",detail.getVechileNo());
            vehDetail.put("qty",detail.getQty());
            vehDetails.add(vehDetail);
        }
        body.put("vechileDetails",vehDetails);
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/loadLpn").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(body.toJSONString()).execute().body();
        System.out.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        String flag=response.getString("flag");
        if (StringUtil.equalsIgnoreCase("FAILURE",flag))
            throw new BadRequestException(response.getString("message"));
    }

    /**
     * 通知单状态回传
     */
    public void noticeDocStatus(WmsInstock wmsInstock,WmsOutstock wmsOutstock,WmsStockLog wmsStockLog){
        JSONObject body=new JSONObject();
        if (wmsInstock!=null){
            body.put("orderSn",wmsInstock.getInOrderSn());
            body.put("orderType","0");
            body.put("optTime",DateUtils.now());
            body.put("tenantCode",wmsInstock.getTenantCode());
            switch (wmsInstock.getInStatus()){
                case "00":
                    body.put("status","00");//接单
                    break;
                case "10":
                    body.put("status","10");//到货
                    break;
                case "20":
                    body.put("status","20");//理货完成
                    break;
                case "60":
                    body.put("status","30");//入库
                    break;
                default:
                    return;
            }
        }else {
            body.put("orderSn",wmsOutstock.getOutOrderSn());
            body.put("orderType","1");
            body.put("optTime",DateUtils.now());
            body.put("tenantCode",wmsOutstock.getTenantCode());
            switch (wmsOutstock.getOutStatus()){
                case "00":
                    body.put("status","00");//接单
                    break;
                case "10":
                    body.put("status","10");//下架完成
                    break;
                case "40":
                    body.put("status","30");//出库
                    break;
                default:
                    return;
            }
        }
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/noticeStatus").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(body.toJSONString()).execute().body();
        wmsStockLog.setRequest(body.toJSONString());
        wmsStockLog.setResponse(resp);
        wmsStockLog.setOperationTime(new Timestamp(System.currentTimeMillis()));
        wmsStockLog.setType("1");
        wmsStockLog.setOrderSn(wmsInstock==null?wmsOutstock.getOutOrderSn():wmsInstock.getInOrderSn());
        System.out.println(resp);
        if (StringUtil.indexOf(resp,"{")!=0)
            throw new BadRequestException("不是JSON字符串:"+resp);
        JSONObject response=JSONObject.parseObject(resp);
        String flag=response.getString("flag");
        if (!StringUtil.equalsIgnoreCase("success",flag)){
            wmsStockLog.setIsSuccess("0");
            wmsStockLog.setRemark(response.getString("message"));
            throw new BadRequestException(response.getString("message"));
        }
        wmsStockLog.setIsSuccess("1");
    }
    /***
     * 库存属性调整
     */
    public void stockAttr(StockAttrNotice stockAttrNotice){
        JSONObject body=new JSONObject();
        body.put("srvlogId",stockAttrNotice.getSrvlogId());
        body.put("warehouseId",stockAttrNotice.getWarehouseId());
        body.put("tenantCode",stockAttrNotice.getTenantCode());
        JSONArray array=new JSONArray();
        for (StockAttrNoticeDetail item : stockAttrNotice.getItems()) {
            JSONObject detail=new JSONObject();
            detail.put("seq",""+item.getSeq());
            detail.put("docCode",item.getDocCode());
            detail.put("merchantId",item.getMerchantId());
            detail.put("transactionType",item.getTransactionType());
            detail.put("productId",item.getProductId());
            detail.put("docType",item.getDocType());
            detail.put("transactionQty",item.getTransactionQty());
            detail.put("productionTime",DateUtils.formatDate(item.getProductionTime()));
            detail.put("toWarehouseId",item.getToWarehouseId());
            detail.put("consignorId",item.getMerchantId());
            detail.put("isDamaged",item.getIsDamaged());
            detail.put("fundProviderId",item.getMerchantId());
            detail.put("lotNo",item.getLotNo());
            detail.put("wmsBatchCode",item.getWmsBatchCode());
            detail.put("customerBatchNo",item.getCustomerBatchNo());
            detail.put("sourceAsnInWarehouseTime",DateUtils.formatDateTime(item.getSourceAsnInWarehouseTime()));
            detail.put("lot27",item.getBookNo());
            detail.put("expireTime",item.getExpireTime());
            array.add(detail);
        }
        body.put("stockDetails",array);
        String resp=HttpRequest.post(urlPms+"/rest/flWms/rewrite/stockIncrementUpdate").header("token",JwtUtils.createToken(appKey,body.toJSONString())).body(body.toJSONString()).execute().body();
        System.err.println(resp);
        JSONObject response=JSONObject.parseObject(resp);
        String flag=response.getString("flag");
        if (!StringUtil.equalsIgnoreCase("success",flag))
            throw new BadRequestException(response.getString("message"));
    }


}
