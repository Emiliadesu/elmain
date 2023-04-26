package me.zhengjie.rest;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.*;
import me.zhengjie.entity.Result;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.*;
import me.zhengjie.service.dto.CrossBorderOrderQueryCriteria;
import me.zhengjie.service.dto.CustomerKeyDto;
import me.zhengjie.service.dto.DomesticOrderQueryCriteria;
import me.zhengjie.support.douyin.SorterSupport;
import me.zhengjie.support.kjg.KJGSupport;
import me.zhengjie.utils.*;
import me.zhengjie.utils.enums.CBOrderStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.*;

/**
 * 异常处理工具类
 */
@RestController
@RequiredArgsConstructor
@Api(tags = "异常处理工具")
@RequestMapping("/api/errTools")
public class ErrToolsController {

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private KJGSupport kjgSupport;

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private CustomerKeyService customerKeyService;

    @Autowired
    private PddOrderService pddOrderService;

    @Autowired
    private YouZanOrderService youZanOrderService;

    @Autowired
    private YmatouService ymatouService;

    @Autowired
    private OrderReturnService orderReturnService;

    @Autowired
    private RedisUtils redisUtils;

    @Autowired
    private MoGuJieService moGuJieService;

    @Autowired
    private MeituanService meituanService;

    @Autowired
    private DouyinMailMarkService douyinMailMarkService;

    @Autowired
    private DailyCrossBorderOrderService dailyCrossBorderOrderService;

    @Autowired
    private SorterSupport sorterSupport;

