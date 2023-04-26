package me.zhengjie.rest;

import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.CNService;
import me.zhengjie.service.dto.ClearInfoQueryCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@Api(tags = "CN管理")
@RequestMapping("/api/cn")
public class CNController {

    @Autowired
    private CNService cnService;

    @Log("导出预售数据")
    @ApiOperation("导出预售数据")
    @GetMapping(value = "/download-pre")
    public void download(HttpServletResponse response, @RequestParam(value = "_csrf") String _csrf, @RequestParam(value = "cookie") String cookie) throws IOException {
        cnService.download(cnService.queryPre(_csrf, cookie), response);
    }

    @Log("导出菜鸟关务单")
    @ApiOperation("导出菜鸟关务单")
    @GetMapping(value = "/download-dec-order")
    public void downloadDecOrder(HttpServletResponse response, @RequestParam(value = "_csrf") String _csrf, @RequestParam(value = "cookie") String cookie) throws IOException {
        cnService.download(cnService.queryDecOrder(_csrf, cookie), response);
    }

    @Log("菜鸟拣选单出库")
    @ApiOperation("菜鸟拣选单出库")
    @GetMapping(value = "/wave_out")
    public ResponseEntity<Object> waveOut(@RequestParam(value = "waveNos") String waveNos, @RequestParam(value = "_csrf") String _csrf, @RequestParam(value = "cookie") String cookie) throws IOException {
        JSONObject result = new JSONObject();
        try {
            String msg = cnService.waveOut(waveNos, _csrf, cookie);
            result.putOnce("success", true);
            result.putOnce("resMsg", msg);
        }catch (Exception e) {
            e.printStackTrace();
            result.putOnce("success", false);
            result.putOnce("resMsg", e.getMessage());
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
