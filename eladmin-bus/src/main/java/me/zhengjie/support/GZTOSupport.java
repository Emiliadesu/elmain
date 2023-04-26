package me.zhengjie.support;

import ZtoIntlSdk.Encrypt;
import ZtoIntlSdk.Import.ImportOrderBBCWithDtbAdd;
import ZtoIntlSdk.Request.BillEntity;
import ZtoIntlSdk.Request.ImportOrderBBCAddRequest;
import ZtoIntlSdk.Request.ImportOrderItem;
import ZtoIntlSdk.Response.ImportOrderAddResponse;
import ZtoIntlSdk.ZtoIntlException;
import ZtoIntlSdk.ZtoIntlResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.CrossBorderOrderDetails;
import me.zhengjie.domain.LogisticsUnarrivePlace;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.LogisticsUnarrivePlaceService;
import me.zhengjie.service.ShopInfoService;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.StringUtils;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.*;

/**
 * 中通国际支持类
 */
public class GZTOSupport {

    @Value("${express.g-zto-companyid}")
    private String COMPANY_Id;

    @Value("${express.g-zto-key}")
    private String KEY;

    @Value("${express.g-zto-url}")
    private String url;

    @Autowired
    private BusinessLogService businessLogService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private LogisticsUnarrivePlaceService logisticsUnarrivePlaceService;

