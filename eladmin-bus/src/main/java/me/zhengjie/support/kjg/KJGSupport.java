package me.zhengjie.support.kjg;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.ClearCompanyInfoDto;
import me.zhengjie.service.dto.ShopInfoDto;
import me.zhengjie.utils.*;
import me.zhengjie.utils.constant.KJGMsgType;
import me.zhengjie.utils.constant.OrderLogConstant;
import me.zhengjie.utils.constant.PlatformConstant;
import me.zhengjie.utils.enums.BooleanEnum;
import me.zhengjie.utils.enums.CBGoodsStatusEnum;
import org.apache.commons.codec.binary.Base64;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
public class KJGSupport {

    private static String KJG_USER = "wms";
    private static String KJG_KEY = "fuliwms123";

    @Autowired
    private OrderLogService orderLogService;

    @Autowired
    private QueryMftLogService queryMftLogService;

    @Autowired
    private CustomsTariffService customsTariffService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private ShopInfoService shopInfoService;

    @Autowired
    private ClearCompanyInfoService clearCompanyInfoService;

    @Autowired
    private SkuLogService skuLogService;

    @Autowired
    private LogisticsInfoService logisticsInfoService;

    @Autowired
    private HezhuInfoService hezhuInfoService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private ClearDetailsService clearDetailsService;

    @Autowired
    private OrderReturnService orderReturnService;

