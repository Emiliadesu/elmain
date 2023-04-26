package me.zhengjie.support.youzan;

import com.alibaba.fastjson.JSONObject;
import com.youzan.cloud.open.sdk.core.client.auth.Token;
import com.youzan.cloud.open.sdk.core.client.core.DefaultYZClient;
import com.youzan.cloud.open.sdk.core.client.core.YouZanClient;
import com.youzan.cloud.open.sdk.core.oauth.model.OAuthToken;
import com.youzan.cloud.open.sdk.core.oauth.token.TokenParameter;
import com.youzan.cloud.open.sdk.gen.v1_0_1.api.YouzanPayCustomsDeclarationReportpaymentReport;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportParams;
import com.youzan.cloud.open.sdk.gen.v1_0_1.model.YouzanPayCustomsDeclarationReportpaymentReportResult;
import com.youzan.cloud.open.sdk.gen.v3_0_0.api.YouzanLogisticsOnlineConfirm;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmParams;
import com.youzan.cloud.open.sdk.gen.v3_0_0.model.YouzanLogisticsOnlineConfirmResult;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradeGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.api.YouzanTradesSoldGet;
import com.youzan.cloud.open.sdk.gen.v4_0_0.model.YouzanTradeGetParams;
import com.youzan.cloud.open.security.SecretClient;
import com.youzan.cloud.open.security.exception.DataSecurityException;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.ShopToken;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Timestamp;


@Slf4j
public class YouZanSupport {
    //@Value("${youZan.clientId}")
    private final String clientId="bac9338708e8cef6d6";
    //@Value("${youZan.clientSecret}")
    private final String clientSecret="a5a1524eabe5228714666c1495f07de3";

    private static volatile YouZanSupport youZanSupport;
    public static void main(String[] args) throws Exception{
        YouZanSupport support=new YouZanSupport();
        YouzanTradeGetResult result=support.pullOrderByTid("E20210825115817063404187","7918016993f62ebe79fe8141c632eba");
        if (result.getSuccess())
            System.out.println("成功");
        else
            System.out.println("失败");
    }

    public static YouZanSupport initSupport() throws DataSecurityException{
        if(youZanSupport == null){
            synchronized (YouZanSupport.class) {
                if(youZanSupport == null){
                    youZanSupport = new YouZanSupport();
                }
            }
        }
        return youZanSupport;
    }

    private final SecretClient secretClient=new SecretClient(clientId, clientSecret);

    private YouZanSupport() throws DataSecurityException {
        log.info(DateUtils.now()+"\t有赞Support实例化成功");
    }

    public void init(){
        log.info(DateUtils.now()+"\t有赞Support开始实例化");
    }

    public void detory(){
        log.info(DateUtils.now()+"\t有赞Support开始销毁");
    }

