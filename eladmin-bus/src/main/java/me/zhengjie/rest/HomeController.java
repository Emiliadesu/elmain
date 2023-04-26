package me.zhengjie.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.CrossBorderOrder;
import me.zhengjie.domain.UserCustomer;
import me.zhengjie.service.CrossBorderOrderService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.HomeDto;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Api(tags = "home管理")
@RequestMapping("/api/home")
public class HomeController {

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private UserCustomerService userCustomerService;

    @Log("抖音订单总量情况")
    @ApiOperation("抖音订单总量情况")
    @GetMapping(value = "/order-total-count")
    @PreAuthorize("@el.check('home:orderTotalCount')")
    public ResponseEntity<Object> orderTotalCount(String startTime, String endTime){
        Map<String,Object> homeDto = crossBorderOrderService.orderTotalCount(startTime, endTime);
        return new ResponseEntity<>(homeDto, HttpStatus.OK);
    }

    @Log("抖音店铺订单情况")
    @ApiOperation("抖音店铺订单情况")
    @GetMapping(value = "/shop-order-count")
    @PreAuthorize("@el.check('home:shopOrderCount')")
    public ResponseEntity<Object> shopOrderCount(String startTime, String endTime){
        List<Map<String, Object>> homeDto = crossBorderOrderService.shopOrderCount(startTime, endTime);
        return new ResponseEntity<>(homeDto, HttpStatus.OK);
    }
}