    /**
     * 获取申报所需信息
     * @param order)
     */
    public void getDecinfo(CrossBorderOrder order) throws Exception {
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        BigDecimal netWeight = BigDecimal.ZERO;//净重
        BigDecimal grossWeight = BigDecimal.ZERO;//毛重
        BigDecimal taxAmount=BigDecimal.ZERO;
        for (CrossBorderOrderDetails item: order.getItemList()) {
            JSONObject request = new JSONObject();
            JSONObject Message = new JSONObject();
            JSONObject Header = new JSONObject();
            Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
            Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
            Header.putOnce("ProductId", item.getGoodsNo());
            Message.putOnce("Header", Header);
            request.putOnce("Message", Message);
            String xml = JSONUtil.toXmlStr(request);
            String res = reqDSKJG(
                    KJGMsgType.NINGBO_GET_GOODS_INFO,
                    xml,
                    clearCompanyInfo.getKjgUser(),
                    clearCompanyInfo.getKjgKey()
            );

            JSONObject resJSON = JSONUtil.xmlToJson(res);
            JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
            if (StringUtil.equals("T", resHeader.getStr("Result"))) {
                JSONObject resBody = resJSON.getJSONObject("Message").getJSONObject("Body");
                // 后期这些信息在货品中心里获取
                item.setHsCode(resBody.getStr("HsCode"));
                item.setUnit(resBody.getStr("Unit"));
                item.setMakeCountry(resBody.getStr("OriginPlace"));
                item.setGoodsName(resBody.getStr("GoodsName"));
                if (resBody.getBigDecimal("Weight")==null)
                    throw new BadRequestException("海关备案处货号"+item.getGoodsNo()+"的重量为空");
                item.setNetWeight(resBody.getBigDecimal("Weight").toString());// 净重
                item.setGrossWeight(resBody.getBigDecimal("Weight").multiply(new BigDecimal(1.2)).setScale(4, BigDecimal.ROUND_HALF_UP).toString());// 毛重
                netWeight = netWeight.add(resBody.getBigDecimal("Weight").multiply(new BigDecimal(item.getQty())));
                grossWeight = grossWeight.add(resBody.getBigDecimal("Weight").multiply(new BigDecimal(1.2)).multiply(new BigDecimal(item.getQty())));
            }else {
                throw new BadRequestException(resHeader.getStr("ResultMsg"));
            }
            // 税费计算
            taxAmount = getTaxAmount(taxAmount, order, item,StringUtil.equals("1",shopInfoDto.getPushTo()));
        }
        order.setNetWeight(netWeight.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        order.setGrossWeight(grossWeight.setScale(4, BigDecimal.ROUND_HALF_UP).toString());
        if (StringUtil.isBlank(order.getTaxAmount())){
            if (!StringUtil.equals(shopInfoDto.getPushTo(),"0")){
                //修正税费误差(仅±0.01)
                BigDecimal totalAmount=BigDecimal.ZERO;//存放申报金额的和
                for (CrossBorderOrderDetails details : order.getItemList()) {
                    totalAmount = totalAmount.add(new BigDecimal(details.getDutiableTotalValue()));
                }
                BigDecimal calPayment = taxAmount.add(totalAmount).add(new BigDecimal(order.getPostFee())).subtract(new BigDecimal(order.getDisAmount()));
                BigDecimal paymentOffset = new BigDecimal(order.getPayment()).subtract(calPayment);//误差
                if (BigDecimalUtils.le(paymentOffset.abs(),new BigDecimal("0.01"))&&BigDecimalUtils.gt(paymentOffset.abs(),BigDecimal.ZERO)){
                    taxAmount=taxAmount.add(paymentOffset);
                }
            }
            order.setTaxAmount(taxAmount.toString());
        }
    }

    private BigDecimal getTaxAmount(BigDecimal taxAmount, CrossBorderOrder order, CrossBorderOrderDetails details,boolean isCN) {
        String payment = details.getPayment();// 商品实付总价
        if (StringUtil.isNotBlank(order.getTaxAmount())&&StringUtil.isNotEmpty(details.getPayment())
                && StringUtil.isNotEmpty(details.getTaxAmount())
                && StringUtil.isNotEmpty(details.getDutiableTotalValue())
                && StringUtil.isNotEmpty(details.getDutiableValue())) {
            //如果提前设置了税费、单价、总价则不计算
            taxAmount = new BigDecimal(order.getTaxAmount());
        } else if (StringUtil.isNotEmpty(payment)) {
            // 如果设置了实付单价，那么就按照这个单价来计算申报金额
            BigDecimal singlePayment = new BigDecimal(payment).divide(new BigDecimal(details.getQty()), 2, BigDecimal.ROUND_HALF_UP);// 商品实付单价
            // 计算税金
            // 查询HS对应的税率
            CustomsTariff customsTariff = customsTariffService.queryByHsCode(details.getHsCode());
            // 总税率
            if (customsTariff==null){
                customsTariff=new CustomsTariff();
                customsTariff.setValueAddTariff(new BigDecimal(0.091));
            }
            BigDecimal tariff = customsTariff.getValueAddTariff().multiply(new BigDecimal(0.7));// 增值税打七折
            BigDecimal price = singlePayment.divide(BigDecimal.ONE.add(tariff), 2, BigDecimal.ROUND_HALF_UP);// 申报单价,实付金额/(1+税率)

            BigDecimal tax;
            if (isCN)
                tax=singlePayment.subtract(price);
            else
                tax=price.multiply(tariff).setScale(2, BigDecimal.ROUND_HALF_UP);

            BigDecimal taxTotal=tax.multiply(new BigDecimal(details.getQty()));
            BigDecimal priceTotal=price.multiply(new BigDecimal(details.getQty()));
            if (isCN){
                if (BigDecimalUtils.nq(new BigDecimal(details.getPayment()),taxTotal.add(priceTotal))){
                    //有误差，需要修正，误差范围0.01_-0.01,否则不处理
                    BigDecimal sub=new BigDecimal(details.getPayment()).subtract(taxTotal.add(priceTotal));
                    if (BigDecimalUtils.le(sub.abs().divide(new BigDecimal(details.getQty()),2,BigDecimal.ROUND_HALF_UP),new BigDecimal("0.01"))){
                        taxTotal=taxTotal.add(sub);
                    }
                }
            }
            taxAmount = taxAmount.add(taxTotal);

            details.setDutiableValue(price.toString());
            details.setDutiableTotalValue(priceTotal.toString());
            details.setTaxAmount(taxTotal.toString());
        }
        return taxAmount;
    }

    public String declare(CrossBorderOrder order) throws Exception {
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
        Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
        Header.putOnce("CreateTime", DateUtil.formatDateTime(order.getOrderCreateTime()));

        JSONObject Body = new JSONObject();
        JSONObject Order = new JSONObject();
        // 主单信息
        Order.putOnce("Operation", "0");
        Order.putOnce("OrderFrom", order.getOrderForm());
        if (StringUtils.equals(order.getPlatformCode(), PlatformConstant.DY)) {
            Order.putOnce("OrderShop", "30940");
        }
        Order.putOnce("OrderNo", order.getCrossBorderNo());
        Order.putOnce("PostFee", order.getPostFee());
        Order.putOnce("Amount", order.getPayment());// 实际支付金额
        Order.putOnce("BuyerAccount", order.getBuyerAccount());
        Order.putOnce("Phone", order.getBuyerPhone());
        Order.putOnce("TariffAmount", "0");
        Order.putOnce("AddedValueTaxAmount", "0");
        Order.putOnce("ConsumptionDutyAmount", "0");
        Order.putOnce("BuyerIdnum", order.getBuyerIdNum());
        Order.putOnce("BuyerName", order.getBuyerName());
        Order.putOnce("BuyerIsPayer", "1");
        Order.putOnce("NetWeight", order.getNetWeight());
        Order.putOnce("GrossWeight", order.getGrossWeight());
        Order.putOnce("DisAmount", order.getDisAmount());
        Order.putOnce("TaxAmount", order.getTaxAmount());
        if (StringUtils.equals(PlatformConstant.DY, order.getPlatformCode())) {
            if (StringUtils.equals("1", order.getFourPl())) {
                Order.putOnce("BooksNo", "T3105W000185");
                Order.putOnce("AssureCode", "11089697EJ");// 平台担保
            }else {
                Order.putOnce("BooksNo", "T3105W000159");
            }
        }else {
            Order.putOnce("BooksNo", shopInfoDto.getBooksNo());
        }

        // 优惠信息
        JSONArray Promotions = new JSONArray();
        JSONObject p = new JSONObject();
        p.putOnce("ProAmount", order.getDisAmount());
        p.putOnce("ProRemark", "优惠合计");
        JSONObject Promotion = new JSONObject();
        Promotion.putOnce("Promotion", p);
        Promotions.add(Promotion);
        Order.putOnce("Promotions", Promotions);

        // 货品信息
        JSONObject Goods = new JSONObject();
        List<CrossBorderOrderDetails> itemList = order.getItemList();
        JSONArray Detail = new JSONArray();
        for (CrossBorderOrderDetails item : itemList) {
            JSONObject good = new JSONObject();
            good.putOnce("ProductId", item.getGoodsNo());// 货号
            good.putOnce("GoodsName", item.getGoodsName());
            good.putOnce("Qty", item.getQty());
            BaseSku baseSku = baseSkuService.queryByGoodsNo(item.getGoodsNo());
            if (baseSku != null) {
                good.putOnce("BarCode", baseSku.getBarCode());
            }
            good.putOnce("Unit", item.getUnit());
            good.putOnce("Price", item.getDutiableValue());// 申报单价
            good.putOnce("Amount", item.getDutiableTotalValue());// 申报总价
            Detail.add(good);
        }
        Goods.putOnce("Detail", Detail);
        Order.putOnce("Goods", Goods);
        Body.putOnce("Order", Order);

        // 支付信息
        JSONObject Pay = new JSONObject();
        Pay.putOnce("Paytime", DateUtil.formatDateTime(order.getPayTime()));
        Pay.putOnce("PaymentNo", order.getPaymentNo());
        Pay.putOnce("OrderSeqNo", order.getOrderSeqNo());
        Pay.putOnce("Source", order.getPayCode());// 需转换
        Body.putOnce("Pay", Pay);

        // 物流信息
        JSONObject Logistics = new JSONObject();
        Logistics.putOnce("LogisticsNo", order.getLogisticsNo());
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        Logistics.putOnce("LogisticsName", logisticsInfo == null ? "中通速递" : logisticsInfo.getKjgName());
        Logistics.putOnce("Consignee", order.getConsigneeName());
        Logistics.putOnce("Province", order.getProvince());
        Logistics.putOnce("City", order.getCity());
        Logistics.putOnce("District", order.getDistrict());
        if (StringUtil.contains(order.getConsigneeAddr(),order.getProvince())){
            Logistics.putOnce("ConsigneeAddr", order.getConsigneeAddr());
        }else {
            Logistics.putOnce("ConsigneeAddr", String.format("%s %s %s %s",
                    order.getProvince(),
                    order.getCity(),
                    order.getDistrict(),
                    order.getConsigneeAddr()));
        }

        Logistics.putOnce("ConsigneeTel", order.getConsigneeTel());
        Logistics.putOnce("LgRemark01", order.getAddMark());
        Body.putOnce("Logistics", Logistics);

        Message.putOnce("Header", Header);
        Message.putOnce("Body", Body);
        request.putOnce("Message", Message);
        String xml = JSONUtils.toXml(request);
        String res = reqDSKJG(
                KJGMsgType.NINGBO_DECLARE_ORDER,
                xml,
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey()
        );
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            // 申报成功
            return resHeader.getStr("MftNo");
        } else {
            if (StringUtils.contains(resHeader.getStr("ResultMsg"),"该订单编号已存在")) {
                String declareNo = getDeclareNoByOrderNo(order.getOrderNo(), order.getOrderForm(), clearCompanyInfo.getKjgUser(), clearCompanyInfo.getKjgKey());
                return declareNo;
            }else {
                order.setDeclareMsg(resHeader.getStr("ResultMsg"));
                throw new BadRequestException(resHeader.getStr("ResultMsg"));
            }

        }

    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String date = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();

        Header.putOnce("CustomsCode", "33024609V0");
        Header.putOnce("OrgName", "宁波环球云连信息科技有限公司");
        Header.putOnce("CreateTime", date);

        JSONObject Body = new JSONObject();
        JSONObject Order = new JSONObject();
        // 主单信息
        Order.putOnce("MftNo", "31052021I491632923");
        Order.putOnce("Reason", "取消");
        Body.putOnce("Order", Order);

        Message.putOnce("Header", Header);
        Message.putOnce("Body", Body);
        request.putOnce("Message", Message);
        String xml = JSONUtil.toXmlStr(request);
        String res = new KJGSupport().reqDSKJG(
                KJGMsgType.NINGBO_CANCEL_ORDER,
                xml,
                "dream6088",
                "6bb46385-648d-4b63-b6b2-f2530fe7b415"
        );
        System.out.println(res);
    }

