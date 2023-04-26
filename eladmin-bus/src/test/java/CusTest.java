import cn.hutool.core.date.DatePattern;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.aliyun.openservices.shade.com.alibaba.fastjson.JSON;
import me.zhengjie.domain.ClearCompanyInfo;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.support.GZTOSupport;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.support.dewu.DewuSupport;
import me.zhengjie.support.sf.SFGJSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.JSONUtils;
import me.zhengjie.utils.constant.KJGMsgType;
import me.zhengjie.utils.constant.MsgType;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
public class CusTest {

    @Test
    public void testGetStatus() throws Exception {
        long start = System.currentTimeMillis();
        System.out.println("开始时间：" + start);
        KJGSupport kjgSupport = new KJGSupport();
        kjgSupport.deliver("1", "77120992763474");
        System.out.println("结束时间：" + (System.currentTimeMillis() - start));
    }

    @Test
    public void testStock() throws Exception {
        WmsSupport wmsSupport = new WmsSupport();
        wmsSupport.setUrl("http://kjgapi.fl56.net:19192/datahub/FluxWmsJsonApi/");
        wmsSupport.setAppkey("80AC1A3F-F949-492C-A024-7044B28C8025");
        wmsSupport.setAppSecret("80AC1A3F-F949-492C-A024-7044B28C8025");
        JSONObject jsonObject = wmsSupport.querySo("5010789905894618577", "31052022I631215896");
        System.out.println(jsonObject);
    }

    @Test
    public void testInOut() throws Exception {
        WmsSupport wmsSupport = new WmsSupport();
        wmsSupport.setUrl("http://kjgapi.fl56.net:19192/datahub/FluxWmsJsonApi/");
        wmsSupport.setAppkey("80AC1A3F-F949-492C-A024-7044B28C8025");
        wmsSupport.setAppSecret("80AC1A3F-F949-492C-A024-7044B28C8025");
//        JSONObject jsonObject = wmsSupport.queryAsn2("31052021I514576378");
        wmsSupport.deliver("S2205120006606");

    }

    @Test
    public void testEnum() throws Exception {
        Timestamp start =  DateUtils.parseDate("2022-01-19 09:59:39");
        Timestamp end = new Timestamp(System.currentTimeMillis());
        int days = DateUtils.differentDays(new Date(start.getTime()), new Date(end.getTime()));
        System.out.println(days);
    }

    @Test
    public void testDw() throws Exception {
//        DewuSupport dewuSupport = new DewuSupport();
//        dewuSupport.setAppkey("PXDRNkie1CtY1TW9");
//        dewuSupport.setAppSecret("8m92ZmhT0j0uF4KRtZJycP6guZJMm6");
//        dewuSupport.setBaseUrl("https://authgw.dewu.com");
//        dewuSupport.autoHandover("JDVB12165172117", new BigDecimal(250), "2208");

        Long t = 1648003324L;
        BigDecimal multiply = new BigDecimal(t).multiply(new BigDecimal(1000));
        Timestamp timestamp = new Timestamp(multiply.longValue());
        System.out.println(timestamp);
    }

    @Test
    public void testOut() {
        String str = "1";
        String[] split = str.split(",");
        List<String> shopIds = new ArrayList<>();
        for(int i = 0; i < split.length; i++) {
            shopIds.add(split[i]);
        }
        System.out.println(shopIds);
        System.out.println(CollectionUtils.contains(shopIds, "1"));
    }

    @Test
    public void testLogis() throws Exception {
        GZTOSupport gztoSupport = new GZTOSupport();
        com.alibaba.fastjson.JSONObject trance = gztoSupport.getTrance("77121198041026");
        System.out.println(trance);
    }

    @Test
    public void testSF() throws Exception {
        SFGJSupport sfgjSupport = new SFGJSupport();
        sfgjSupport.setPartnerID("FLWLXX");
        sfgjSupport.setCustId("5744651295");
        sfgjSupport.setUrl("http://ESG-ISP-GW.sf-express.com/isp/ws/sfexpressService");
        sfgjSupport.setAesKey("YuBD6SOUSj3gJrPqukWGq0V6JbPB6vSi");
        sfgjSupport.setHMACKey("BNC6EwjTAoy1kmt2BMu6T4/ZJT092gHO");
        String phone= "13912346448";
        String s = phone.substring(phone.length() - 4);
        JSONObject response = sfgjSupport.queryRoute("SF1348660198148", s);
//        JSONObject response = sfgjSupport.queryRoute(order.getLogisticsNo(), order.getConsigneeTel().substring(order.getConsigneeTel().length() - 4));
        if (response.getJSONObject("apiResultData") != null &&
                response.getJSONObject("apiResultData").getBool("success")) {
            JSONArray routesArray = response.getJSONObject("apiResultData").getJSONObject("msgData").getJSONArray("routeResps").getJSONObject(0).getJSONArray("routes");
            if (CollectionUtils.isNotEmpty(routesArray)) {
                System.out.println("有");
            }else {
                System.out.println("无");
            }
        }
    }

