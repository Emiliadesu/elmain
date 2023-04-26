package me.zhengjie.support.pdd;

public class PddUpdateShopTokenRequest extends PddShopToken implements PddCommonRequest{
    /**
     * 注意：该请求并不是更新云服务器的token数据而是把云服务器指定的token同步到业务服务器
     * @return
     */
    @Override
    public String getApiPath() {
        return "/api/pdd/get-new-token";
    }
}