    public String getDeclareStatus(CrossBorderOrder order) throws Exception{
        String res=refresh(order);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            JSONObject mft = resJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            if ("99".equals(String.valueOf(mft.get("Status")))) {
                return String.valueOf(mft.get("Status"));
            }
            cn.hutool.json.JSONArray MftInfos = mft.getJSONObject("MftInfos").getJSONArray("MftInfo");
            if (MftInfos == null) {
                return mft.getStr("Status");
            } else {
                return MftInfos.getJSONObject(0).getStr("Status");
            }
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    public String refresh(CrossBorderOrder order) throws Exception {
        try {
            ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            if (StringUtils.isEmpty(order.getDeclareNo())) {
                // 平台申报的，接单回传订单没有申报单号
                String declareNo = getDeclareNoByOrderNo(order.getOrderNo(), order.getOrderForm(), clearCompanyInfo.getKjgUser(), clearCompanyInfo.getKjgKey());
                order.setDeclareNo(declareNo);
            }
            JSONObject request = new JSONObject();
            JSONObject Message = new JSONObject();
            JSONObject Header = new JSONObject();
            Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
            Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
            Header.putOnce("MftNo", order.getDeclareNo());
            Message.putOnce("Header", Header);
            request.putOnce("Message", Message);
            String xml = JSONUtil.toXmlStr(request);
            String res = reqDSKJG(
                    KJGMsgType.NINGBO_REFRESH_ORDER,
                    xml,
                    clearCompanyInfo.getKjgUser(),
                    clearCompanyInfo.getKjgKey()
            );
            return res;
        }catch (Exception e) {
            // 定时刷新状态，只记录异常LOG,正常的LOG不记录
            OrderLog orderLog = new OrderLog(
                    order.getId(),
                    order.getOrderNo(),
                    OrderLogConstant.REFRESH,
                    BooleanEnum.FAIL.getCode(),
                    e.getMessage()
            );
            orderLogService.create(orderLog);
            throw e;
        }
    }

    /**
     * 此方法弃用
     * @param startTime
     * @param endTime
     * @param page
     * @return
     */
    public String getStatusBatch(Timestamp startTime, Timestamp endTime, Integer page) {
        try {
            ShopInfoDto shopInfoDto = shopInfoService.queryById(null);
            ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
            JSONObject request = new JSONObject();
            JSONObject Message = new JSONObject();
            JSONObject Header = new JSONObject();
            Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
            Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
            Header.putOnce("StartTime", DateUtil.format(startTime, DatePattern.NORM_DATETIME_FORMAT));
            Header.putOnce("EndTime", DateUtil.format(endTime, DatePattern.NORM_DATETIME_FORMAT));
            Header.putOnce("Page", page);
            Message.putOnce("Header", Header);
            request.putOnce("Message", Message);
            String xml = JSONUtil.toXmlStr(request);
            String res = reqDSKJG(
                    KJGMsgType.NINGBO_UPDATEMFT_ORDER_BYTIME,
                    xml,
                    clearCompanyInfo.getKjgUser(),
                    clearCompanyInfo.getKjgKey()
            );
            return res;
        }catch (Exception e) {
            QueryMftLog log = new QueryMftLog(
                    startTime,
                    endTime,
                    page,
                    1000,
                    "E",
                    "F",
                    e.getMessage()
            );
            queryMftLogService.create(log);
            return null;
        }

    }

    public String deliver(String weight, String mailNo) {
        JSONObject request = new JSONObject();
        JSONObject wmsFxRequest = new JSONObject();
        wmsFxRequest.putOnce("mailNo", mailNo);
        wmsFxRequest.putOnce("statusCode", "01");
        wmsFxRequest.putOnce("exTime1", DateUtils.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        wmsFxRequest.putOnce("weight", weight);
        request.putOnce("wmsFxRequest", wmsFxRequest);

        String xml = JSONUtil.toXmlStr(request);
        String result = reqCKKJG(KJGMsgType.NINGBO_DELIVER, xml);
        return result;
    }

    public void cancelBeforeSucc(String mftNo) throws UnsupportedEncodingException {
// 走跨境购报文取消
        String date = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        Header.putOnce("CustomsCode", "33024609V0");
        Header.putOnce("OrgName", "宁波环球云连信息科技有限公司");
        Header.putOnce("CreateTime", date);

        JSONObject Body = new JSONObject();
        JSONObject Order = new JSONObject();
        // 主单信息
        Order.putOnce("MftNo", "mftNo");
        Order.putOnce("Reason", "取消");
        Body.putOnce("Order", Order);

        Message.putOnce("Header", Header);
        Message.putOnce("Body", Body);
        request.putOnce("Message", Message);
        String xml = JSONUtil.toXmlStr(request);
        log.info("跨境购发起订单取消,单号：{}，请求参数：{}", mftNo, xml);
        String res = reqDSKJG(
                KJGMsgType.NINGBO_CANCEL_ORDER,
                xml,
                "dream6088",
                "6bb46385-648d-4b63-b6b2-f2530fe7b415"
        );
        log.info("跨境购发起订单取消,单号：{}，返回：{}", mftNo, res);
    }

    // 取消订单
    public String cancelOrder(CrossBorderOrder order) throws Exception {
        String res;
        // 先查询当前申报单状态是否是单证放行
        String refresh = refresh(order);
        JSONObject refreshJSON = JSONUtil.xmlToJson(refresh);
        JSONObject refreshHeader = refreshJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", refreshHeader.getStr("Result"))) {
            JSONObject mft = refreshJSON.getJSONObject("Message").getJSONObject("Body").getJSONObject("Mft");
            String status = mft.getStr("Status");
//            if ("24".equals(status)) {
//                throw new BadRequestException("货物放行订单不可取消");
//                return res;
//            }
            if ("99".equals(status)){
                return refresh;
            }
            else if ("22".equals(status) || "24".equals(status)) {
                if (StringUtils.isEmpty(order.getInvtNo())) {
                    String ManifestId = mft.getStr("ManifestId");
                    order.setInvtNo(ManifestId);
                }
                // 单证放行走总署版取消
                if (StringUtil.equals(order.getPlatformCode(),"PDD"))
                    res= pddOrderService.cancelDeclare(order);
                else
                    res = cancelAfterClearSucc(order);
            }else {
                // 走跨境购报文取消
                String date = DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
                JSONObject request = new JSONObject();
                JSONObject Message = new JSONObject();
                JSONObject Header = new JSONObject();
                ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
                ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
                Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
                Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
                Header.putOnce("CreateTime", date);

                JSONObject Body = new JSONObject();
                JSONObject Order = new JSONObject();
                // 主单信息
                Order.putOnce("MftNo", order.getDeclareNo());
                Order.putOnce("Reason", "取消");
                Body.putOnce("Order", Order);

                Message.putOnce("Header", Header);
                Message.putOnce("Body", Body);
                request.putOnce("Message", Message);
                String xml = JSONUtil.toXmlStr(request);
                log.info("跨境购发起订单取消,单号：{}，请求参数：{}", order.getOrderNo(), xml);
                res = reqDSKJG(
                        KJGMsgType.NINGBO_CANCEL_ORDER,
                        xml,
                        clearCompanyInfo.getKjgUser(),
                        clearCompanyInfo.getKjgKey()
                );
                log.info("跨境购发起订单取消,单号：{}，返回：{}", order.getOrderNo(), res);
            }
        }else {
            throw new BadRequestException("取消查询申报单状态报错："+refreshHeader.getStr("ResultMsg"));
        }
        return res;
    }

    // 单证放行之后的申报单取消
    public String cancelAfterClearSucc(CrossBorderOrder order) throws UnsupportedEncodingException {
        System.setProperty("javax.xml.parsers.DocumentBuilderFactory", "com.sun.org.apache.xerces.internal.jaxp.DocumentBuilderFactoryImpl");

        Document xml = DocumentHelper.createDocument();
        Element CEB623Message = xml.addElement("CEB623Message");

        String guid = UUID.randomUUID().toString().toUpperCase();

        CEB623Message.addAttribute("guid", guid);
        CEB623Message.addAttribute("version", "1.0");
        CEB623Message.addNamespace("", "http://www.chinaport.gov.cn/ceb");
        CEB623Message.addNamespace("ns2", "http://www.w3.org/2000/09/xmldsig#");
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());

        Element InvtCancel = CEB623Message.addElement("InvtCancel");
        InvtCancel.addElement("guid").setText(guid);
        InvtCancel.addElement("appType").setText("1");
        InvtCancel.addElement("appTime").setText(DateUtils.format(new Date(), DatePattern.PURE_DATETIME_PATTERN));
        InvtCancel.addElement("appStatus").setText("2");
        InvtCancel.addElement("customsCode").setText("3105");
        InvtCancel.addElement("orderNo").setText(order.getCrossBorderNo());
        InvtCancel.addElement("ebpCode").setText(order.getEbpCode());
        InvtCancel.addElement("ebpName").setText(order.getEbpName());
        InvtCancel.addElement("ebcCode").setText(clearCompanyInfo.getCustomsCode());
        InvtCancel.addElement("ebcName").setText(clearCompanyInfo.getClearCompanyName());
        InvtCancel.addElement("logisticsNo").setText(order.getLogisticsNo());
        LogisticsInfo logisticsInfo = logisticsInfoService.queryById(order.getSupplierId());
        InvtCancel.addElement("logisticsCode").setText(logisticsInfo == null ? "3122480063" : logisticsInfo.getCustomsCode());
        InvtCancel.addElement("logisticsName").setText(logisticsInfo == null ? "上海大誉国际物流有限公司" : logisticsInfo.getCustomsName());
        InvtCancel.addElement("copNo").setText(order.getDeclareNo());
        InvtCancel.addElement("invtNo").setText(order.getInvtNo());
        InvtCancel.addElement("buyerIdType").setText("1");
        InvtCancel.addElement("buyerIdNumber").setText(order.getBuyerIdNum());
        InvtCancel.addElement("buyerName").setText(order.getBuyerName());
        InvtCancel.addElement("buyerTelephone").setText(order.getBuyerPhone());
        InvtCancel.addElement("agentCode").setText("3302980402");
        InvtCancel.addElement("agentName").setText("宁波东方意向信息技术有限公司");
        InvtCancel.addElement("reason").setText("客户取消订单");

        Element BaseTransfer = CEB623Message.addElement("BaseTransfer");
        BaseTransfer.addElement("copCode").setText("3302980402");
        BaseTransfer.addElement("copName").setText("宁波东方意向信息技术有限公司");
        BaseTransfer.addElement("dxpMode").setText("DXP");
        BaseTransfer.addElement("dxpId").setText("DXPENT0000013249");

        String xmlStr = xml.asXML();
        xmlStr = xmlStr.replace("UTF-8", "utf-8");
        xmlStr = xmlStr.replace("xmlns=\"\"", "");
        String result = reqZSKjg("CEB623", Base64.encodeBase64String(xmlStr.getBytes()), clearCompanyInfo.getKjgUser(), clearCompanyInfo.getKjgKey());
        return result;
    }

