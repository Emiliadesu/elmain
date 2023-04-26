package me.zhengjie.support.meituan;

import com.alibaba.fastjson.JSONObject;

public class MeiTuanBaseResponse {
    private String data;

    private MeiTuanError error;

    public MeiTuanError getError() {
        return error;
    }

    public void setError(MeiTuanError error) {
        this.error = error;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
