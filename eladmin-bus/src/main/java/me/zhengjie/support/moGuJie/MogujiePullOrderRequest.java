package me.zhengjie.support.moGuJie;

import me.zhengjie.support.CommonApiParam;

public class MogujiePullOrderRequest implements CommonApiParam {
    /**
     * 订单创建时间开始，格式:yyyy-MM-dd HH:mm:ss，与endCreated配对使用
     */
    private String startCreated;
    /**
     * 订单创建时间结束，格式:yyyy-MM-dd HH:mm:ss，与startCreated配对使用
     */
    private String endCreated;
    /**
     * 订单最后更新时间开始，格式:yyyy-MM-dd HH:mm:ss，与endUpdated配对使用
     */
    private String startUpdated;
    /**
     * 订单最后更新时间结束，格式:yyyy-MM-dd HH:mm:ss，与startUpdated配对使用
     */
    private String endUpdated;
    /**
     * 交易状态，默认查询所有交易状态的数据，除了默认值外每次只能查询一种状态，非必填参数，取值：
     * ORDER_CREATED - 已下单，
     * ORDER_CANCELLED - 订单取消，
     * ORDER_PAID - 已支付，
     * ORDER_SHIPPED - 已发货，
     * ORDER_RECEIVED - 已收货，
     * ORDER_COMPLETED - 订单完成，
     * ORDER_CLOSED - 订单关闭
     */
    private String orderStatus;
    /**
     * 页码，大于零的整数，默认1，非必填
     */
    private Integer page;
    /**
     * 每页条数，大于零的整数，默认值:10;最大值:50，非必填
     */
    private Integer pageSize;
    /**
     * 起始id 非必填
     */
    private Long startOrderId;
    /**
     * 排序类型，值为 ASC 或 DESC 非必填
     */
    private String orderBy="DESC";

    private Long lShopId;

    private Long custId;

    public String getStartCreated() {
        return startCreated;
    }

    public void setStartCreated(String startCreated) {
        this.startCreated = startCreated;
    }

    public String getEndCreated() {
        return endCreated;
    }

    public void setEndCreated(String endCreated) {
        this.endCreated = endCreated;
    }

    public String getStartUpdated() {
        return startUpdated;
    }

    public void setStartUpdated(String startUpdated) {
        this.startUpdated = startUpdated;
    }

    public String getEndUpdated() {
        return endUpdated;
    }

    public void setEndUpdated(String endUpdated) {
        this.endUpdated = endUpdated;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Long getStartOrderId() {
        return startOrderId;
    }

    public void setStartOrderId(Long startOrderId) {
        this.startOrderId = startOrderId;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    @Override
    public String getMethod() {
        return "xiaodian.trade.sold.get";
    }

    @Override
    public String getKeyWord() {
        return "";
    }

    public Long getLShopId() {
        return lShopId;
    }

    public void setLShopId(Long lShopId) {
        this.lShopId = lShopId;
    }

    public Long getCustId() {
        return custId;
    }

    public void setCustId(Long custId) {
        this.custId = custId;
    }
}
