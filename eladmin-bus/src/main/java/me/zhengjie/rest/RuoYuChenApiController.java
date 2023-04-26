package me.zhengjie.rest;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.AnonymousAccess;
import me.zhengjie.annotation.Log;
import me.zhengjie.rest.model.ruoyuchen.request.RuoYuChenMsgPush;
import me.zhengjie.rest.model.ruoyuchen.response.RuoYuChenStockCheckSignTest;
import me.zhengjie.service.RuoYuChenService;
import me.zhengjie.support.ruoYuChen.RuoYuChenSupport;
import me.zhengjie.support.ruoYuChen.response.RuoYuChenFileUpload;
import me.zhengjie.utils.Md5Utils;
import me.zhengjie.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;

@RestController
@RequiredArgsConstructor
@Api(tags = "crossBorderOrder管理")
@RequestMapping("/api/ruo-yu-chen")
public class RuoYuChenApiController {
    @Autowired
    private RuoYuChenSupport ruoYuChenSupport;

    @Autowired
    private RuoYuChenService ruoYuChenService;

    private static class Result<T>{
        private Integer status;
        private String msg;
        private T data;

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public void success(){
            this.status = 0;
            this.msg = "成功";
        }

        public void success(T data){
            this.status = 0;
            this.msg = "成功";
            this.data=data;
        }

        public void fail(String errMsg){
            this.status = 1;
            this.msg = errMsg;
        }
    }

    @Log("若羽臣消息推送")
    @ApiOperation("若羽臣消息推送")
    @PostMapping(value = "/stock/warehouse/send")
    @AnonymousAccess
    public Result stockWarehouseSend(@RequestBody String body,
                                     @RequestHeader String method,
                                     @RequestHeader String warehouseCode,
                                     @RequestHeader String appId,
                                     @RequestHeader(name = "signTimeStamp") String signTimeStampStr,
                                     @RequestHeader String sign){
        Result result=new Result();
        try {
            System.out.println(body);
            System.out.println(method);
            System.out.println(warehouseCode);
            System.out.println(appId);
            System.out.println(signTimeStampStr);
            System.out.println(sign);
            if (!StringUtils.equals(appId,ruoYuChenSupport.getAppId()))
                throw new Exception("appId错误");
            Date signTimeStamp= DateUtil.parse(signTimeStampStr,"yyyyMMddHHmmss");
            if (System.currentTimeMillis()-signTimeStamp.getTime()>30000)
                throw new Exception("请求时间过长，签名已失效");
            boolean verify=ruoYuChenSupport.verify(new JSONObject(body),sign,null);
            if (!verify)
                throw new Exception("验签失败");
            RuoYuChenMsgPush ruoYuChenMsgPush=new RuoYuChenMsgPush();
            ruoYuChenMsgPush.setMethod(method);
            ruoYuChenMsgPush.setWarehouseCode(warehouseCode);
            ruoYuChenMsgPush.setData(body);
            ruoYuChenService.stockWarehouseSend(ruoYuChenMsgPush);
            result.success();
        }catch (Exception e){
            e.printStackTrace();
            result.fail(e.getMessage()==null?"Null":e.getMessage());
        }
        return result;
    }

    @Log("若羽臣验证签名测试")
    @ApiOperation("若羽臣验证签名测试")
    @PostMapping(value = "/stock/checkSign/test")
    @AnonymousAccess
    public Result<RuoYuChenStockCheckSignTest> stockCheckSignTest(String secret, Object data){
        Result<RuoYuChenStockCheckSignTest> result=new Result<>();
        try {
            JSONObject jsonObject=new JSONObject(data);
            StringBuilder builder=new StringBuilder();
            ruoYuChenSupport.appendSignStr(builder,jsonObject,false);
            builder.append(StringUtils.isEmpty(secret)?ruoYuChenSupport.getAppSecret():secret);
            String sign=Md5Utils.md5Hex(builder.toString());
            result.setStatus(0);
            RuoYuChenStockCheckSignTest resp=new RuoYuChenStockCheckSignTest();
            resp.setSign(sign);
            resp.setStrSign(builder.toString());
            result.setData(resp);
        }catch (Exception e){
            result.fail(e.getMessage()==null?"Null":e.getMessage());
        }
        return result;
    }

    @ApiOperation("上传SKU")
    @RequestMapping(value = "uploadSku")
    @PreAuthorize("@el.check('baseSku:uploadSku')")
    @Log("上传SKU")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file, @RequestParam("id") Long id){
        ruoYuChenService.upLoadFile(file,id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
