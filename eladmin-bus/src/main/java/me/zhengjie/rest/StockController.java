package me.zhengjie.rest;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import me.zhengjie.annotation.Log;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.UserCustomerService;
import me.zhengjie.service.dto.BaseSkuQueryCriteria;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Api(tags = "库存管理")
@RequestMapping("/api/stock")
public class StockController {

    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private UserCustomerService userCustomerService;

    @GetMapping
    @Log("查询库存")
    @ApiOperation("查询库存")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> query(BaseSkuQueryCriteria criteria, Pageable pageable){
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        criteria.setGoodsNoNotNull("1");
        return new ResponseEntity<>(baseSkuService.querySumStock(criteria,pageable), HttpStatus.OK);
    }

    @PostMapping("/query-detail-stock")
    @Log("查询库存")
    @ApiOperation("查询库存")
    @PreAuthorize("@el.check('baseSku:list')")
    public ResponseEntity<Object> queryDetailStock(String goodsNo){
        return new ResponseEntity<>(baseSkuService.queryDetailStock(goodsNo), HttpStatus.OK);
    }

    @Log("导出数据")
    @ApiOperation("导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('baseSku:list')")
    public void download(HttpServletResponse response, BaseSkuQueryCriteria criteria) throws IOException {
        Long userId = SecurityUtils.getCurrentUserId();
        List<Long> shopIds = userCustomerService.queryShops(userId);
        if (CollectionUtils.isEmpty(criteria.getShopId()) && CollectionUtils.isNotEmpty(shopIds)) {
            criteria.setShopId(shopIds);
        }
        criteria.setGoodsNoNotNull("1");
        baseSkuService.downloadStock(baseSkuService.queryAllStock(criteria), response);
    }

}
