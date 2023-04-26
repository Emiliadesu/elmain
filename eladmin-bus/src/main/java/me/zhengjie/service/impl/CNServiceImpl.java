package me.zhengjie.service.impl;

import cn.hutool.http.Header;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.CNService;
import me.zhengjie.support.CNSupport;
import me.zhengjie.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CNServiceImpl implements CNService {

    @Autowired
    private CNSupport cnSupport;

    @Override
    public List<Map<String, Object>> queryPre(String csrf, String cookie) {
        String url = "https://tida.cainiao.com/api/campaign_task/queryCampTopOrderStructureList";
        JSONObject params = new JSONObject();
        params.putOnce("_csrf", csrf);
        params.putOnce("workStationCode", "STORE_12018381");
        params.putOnce("currentPage", 1);
        params.putOnce("pageSize", 1000);
        params.putOnce("campPrimaryId", 165194);
        JSONArray forecastSourceList = new JSONArray();
        forecastSourceList.add("camp");
        forecastSourceList.add("algorithm_camp");
        params.putOnce("forecastSourceList", forecastSourceList);
        JSONObject resultObject = requestCN(cookie, params, url, csrf, null);
        if (resultObject.getBool("success")) {
            List<Map<String, Object>> result= new ArrayList<>();
            JSONArray data = resultObject.getJSONObject("data").getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                log.info("开始解析：" + i);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("货主", data.getJSONObject(i).getStr("ownerName"));
                item.put("数据来源", data.getJSONObject(i).getStr("forecastSourceName"));
                item.put("结构", data.getJSONObject(i).getStr("outItemIds"));
                item.put("当前库存", data.getJSONObject(i).getStr("itemInventory"));
                item.put("结构名称", data.getJSONObject(i).getStr("itemNames"));
                item.put("当前预包", data.getJSONObject(i).getInt("bomInventory"));
                item.put("预售量", data.getJSONObject(i).getInt("forecastOrderNum"));
                result.add(item);
            }
            return result;
        }else {
            throw new BadRequestException(resultObject.toStringPretty());
        }
    }

    @Override
    public List<Map<String, Object>> queryDecOrder(String csrf, String cookie) {
        String url = "https://partner.customs.cainiao.com/manifest/queryData";
        Map<String, Object> params = new HashMap<>();
        params.put("serviceId", 5000000000009L);
        params.put("p_csrf", csrf);
        params.put("storeCode", "STORE_12018381");
        params.put("searchCode", "");
        JSONArray date = new JSONArray();
        date.add("2021-10-01 00:00:00");
        date.add("2021-11-01 00:00:00");

        params.put("tmsWayBill", false);
        params.put("declareFormNo", false);
        params.put("pageIndex", 1);
        params.put("pageSize", 10);

        params.put("dateStart", "2021-10-01 00:00:00");
        params.put("dateEnd", "2021-11-01 00:00:00");

        params.put("date", date);
        JSONObject resultObject = requestCN(cookie, null, url, csrf, params);

        System.out.println(resultObject);
        return null;
    }



    @Override
    public String waveOut(String waveNos, String csrf, String cookie) {
        String url = "https://data-express.wms.cainiao.com/api/package/list";
        String[] waveArr = waveNos.split(" ");
        String result = "";
        for (int i = 0; i < waveArr.length; i++) {
            JSONObject params = new JSONObject();
            params.putOnce("csrfId", csrf);
            params.putOnce("_csrf", csrf);
            JSONObject data = new JSONObject();
            data.putOnce("wmphu_pickBillCodeList", waveArr[i]);
            JSONObject paging = new JSONObject();
            paging.putOnce("currentPage", 1);
            paging.putOnce("pageSize", 300);
            data.putOnce("paging", paging);
            params.putOnce("data", data);
            JSONObject resultObject = requestCN(cookie, params, url, csrf, null);
            if (resultObject.getBool("success")) {
                JSONArray items = resultObject.getJSONObject("data")
                        .getJSONArray("items");
                if (items == null || items.size() == 0){
                    throw new BadRequestException("查询到的订单数为空，请检查拣选单号是否正确");
                }
                int errCount = 0;
                int checkCount = 0;
                int outCount = 0;
                for (int j = 0; j < items.size(); j++) {
                    JSONObject item = items.getJSONObject(j);
                    String mailNo = item.getStr("wmphu_waybillNo");
//                String wight = item.getString("wmphu_roughWeightDesc");
                    String wight = item.getStr("wmphu_theoreticalRoughWeightDesc");
                    String s = cnSupport.requestCN(new BigDecimal(wight).divide(new BigDecimal(1000), 3, BigDecimal.ROUND_HALF_UP), mailNo, "");
                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    System.out.println(mailNo + ": " + s);
                    switch (s) {
                        case "00":
                            errCount += 1;
                            break;
                        case "01":
                            checkCount += 1;
                            break;
                        case "02":
                            outCount += 1;
                            break;
                        case "03":
                            outCount += 1;
                            break;
                        case "04":
                            outCount += 1;
                            break;
                        case "05":
                            outCount += 1;
                            break;
                        default:
                            errCount += 1;
                            break;
                    }
                }
                result += waveArr[i] + "结果：出库-" + outCount + "，异常-" + errCount   + "，抽检-" + checkCount + "；";
            }else {
                throw new BadRequestException("查询包裹异常");
            }
        }
        return result;
    }


    private JSONObject requestCN(String cookie, JSONObject params, String url, String csrf, Map<String, Object> paramsMap) {
        String result = HttpRequest.post(url)
                .header(Header.CONTENT_TYPE, "application/json; charset=utf-8")
//                .header("x-csrf-token", csrf)
                .cookie(cookie)
                .body(params.toStringPretty())
                .execute().body();
        return JSONUtil.parseObj(result);
    }

    @Override
    public void download(List<Map<String, Object>> list, HttpServletResponse response) throws IOException {
        FileUtil.downloadExcel(list, response);
    }
}