    @Log("重推接单")
    @ApiOperation("重推接单")
    @GetMapping(value = "/re-push-con")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> rePushCon(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                douyinService.confirmOrderByTools(order);
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推清关开始")
    @ApiOperation("重推清关开始")
    @GetMapping(value = "/re-push-dec-start")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> rePushDecStart(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                douyinService.confirmClearStartByTools(order);
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推清关完成")
    @ApiOperation("重推清关完成")
    @GetMapping(value = "/re-push-dec-succ")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> rePushDecSucc(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                douyinService.confirmClearSuccessByTools(order);
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推出库")
    @ApiOperation("重推出库")
    @GetMapping(value = "/re-push-deliver")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> rePushDeliver(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                order.setStatus(CBOrderStatusEnum.STATUS_240.getCode());
                switch (order.getPlatformCode()){
                    case "YZ":
                        youZanOrderService.confirmDeliver(order);
                        break;
                    case "DY":
                        douyinService.confirmPack(order);
                        break;
                    case "PDD":
                        for (int j = 0; j < 50; j++) {
                            try {
                                pddOrderService.confirmDeliver(order);
                                break;
                            } catch (Exception e) {
                                e.printStackTrace();
                                if (!StringUtil.contains(e.getMessage(),"获取订单失败：业务服务错误")){
                                    throw new BadRequestException(e.getMessage());
                                }else if (j>48)
                                    throw new BadRequestException(e.getMessage());
                            }
                        }
                        break;
                    case "Ymatou":
                        ymatouService.confirmDeliver(order);
                        break;
                    case "MGJ":
                        moGuJieService.confirmDeliver(order);
                        break;
                    case "MeiTuan":
                        meituanService.confirmDeliver(order);
                        break;
                }
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推出库")
    @ApiOperation("重推出库")
    @GetMapping(value = "/strit-push-deliver")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> stritPushDeliver(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                switch (order.getPlatformCode()){
                    case "DY":
                        douyinService.confirmDeliver(order);
                        break;
                }
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Log("订单出库")
    @ApiOperation("订单出库-直接出库")
    @GetMapping(value = "/order-deliver")
    @PreAuthorize("@el.check('errTools:rePushCon')")
    public ResponseEntity<Object> orderDeliver(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                dailyCrossBorderOrderService.addOrderCache(order.getId());
                if (order.getStatus()!=245&&order.getStatus()>=235&&order.getStatus()<245){
                    order.setStatus(245);
                    order.setDeliverTime(new Timestamp(System.currentTimeMillis()));
                    crossBorderOrderService.update(order);
                }
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Log("重推申报")
    @ApiOperation("重推申报")
    @GetMapping(value = "/re-push-declare")
    @PreAuthorize("@el.check('errTools:rePushDeclare')")
    public ResponseEntity<Object> rePushDeclare(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                if (order == null) {
                    result.put("success", false);
                    resMsg = resMsg + split[i] + "报错：" + "无此订单" + ",";
                }else {
                    List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
                    order.setItemList(list);
                    String declareNo = reDeclare(order);
                    order.setDeclareNo(declareNo);
                    crossBorderOrderService.update(order);
                    result.put("success", true);
                    resMsg = resMsg + split[i] + "处理成功," ;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.putOnce("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推支付单")
    @ApiOperation("重推支付单")
    @GetMapping(value = "/re-push-pay-order")
    @PreAuthorize("@el.check('errTools:rePushPayOrder')")
    public ResponseEntity<Object> rePushPayOrder(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        StringBuilder resMsg = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                if (order == null) {
                    result.put("success", false);
                    resMsg.append(split[i]).append("报错：").append("无此订单").append(",");
                }else {
                    if (!StringUtil.equals("YZ",order.getPlatformCode())&&!StringUtil.equals("Ymatou",order.getPlatformCode())){
                        resMsg.append(split[i]).append("无需推送支付单,");
                        continue;
                    }
                    if (StringUtil.equals("YZ",order.getPlatformCode())){
                        String paymentNo=youZanOrderService.rePushPayOrder(order);
                        if (!StringUtil.equals(paymentNo,order.getPaymentNo())){
                            resMsg.append(split[i]).append("(支付单号发生了变化)");
                        }
                        order.setOrderSeqNo(paymentNo);
                        order.setPaymentNo(paymentNo);
                    }else if (StringUtil.equals("Ymatou",order.getPlatformCode())){
                        String[] orderNoArray=ymatouService.rePushPayOrder(order);
                        if (!StringUtil.equals(orderNoArray[0],order.getPaymentNo())){
                            resMsg.append(split[i]).append("(支付单号发生了变化)");
                        }
                        if (!StringUtil.equals(orderNoArray[1],order.getCrossBorderNo())){
                            resMsg.append(split[i]).append("(交易单号发生了变化)");
                        }
                        order.setPaymentNo(orderNoArray[0]);
                        order.setOrderSeqNo(orderNoArray[0]);
                        order.setCrossBorderNo(orderNoArray[1]);
                    }
                    crossBorderOrderService.update(order);
                    result.put("success", true);
                    resMsg.append(split[i]).append("处理成功,");
                }
            } catch (Exception e) {
                e.printStackTrace();
                result.putOnce("success", false);
                resMsg.append(split[i]).append("报错：").append(e.getMessage()).append(",");
            }
        }
        result.putOnce("resMsg", resMsg.toString());
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    private String reDeclare(CrossBorderOrder order) throws Exception{
        if (StringUtil.equals(order.getPlatformCode(),"PDD")){
            return pddOrderService.declare(order);
        }else {
            try {
                //解密
                crossBorderOrderService.decrypt(order);
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException("订单解密失败:"+e.getMessage());
            }
            String declareNo = kjgSupport.declare(order);
            try {
                //加密
                crossBorderOrderService.encrypt(order);
            }catch (Exception e){
                e.printStackTrace();
                throw new BadRequestException("订单加密失败"+e.getMessage());
            }
            return declareNo;
        }
    }

    @Log("取消申报")
    @ApiOperation("取消申报")
    @GetMapping(value = "/push-cancel-dec-orders")
    @PreAuthorize("@el.check('errTools:pushCancelDecOrders')")
    public ResponseEntity<Object> pushCancelDecOrders(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
//                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
//                crossBorderOrderService.decrypt(order);
//                kjgSupport.cancelOrder(order);
//                if (StringUtil.isNotBlank(order.getInvtNo())){
//                    order.setClearDelStartTime(DateUtils.now());
//                    order.setDeclareStatus("29");
//                    crossBorderOrderService.update(order);
//                }


                kjgSupport.cancelBeforeSucc(split[i]);

                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("退货单状态")
    @ApiOperation("退货单状态")
    @GetMapping(value = "/push-return")
    @PreAuthorize("@el.check('errTools:pushCancelDecOrders')")
    public ResponseEntity<Object> pushReturn(@Validated String orderNos, @Validated String status){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                OrderReturn orderReturn = orderReturnService.queryByOrderNo(split[i]);
                douyinService.confirmReturnByTools(orderReturn, status);
                result.put("success", true);
                resMsg = resMsg + split[i] + "处理成功," ;
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("重推锁单")
    @ApiOperation("重推锁单")
    @GetMapping(value = "/re-push-lock")
    @PreAuthorize("@el.check('errTools:rePushLock')")
    public ResponseEntity<Object> rePushLock(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order = crossBorderOrderService.queryByOrderNo(split[i]);
                douyinService.confirmPackByTool(order);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("接口测试")
    @ApiOperation("接口测试")
    @GetMapping(value = "/api-test")
    @PreAuthorize("@el.check('errTools:pushCancelDecOrders')")
    public Result apiest(@RequestParam("data")String data,
                         @RequestParam("customersCode")String customersCode){
        try {
            CustomerKeyDto customerKeyDto = customerKeyService.findByCustCode(customersCode);
            if (customerKeyDto == null)
                return ResultUtils.getFail("customersCode未配置");
            String sign = SecureUtils.encryptDexHex(data, customerKeyDto.getSignKey());
            Map<String, Object> params = new HashMap<>();
            params.put("customersCode", customersCode);
            params.put("data", sign);
            String result = HttpUtil.post("http://192.168.1.207:8000//api/addValueOrder/add-add-value-order", params);
            return ResultUtils.getSuccess(result);
        }catch (Exception e) {
            return ResultUtils.getFail(e.getMessage());
        }

    }

    @Autowired
    private OrderDeliverService orderDeliverService;

    @ApiOperation("上传查询文件")
    @RequestMapping(value = "uploadData")
    @PreAuthorize("@el.check('errTools:uploadData')")
    @Log("上传查询文件")
    public ResponseEntity<Object> uploadSku(@RequestParam("file") MultipartFile file, @RequestParam("name") String name, HttpServletRequest httpServletRequest) throws Exception {

        if(StringUtils.equals("1", name)) {
            List<Map<String, Object>> list = new ArrayList<>();
            InputStream ins = file.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(ins);
            BufferedReader bufRead = new BufferedReader(inputStreamReader);
            String line = "";
            while (StringUtil.isNotEmpty(line = bufRead.readLine())) {
                Map<String, Object> order = new LinkedHashMap<>();
                order.put("订单号", line);
                try {
                    CrossBorderOrder order1 = crossBorderOrderService.queryByOrderNo(line);
                    Integer status = douyinService.getStatus(order1);
                    switch (status) {
                        case 2:
                            order.put("状态", "待发货");
                            break;
                        case 16:
                            order.put("状态", "退款中");
                            break;
                        case 17:
                            order.put("状态", "已退款-商家同意");
                            break;
                        case 21:
                            order.put("状态", "已退款");
                            break;
                        case 25:
                            order.put("状态", "取消退款，可发货");
                            break;
                        case 4:
                            order.put("状态", "退款完成");
                            break;
                        case 5:
                            order.put("状态", "5");
                            break;
                        default:
                            order.put("状态", "未知状态：" + status);
                            break;
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    order.put("状态", "系统异常");
                }
                list.add(order);
            }
            redisUtils.set("data1", list);
        }else if (StringUtils.equals("2", name)){
            List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
            for (Map<String, Object> map : maps) {

                String bw = String.valueOf(map.get("报文"));
                JSONObject jsonObject = JSONUtil.parseObj(bw);
                String resp = HttpRequest.get("http://erp.fl56.net:8000/api/douyin/crossborder/msg/push?app_key=6910382317422233088&timestamp=2022-01-29 16: 41: 56&sign=7cd2ca95cbc231b85f9a8255c15536f5")
                        .body(jsonObject.toStringPretty()).execute().body();
                System.out.println(resp);
            }
        }else if (StringUtils.equals("3", name)){
            List<Map<String, Object>> maps = FileUtils.importMapExcel(file);
            for (Map<String, Object> map : maps) {
                try {
                    String mailNo = String.valueOf(map.get("运单号"));
                    SortingLineChuteCode chuteCode = orderDeliverService.deliverDwV2("1", mailNo, null);
                    System.out.println(chuteCode.getChuteCode());
                }catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }else {
            throw new BadRequestException("异常");
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    public void download(HttpServletResponse response, HttpSession session) throws IOException {
        List<Map<String, Object>> list = (List<Map<String, Object>>) redisUtils.get("data1");
        redisUtils.del("data1");
        FileUtil.downloadExcel(list, response);
    }

    @ApiOperation("导出数据")
    @RequestMapping(value = "/download-json")
    public void downloadJson(@RequestBody(required = false) JSONArray array) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            String province = array.getJSONObject(i).getStr("Name");
            JSONArray Cities = array.getJSONObject(i).getJSONArray("Cities");
            for (int j = 0; j < Cities.size(); j++) {
                String city = Cities.getJSONObject(j).getStr("Name");
                JSONArray Districts = Cities.getJSONObject(j).getJSONArray("Districts");
                for (int h = 0; h < Districts.size(); h++) {
                    String district = Districts.getJSONObject(h).getStr("Name");
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("省", province);
                    map.put("市", city);
                    map.put("区", district);
                    list.add(map);
                }
            }
        }
        redisUtils.set("data2", list);
    }

    @Log("抖音重推运单")
    @ApiOperation("抖音重推运单")
    @GetMapping(value = "/re-push-dy-mail-no")
    @PreAuthorize("@el.check('errTools:rePushDyMailNo')")
    public ResponseEntity<Object> rePushDyMailNo(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                DouyinMailMark order = douyinMailMarkService.queryByOrderNo(split[i]);
                if (order==null)
                    throw new Exception("抖音未下发该订单号的获取运单号指令");
                douyinService.getMailNo(order.getId().toString());
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("抖音重推撤单开始")
    @ApiOperation("抖音重推撤单开始")
    @GetMapping(value = "/re-push-dy-del-clear-start")
    @PreAuthorize("@el.check('errTools:rePushDyDelClearStart')")
    public ResponseEntity<Object> rePushDyDelClearStart(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(split[i]);
                if (order==null)
                    throw new Exception(split[i]+"订单不存在");
                douyinService.confirmDelClearStart(order);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
    @Log("抖音重推撤单开始")
    @ApiOperation("抖音重推撤单开始")
    @GetMapping(value = "/re-push-dy-del-clear-succ")
    @PreAuthorize("@el.check('errTools:rePushDyDelClearSucc')")
    public ResponseEntity<Object> rePushDyDelClearSucc(String orderNos){
        if (StringUtil.isBlank(orderNos))
            throw new BadRequestException("请输入单号");
        String[] split = orderNos.split(" ");
        JSONObject result = new JSONObject();
        String resMsg = "";
        for (int i = 0; i < split.length; i++) {
            try {
                CrossBorderOrder order=crossBorderOrderService.queryByOrderNo(split[i]);
                if (order==null)
                    throw new Exception(split[i]+"订单不存在");
                douyinService.confirmDelClearSuccess(order);
            } catch (Exception e) {
                e.printStackTrace();
                result.put("success", false);
                resMsg = resMsg + split[i] + "报错：" + e.getMessage() + ",";
            }
        }
        result.putOnce("resMsg", resMsg);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("抖音注册")
    @ApiOperation("抖音注册")
    @GetMapping(value = "/dy-register")
    @PreAuthorize("@el.check('errTools:rePushDyDelClearSucc')")
    public ResponseEntity<Object> dyRegister(){
        JSONObject result = new JSONObject();

        try {
            String s = sorterSupport.register();
            result.putOnce("success", true);
            result.putOnce("resMsg", s);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("抖音登录")
    @ApiOperation("抖音登录")
    @GetMapping(value = "/dy-login")
    @PreAuthorize("@el.check('errTools:rePushDyDelClearSucc')")
    public ResponseEntity<Object> dyLogin(){
        JSONObject result = new JSONObject();

        try {
            String s = sorterSupport.login();
            result.putOnce("success", true);
            result.putOnce("resMsg", s);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Log("抖音登出")
    @ApiOperation("抖音登出")
    @GetMapping(value = "/dy-logout")
    @PreAuthorize("@el.check('errTools:rePushDyDelClearSucc')")
    public ResponseEntity<Object> dyLogout(){
        JSONObject result = new JSONObject();

        try {
            String s = sorterSupport.logout();
            result.putOnce("success", true);
            result.putOnce("resMsg", s);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
