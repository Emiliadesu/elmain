package me.zhengjie.utils.constant;

public class KJGMsgType {

    /**
     * 商品备案
     */
    public static final String NINGBO_REGISTER_SKU = "cnec_jh_hscode";

    /**
     * 申报订单REGISTER_SKU
     */
    public static final String NINGBO_DECLARE_ORDER = "cnec_jh_order";

    /**
     * 取消申报订单
     */
    public static final String NINGBO_CANCEL_ORDER = "cnec_jh_cancel";

    /**
     * 刷新申报订单（更新单据状态）
     */
    public static final String NINGBO_REFRESH_ORDER = "cnec_jh_decl_byorder";

    /**
     * 申报单状态查询(根据更新时间)
     */
    public static final String NINGBO_UPDATEMFT_ORDER_BYTIME = "cnec_jh_decl_byupdatetime";

    /**
     * 刷新申报订单总署状态
     */
    public static final String NINGBO_REFRESH_ORDER_ZS = "cnec_tyb_workflow";

    /**
     * 查询备案商品详情
     */
    public static final String NINGBO_GET_GOODS_INFO= "cnec_jh_getgoods";

    /**
     * 退货申请
     */
    public static final String NINGBO_DECLARE_RETURN= "cnec_jh_rejdec";

    /**
     * 退货状态查询
     */
    public static final String NINGBO_DECLARE_RETURN_REFRESH= "cnec_jh_rejser";

    /**
     * 核注单发起
     */
    public static final String NINGBO_DECLARE_INVT= "CNEC_NEMS_INVT";

    /**
     * 查询清关单号
     */
    public static final String NINGBO_GET_DECLARE_NO= "cnec_get_mftno";




    /****************************************仓库相关msg********************************************/

    /**
     * 仓单数据上传请求
     */
    public static final String LDG_CHART_SUBMIT = "LDG_CHART_SUBMIT";

    /**
     * 仓单节点查询
     */
    public static final String LDG_CHART_WORKFLOW_SYNC = "LDG_CHART_WORKFLOW_SYNC";

    /**
     * 核注单节点查询
     */
    public static final String LDG_NEMSINVT_WORKFLOW_SYNC = "LDG_NEMSINVT_WORKFLOW_SYNC";

    /**
     * 核放单节点查询
     */
    public static final String LDG_PASSPORT_WORKFLOW_SYNC = "LDG_PASSPORT_WORKFLOW_SYNC";


    /**
     * 货品下发
     */
    public static final String NINGBO_SKU= "cnec_wh_3";


    /**
     * 入库
     */
    public static final String NINGBO_WH_IN= "cnec_wh_4";

    /**
     * 入库取消
     */
    public static final String NINGBO_WH_IN_CANCEL= "cnec_wh_5";

    /**
     * 入库回传
     */
    public static final String NINGBO_WH_IN_CONFIRM= "cnec_im_1";

    /**
     * 订单
     */
    public static final String NINGBO_WH_ORDER= "cnec_wh_6";


    /**
     * 订单审核状态下发
     */
    public static final String NINGBO_WH_STATUS= "cnec_wh_9";

    /**
     * 订单取消
     */
    public static final String NINGBO_WH_ORDER_CANCEL= "cnec_wh_11";

    /**
     * 发货
     */
    public static final String NINGBO_DELIVER= "cnec_im_6";
}
