package me.zhengjie.support.jackYun;

public class JackYunDeliver2bRequest extends JackYunDeliverRequest{
    @Override
    public String getMethod() {
        return "stockout.confirm";
    }
}
