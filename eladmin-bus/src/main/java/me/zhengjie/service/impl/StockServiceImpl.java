package me.zhengjie.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.service.dto.InvLotLocIdAtt;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.service.dto.StackStockRecordQueryCriteria;
import me.zhengjie.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {
    private final BaseSkuService baseSkuService;
    private final CustomerKeyService customerKeyService;
    private final ShopInfoServiceImpl shopInfoService;
    @Autowired
    private StockAttrService stockAttrService;

    @Autowired
    private StackStockRecordService stackStockRecordService;
    @Override
    public JSONObject queryStock(String decData, String customersCode) {
        JSONObject data=new JSONObject(decData);
        String atTime=data.getStr("atTime");
        if (StringUtil.isBlank(atTime))
            throw new BadRequestException("atTime不能为空");
        int pageNo=data.getInt("pageNo");
        int pageSize=data.getInt("pageSize");
        List<String>productCodes=new ArrayList<>();
        JSONArray array=data.getJSONArray("productCodes");
        String merchantCode=data.getStr("merchantCode");
        if (CollectionUtils.isNotEmpty(array)){
            for (int i = 0; i < array.size(); i++) {
                String productCode=array.getStr(i);
                BaseSku baseSku=baseSkuService.queryByCode(productCode);
                productCodes.add(baseSku.getGoodsNo());
            }
        }
        StackStockRecordQueryCriteria criteria=new StackStockRecordQueryCriteria();
        if (CollectionUtil.isNotEmpty(productCodes))
            criteria.setSku(productCodes);
        if (StringUtil.isNotBlank(merchantCode))
            criteria.setShopCode(merchantCode);
        criteria.setCreateDate(atTime);
        Pageable pageable=new PageRequest(pageNo-1,pageSize);
        Map<String,Object>map=stackStockRecordService.queryPage(atTime,productCodes,merchantCode,pageable);
        data= JSONUtil.parseObj(map);
        int total=data.getInt("totalElements");
        List<StackStockRecord>records=data.getJSONArray("content").toList(StackStockRecord.class);
        JSONObject resp=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        if (CollectionUtils.isNotEmpty(records)){
            for (StackStockRecord record : records) {
                putRespData(jsonArray, record);
            }
        }
        resp.putOnce("item",jsonArray);
        resp.putOnce("totalCount",total);
        resp.putOnce("pageSize",pageSize);
        resp.putOnce("pageCount",total%pageSize!=0?total/pageSize+1:total/pageSize);
        return resp;
    }

    private void putRespData(JSONArray jsonArray, StackStockRecord stackStockRecord) {
        BaseSku baseSku=baseSkuService.queryByGoodsNo(stackStockRecord.getSku());
        if (baseSku==null) return;
        StockAttr stockAttr=stockAttrService.queryByWmsBatch(stackStockRecord.getBatchNo());
        if (stockAttr==null) return;
        JSONObject item=new JSONObject();
        item.putOnce("atTime", stackStockRecord.getCreateDate());
        item.putOnce("productLength",baseSku.getSaleL());
        item.putOnce("productWidth",baseSku.getSaleW());
        item.putOnce("productHeight",baseSku.getSaleH());
        if (baseSku.getSaleVolume()==null)
            baseSku.setSaleVolume(baseSku.getSaleL().multiply(baseSku.getSaleW()).multiply(baseSku.getSaleH()));
        item.putOnce("productVolume",baseSku.getSaleVolume());
        item.putOnce("productSpecification",baseSku.getProperty());
        item.putOnce("packageLength",baseSku.getPackL()==null?0:baseSku.getPackL());
        item.putOnce("packageWidth",baseSku.getPackW()==null?0:baseSku.getPackW());
        item.putOnce("packageHeight",baseSku.getPackH()==null?0:baseSku.getPackH());
        if (baseSku.getPackVolume()==null){
            if (baseSku.getPackL()!=null&&baseSku.getPackW()!=null&&baseSku.getPackH()!=null){
                baseSku.setPackVolume(baseSku.getPackL().multiply(baseSku.getPackW()).multiply(baseSku.getPackH()));
            }else {
                baseSku.setPackVolume(BigDecimal.ZERO);
            }
        }
        item.putOnce("packageVolume",baseSku.getPackVolume());
        item.putOnce("packageWeight",baseSku.getPackWeight());
        item.putOnce("packageNum",baseSku.getPackNum()==null?0:baseSku.getPackNum());
        item.putOnce("avgPackageVolume",item.getInt("packageNum")==0?0:baseSku.getPackVolume().divide(new BigDecimal(baseSku.getPackNum()), 0, RoundingMode.HALF_UP));
        item.putOnce("weightWithMaterial",baseSku.getGrossWeight());
        item.putOnce("gCode",baseSku.getGoodsNo());
        item.putOnce("productCode",baseSku.getGoodsCode());
        item.putOnce("productCname",baseSku.getGoodsNameC());
        item.putOnce("plateNum",stackStockRecord.getPlateNum());
        item.putOnce("id",baseSku.getId());
        ShopInfoDto shopInfoDto=shopInfoService.queryById(baseSku.getShopId());
        item.putOnce("merchantCode",shopInfoDto.getCode());
        item.putOnce("warehouseId","8020");
        item.putOnce("stockBusinessType","B2C");
        item.putOnce("att8",stackStockRecord.getCustomerBatch());//客户批次号
        item.putOnce("originBatchCode",stackStockRecord.getBatchNo());
        item.putOnce("firstReceiveDate",stackStockRecord.getFirstReceiveDate());
        item.putOnce("asnArrivedTime",stackStockRecord.getFirstReceiveDate());
        item.putOnce("asnCode",stackStockRecord.getAsnCode());
        item.putOnce("isDamaged", stackStockRecord.getIsDamaged());
        item.putOnce("isColdStorage",0);
        item.putOnce("temperature",0);
        item.putOnce("stockCount",stackStockRecord.getStockQty());
        jsonArray.add(item);
    }

    @Override
    public JSONObject querySapStock(String decData, String customersCode) {
        JSONObject data=new JSONObject(decData);
        String merchantCode=data.getStr("merchantCode");
        String tenantCode=data.getStr("tenantCode");
        ShopInfo shopInfo=shopInfoService.queryByShopCode(merchantCode);
        List<BaseSku>baseSkuList=baseSkuService.queryByShopId(shopInfo.getId());
        CustomerKeyDto customerKey=customerKeyService.findByCustCode(customersCode);
        JSONArray stockData=FluxUtils.sapQueryStock(customerKey);
        JSONObject resp=new JSONObject();
        JSONArray jsonArray=new JSONArray();
        List<InvLotLocIdAtt>records=stockData.toList(InvLotLocIdAtt.class);
        if (CollectionUtils.isNotEmpty(records)){
            for (InvLotLocIdAtt record : records) {
                for (BaseSku baseSku : baseSkuList) {
                    if (StringUtil.equals(baseSku.getGoodsNo(),record.getSku())){
                        JSONObject item=new JSONObject();
                        StockAttr stockAttr = stockAttrService.queryByWmsBatch(record.getLotNum());
                        if (stockAttr==null){
                            continue;
                        }else {
                            item.putOnce("superviseCode",stockAttr.getSuperviseCode());
                            item.putOnce("bookNo", stockAttr.getBookNo());
                            item.putOnce("subType",stockAttr.getSubType());
                        }
                        item.putOnce("customerBatchCode",record.getLotAtt09());
                        item.putOnce("att3",record.getLotAtt04());
                        item.putOnce("productCode",baseSku.getGoodsCode());
                        int qty=record.getQty();
                        if (StringUtil.equals("良品",record.getLotAtt08())){
                            item.putOnce("realStockNum",qty);
                            item.putOnce("damageStockNum",0);
                        }else if (StringUtil.equals("非良品",record.getLotAtt08())){
                            item.putOnce("realStockNum",0);
                            item.putOnce("damageStockNum",qty);
                        }
                        jsonArray.add(item);
                        break;
                    }
                }
            }
        }
        resp.putOnce("item",jsonArray);
        return resp;
    }

    private void getStockCount(Map<String, Map<String, Object>> badMap, InvLotLocIdAtt record) {
        if (badMap.get(record.getSku()+"-"+record.getLotNum())!=null){
            Map<String,Object>skuInfo=badMap.get(record.getSku()+"-"+record.getLotNum());
            int plateNum=Integer.parseInt(skuInfo.get("plateNum")+"");//托盘数（库位数）
            int stockCount=Integer.parseInt(skuInfo.get("stockCount")+"");//总库存数
            String asnArrivedTime=record.getLotAtt03();
            String asnCode=record.getLotAtt04();
            skuInfo.put("plateNum",plateNum+1);
            skuInfo.put("stockCount",stockCount+(record.getQty()-record.getQtyAllocated()));
            skuInfo.put("asnArrivedTime",asnArrivedTime);
            skuInfo.put("asnCode",asnCode);
            badMap.put(record.getSku()+"-"+record.getLotNum(),skuInfo);
        }else {
            Map<String,Object>skuInfo=new HashMap<>();
            int plateNum=1;//托盘数（库位数）
            int stockCount=record.getQty()-record.getQtyAllocated();//总库存数
            String asnArrivedTime=record.getLotAtt03();//进场日期
            String asnCode=record.getLotAtt04();//报检号
            skuInfo.put("plateNum",plateNum);
            skuInfo.put("stockCount",stockCount);
            skuInfo.put("asnArrivedTime",asnArrivedTime);
            skuInfo.put("asnCode",asnCode);
            badMap.put(record.getSku()+"-"+record.getLotNum(),skuInfo);
        }
    }
}
