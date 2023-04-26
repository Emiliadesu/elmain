package me.zhengjie.support.cainiao;

import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.json.XML;
import com.taobao.pac.sdk.cp.PacClient;
import com.taobao.pac.sdk.cp.SendSysParams;
import com.taobao.pac.sdk.cp.dataobject.request.CAINIAO_GLOBAL_LASTMILE_HOIN_CALLBACK.CainiaoGlobalLastmileHoinCallbackRequest;
import com.taobao.pac.sdk.cp.dataobject.request.CAINIAO_GLOBAL_LASTMILE_HOIN_CALLBACK.Parcel;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_LOGISTICS_DETAIL_QUERY.CrossborderLogisticsDetailQueryRequest;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_ORDER_CONSIGN.Contact;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_ORDER_CONSIGN.CrossborderOrderConsignRequest;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_ORDER_CONSIGN.Goods;
import com.taobao.pac.sdk.cp.dataobject.request.CROSSBORDER_SALES_CANCEL.CrossborderSalesCancelRequest;
import com.taobao.pac.sdk.cp.dataobject.request.CUSTOMS_DECLARE_ORDER_MESSAGE_QUERY.CustomsDeclareOrderMessageQueryRequest;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_DECLARE_ORDER_CALLBACK.GaCustomsDeclareOrderCallbackRequest;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_DECLARE_ORDER_CALLBACK.InventoryReturn;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_TAX_CALLBACK.GaCustomsTaxCallbackRequest;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_TAX_CALLBACK.Tax;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_TAX_CALLBACK.TaxHeadRd;
import com.taobao.pac.sdk.cp.dataobject.request.GA_CUSTOMS_TAX_CALLBACK.TaxRd;
import com.taobao.pac.sdk.cp.dataobject.request.NB_CUSTOMS_AUDIT_STATUS_CALLBACK.Body;
import com.taobao.pac.sdk.cp.dataobject.request.NB_CUSTOMS_AUDIT_STATUS_CALLBACK.NbCustomsAuditStatusCallbackRequest;
import com.taobao.pac.sdk.cp.dataobject.response.CAINIAO_GLOBAL_LASTMILE_HOIN_CALLBACK.CainiaoGlobalLastmileHoinCallbackResponse;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.CrossborderLogisticsDetailQueryResponse;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.Node;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_LOGISTICS_DETAIL_QUERY.PackageStatus;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_ORDER_CONSIGN.CrossborderOrderConsignResponse;
import com.taobao.pac.sdk.cp.dataobject.response.CROSSBORDER_SALES_CANCEL.CrossborderSalesCancelResponse;
import com.taobao.pac.sdk.cp.dataobject.response.CUSTOMS_DECLARE_ORDER_MESSAGE_QUERY.CustomsDeclareOrderMessageQueryResponse;
import com.taobao.pac.sdk.cp.dataobject.response.GA_CUSTOMS_DECLARE_ORDER_CALLBACK.GaCustomsDeclareOrderCallbackResponse;
import com.taobao.pac.sdk.cp.dataobject.response.GA_CUSTOMS_TAX_CALLBACK.GaCustomsTaxCallbackResponse;
import com.taobao.pac.sdk.cp.dataobject.response.NB_CUSTOMS_AUDIT_STATUS_CALLBACK.NbCustomsAuditStatusCallbackResponse;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.BusinessLogService;
import me.zhengjie.service.ConfigService;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import me.zhengjie.utils.constant.KJGMsgType;
import me.zhengjie.utils.enums.BusTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CaiNiaoSupport {
    private final String appKey = "090754";
    private final String appSecret = "6Au37zr568qS4t8rj96A4zfllJre3720";
    private final String guanWuAppKey = "CP_DECLARE_COMMON_XML_APP_KEY";
    private final String guanWuAppSecret = "123456";
    private final String guanWuCpCode = "CP_QG_NBZXC004";
    private final String url = "http://link.cainiao.com/gateway/link.do";//"http://link.cainiao.com/gateway/link.do";
    private static volatile CaiNiaoSupport caiNiaoSupport;
    public volatile PacClient pacClient;
    public volatile PacClient guanWuClient;//关务的pacClient

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private BusinessLogService businessLogService;

    @Autowired
    private ConfigService configService;

    private CaiNiaoSupport(){
        pacClient = new PacClient(this.appKey, this.appSecret, this.url);
        pacClient.getLogger().setLogEnable(false);
        guanWuClient = new PacClient(this.guanWuAppKey, this.guanWuAppSecret, this.url);
    }

    public static CaiNiaoSupport initSupport(){
        if(caiNiaoSupport == null){
            synchronized (CaiNiaoSupport.class) {
                if(caiNiaoSupport == null){
                    caiNiaoSupport = new CaiNiaoSupport();
                }
            }
        }
        return caiNiaoSupport;
    }

    public static void main(String[] args) {
        CainiaoShopInfo caiNiaoShopInfo = new CainiaoShopInfo();
        caiNiaoShopInfo.setBusinessUnitId("B00016001");
        caiNiaoShopInfo.setCnOwnerId("2206903100025");
        caiNiaoShopInfo.setHsCode("6204420000");
        caiNiaoShopInfo.setCpCode("279a8aae231fc85b04c06cd4b52d3a9b");
        CrossBorderOrder order = new JSONObject("{\"ebpName\":\"上海寻梦信息技术有限公司\",\"orderMsg\":\"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\"?>\\n<kjsoOrderRequest>\\n  <storer>3302461510</storer>\\n  <wmwhseid>3302461510</wmwhseid>\\n  <externalNo>31052023I649405219</externalNo>\\n  <externalNo2>XP1423022213201782300097006795</externalNo2>\\n  <shipToName>杨文娟</shipToName>\\n  <shipToPhone>13520878458</shipToPhone>\\n  <userName>杨文娟</userName>\\n  <billDate>20230222</billDate>\\n  <paymentDateTime>20230222 15:30:25</paymentDateTime>\\n  <receipType>01</receipType>\\n  <provinceName>北京市</provinceName>\\n  <cityName>北京市</cityName>\\n  <regionName>海淀区</regionName>\\n  <shipToAddr>北京市北京市海淀区西三旗新康园小区9-2801室</shipToAddr>\\n  <shipToPassCode>800-北京</shipToPassCode>\\n  <zipCode>100000</zipCode>\\n  <dsPlatform>拼多多</dsPlatform>\\n  <dsStorer/>\\n  <carrierKey>73698071-8</carrierKey>\\n  <expressID>77128332709826</expressID>\\n  <expressType/>\\n  <payment>344.0</payment>\\n  <flag/>\\n  <lgRemark01>800-,北京</lgRemark01>\\n  <tdq>1</tdq>\\n  <item>\\n    <externalNo>31052023I649405219</externalNo>\\n    <sku>P31051510223902030</sku>\\n    <uom>盒</uom>\\n    <taxPrice>157.65</taxPrice>\\n    <expectedQty>2.0</expectedQty>\\n  </item>\\n</kjsoOrderRequest>\\n\",\"payTime\":1677042199000,\"city\":\"北京市\",\"clearStartTime\":1677051025000,\"addMark\":\"800-,北京\",\"platformShopId\":\"866896466\",\"orderForm\":\"1134\",\"booksNo\":\"T3105W000172\",\"consigneeName\":\"~AgAAAAHp3pYFV/+PkQB0m+lEhFKZVSsG/pMlGqoTaoA=~wD+wH1dw~4~~\",\"declareStatus\":\"22\",\"province\":\"北京市\",\"ebpCode\":\"3105961682\",\"payment\":\"344\",\"id\":8865575,\"shopId\":3365,\"buyerIdNum\":\"#ubkI#AgAAAAHp3pYKV/+PkQCrnxgFMHoKK8CdXe7uNABefSpPlXNL1rQMSD1RJahmGDLi#4##\",\"clearSuccessBackTime\":1677051138000,\"orderNo\":\"230222-029512252840089\",\"disAmount\":\"0\",\"consigneeAddr\":\"~AgAAAAHp3pYHV/+PkQF8EjjgWRePtB4XtaKYM6ZnyRe+m7C2Mejp50MRGEJkLlAvlKVCgRrMwOUFLSmbaz2YXKvzsEF+dCma3cEuXM+cfqos6AJIgDWOaTSHt69AD/2UJNNo3VnZQCpWz+h/1pmdyw==~LXHBch8pFwu8LXHBch8pRvZC3dZS1wepI34kMhxwhBjH2zpaawRykk1+m2yEMnan7z5xga0K4gI70wmqw9RE6AHg~4~~\",\"consigneeTel\":\"$aM3Gj9VcKLid$AgAAAAHp3pYGV/+PkQCJ2Nrc5LELE7depUy5zoS6xi8=$4$$\",\"orderCreateTime\":1677042192000,\"postFee\":\"0\",\"receivedBackTime\":1677051024000,\"crossBorderNo\":\"XP1423022213201782300097006795\",\"buyerName\":\"~AgAAAAHp3pYLV/+PkQAN6FRtnHxgLxVkTeBlslN3rkw=~wD+wH1dw~4~~\",\"clearStartBackTime\":1677051029000,\"clearSuccessTime\":1677051137000,\"customersId\":189,\"paymentNo\":\"~AgAAAAHp3pYEV/+PkQCrZnArL8yovZvJs7F7dcQ3vKWKQsdCouqflew3PlQHFfMV~+DM5cld3HhZpCYqvOupOjmKK3P2uV+OVN8p4Wg8JEbhbNgmIuCmutxgvY3wcQqQQ+xmjvF53Ot37xzOdozIufPaXXrKnwVp4uJeH~4~~\",\"netWeight\":\"0.0940\",\"grossWeight\":\"0.1128\",\"createBy\":\"SYSTEM\",\"buyerPhone\":\"$aM3Gj9VcKLid$AgAAAAHp3pYGV/+PkQCJ2Nrc5LELE7depUy5zoS6xi8=$4$$\",\"orderSeqNo\":\"~AgAAAAHp3pYEV/+PkQCrZnArL8yovZvJs7F7dcQ3vKWKQsdCouqflew3PlQHFfMV~+DM5cld3HhZpCYqvOupOjmKK3P2uV+OVN8p4Wg8JEbhbNgmIuCmutxgvY3wcQqQQ+xmjvF53Ot37xzOdozIufPaXXrKnwVp4uJeH~4~~\",\"buyerAccount\":\"null\",\"createTime\":1677051020000,\"district\":\"海淀区\",\"logisticsNo\":\"77128332709826\",\"declareNo\":\"31052023I649405219\",\"itemList\":[{\"goodsNo\":\"P31051510223902030\",\"orderNo\":\"230222-029512252840089\",\"orderId\":8865575,\"goodsId\":144254,\"barCode\":\"8033120250514\",\"unit\":\"盒\",\"netWeight\":\"0.047\",\"grossWeight\":\"0.0564\",\"hsCode\":\"1504200091\",\"dutiableValue\":\"157.65\",\"qty\":\"2\",\"payment\":\"344.00\",\"id\":53612806,\"goodsCode\":\"7495443\",\"taxAmount\":\"28.70\",\"goodsName\":\"ZYSU金凯撒Meaquor1000鱼油胶囊30粒\",\"makeCountry\":\"意大利\",\"dutiableTotalValue\":\"315.30\"}],\"taxAmount\":\"28.70\",\"payCode\":\"13\",\"platformCode\":\"PDD\",\"status\":235}").toBean(CrossBorderOrder.class);
        CaiNiaoSupport support = new CaiNiaoSupport();
        ShopInfo shopInfo = new JSONObject("{\"serviceType\":\"01\",\"createUserId\":22,\"code\":\"PDD-GC\",\"platformId\":18,\"registerType\":\"1\",\"booksNo\":\"T3105W000172\",\"createTime\":1663578446000,\"orderSourceType\":\"1\",\"custId\":189,\"name\":\"GoldenCaesar海外旗舰店\",\"id\":3365,\"serviceId\":5,\"platformCode\":\"PDD\"}").toBean(ShopInfo.class);
        order.getItemList().get(0).setGoodsNo("610240611644");
        shopInfo.setName("货主库存测试商家01");
        PddCloudPrintData printData = new JSONObject("{\"crossBorderOrderNo\":\"XP1423022213201782300097006795\",\"orderNo\":\"230222-029512252840089\",\"printData\":\"{\\\"encryptedData\\\":\\\"4c23c09ff38d4dc86c25aa6abefa895c09536e94037ccddee211384b1d2a1380c17bf9a3bbc6562ce2842e7441ce19a480fa3f2f1594cbfa67e07b64fb774c61fdff555623c46b4cf8f08092666f9359cf561cb9c7225f61fd03ee8d46a0d2a3a08406c14b677014aa90a9c4e99ffe7927eaef7710eb735a50b1d66cf5eb3c811c0d87c7fc200140823b62d4be89b002491f1a08a00e27971d95855726c86f92f7364d49490cc03d26c045953b17fd2a5aa82c6aca49a17bdc290c002621bac1b2a694e02c2d7cd6207396b204eef210b6228ba280ec44371c02e69f55977d046e48532941725b41e1e9ed962c71c840db3ed58d84ce716bbe77ac6785e500f6a6b739b7885815750ba367bb867933de7239aa63301b7a11420f24ffa141d3a3f813f46307e05d0ba041a6ab2cafdd4f60443af9ba16d579d133e2ed8efc88d3da3aacfac6bcf887145defa865a42a391c9b89813299e264924f1a2a6a86ba402cb476a7997905b801f7add9cda324fd11ec84b36507a45268c5a9386a81604eb02dd14dc57e5ddcfa98ba6aed49904798b63000be2d8b405eaf764bcf325f63b3837dcde11a94b92c318dffe1f1751970cfde91e066f85f7cb2bbb2456c701bf6ec4432212621bed7fddc0f12b210a03d06f0cde17a7c1f236edf13b68a4afdd83af41923123dd0d47b61d683dac998bebb8c92dc83bbd18909105fbf0168417a61505dba48c9c585b930f624f5d4f66a597fcaac195b961cc6d58f0b687b0599b7a591a8cf09f66a80c5344fbb1c2f41a77a8dfd31c9024d2973b8445bfe844af5cb896eb5de9f2cbdc60de51c50beb7cce89af073c0f466b24595da112e290c574f05d459512d33ca68b90a7c97ea418298855357e58abacf322f1dced5d1a25dbe375c6e1764c414417b1612ed932ae2a87008f2c3dc0ded65557ba967f7c175703a17bc5f7f58358d5429cf03e85f3e6599892c859c99caad97f0f0e1d8d35e4dbbf2af8f160446361f4a35d5378702bac252cc3453c1c7898a8e14007ca6854ac4dfcdc4474444148d8ae43b4bf93c4d6a05e97113b6a780c7aebb7923c211d26a171457363b1bae2fcd5d9bc960320a2033ccc0a346531655221699f703b032332eae9086eed48eb2424cd879da84d982f7600a8b21583c7241ddc9285759e84732ddafbadb431b54820aaff55b0536db3a560c47af801455450e4a3f1415f99903afbedb8902dae5dfcc68635c6fa2fe4c0f7639397de5db9a28b8b94d920f9a4cedf2d14e6b056b9da4e4cca0814eb6ddc08e4f5ed755777b1c758e7c78d563e67ec35f0484d5ec149f587e1fc10b6d2556a90f6068bb496c4b11a78d8523ad80efa7c1e79368b5184dbe0c7de476c2c85ccd0c3be900a558709e8fd666c8a82936d779f45da649dc351f71f59261f2bd61a1feee0488fd1a6412c1e76b7437263ecbea3a9b2fa39303c750f3b593d133a7219743f8b537b3cddd93dcd6e8190f297ac54be4309ca6b0b8d05d3f878633ee0df8e3b0e8df82b56e98b9fdd825e4f7a9bc75cad050c95d10f0e647ba799402c2fbccb985614d9d44fa7ded44965b4feefacc34eae18248a6116c1e32fcb77bf4583abc29d0fe156cdef247bbf870c059d4b6e8074507b575dd1ef36b551280a0bf27cce3c4e4d49122052ebe40dc55d465cdb7dfb7a3c118d470945b4951d97bfe84cf54a8bcadd17e6b91b7c58a73fc3c7e4c246d9590a7181bded81c89d7b7e8e9a319a984dc85f03bdb481b9a4fc9135cc55cf09a9c90e2f092a5da9f5165e09a1e94a6a328ef8d43710ec9aff0db9f00b6b1bbf402997e078e929aeec62fc9be16bfd6123fefc303d3c52b4c032c06e3bfc5a6938ea067?v=6&pv=1\\\",\\\"signature\\\":\\\"eRdh+ke6EDobD64qZ7LYeHJ7RVokgDwajwUBdx7rTjwFkCaWdEJMVFmKdKNRYkGsxTSwPypHjc0lBg/8dt9eFv5kfyvrts53LCFocIQfivUVtEy4+HwwMG9+5WZnB3k3dzrQliV3zVOdnN4tEt3bW4vjNsdFuTp0alzL15xchdA=\\\",\\\"templateUrl\\\":\\\"http://pinduoduoimg.yangkeduo.com/msfe/2019-0221/2bae9ebcf3c05fb50541760529abc837.xml\\\",\\\"ver\\\":\\\"6\\\"}\",\"id\":43403}").toBean(PddCloudPrintData.class);
        //发货给菜鸟
//        String lpCode = support.sendOrderToWms(order,shopInfo,caiNiaoShopInfo,printData);
//        System.out.println(lpCode);
        //查询状态
        //order.setLpCode("LP00560799918466");
        //PackageStatus packStatus = support.queryWmsStatus(order,caiNiaoShopInfo);
        //System.out.printf("[%s]%s",packStatus.getLogisticStatus(),packStatus.getLogisticStatusDesc());
        //揽收
//        order.setLpCode("LP00560799918466");
//        order.setDeliverTime(DateUtils.parseDateTime("2023-02-22 15:30:26"));
//        support.lastmineHoinCallback(order,caiNiaoShopInfo);
        order.setLpCode("LP00560799918466");
        boolean isSuccess = support.cancelDeclare(order,caiNiaoShopInfo);
        System.out.println(isSuccess);
    }
    /**
     * 发货给菜鸟
     *
     * @return
     */
    public String sendOrderToWms(CrossBorderOrder order, ShopInfo shopInfo, CainiaoShopInfo caiNiaoShopInfo,PddCloudPrintData printData) {
        long start = System.currentTimeMillis();
        CrossborderOrderConsignRequest request = new CrossborderOrderConsignRequest();
        /**
         * 填充参数
         * .......................................................
         * */
        request.setExternalOrderId(order.getOrderNo());
        request.setUserId(caiNiaoShopInfo.getCnOwnerId());
        request.setShopNick(shopInfo.getName());
        switch (order.getPlatformCode()) {
            case "YZ":
                request.setOrderSource(713);
                break;
            case "PDD":
                request.setOrderSource(701);
                break;
            case "MGJ":
                request.setOrderSource(222);
                break;
            case "BD":
                request.setOrderSource(802);
                break;
            case "Ymatou":
                request.setOrderSource(801);
                break;
            case "AiKucun":
                request.setOrderSource(815);
                break;
            default:
                throw new BadRequestException("该平台不在菜鸟范围内");
        }
        request.setOrderType("01");
        request.setStoreCode("NBZXC004");//保税仓编码
        request.setPostFee(new BigDecimal(order.getPostFee()).multiply(new BigDecimal("100")).longValue());
        request.setCurrency("CNY");
        request.setBuyerName(order.getBuyerName().length()>64?order.getBuyerName().substring(0,64):order.getBuyerName());
        request.setBuyerId(order.getBuyerAccount());
        request.setBuyerIDType(1);
        request.setBuyerIDNo(order.getBuyerIdNum().length()>512?order.getBuyerIdNum().substring(0,512):order.getBuyerIdNum());
        switch (order.getPayCode()) {
            case "02":
                request.setPayType("01");
                break;
            case "13":
                request.setPayType("02");
                break;
            case "38":
                request.setPayType("03");//有赞支付
                break;
            case "64":
                request.setPayType("25");//多多支付
                break;
            case "":
                request.setPayType("04");//网易支付
                break;
        }
        request.setPayOrderId(order.getPaymentNo().length()>32?order.getPaymentNo().substring(0,32):order.getPaymentNo());
        request.setBuyTime(order.getCreateTime());
        request.setPayTime(order.getPayTime());
        request.setDutiablePrice(new BigDecimal(order.getPayment())
                .add(new BigDecimal(order.getDisAmount())).
                        subtract(new BigDecimal(order.getTaxAmount()))
                .add(new BigDecimal(order.getPostFee()))
                .multiply(new BigDecimal("100")).longValue());//(支付价+优惠-税费+运费)*100
        request.setCustomsTax(0L);
        request.setConsumptionTax(0L);
        request.setVAT(new BigDecimal(order.getTaxAmount()).multiply(new BigDecimal("100")).longValue());
        request.setTotalTax(request.getVAT());
        request.setInsurance(0L);
        request.setCoupon(new BigDecimal(order.getDisAmount()).multiply(new BigDecimal("100")).longValue());
        request.setActualpayment(new BigDecimal(order.getPayment()).multiply(new BigDecimal("100")).longValue());
        List<Goods> goodsList = new ArrayList<>();
        for (CrossBorderOrderDetails detail : order.getItemList()) {
            Goods goods = new Goods();
            BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
            goods.setItemId(baseSku.getOuterGoodsNo());
            goods.setExtItemId(detail.getGoodsNo());
            goods.setGoodsQuantity(Integer.parseInt(detail.getQty()));
            goods.setGoodsPrice(new BigDecimal(detail.getDutiableValue()).multiply(new BigDecimal(100)).longValue());
            //queryTaxrate(detail.getHsCode(),Long.parseLong(shopInfo.getCode()));
            goods.setGoodsActualPrice(new BigDecimal(detail.getDutiableValue()).multiply(new BigDecimal(100)).longValue());
            goods.setGoodsCustomsTax(0L);
            goods.setGoodsConsumptionTax(0L);
            goods.setGoodsVAT(new BigDecimal(detail.getTaxAmount()).multiply(new BigDecimal(100)).longValue());
            goods.setGoodsTotalTax(goods.getGoodsVAT());
            goods.setHscode(detail.getHsCode());
            goodsList.add(goods);
        }
        request.setGoodsList(goodsList);
        Contact receiverContact = new Contact();
        receiverContact.setProvince(order.getProvince());
        receiverContact.setCity(order.getCity());
        receiverContact.setDistrict(order.getDistrict());
        receiverContact.setAddress(order.getConsigneeAddr().length()>512?order.getConsigneeAddr().substring(0,512):order.getConsigneeAddr());
        receiverContact.setZipCode("000000");
        receiverContact.setContactName(order.getConsigneeName().length()>64?order.getConsigneeName().substring(0,64):order.getConsigneeName());
        receiverContact.setMobilePhone(order.getConsigneeTel().length()>64?order.getConsigneeName().substring(0,64):order.getConsigneeName());
        request.setReceiverContact(receiverContact);
        Contact senderContact = new Contact();
        senderContact.setCountry("CN");
        senderContact.setProvince("浙江省");
        senderContact.setCity("宁波市");
        senderContact.setDistrict("北仑区");
        senderContact.setAddress("保税东区兴业四路二号");
        senderContact.setZipCode("000000");
        senderContact.setContactName("富立物流");
        senderContact.setMobilePhone("0574-86873070");
        request.setSenderContact(senderContact);
        Contact refundContact = new Contact();
        refundContact.setCountry("CN");
        refundContact.setProvince("浙江省");
        refundContact.setCity("宁波市");
        refundContact.setDistrict("北仑区");
        refundContact.setAddress("保税东区兴业四路二号");
        refundContact.setZipCode("000000");
        refundContact.setContactName("富立物流");
        refundContact.setMobilePhone("0574-86873070");

        request.setRefunderContact(refundContact);
        JSONObject ext = new JSONObject();
        ext.putOnce("is_CNQG","1");
        ext.putOnce("extApplyWaybill","Y");
        ext.putOnce("cpCode","ZTO");
        ext.putOnce("mailNo",order.getLogisticsNo());
        JSONObject print = new JSONObject(printData.getPrintData());
        Config config = configService.queryByK("PDD_CLOUD_PRINT_MODEL_URL");
        print.set("templateUrl",config.getV());
        ext.putOnce("printData",print.toString());
        request.setExtendsField(ext.toString());
        /**********************************************************************/
        SendSysParams params = new SendSysParams();
        params.setFromCode(caiNiaoShopInfo.getCpCode());
        CrossborderOrderConsignResponse resp = pacClient.send(request, params);
        businessLogService.saveLog(BusTypeEnum.CNLINK_OUT, request.getApi(), url, request.getExternalOrderId(),  new JSONObject(request).toString(), new JSONObject(resp).toString(), (System.currentTimeMillis() - start));
        if (resp.isSuccess()) {
            return resp.getLgorderCode();
        }
        throw new BadRequestException(resp.getErrorMsg());
    }

    public void decalreResultCallBackGA(CrossBorderOrder order) throws Exception{
        long start = System.currentTimeMillis();
        String declareInfoRes=getDeclareInfo2(order.getLpCode(),guanWuCpCode);
        JSONObject resJSON = new JSONObject(declareInfoRes);
        GaCustomsDeclareOrderCallbackRequest request = new GaCustomsDeclareOrderCallbackRequest();
        InventoryReturn inv =new InventoryReturn();
        inv.setGuid(resJSON.getStr("guid"));
        inv.setCustomsCode(resJSON.getStr("customsCode"));
        inv.setEbpCode(resJSON.getStr("ebpCode"));
        inv.setEbcCode(resJSON.getStr("ebcCode"));
        inv.setAgentCode(resJSON.getStr("agentCode"));
        inv.setCopNo(resJSON.getStr("copNo"));
        inv.setPreNo(order.getDeclareNo());
        inv.setInvtNo(order.getInvtNo());
        inv.setReturnStatus("800");
        inv.setReturnTime(DateUtils.format(order.getClearSuccessTime(),"yyyyMMddHHmmssSSS"));
        inv.setReturnInfo("[Code:2600;Desc:放行]");
        request.setInventoryReturn(inv);
        SendSysParams params = new SendSysParams();
        params.setFromCode(guanWuCpCode);
        GaCustomsDeclareOrderCallbackResponse resp = guanWuClient.send(request,params);
        businessLogService.saveLog(BusTypeEnum.CNLINK_OUT, request.getApi(), url, order.getOrderNo(),  new JSONObject(request).toString(), new JSONObject(resp).toString(), (System.currentTimeMillis() - start));
        if (!resp.isSuccess())
            throw new BadRequestException(resp.getErrorMsg());
    }

    /**
     * 申报报文查询接口
     * @param logisticsNo
     * @param cpCode
     * @return
     */
    public String getDeclareInfo2(String logisticsNo,String cpCode){
        CustomsDeclareOrderMessageQueryRequest request = new CustomsDeclareOrderMessageQueryRequest();
        request.setLogisticsOrderNo(logisticsNo);
        SendSysParams params = new SendSysParams();
        params.setFromCode(cpCode);
        CustomsDeclareOrderMessageQueryResponse response = guanWuClient.send(request,params);
        if (!response.isSuccess())
            throw new BadRequestException(response.getErrorMsg());
        return response.getData();
    }

    /**
     * 跨境外部商家订单取消接口
     * @param order
     * @param cainiaoShopInfo
     * @return
     */
    public boolean cancelDeclare(CrossBorderOrder order,CainiaoShopInfo cainiaoShopInfo){
        long start = System.currentTimeMillis();
        CrossborderSalesCancelRequest request = new CrossborderSalesCancelRequest();
        request.setUserId(Long.parseLong(cainiaoShopInfo.getCnOwnerId()));
        request.setLgOrderCode(order.getLpCode());
        switch (order.getPlatformCode()) {
            case "YZ":
                request.setOrderSource("713");
                break;
            case "PDD":
                request.setOrderSource("701");
                break;
            case "MGJ":
                request.setOrderSource("222");
                break;
            case "BD":
                request.setOrderSource("802");
                break;
            case "Ymatou":
                request.setOrderSource("801");
                break;
            case "AiKucun":
                request.setOrderSource("815");
                break;
            default:
                throw new BadRequestException("该平台不在菜鸟范围内");
        }
        SendSysParams sendSysParams = new SendSysParams();
        sendSysParams.setFromCode(cainiaoShopInfo.getCpCode());
        CrossborderSalesCancelResponse response = pacClient.send(request, sendSysParams);
        businessLogService.saveLog(BusTypeEnum.CNLINK_OUT, request.getApi(), url, order.getOrderNo(),  new JSONObject(request).toString(), new JSONObject(response).toString(), (System.currentTimeMillis() - start));
        if (response.isSuccess()) {
            return order.getLpCode().equals(response.getLgOrderCode());
        }
        throw new BadRequestException(response.getErrorMsg());
    }

    /**
     *交接接收
     * CP传揽收节点给菜鸟，用于结算物流费用
     * @param order
     * @param cainiaoShopInfo
     */
    public void lastmineHoinCallback(CrossBorderOrder order,CainiaoShopInfo cainiaoShopInfo){
        long start = System.currentTimeMillis();
        CainiaoGlobalLastmileHoinCallbackRequest request = new CainiaoGlobalLastmileHoinCallbackRequest();
        request.setLogisticsOrderCode(order.getLpCode());
        request.setTrackingNumber(order.getLogisticsNo());
        request.setWaybillNumber(order.getOrderNo());
        request.setOpTime(order.getDeliverTime());
        request.setTimeZone(8);
        request.setTransportType("4");
        request.setFromPortCode("NINGBO");
        request.setToPortCode(StringUtil.getPinyin(order.getCity().replaceAll("市",""),""));
        request.setOperator("ZTO");
        request.setOperatorContact("中通国际");
        request.setOpCode("0");
        Parcel parcel = new Parcel();
        parcel.setBigBagID(order.getOrderNo());
        request.setParcel(parcel);
        SendSysParams params = new SendSysParams();
        params.setFromCode(cainiaoShopInfo.getCpCode());
        CainiaoGlobalLastmileHoinCallbackResponse response = pacClient.send(request,params);
        businessLogService.saveLog(BusTypeEnum.CNLINK_OUT, request.getApi(), url, order.getOrderNo(),  new JSONObject(request).toString(), new JSONObject(response).toString(), (System.currentTimeMillis() - start));
        if (!response.isSuccess()){
            if (response.getErrorMsg().contains("非法的XML/JSON:")){
                String  xml = response.getErrorMsg().replaceAll("非法的XML/JSON:","");
                JSONObject object = XML.toJSONObject(xml);
                JSONObject resp = object.getJSONObject("response");
                if (resp!=null&&!resp.getBool("success"))
                    throw new BadRequestException("["+response.getErrorCode()+"]"+response.getErrorMsg());
            }else
                throw new BadRequestException("["+response.getErrorCode()+"]"+response.getErrorMsg());
        }
    }

    /**
     * 查询菜鸟订单物流状态接口
     */
    public Node queryWmsStatus(String lpCode, CainiaoShopInfo caiNiaoShopInfo) {
        CrossborderLogisticsDetailQueryRequest request = new CrossborderLogisticsDetailQueryRequest();
        request.setUserId(Long.parseLong(caiNiaoShopInfo.getCnOwnerId()));
        request.setLgOrderCode(lpCode);
        SendSysParams sendSysParams = new SendSysParams();
        sendSysParams.setFromCode(caiNiaoShopInfo.getCpCode());
        CrossborderLogisticsDetailQueryResponse response = pacClient.send(request, sendSysParams);
        if (response.isSuccess()) {
            if (CollectionUtils.isEmpty(response.getNodeList())){
                throw new BadRequestException("没有任何状态");
            }
            return response.getNodeList().get(0);
        }
        throw new BadRequestException(response.getErrorMsg());
    }

    /**
     * 跨境订单物流详情查询接口
     */
    public void queryLogisticsDetail(CrossBorderOrder order, CainiaoShopInfo caiNiaoShopInfo) {
        CrossborderLogisticsDetailQueryRequest request = new CrossborderLogisticsDetailQueryRequest();
        request.setLgOrderCode(order.getLpCode());
        request.setUserId(Long.parseLong(caiNiaoShopInfo.getCnOwnerId()));
        SendSysParams sendSysParams = new SendSysParams();
        sendSysParams.setFromCode(caiNiaoShopInfo.getCpCode());
        CrossborderLogisticsDetailQueryResponse response = pacClient.send(request,sendSysParams);
        if (response.isSuccess()) {
            List<Node> nodeList = response.getNodeList();
            for (Node node : nodeList) {
                String mailNo = mailNo = node.getMailNo();
                if (StringUtil.isNotEmpty(mailNo)) {
                    order.setLogisticsNo(mailNo);
                    order.setLogisticsName(node.getPartnerList().get(0).getPartnerCode());
                }
            }
        } else {
            throw new BadRequestException(response.getErrorMsg());
        }
    }

    /**
     * 总署版关税回传
     * @param order
     */
    public void gaCustomsTaxCallback(CrossBorderOrder order){
        long start = System.currentTimeMillis();
        GaCustomsTaxCallbackRequest request = new GaCustomsTaxCallbackRequest();
        Tax tax = new Tax();
        TaxHeadRd taxHeadRd = new TaxHeadRd();
        taxHeadRd.setGuid(IdUtil.simpleUUID());
        taxHeadRd.setReturnTime(DateUtils.format(order.getClearSuccessTime(),"yyyyMMddHHmmssSSS"));
        taxHeadRd.setInvtNo(order.getInvtNo());
        taxHeadRd.setOrderNo(order.getOrderNo());
        taxHeadRd.setTaxNo(order.getDeclareNo());
        taxHeadRd.setCustomsTax(StringUtil.isEmpty(order.getTariffAmount())?0.0:Double.parseDouble(order.getTariffAmount()));
        taxHeadRd.setValueAddedTax(Double.parseDouble(order.getTaxAmount()));//增值税
        taxHeadRd.setConsumptionTax(StringUtil.isEmpty(order.getConsumptionDutyAmount())?0.0:Double.parseDouble(order.getConsumptionDutyAmount()));//消费税
        taxHeadRd.setStatus(1);
        taxHeadRd.setEntDutyNo("");
        taxHeadRd.setNote("");
        taxHeadRd.setAssureCode("3302461510");
        taxHeadRd.setEbcCode("3302461510");
        taxHeadRd.setLogisticsCode("ZTO");
        taxHeadRd.setAgentCode("3302980402");
        taxHeadRd.setCustomsCode("3105");
        tax.setTaxHeadRd(taxHeadRd);
        List<TaxRd>taxRds = new ArrayList<>();
        for (int i = 0; i < order.getItemList().size(); i++) {
            CrossBorderOrderDetails detail=order.getItemList().get(i);
            TaxRd taxRd = new TaxRd();
            taxRd.setGnum(i+1);
            taxRd.setGcode(detail.getHsCode());
            taxRd.setTaxPrice(Double.parseDouble(detail.getDutiableTotalValue()));
            taxRd.setCustomsTax(StringUtil.isEmpty(detail.getTariffAmount())?0.0:Double.parseDouble(detail.getTariffAmount()));
            taxRd.setConsumptionTax(StringUtil.isEmpty(detail.getConsumptionDutyAmount())?0.0:Double.parseDouble(detail.getConsumptionDutyAmount()));
            taxRd.setValueAddedTax(Double.parseDouble(detail.getTaxAmount()));
            taxRds.add(taxRd);
        }
        tax.setTaxListRd(taxRds);
        request.setTax(tax);
        SendSysParams sendSysParams = new SendSysParams();
        sendSysParams.setFromCode(guanWuCpCode);
        GaCustomsTaxCallbackResponse response = guanWuClient.send(request,sendSysParams);
        businessLogService.saveLog(BusTypeEnum.CNLINK_OUT, request.getApi(), url, order.getOrderNo(),  new JSONObject(request).toString(), new JSONObject(response).toString(), (System.currentTimeMillis() - start));
        if (!response.isSuccess())
            throw new BadRequestException(response.getErrorMsg());
    }

    public static String getStatusText(String statusCode) {
        if (StringUtil.isEmpty(statusCode)) return "null";
        if (StringUtil.equals("7010", statusCode)) {
            return "仓库已接单";
        } else if (StringUtil.equals("-400", statusCode)) {
            return "订单已取消";
        } else if (StringUtil.equals("CONSIGN", statusCode)) {
            return "已发货";
        } else {
            return statusCode;
        }
    }
}
