package me.zhengjie.support.meituan;

import com.alibaba.fastjson.annotation.JSONField;
import me.zhengjie.support.CommonApiParam;
import me.zhengjie.support.douyin.ErrorInfo;

import java.util.List;

public class MeiTuanGetCrossBorderDetailResponse {

    /**
     * 调用结果码
     * 1-全部操作成功，查询到的数据在data字段中；
     * 2-部分成功，成功的数据存储在data字段中，失败的数据存在error_list字段中；
     * 3-全部操作失败，失败的数据存在error_list字段中；
     * 4-请求失败，一般为签名或限流问题，关注error字段中的具体描述即可
     */
    @JSONField(name = "result_code")
    private Integer resultCode;

    /**
     * 具体数据，json字符串格式
     */
    private String  data;

    @JSONField(name = "error_list")
    private List<MeiTuanError>errorList;

    public Integer getResultCode() {
        return resultCode;
    }

    public void setResultCode(Integer resultCode) {
        this.resultCode = resultCode;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public List<MeiTuanError> getErrorList() {
        return errorList;
    }

    public void setErrorList(List<MeiTuanError> errorList) {
        this.errorList = errorList;
    }
}
