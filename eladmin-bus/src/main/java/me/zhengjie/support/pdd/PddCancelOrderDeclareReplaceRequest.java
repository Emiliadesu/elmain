package me.zhengjie.support.pdd;

public class PddCancelOrderDeclareReplaceRequest extends PddCancelOrderDeclareRequest {
    @Override
    public String getApiPath() {
        return super.getApiPath()+"-replace";
    }
}
