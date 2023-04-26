package me.zhengjie.service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface CNService {

    List<Map<String, Object>> queryPre(String csrf, String cookie);

    void download(List<Map<String, Object>> list, HttpServletResponse response) throws IOException;

    String waveOut(String waveNos, String csrf, String cookie);

    List<Map<String, Object>> queryDecOrder(String csrf, String cookie);

}