    // 退货
    public String declareReturn(CrossBorderOrder order) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        ShopInfoDto shopInfoDto = shopInfoService.queryById(order.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());


        Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
        Header.putOnce("CreateTime", DateUtil.formatDateTime(new Date()));

        JSONObject Body = new JSONObject();
        JSONObject RejectedInfo = new JSONObject();
        RejectedInfo.putOnce("MftNo", order.getDeclareNo());
        RejectedInfo.putOnce("WaybillNo", order.getLogisticsNo());
        RejectedInfo.putOnce("Flag", "00");

        JSONObject RejectedGoods = new JSONObject();
        JSONArray Detail = new JSONArray();
        List<CrossBorderOrderDetails> itemList = order.getItemList();
        for (CrossBorderOrderDetails item: itemList) {
            JSONObject good = new JSONObject();
            good.putOnce("ProductId", item.getGoodsNo());
            good.putOnce("RejectedQty", item.getQty());
            Detail.add(good);
        }
        RejectedGoods.putOnce("Detail", Detail);
        RejectedInfo.putOnce("RejectedGoods", RejectedGoods);

        Body.putOnce("RejectedInfo", RejectedInfo);
        Message.putOnce("Header", Header);
        Message.putOnce("Body", Body);
        request.putOnce("Message", Message);
        String xml = JSONUtils.toXml(request);
        String res = reqDSKJG(
                KJGMsgType.NINGBO_DECLARE_RETURN,
                xml,
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey()
        );
        return res;
    }

    // 查询退货单申报状态
    public String getReturnStatus(OrderReturn orderReturn) throws Exception {
        ShopInfoDto shopInfoDto = shopInfoService.queryById(orderReturn.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        if (StringUtils.isBlank(orderReturn.getDeclareNo())) {
            String declareNo = getDeclareNoByOrderNo(orderReturn.getOrderNo(), "1506", clearCompanyInfo.getKjgUser(), clearCompanyInfo.getKjgKey());
            orderReturn.setDeclareNo(declareNo);
            orderReturnService.update(orderReturn);
        }
        Header.putOnce("MftNo", orderReturn.getDeclareNo());
        Header.putOnce("WaybillNo", orderReturn.getSExpressNo());
        Header.putOnce("Flag", "00");
        Message.putOnce("Header", Header);
        request.putOnce("Message", Message);
        String xml = JSONUtils.toXml(request);

        String res = reqDSKJG(
                KJGMsgType.NINGBO_DECLARE_RETURN_REFRESH,
                xml,
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey()
        );
        return res;
    }

    // 发送核注单
    public String reqInvt () {
        Document xml = DocumentHelper.createDocument();
        Element BussinessData = xml.addElement("BussinessData");
        Element InvtMessage = BussinessData.addElement("InvtMessage");
        Element InvtHeadType = InvtMessage.addElement("InvtHeadType");
        InvtHeadType.addElement("PutrecNo").setText("T3105W000159");
        InvtHeadType.addElement("appType").setText("1");
        InvtHeadType.addElement("FIN000000000001").setText("1");

        Element InvtListType = InvtMessage.addElement("InvtListType");
        InvtListType.addElement("GdsSeqno").setText("1");
        InvtListType.addElement("PutrecSeqno").setText("12");

        String xmlStr = xml.asXML();
        System.out.println(xmlStr);
//        String result = reqZSKjg(KJGMsgType.NINGBO_DECLARE_INVT, Base64.encodeBase64String(xmlStr.getBytes()));
        String result = null;
        return result;
    }

    public String getDeclareNoByOrderNo(String orderNo,String orderFrom,String user,String pwd) throws Exception{
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        Header.putOnce("OrderNo",orderNo);
        Header.putOnce("OrderFrom",orderFrom);
        Message.putOnce("Header",Header);
        request.putOnce("Message",Message);
        String xml = JSONUtils.toXml(request);
        String res = reqDSKJG(
                KJGMsgType.NINGBO_GET_DECLARE_NO,
                xml,
                user,
                pwd
        );
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            return resHeader.getStr("MftNo");
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }


    /**
     * 商品备案
     * @param baseSku
     * @return
     */
    public String register(BaseSku baseSku) throws Exception {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        ShopInfoDto shopInfoDto = shopInfoService.queryById(baseSku.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfoDto.getServiceId());
        Header.putOnce("CustomsCode", clearCompanyInfo.getCustomsCode());
        Header.putOnce("OrgName", clearCompanyInfo.getClearCompanyName());
        Message.putOnce("Header", Header);

        JSONObject Body = new JSONObject();
        Body.putOnce("Name", baseSku.getGoodsNameC());
        Body.putOnce("NameEn", baseSku.getGoodsNameE());
        Body.putOnce("HsNumber", baseSku.getHsCode());
        Body.putOnce("Weight", baseSku.getNetWeight());
        Body.putOnce("Property", baseSku.getProperty());
        Body.putOnce("Gproduction", baseSku.getMakeContry());
        Body.putOnce("Brand", baseSku.getBrand());
        Body.putOnce("Unit", baseSku.getUnit());
        Body.putOnce("Guse", baseSku.getGuse());
        Body.putOnce("Gcomposition", baseSku.getGcomposition());
        Body.putOnce("Gfunction", baseSku.getGfunction());
        Body.putOnce("DsSku", baseSku.getOuterGoodsNo());
        Body.putOnce("DsSkuCode", baseSku.getBarCode());
        Body.putOnce("WarehouseCode", "3302461510");
        Body.putOnce("Flag", "1");
        Body.putOnce("Supplier", baseSku.getSupplier());
        Body.putOnce("LegalQty", baseSku.getLegalNum());
        Body.putOnce("SecondQty", baseSku.getSecondNum());
        Body.putOnce("HsfileName", "name.png");
        Message.putOnce("Body", Body);
        request.putOnce("Message", Message);
        String xml = JSONUtils.toXml(request);
        String res = reqDSKJG(
                KJGMsgType.NINGBO_REGISTER_SKU,
                xml,
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey()
        );
        SkuLog log = new SkuLog(
                baseSku.getId(),
                String.valueOf(CBGoodsStatusEnum.STATUS_115.getCode()),
                xml,
                res,
                SecurityUtils.getCurrentUsername()
        );
        skuLogService.create(log);
        JSONObject resJSON = JSONUtil.xmlToJson(res);
        JSONObject resHeader = resJSON.getJSONObject("Message").getJSONObject("Header");
        if (StringUtil.equals("T", resHeader.getStr("Result"))) {
            // 申报成功
            return resHeader.getStr("ProductId");
        } else {
            throw new BadRequestException(resHeader.getStr("ResultMsg"));
        }
    }

    /**
     * API-仓单进度节点查询接口
     */
    public String getReceiptNode(ClearInfo clearInfo) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(clearInfo.getClearCompanyId());
        Header.putOnce("ContractNumber",clearInfo.getContractNo());
        Header.putOnce("BlNo",clearInfo.getBillNo());
        Header.putOnce("StartTime",DateUtils.format(clearInfo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));   //格式为yyyy-MM-dd HH:mm:ss
        Header.putOnce("EndTime",DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        Message.putOnce("Header",Header);
        request.putOnce("Message",Message);

        String xml = JSONUtils.toXml(request);
        System.out.println(xml);
        String response = reqKJGReceipt(
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey(),
                xml,
                KJGMsgType.LDG_CHART_WORKFLOW_SYNC
        );
        System.out.println(response);
        return response;
    }

    /**
     * API-核注单进度节点查询接口
     */
    public String getHezhuNode(HezhuInfo hezhuInfo) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        ShopInfoDto shopInfo = shopInfoService.queryById(hezhuInfo.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfo.getServiceId());
        Header.putOnce("BondInvtNo",hezhuInfo.getQdCode());
        Header.putOnce("StartTime",DateUtils.format(hezhuInfo.getCreateTime(),"yyyy-MM-dd HH:mm:ss"));   //格式为yyyy-MM-dd HH:mm:ss
        Header.putOnce("EndTime",DateUtils.format(hezhuInfo.getUpdateTime(),"yyyy-MM-dd HH:mm:ss"));   //格式为yyyy-MM-dd HH:mm:ss
        Message.putOnce("Header",Header);
        request.putOnce("Message",Message);

        String xml = JSONUtils.toXml(request);
        String response = reqKJGReceipt(
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey(),
                xml,
                KJGMsgType.LDG_NEMSINVT_WORKFLOW_SYNC
        );
        System.out.println(response);
        return response;
    }

    /**
     * API-核放单进度节点查询接口
     */
    /*public String getTransNode(TransInfo transInfo) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject Header = new JSONObject();
        ShopInfoDto shopInfo = shopInfoService.queryById(transInfo.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(shopInfo.getServiceId());
        Header.putOnce("PassportNo",transInfo.getOrderNo());
        Header.putOnce("StartTime",DateUtils.formatDateTime(transInfo.getCreateTime()));   //格式为yyyy-MM-dd HH:mm:ss
        Message.putOnce("Header",Header);
        request.putOnce("Message",Message);

        String xml = JSONUtils.toXml(request);
        String response = reqKJGReceipt(
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey(),
                xml,
                KJGMsgType.LDG_PASSPORT_WORKFLOW_SYNC
        );
        System.out.println(response);
        return response;
    }*/

    /**
     * API-一线仓单数据接口(支持仓单新增和撤销)
     * 回传kjg仓单数据
     * @param
     * @param
     * @return
     */
    public String warehouseReceipt(ClearInfo clearInfo) throws UnsupportedEncodingException {
        JSONObject request = new JSONObject();
        JSONObject Message = new JSONObject();
        JSONObject BillHead = new JSONObject();
        JSONObject NemsInvtHeadNode = new JSONObject();
        JSONObject SwDecHeadNode = new JSONObject();
//        JSONArray AttchListNode = new JSONArray();    //无暂时需要必传,先注释掉
//        JSONArray PreDecContainerListNode = new JSONArray();
        JSONObject GoodsListNode = new JSONObject();
        JSONArray GoodsDetail = new JSONArray();
//        ShopInfoDto shopInfo = shopInfoService.queryById(clearInfo.getShopId());
        ClearCompanyInfo clearCompanyInfo = clearCompanyInfoService.findById(clearInfo.getClearCompanyId());

        BillHead.putOnce("OperCode","2");  //1为新增
        BillHead.putOnce("SerialNo",clearInfo.getSerialNo()); // 只有 OperCode不为1 时需填
        String tradeType = clearInfo.getBusType();
        switch (tradeType){
            case "01":
                tradeType = "1";
                break;
            case "02":
                tradeType = "1";
                break;
            case "03":
                tradeType = "2";
                break;
            case "04":
                tradeType = "3";
                break;
            case "05":
                tradeType = "3";
                break;
            default:
                throw new BadRequestException("只能传 一线进境 ,入仓和出仓");

        }
        BillHead.putOnce("TradeType",tradeType);
        BillHead.putOnce("DclPlcCuscd",clearInfo.getDclCusCode()); //申报海关 关区代码（申报地关区代码）
        BillHead.putOnce("SupvModecd",clearInfo.getSupvCode());
        BillHead.putOnce("PutrecNo",clearInfo.getBooksNo());
        BillHead.putOnce("EtpsInnerInvtNo",clearInfo.getClearNo());
        BillHead.putOnce("ImpexpPortcd",clearInfo.getImpexpCode());
        BillHead.putOnce("TrspModecd",clearInfo.getTransWay());
        BillHead.putOnce("CusTradeCountryCode",clearInfo.getShipCountry());
        BillHead.putOnce("DclEtpsCode",clearCompanyInfo.getCustomsCode());
        BillHead.putOnce("DclEtpsName",clearCompanyInfo.getClearCompanyName());

        NemsInvtHeadNode.putOnce("DclTypecd",clearInfo.getDclTypeCode()); //申报类型 1:备案申请, 2:变更申请， 3: 删除申请
        NemsInvtHeadNode.putOnce("ImpexpMarkcd",clearInfo.getImpexpMarkCode());
        NemsInvtHeadNode.putOnce("StshipTrsarvNatcd",clearInfo.getShipCountry());
        NemsInvtHeadNode.putOnce("InvtType",clearInfo.getInvtType()); //清单类型 0 普通清单 1 集报清单 3 先入区后报关 4 简单加工 5 保税展示交易 6 区内流转 7 港区联动 8 保税电商 9 一纳成品内销
        NemsInvtHeadNode.putOnce("DclcusFlag","1");  //是否报关标志(1.报关2.非报关)
        NemsInvtHeadNode.putOnce("DecType",clearInfo.getDclTypeCode());  //申报类型 1:备案申请, 2:变更申请， 3: 删除申请
        NemsInvtHeadNode.putOnce("BizopEtpsno",clearInfo.getBizopNo());
        NemsInvtHeadNode.putOnce("BizopEtpsNm",clearInfo.getBizopName());
        NemsInvtHeadNode.putOnce("ProcEtpsno",clearInfo.getProcNo());
        NemsInvtHeadNode.putOnce("ProcEtpsNm",clearInfo.getProcName());
        NemsInvtHeadNode.putOnce("DclEtpsno",clearInfo.getDcletpsNo());
        NemsInvtHeadNode.putOnce("DclEtpsNm",clearInfo.getDcletpsName());
        NemsInvtHeadNode.putOnce("MtpckEndprdMarkcd",clearInfo.getMaterialType());  //料件成品标记代码(I：料件，E：成品)
        NemsInvtHeadNode.putOnce("ListType",clearInfo.getCirculationType());  //流转类型 A 加工贸易深加工结转 B 加工贸易余料结转 C 不作价设备结转
        NemsInvtHeadNode.putOnce("InputCode",clearInfo.getInputCode());
        NemsInvtHeadNode.putOnce("InputName",clearInfo.getInputName());

        SwDecHeadNode.putOnce("BillTypeCode",clearInfo.getInvtType());
        SwDecHeadNode.putOnce("EntryTypeCode",clearInfo.getDclTypeCode());
        String advanceTime = DateUtils.format(clearInfo.getAdvanceTime(),"yyyyMMdd");
        SwDecHeadNode.putOnce("IEDate",DateUtils.parse(advanceTime,"yyyyMMdd"));  //Date格式 yyyyMMdd
        SwDecHeadNode.putOnce("IEPortCode",clearInfo.getInPort());
        SwDecHeadNode.putOnce("OverseasEtpsNm",clearInfo.getOverseasName());
        SwDecHeadNode.putOnce("TradeCountryCode",clearInfo.getTradeCountry());
        SwDecHeadNode.putOnce("RcvgdEtpsno",clearInfo.getProcNo());
        SwDecHeadNode.putOnce("RcvgdEtpsNm",clearInfo.getProcName());
        SwDecHeadNode.putOnce("TradeEtpsCode",clearInfo.getTradeEbpCode());
        SwDecHeadNode.putOnce("TradeEtpsNm",clearInfo.getTradeEbpName());
        SwDecHeadNode.putOnce("TradeEtpsSccd",clearInfo.getTradeEbpSccode());
        SwDecHeadNode.putOnce("BillNo",clearInfo.getBillNo());
        SwDecHeadNode.putOnce("DistinatePortCode",clearInfo.getShipPort());
        SwDecHeadNode.putOnce("WrapTypeCode",clearInfo.getPackTypeCode());
        SwDecHeadNode.putOnce("PackNo",clearInfo.getTotalNum());
        SwDecHeadNode.putOnce("TotalGrossWet",clearInfo.getGroosWeight());
        SwDecHeadNode.putOnce("TotalNetWt",clearInfo.getNetWeight());
        SwDecHeadNode.putOnce("TransMode",clearInfo.getTransWay());
        SwDecHeadNode.putOnce("GoodsPlace",clearInfo.getInWarehose());
        SwDecHeadNode.putOnce("DistrictCode",clearInfo.getDistrictCode());
        SwDecHeadNode.putOnce("DestCode",clearInfo.getDestCode());

        List<ClearDetails> details = clearDetailsService.queryByClearId(clearInfo.getId());
        for (ClearDetails detail : details) {
            JSONObject skus = new JSONObject();
            skus.putOnce("GdsSeqno",detail.getSeqNo());
            skus.putOnce("NewRecord","Y"); //          是否是新增 N 否 Y 是
            skus.putOnce("PutrecSeqno",detail.getRecordNo());
            skus.putOnce("GdsMtno",detail.getGoodsCode());
            skus.putOnce("Gdecd",detail.getHsCode());
            skus.putOnce("GdsNm",detail.getGoodsName());
            skus.putOnce("GdsSpcfModelDesc",detail.getProperty());
            skus.putOnce("Natcd",detail.getMakeCountry());
            skus.putOnce("DclUnitcd",detail.getUnit());
            skus.putOnce("LawfUnitcd",detail.getLegalUnit());
            skus.putOnce("SecdLawfUnitcd",detail.getSecondUnit());
            skus.putOnce("DclQty",clearInfo.getTotalNum()); //申报数量
            skus.putOnce("LawfQty",detail.getLegalNum());
            skus.putOnce("SecdLawfQty",detail.getSecondNum());
            skus.putOnce("DclUprcAmt",clearInfo.getSumMoney().divide(new BigDecimal(clearInfo.getTotalNum()), 2, BigDecimal.ROUND_HALF_UP));  //企业申报单价
            skus.putOnce("DclCurrcd",clearInfo.getCurrency());
            skus.putOnce("DclTotalAmt",clearInfo.getSumMoney());  //企业申报总价
            skus.putOnce("UsdStatTotalAmt",clearInfo.getSumMoney().divide(new BigDecimal("6.3776"),2,BigDecimal.ROUND_HALF_UP)); //美元统计总金额
            BaseSku baseSku = baseSkuService.queryByCode(detail.getGoodsCode());
            skus.putOnce("UseCd",baseSku.getGuse());
            skus.putOnce("DestinationNatcd",clearInfo.getTradeCountry());
            skus.putOnce("LvyrlfModecd","无"); //征免方式
            skus.putOnce("ModfMarkcd","3");  //0-未修改 1-修改 2-删除 3-增加（目前默认给：3）

            GoodsDetail.add(skus);
        }
        GoodsListNode.putOnce("GoodsDetail",GoodsDetail);
        Message.putOnce("BillHead",BillHead);
        Message.putOnce("NemsInvtHeadNode",NemsInvtHeadNode);
        Message.putOnce("SwDecHeadNode",SwDecHeadNode);
        Message.putOnce("GoodsListNode",GoodsListNode);
        request.putOnce("Message",Message);
        String xml = JSONUtils.toXml(request);
        System.out.println(xml);
        String response = reqKJGReceipt(
                clearCompanyInfo.getKjgUser(),
                clearCompanyInfo.getKjgKey(),
                xml,
                KJGMsgType.LDG_CHART_SUBMIT
        );
        System.out.println(response);
        return response;
    }

    /**
     * 仓单数据身份请求(包含查询仓单节点,核注单,核放单节点)
     */
    public String reqKJGReceipt(String userName,String key,String xmlStr,String msgType) throws UnsupportedEncodingException {
        //先用测试请求地址
        String host = "http://xtbiz.trainer.kjb2c.com/open/open.do";
        String timestamp = DateUtils.format(new Date(),"yyyy-MM-dd HH:mm:ss");
        String sign = SecureUtil.md5(userName + key + timestamp);
//        timestamp = URLEncoder.encode(timestamp,"UTF-8");
        xmlStr = URLEncoder.encode(xmlStr,"UTF-8");
        String params = "userid=" + userName + "&timestamp=" + timestamp + "&sign=" + sign + "&xmlstr=" + xmlStr + "&msgtype=" + msgType;
         System.out.println(params);
        String result = HttpUtil.post(host,params);
        return result;
    }

    // 电商身份请求
    public String reqDSKJG( String msgType, String xml, String userName, String key) throws UnsupportedEncodingException {
        String host = "https://api.kjb2c.com/uniform/uniform.do";
        String timestamp = DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
        String sign = SecureUtil.md5(userName + key + timestamp);
        timestamp = URLEncoder.encode(timestamp, "UTF-8");
        xml = URLEncoder.encode(xml, "UTF-8");
        String params = "userid=" + userName + "&timestamp=" + timestamp + "&sign=" + sign + "&xmlstr=" + xml + "&msgtype=" + msgType + "&customs=3105";
        System.out.println(params);
        String result = HttpUtil.post(host, params);
        return result;
    }

    // 电商身份请求
    public String reqDSKJG( String msgType, String xml) throws UnsupportedEncodingException {
        String host = "https://api.kjb2c.com/uniform/uniform.do";
        String timestamp = DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
        String sign = SecureUtil.md5(KJG_USER + KJG_KEY + timestamp);
        timestamp = URLEncoder.encode(timestamp, "UTF-8");
        xml = URLEncoder.encode(xml, "UTF-8");
        String params = "userid=" + KJG_USER + "&timestamp=" + timestamp + "&sign=" + sign + "&xmlstr=" + xml + "&msgtype=" + msgType + "&customs=3105";
        String result = HttpUtil.post(host, params);
        return result;
    }

    // 总署报文请求
    public static String reqZSKjg(String msgType, String xmlstr, String userName, String key) {
        try {
            String host = "https://api2.kjb2c.com/dsapi/dsapi.do";
            String timestamp = DateUtils.format(new Date(), DatePattern.NORM_DATETIME_FORMAT);
            String sign = SecureUtil.md5(userName + key + timestamp);
            timestamp = URLEncoder.encode(timestamp, "UTF-8");
            xmlstr = URLEncoder.encode(xmlstr, "UTF-8");

            String params = "userid=" + userName + "&timestamp=" + timestamp + "&sign=" + sign + "&xmlstr=" + xmlstr + "&msgtype=" + msgType + "&customs=3105";
            System.out.println(xmlstr);
            String result = HttpUtil.post(host, params);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    // 仓库身份请求
    public static String reqCKKJG(String msgType, String msg) {
        String host = "https://api.kjb2c.com/stock/whmsg.do";
        String sign = SecureUtil.md5(KJG_USER + KJG_KEY );
        String params = "userid=" + KJG_USER + "&msgtype=" + msgType + "&sign=" + sign + "&msg=" + msg;
        String post = HttpUtil.post(host, params);
        return post;
    }


}
