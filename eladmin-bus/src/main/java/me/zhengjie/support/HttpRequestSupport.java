package me.zhengjie.support;

import cn.hutool.http.HttpRequest;
import me.zhengjie.annotation.ReqLog;
import me.zhengjie.annotation.type.ReqLogType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 网络请求类
 */
@Service
public class HttpRequestSupport {

    public String doPost(ReqLogType reqLogType, String url, String body, Map<String, List<String>> headers) {
        String res = HttpRequest.post(url)
                .header(headers)
                .body(body).execute()
                .body();
        return res;
    }

    public String doPost(ReqLogType reqLogType, String url) {
        String res = HttpRequest.post(url).execute().body();
        return res;
    }
}