    public void getTokenByToolType(String code, ShopToken shopToken) throws Exception {
        DefaultYZClient yzClient = new DefaultYZClient();
        TokenParameter tokenParameter = TokenParameter.code()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .code(code)
                .build();
        OAuthToken codeToken = yzClient.getOAuthToken(tokenParameter);
        String refreshToken = codeToken.getRefreshToken();
        long expires = codeToken.getExpires();
        String accessToken = codeToken.getAccessToken();
        shopToken.setAccessToken(accessToken);
        shopToken.setRefreshToken(refreshToken);
        shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()));
        shopToken.setTokenTime(expires / 1000);
        shopToken.setPlatformShopId(codeToken.getAuthorityId());
        log.info("获取到的token {}", accessToken);
    }

    public void refreshToken(ShopToken shopToken) throws Exception {
        DefaultYZClient yzClient = new DefaultYZClient();
        TokenParameter tokenParameter = TokenParameter.refresh().clientId(clientId)
                .clientSecret(clientSecret)
                .refreshToken(shopToken.getRefreshToken()).build();
        OAuthToken token = yzClient.getOAuthToken(tokenParameter);
        String refreshToken = token.getRefreshToken();
        long expires = token.getExpires();
        String accessToken = token.getAccessToken();
        shopToken.setAccessToken(accessToken);
        shopToken.setRefreshToken(refreshToken);
        shopToken.setRefreshTime(new Timestamp(System.currentTimeMillis()));
        shopToken.setTokenTime(expires / 1000);
        log.info("获取到的token {}", accessToken);
    }

    /**
     * 拉取订单
     *
     * @param params      拉取订单参数
     * @param accessToken 授权令牌
     * @return 拉单结果
     * @throws Exception
     */
    public YouzanTradesSoldGetResponse pullOrder(YouzanTradesSoldGetParams params, String accessToken) throws Exception {
        YouZanClient client = new DefaultYZClient();
        YouzanTradesSoldGet youzanTradesSoldGet = new YouzanTradesSoldGet();
        youzanTradesSoldGet.setAPIParams(params);//设置参数
        Token token = new Token(accessToken);//设置令牌
        String resultBody = client.invoke(youzanTradesSoldGet, token);//发起请求
        resultBody = StringUtil.filterEmoji(resultBody);//去除emoji表情符号
        return JSONObject.toJavaObject(JSONObject.parseObject(resultBody), YouzanTradesSoldGetResponse.class);
    }

    /**
     * 查询平台订单状态
     *
     * @param orderNo     平台订单号
     * @param accessToken 授权令牌
     * @return 有赞状态码
     */
    public String queryOrderStatus(String orderNo, String accessToken) throws Exception {
        YouZanClient client = new DefaultYZClient();
        YouzanTradeGet youzanTradeGet = new YouzanTradeGet();
        //创建参数对象,并设置参数
        YouzanTradeGetParams youzanTradeGetParams = new YouzanTradeGetParams();
        youzanTradeGetParams.setTid(orderNo);
        youzanTradeGet.setAPIParams(youzanTradeGetParams);//设置参数
        Token token = new Token(accessToken);
        YouzanTradeGetResult result = client.invoke(youzanTradeGet, token, YouzanTradeGetResult.class);
        if (result.getSuccess()) {
            YouzanTradeGetResult.YouzanTradeGetResultFullorderinfo fullOrderInfo = result.getData().getFullOrderInfo();
            YouzanTradeGetResult.YouzanTradeGetResultOrderinfo orderInfo = fullOrderInfo.getOrderInfo();
            String status = orderInfo.getStatus();
            Integer refundStatus = orderInfo.getRefundState();
            if (refundStatus != null && refundStatus != 0 && !StringUtil.equals("TRADE_CLOSED", status)) {
                return "TRADE_REFUND";
            }
            return status;
        }
        throw new BadRequestException(result.getMessage());
    }

    /**
     * 根据单号拉取订单
     *
     * @param orderNo     有赞订单号
     * @param accessToken 授权令牌
     * @return 有赞订单响应
     * @throws Exception
     */
    public YouzanTradeGetResult pullOrderByTid(String orderNo, String accessToken) throws Exception {
        YouZanClient client = new DefaultYZClient();
        YouzanTradeGet youzanTradeGet = new YouzanTradeGet();
        //创建参数对象,并设置参数
        YouzanTradeGetParams youzanTradeGetParams = new YouzanTradeGetParams();
        youzanTradeGetParams.setTid(orderNo);
        youzanTradeGet.setAPIParams(youzanTradeGetParams);//设置参数
        Token token = new Token(accessToken);
        return client.invoke(youzanTradeGet, token, YouzanTradeGetResult.class);
    }

    public YouzanPayCustomsDeclarationReportpaymentReportResponse pushYzOrder(String orderNo, ShopToken shopToken) throws Exception {
        YouzanTradeGetResult orderResult = pullOrderByTid(orderNo, shopToken.getAccessToken());
        if (!orderResult.getSuccess()) {
            throw new BadRequestException(orderResult.getMessage());
        }
        YouzanPayCustomsDeclarationReportpaymentReport report = new YouzanPayCustomsDeclarationReportpaymentReport();
        YouzanPayCustomsDeclarationReportpaymentReportParams youzanPayCustomsDeclarationReportpaymentReportParams = new YouzanPayCustomsDeclarationReportpaymentReportParams();
        //判断是否是分销单(优先使用分销)
        String fxOrderNo = null;
        String fxInnerTransactionNo = null;
        String fxKdtId = null;
        try {
            fxOrderNo = orderResult.getData().getFullOrderInfo().getOrderInfo().getOrderExtra().getFxOrderNo();
            fxInnerTransactionNo = orderResult.getData().getFullOrderInfo().getOrderInfo().getOrderExtra().getFxInnerTransactionNo();
            fxKdtId = orderResult.getData().getFullOrderInfo().getOrderInfo().getOrderExtra().getFxKdtId();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        if (StringUtil.isNotEmpty(fxOrderNo) && StringUtil.isNotEmpty(fxInnerTransactionNo) && StringUtil.isNotEmpty(fxKdtId)) {
            youzanPayCustomsDeclarationReportpaymentReportParams.setTid(fxOrderNo);//分销订单号
            youzanPayCustomsDeclarationReportpaymentReportParams.setTransaction(fxInnerTransactionNo);//分销支付流水号
            youzanPayCustomsDeclarationReportpaymentReportParams.setKdtId(Long.parseLong(fxKdtId));//店铺id
        } else {
            youzanPayCustomsDeclarationReportpaymentReportParams.setTid(orderResult.getData().getFullOrderInfo().getOrderInfo().getTid());
            //youzanPayCustomsDeclarationReportpaymentReportParams.setAmount(Long.parseLong(orderInfoYz.getOrders().get(0).getPayment()));//报关金额 拆单必传
            try {
                youzanPayCustomsDeclarationReportpaymentReportParams.setTransaction(orderResult.getData().getFullOrderInfo().getPayInfo().getTransaction().get(0));//有赞支付流水号
            } catch (NullPointerException e) {
                throw new BadRequestException(orderNo + "没有支付单号");
            }
            youzanPayCustomsDeclarationReportpaymentReportParams.setKdtId(Long.parseLong(shopToken.getPlatformShopId()));
        }
        youzanPayCustomsDeclarationReportpaymentReportParams.setActionType(1);//报关类型
        youzanPayCustomsDeclarationReportpaymentReportParams.setCustomsCode("NN");//海关编号
        report.setAPIParams(youzanPayCustomsDeclarationReportpaymentReportParams);//设置参数
        YouZanClient client = new DefaultYZClient();
        return client.invoke(report, new Token(shopToken.getAccessToken()), YouzanPayCustomsDeclarationReportpaymentReportResponse.class);
    }

    public YouzanLogisticsOnlineConfirmResult deliverGoods(YouzanLogisticsOnlineConfirmParams youzanLogisticsOnlineConfirmParams, String token) throws Exception {
        DefaultYZClient yzClient = new DefaultYZClient();
        YouzanLogisticsOnlineConfirm youzanLogisticsOnlineConfirm = new YouzanLogisticsOnlineConfirm();
        youzanLogisticsOnlineConfirm.setAPIParams(youzanLogisticsOnlineConfirmParams);
        return yzClient.invoke(youzanLogisticsOnlineConfirm, new Token(token), YouzanLogisticsOnlineConfirmResult.class);
    }

    public static String translationStatusCode(String statusCode) {
        String status = null;
        if (statusCode == null)
            return "null";
        switch (statusCode) {
            case "WAIT_BUYER_PAY":
                status = "等待买家付款";
                break;
            case "TRADE_PAID":
                status = "订单已支付";
                break;
            case "WAIT_CONFIRM":
                status = "待确认";
                break;
            case "WAIT_SELLER_SEND_GOODS":
                status = "等待卖家发货";
                break;
            case "WAIT_BUYER_CONFIRM_GOODS":
                status = "等待买家确认收货";
                break;
            case "TRADE_SUCCESS":
                status = "买家已签收";
                break;
            case "TRADE_CLOSED":
                status = "交易关闭";
                break;
            case "TRADE_REFUND":
                status = "有退款行为";
                break;
            default:
                status = statusCode;
                break;
        }
        return status;
    }

    public String decryptData(String kdtId, String encryptData) throws Exception {
        if (StringUtil.isBlank(encryptData))
            return "";
        if (secretClient.isEncrypt(encryptData)) {
            return secretClient.decrypt(Long.parseLong(kdtId), encryptData);
        }
        return encryptData;
    }

    public String encryptData(String shopId, String decData) throws Exception {
        if (StringUtil.isBlank(decData))
            return "";
        if (secretClient.isEncrypt(decData)) {
            return decData;
        }
        return secretClient.encrypt(Long.parseLong(shopId), decData);
    }

    public static Integer translationToDYStatus(String youZanStatusCode) throws Exception{
        switch (youZanStatusCode){
            case "WAIT_SELLER_SEND_GOODS":
                return 2;
            case "WAIT_BUYER_CONFIRM_GOODS":
                return 3;
            case "TRADE_SUCCESS":
                return 5;
            case "TRADE_REFUND":
                return 16;
            case "TRADE_CLOSED":
                return 21;
            default:
                throw new Exception("未知异常:"+youZanStatusCode);
        }
    }
}
