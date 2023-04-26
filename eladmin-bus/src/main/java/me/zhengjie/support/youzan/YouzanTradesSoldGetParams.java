package me.zhengjie.support.youzan;

import com.alibaba.fastjson.annotation.JSONField;
import com.youzan.cloud.open.sdk.api.AbstractApiParams;
import java.util.Date;

public class YouzanTradesSoldGetParams extends AbstractApiParams {

    public static final Long serialVersionUID = 1L;
    /** 商品id，有赞生成的商品唯一id */
    @JSONField(name = "goods_id")
    private Long goodsId;
    /** 订单创建结束时间，用于筛选某一段时间的订单，时间格式：yyyy-MM-dd HH:mm:ss。开始时间和结束时间必须成对出现，查询时间跨度不能大于3个月。 */
    @JSONField(name = "end_created")
    private Date endCreated;
    /** 订单创建开始时间，用于筛选某一段时间的订单，时间格式：yyyy-MM-dd HH:mm:ss。开始时间和结束时间必须成对出现，查询时间跨度不能大于3个月。 */
    @JSONField(name = "start_created")
    private Date startCreated;
    /** 多网点id */
    @JSONField(name = "offlineId")
    private Long offlineId;
    /** 收货人昵称 */
    @JSONField(name = "receiver_name")
    private String receiverName;
    /** 订单更新结束时间，用于筛选某一段时间的订单，时间格式：yyyy-MM-dd HH:mm:ss。开始时间和结束时间必须成对出现，查询时间跨度不能大于3个月。 */
    @JSONField(name = "end_update")
    private Date endUpdate;
    /** 粉丝类型， 1:自有粉丝（商家店铺后台绑定的公众号粉丝），9:代销粉丝（有赞大账号粉丝） */
    @JSONField(name = "fans_type")
    private Integer fansType;
    /** 页码，从1~100开始，分页数不能超过100页。如果订单较多请使用时间参数分割。page_size 和page_no相乘总条数不能大于3200条。 */
    @JSONField(name = "page_no")
    private Integer pageNo;
    /** 收货人手机号 */
    @JSONField(name = "receiver_phone")
    private String receiverPhone;
    /**
     * 订单类型：NORMAL：普通订单，PEERPAY：代付，GIFT：我要送人，FX_CAIGOUDAN：分销采购单，PRESENT：赠品，WISH：心愿单，QRCODE：二维码订单，QRCODE_3RD：线下收银台订单，FX_MERGED：合并付货款，VERIFIED：1分钱实名认证，PINJIAN：品鉴，REBATE：返利，FX_QUANYUANDIAN：全员开店，FX_DEPOSIT：保证金，PF：批发你，GROUP：拼团，HOTEL：酒店，TAKE_AWAY：外卖，CATERING_OFFLINE：堂食点餐，CATERING_QRCODE：外卖买单，BEAUTY_APPOINTMENT：美业预约单，BEAUTY_SERVICE：美业服务单，KNOWLEDGE_PAY：知识付费，GIFT_CARD：礼品卡（参照微商城模块）
     */
    @JSONField(name = "type")
    private String type;
    /** 买家id */
    @JSONField(name = "buyer_id")
    private Long buyerId;
    /** 是否需要返回订单url */
    @JSONField(name = "need_order_url")
    private Boolean needOrderUrl;
    /** 订单号 */
    @JSONField(name = "tid")
    private String tid;
    /**
     * 订单状态：一次只能查询一种状态。
     * 待付款：WAIT_BUYER_PAY，
     * 待发货：WAIT_SELLER_SEND_GOODS，
     * 等待买家确认：WAIT_BUYER_CONFIRM_GOODS，
     * 订单完成：TRADE_SUCCESS，
     * 订单关闭：TRADE_CLOSE（新增-参照微商城模块）退款中：TRADE_REFUND
     */
    @JSONField(name = "status")
    private String status;
    /** 每页条数。默认20条，最大不能超过100，建议使用默认分页。如果订单较多请使用时间参数分割。page_size 和page_no相乘总条数不能大于3200条。 */
    @JSONField(name = "page_size")
    private Integer pageSize;
    /** 订单更新开始时间，用于筛选某一段时间的订单，时间格式：yyyy-MM-dd HH:mm:ss。开始时间和结束时间必须成对出现，查询时间跨度不能大于3个月。 */
    @JSONField(name = "start_update")
    private Date startUpdate;
    /** 物流类型:同城送订单：LOCAL_DELIVERY,自提订单：SELF_FETCH,快递配送：EXPRESS */
    @JSONField(name = "express_type")
    private String expressType;
    /** 粉丝id */
    @JSONField(name = "fans_id")
    private Long fansId;

    @JSONField(name = "cust_id")
    private Long custId;

    @JSONField(name = "l_shop_id")
    private Long lShopId;

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }

    public Long getGoodsId() {
        return this.goodsId;
    }

    public void setEndCreated(Date endCreated) {
        this.endCreated = endCreated;
    }

    public Date getEndCreated() {
        return this.endCreated;
    }

    public void setStartCreated(Date startCreated) {
        this.startCreated = startCreated;
    }

    public Date getStartCreated() {
        return this.startCreated;
    }

    public void setOfflineId(Long offlineId) {
        this.offlineId = offlineId;
    }

    public Long getOfflineId() {
        return this.offlineId;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverName() {
        return this.receiverName;
    }

    public void setEndUpdate(Date endUpdate) {
        this.endUpdate = endUpdate;
    }

    public Date getEndUpdate() {
        return this.endUpdate;
    }

    public void setFansType(Integer fansType) {
        this.fansType = fansType;
    }

    public Integer getFansType() {
        return this.fansType;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageNo() {
        return this.pageNo;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getReceiverPhone() {
        return this.receiverPhone;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return this.type;
    }

    public void setBuyerId(Long buyerId) {
        this.buyerId = buyerId;
    }

    public Long getBuyerId() {
        return this.buyerId;
    }

    public void setNeedOrderUrl(Boolean needOrderUrl) {
        this.needOrderUrl = needOrderUrl;
    }

    public Boolean getNeedOrderUrl() {
        return this.needOrderUrl;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTid() {
        return this.tid;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return this.status;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public void setStartUpdate(Date startUpdate) {
        this.startUpdate = startUpdate;
    }

    public Date getStartUpdate() {
        return this.startUpdate;
    }

    public void setExpressType(String expressType) {
        this.expressType = expressType;
    }

    public String getExpressType() {
        return this.expressType;
    }

    public void setFansId(Long fansId) {
        this.fansId = fansId;
    }

    public Long getFansId() {
        return this.fansId;
    }

    public static Long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }

    public Long getLShopId() {
        return lShopId;
    }

    public void setLShopId(Long lShopId) {
        this.lShopId = lShopId;
    }
}

