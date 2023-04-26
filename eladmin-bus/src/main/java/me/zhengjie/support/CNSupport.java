package me.zhengjie.support;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import me.zhengjie.utils.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 菜鸟支持类
 */
@Service
public class CNSupport {

    /**
     * 包裹重量上传
     */
    private final String COMMAND_PARCEL_INFO_UPLOAD = "sorter.parcel_info_upload";
    /**
     * 目的地请求
     */
    private final String COMMAND_DEST_REQUEST = "sorter.dest_request";
    /**
     * 分拣报告
     */
    private final String COMMAND_SORT_REPORT = "sorter.sort_report";

//    private String UCS_URL = "http://192.168.1.70:8001/ucs/api";

    private String UCS_URL = "http://192.168.1.70:14041/ucs/api";

    public String requestCN(BigDecimal kgWeight, String mailNo, String userId) {
        kgWeight = kgWeight.multiply(new BigDecimal(1000));// 菜鸟单位为克
        String bcrCode = "bcr-1";
        if (StringUtils.equals(userId, "xy")) {
            bcrCode = "BCR2";
        }
        String parcelInfoRequest = getCNParcelInfoRequest(mailNo, kgWeight.doubleValue(), bcrCode);

        String res = HttpUtil.post(UCS_URL, parcelInfoRequest);
        JSONObject resObj = JSONUtil.parseObj(res);
        JSONArray parcelInfoResultArray = resObj.getJSONArray("result");
        String chuteCode = "00";
        String sortReportRequest;
        for (int i = 0; i < parcelInfoResultArray.size(); i++) {
            int code = parcelInfoResultArray.getJSONObject(i).getInt("code");
            String command = parcelInfoResultArray.getJSONObject(i).getStr("command");
            if (code == 0 && COMMAND_DEST_REQUEST.equals(command)) {
                JSONObject params = parcelInfoResultArray.getJSONObject(i).getJSONObject("params");
                chuteCode = params.getStr("chuteCode");

                sortReportRequest = getSortReportRequestJson(mailNo, chuteCode);
                HttpUtil.post(UCS_URL, sortReportRequest);
            }
        }
        return chuteCode;
    }

    /**
     * 上报包裹信息
     * @param mailNo
     * @param kgWeight
     * @return
     */
    private String getCNParcelInfoRequest(String mailNo, Double kgWeight, String bcrCode) {
        JSONObject parcelInfoJson = new JSONObject();
        parcelInfoJson.putOnce("source", "wcs");
        parcelInfoJson.putOnce("version", 1);
        JSONArray parcelInfoData = new JSONArray();

        // 包裹信息上传
        com.alibaba.fastjson.JSONObject parcelInfoCommand = new com.alibaba.fastjson.JSONObject();
        parcelInfoCommand.put("command", COMMAND_PARCEL_INFO_UPLOAD);// 包裹信息上报
        com.alibaba.fastjson.JSONObject parcelInfoPram = new com.alibaba.fastjson.JSONObject();
        parcelInfoPram.put("barCode", mailNo);
        parcelInfoPram.put("bcrCode", bcrCode);// 增加扫码器编号
        parcelInfoPram.put("weight", kgWeight);// g
        parcelInfoCommand.put("params", parcelInfoPram);
        parcelInfoData.add(parcelInfoCommand);

        // 目的地请求
        com.alibaba.fastjson.JSONObject destRequestCommand = new com.alibaba.fastjson.JSONObject();
        destRequestCommand.put("command", COMMAND_DEST_REQUEST);
        com.alibaba.fastjson.JSONObject destRequestPram = new com.alibaba.fastjson.JSONObject();
        destRequestPram.put("bcrName", "sorter");
        destRequestPram.put("bcrCode", bcrCode);// 增加扫码器编号
        destRequestPram.put("barCode", mailNo);
        destRequestCommand.put("params", destRequestPram);
        parcelInfoData.add(destRequestCommand);

        parcelInfoJson.putOnce("data", parcelInfoData);
        return parcelInfoJson.toStringPretty();
    }

    /**
     * 上传分拣格口
     * @param mailNo
     * @param chuteCode
     * @return
     */
    private String getSortReportRequestJson(String mailNo, String chuteCode) {
        JSONObject parcelInfoJson = new JSONObject();
        parcelInfoJson.putOnce("source", "wcs");
        parcelInfoJson.putOnce("version", 1);
        JSONArray parcelInfoData = new JSONArray();
        JSONObject parcelInfoCommand = new JSONObject();
        parcelInfoCommand.putOnce("command", COMMAND_SORT_REPORT);
        JSONObject parcelInfoPram = new JSONObject();
        parcelInfoPram.putOnce("bcrName", "sorter");
        parcelInfoPram.putOnce("barCode", mailNo);
        parcelInfoPram.putOnce("chuteCode", chuteCode);
        parcelInfoCommand.putOnce("params", parcelInfoPram);
        parcelInfoData.add(parcelInfoCommand);
        parcelInfoJson.putOnce("data", parcelInfoData);
        return parcelInfoJson.toStringPretty();
    }

}