    // 获取运单信息
    public void getMail(CrossBorderOrder order, Map<String,String>param) throws Exception {
        long start = System.currentTimeMillis();
        //ImportOrderBBCWithDtbAdd OrderBBCAdd;
        String companyId;
        String key;
        if (param!=null&&StringUtil.isNotEmpty(param.get("company_id"))&&StringUtil.isNotEmpty(param.get("KEY"))){
            //OrderBBCAdd = new ImportOrderBBCWithDtbAdd(url, param.get("company_id"), param.get("KEY"));
            companyId = param.get("company_id");
            key = param.get("KEY");
        }else {
            //OrderBBCAdd=new ImportOrderBBCWithDtbAdd(url,COMPANY_Id,KEY);
            companyId = COMPANY_Id;
            key = KEY;
        }
        ImportOrderBBCAddRequest orderbbc = new ImportOrderBBCAddRequest();
        if (StringUtil.isEmpty(order.getLogisticsNo())) {
            orderbbc.logisticsno = "";
        } else {
            orderbbc.logisticsno = order.getLogisticsNo();
        }
        orderbbc.orderno = order.getCrossBorderNo();
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        if (shopInfoDto != null) {
            orderbbc.shipper = shopInfoDto.getName();
            orderbbc.shippermobile = StringUtils.isBlank(shopInfoDto.getContactPhone())?"18888888888":shopInfoDto.getContactPhone();
            orderbbc.shippertelephone = StringUtils.isBlank(shopInfoDto.getContactPhone())?"18888888888":shopInfoDto.getContactPhone();
        }else {
            orderbbc.shipper = "富立物流";
            orderbbc.shippermobile = "18888888888";
            orderbbc.shippertelephone = "0666169082";
        }

        orderbbc.shipperprov = "浙江省";
        orderbbc.shippercity = "宁波市";
        orderbbc.shipperdistrict = "北仑区";
        orderbbc.shipperaddress = " 浙江省宁波市北仑区保税东区兴业四路二号";
        orderbbc.shippercountry = "中国";
        orderbbc.shipperzipcode = "315800";
        orderbbc.consignee = order.getConsigneeName();
        orderbbc.consigneeprov = order.getProvince();
        orderbbc.consigneecity = order.getCity();
        orderbbc.consigneedistrict = order.getDistrict();
        orderbbc.consigneeaddress = order.getConsigneeAddr();
        orderbbc.consigneemobile = order.getConsigneeTel();
        orderbbc.consigneetelephone = "";
        orderbbc.consigneecountry = "中国";
        orderbbc.consigneezipcode = "";
        orderbbc.idtype = 1;
        orderbbc.customerid = order.getBuyerIdNum();
        orderbbc.weight = Float.parseFloat(order.getGrossWeight());
        orderbbc.cumstomscode = "NBCUSTOMS";
//        orderbbc.cumstomscode = "OTHERCUSTOMS";//测试数据，需要注释！
        if (param!=null&&StringUtil.isNotEmpty(param.get("platformsource"))){
            orderbbc.platformSource = Integer.parseInt(param.get("platformsource"));
        }else {
            orderbbc.platformSource = 1363;
        }
//        orderbbc.platformSource = 10676;// 测试数据，需要注释！
        orderbbc.sortContent = "";
        orderbbc.netweight = Float.parseFloat(order.getNetWeight());
        orderbbc.shipType = "8";
        orderbbc.warehouseCode = "FL";

        BillEntity billentity = new BillEntity();
        billentity.ecpcode = order.getEbpCode();
        billentity.ecpname = order.getEbpName();
        billentity.ecpcodeG = order.getEbpCode();
        billentity.ecpnameG = order.getEbpName();
        billentity.quantity = 1;
        billentity.wraptype = "2";
        billentity.batchnumbers = "";
        billentity.agentCode = "";
        billentity.agentName = "";
        billentity.companyCode = "3105";
        billentity.shippercountryC = "142";
        billentity.shippercountryG = "156";
        billentity.consigneecountryC = "142";
        billentity.consigneecountryG = "156";
        orderbbc.billEntity = billentity;

        List<ImportOrderItem> list = new ArrayList<>();
        List<CrossBorderOrderDetails> orderDtails = order.getItemList();
        for (CrossBorderOrderDetails details : orderDtails) {
            ImportOrderItem item = new ImportOrderItem();
            item.itemId = details.getGoodsNo();
            item.itemName = details.getGoodsName();
            item.itemUnitPrice = Double.parseDouble(details.getPayment());
            item.itemQuantity = 1;
            item.itemRemark = "";
            item.dutyMoney = 0;
            item.blInsure = 0;
            item.length = 0;
            item.width = 0;
            item.high = 0;
            item.itemMaterial = "";
            item.itemWeight = 0.023f;
            item.currencyType = "CNY";
            item.itemRule = "";
            item.makeCountry = details.getMakeCountry();
            item.itemUnit = details.getUnit();
            list.add(item);
        }
        orderbbc.intlOrderItemList = list;
        try {
            orderbbc.Validate();
        } catch (ZtoIntlException e) {
            throw new BadRequestException(e.getErrorMsg());
        }
        ObjectMapper mapper = new ObjectMapper();
        String strData = mapper.writeValueAsString(orderbbc).replaceAll("(\r\n|\r|\n|\n\r)", "");
        Map<String, Object> txtParams = new HashMap();
        txtParams.put("data", strData);
        txtParams.put("msg_type", "ZtoIntlApi.Import.BondedInsert");
        txtParams.put("company_id", companyId);
        String sign = Encrypt.MD5Base64(strData + key);
        txtParams.put("data_digest", sign);
        String resp = HttpUtil.post(url+"/api/import/init",txtParams);
        //ZtoIntlResponse<ImportOrderAddResponse> bbd = OrderBBCAdd.Send(orderbbc);
        // 保存日志
        businessLogService.saveLog(BusTypeEnum.MAIL_GZTO, url, order.getCrossBorderNo(),  JSON.toJSONString(orderbbc), resp, (System.currentTimeMillis() - start));
        JSONObject respData = JSONObject.parseObject(resp);
        ZtoIntlResponse<ImportOrderAddResponseOverride> bbc = respData.toJavaObject(ZtoIntlResponse.class);
        if (bbc.getStatus()) {
            Object data = bbc.getData();
            if (data instanceof JSONObject){
                JSONObject dt = respData.getJSONObject("data");
                bbc.setData(dt.toJavaObject(ImportOrderAddResponseOverride.class));
            }
            order.setLogisticsNo(bbc.getData().logisticsId);
            if (StringUtil.isNotBlank(bbc.getExtended()))
                order.setAddMark(bbc.getExtended());
            else {
                if (param==null){
                    param=new HashMap<>();
                }
                param.put("buyerProvince",order.getProvince());
                param.put("buyerCity",order.getCity());
                param.put("buyerDistrict",order.getDistrict());
                param.put("buyerAddress",order.getConsigneeAddr());
                String addMark=getAddMark(param);
                order.setAddMark(addMark);
            }
            List<LogisticsUnarrivePlace> unarrivePlaces =  logisticsUnarrivePlaceService.queryByCityAndDistrictAndLogisticsId(order.getCity(), order.getDistrict(), order.getSupplierId());

            if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode()) &&
                    (CollectionUtils.isNotEmpty(unarrivePlaces) )) {
                order.setLogisticsNo("");
                order.setAddMark("");
                throw new BadRequestException("快递停发");
            }
        }else {
            throw new BadRequestException(bbc.getMsg());
        }

    }

    public String getAddMark(Map<String,String>map) throws Exception {
        String companyId=map.get("company_id");
        String key=map.get("KEY");
        if (StringUtil.isEmpty(companyId)) {
            companyId = "NBFLWL1359487E30";
            key = "92FFDEFF4E1047A6CE";
        }
        JSONObject dataJs=new JSONObject();
        dataJs.put("SenderProvince","浙江省");
        dataJs.put("SenderCity","宁波市");
        dataJs.put("SenderDistrict","北仑区");
        dataJs.put("SenderAddress","保税东区兴业四路二号");
        dataJs.put("BuyerProvince",map.get("buyerProvince"));
        dataJs.put("BuyerCity",map.get("buyerCity"));
        dataJs.put("BuyerDistrict",map.get("buyerDistrict"));
        dataJs.put("BuyerAddress",map.get("buyerAddress"));
        //dataJs.put("LogisticsId",mailNo);
        String data=dataJs.toString();
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update((data+key).getBytes(StandardCharsets.UTF_8));
        byte[] md5Bytes = mdTemp.digest();
        String encrypt = Base64.getEncoder().encodeToString(md5Bytes);
        //String encrypt=Base64Encoder.encode(Md5Utils.md5Hex(data+key),"UTF-8");
        Map<String,Object>paramMap = new HashMap<>();
        paramMap.put("data",data);
        paramMap.put("msg_type","zto.intl.getdatoubi");
        paramMap.put("data_digest",encrypt);
        paramMap.put("company_id",companyId);

        String result= HttpUtil.post(url+"/api/import/init",paramMap);
        JSONObject reBody=JSONObject.parseObject(result);
        String str1=reBody.getString("data");
        String str2=reBody.getString("extended");
        return str1+","+str2;
    }

    public JSONObject getTrance(String logisticsNo) throws Exception{
        String data="["+logisticsNo+"]";
        Map<String,Object>param=new HashMap<>();
        String companyId = "29565633d3224ef6985c29897708d65c";
        String key = "ac3f2ffa3842";
        param.put("company_id",companyId);
        param.put("msg_type","LATEST");
        param.put("data",data);
        MessageDigest mdTemp = MessageDigest.getInstance("MD5");
        mdTemp.update((data+key).getBytes(StandardCharsets.UTF_8));
        byte[] md5Bytes = mdTemp.digest();
        String encrypt = Base64.getEncoder().encodeToString(md5Bytes);
        param.put("data_digest",encrypt);
        String result=HttpUtil.post("http://japi.zto.com/traceInterfaceLatest",param);
        return JSON.parseObject(result);
    }
}