    @Test
    public void testE() throws Exception {

        String aa = "asdasdsa.xmls";
        System.out.println(aa.substring(aa.indexOf(".")));

//        JSONObject request = new JSONObject();
//        JSONObject Message = new JSONObject();
//        JSONObject Header = new JSONObject();
//        Header.putOnce("MftNo", "31052021I493948259");
//        Header.putOnce("WaybillNo", "77121075561132");
//        Header.putOnce("Flag", "00");
//        Message.putOnce("Header", Header);
//        request.putOnce("Message", Message);
//        String xml = JSONUtils.toXml(request);
//        KJGSupport kjgSupport = new KJGSupport();
//        String res = kjgSupport. reqDSKJG(
//                KJGMsgType.NINGBO_DECLARE_RETURN_REFRESH,
//                xml,
//                "dream6088",
//                "6bb46385-648d-4b63-b6b2-f2530fe7b415"
//        );
//        System.out.println(res);


//        Document xml = DocumentHelper.createDocument();
//        Element CEB623Message = xml.addElement("CEB623Message");
//
//        String guid = UUID.randomUUID().toString().toUpperCase();
//
//        CEB623Message.addAttribute("guid", guid);
//        CEB623Message.addAttribute("version", "1.0");
//        CEB623Message.addNamespace("", "http://www.chinaport.gov.cn/ceb");
//        CEB623Message.addNamespace("ns2", "http://www.w3.org/2000/09/xmldsig#");
//
//        Element InvtCancel = CEB623Message.addElement("InvtCancel");
//        InvtCancel.addElement("guid").setText(guid);
//        InvtCancel.addElement("appType").setText("1");
//        InvtCancel.addElement("appTime").setText(DateUtils.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
//        InvtCancel.addElement("appStatus").setText("2");
//        InvtCancel.addElement("customsCode").setText("3105");
//        InvtCancel.addElement("orderNo").setText("4866288991097202181");
//        InvtCancel.addElement("ebpCode").setText("11089697EJ");
//        InvtCancel.addElement("ebpName").setText("北京空间变换科技有限公司");
//        InvtCancel.addElement("ebcCode").setText("33024609V0");
//        InvtCancel.addElement("ebcName").setText("宁波环球云连信息科技有限公司");
//        InvtCancel.addElement("logisticsNo").setText("77121128938881");
//        InvtCancel.addElement("logisticsCode").setText("3122480063");
//        InvtCancel.addElement("logisticsName").setText("上海大誉国际物流有限公司");
//        InvtCancel.addElement("copNo").setText("31052021I513822091");
//        InvtCancel.addElement("invtNo").setText("31052021I210900901");
//        InvtCancel.addElement("buyerIdType").setText("1");
//        InvtCancel.addElement("buyerIdNumber").setText("140525199603266315");
//        InvtCancel.addElement("buyerName").setText("李苏航");
//        InvtCancel.addElement("buyerTelephone").setText("18839053880");
//        InvtCancel.addElement("agentCode").setText("3302980402");
//        InvtCancel.addElement("agentName").setText("宁波东方意向信息技术有限公司");
//        InvtCancel.addElement("reason").setText("客户取消订单");
//
//        Element BaseTransfer = CEB623Message.addElement("BaseTransfer");
//        BaseTransfer.addElement("copCode").setText("3302980402");
//        BaseTransfer.addElement("copName").setText("宁波东方意向信息技术有限公司");
//        BaseTransfer.addElement("dxpMode").setText("DXP");
//        BaseTransfer.addElement("dxpId").setText("DXPENT0000013249");
//
//        String xmlStr = xml.asXML();
//        xmlStr = xmlStr.replace("UTF-8", "utf-8");
//        xmlStr = xmlStr.replace("xmlns=\"\"", "");
//        String result = kjgSupport.reqZSKjg("CEB623", Base64.encodeBase64String(xmlStr.getBytes()), "dream6088",
//                "6bb46385-648d-4b63-b6b2-f2530fe7b415");
    }
}
