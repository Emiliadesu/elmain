package me.zhengjie.service;

import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.dto.PackCheckDto;
import me.zhengjie.service.dto.StockDto;
import me.zhengjie.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppApiServiceImpl implements AppApiService{

    @Autowired
    private CrossBorderOrderService crossBorderOrderService;

    @Autowired
    private CrossBorderOrderDetailsService crossBorderOrderDetailsService;

    @Autowired
    private PackCheckService packCheckService;

    @Autowired
    private PackCheckDetailsService packCheckDetailsService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public PackCheck packCheckInit(String mailNo) {
        // 生成一个抽检单
        PackCheck packCheck = packCheckService.queryByLogisticsNo(mailNo);
        if (packCheck == null) {
            CrossBorderOrder order = crossBorderOrderService.queryByMailNo(mailNo);
            if (order == null) {
                throw new BadRequestException("未查到订单");
            }
            List<CrossBorderOrderDetails> list = crossBorderOrderDetailsService.queryByOrderId(order.getId());
            order.setItemList(list);

            packCheck = new PackCheck();
            packCheck.setOrderId(order.getId());
            packCheck.setOrderNo(order.getOrderNo());
            packCheck.setLogisticsNo(order.getLogisticsNo());
            PackCheckDto packCheckDto = packCheckService.create(packCheck);
            List<PackCheckDetails> saveDetails = new ArrayList<>();
            for (CrossBorderOrderDetails details : list) {
                PackCheckDetails packCheckDetails = new PackCheckDetails();
                BaseSku baseSku = baseSkuService.queryByGoodsNo(details.getGoodsNo());
                packCheckDetails.setGoodsId(baseSku.getId());
                packCheckDetails.setCheckId(packCheckDto.getId());
                packCheckDetails.setBarCode(baseSku.getBarCode());
                packCheckDetails.setGoodsName(baseSku.getGoodsName());
                packCheckDetails.setExpectQty(Integer.valueOf(details.getQty()));
                packCheckDetails.setCurrentQty(0);
                saveDetails.add(packCheckDetails);
            }
            packCheckDetailsService.creates(saveDetails);
            packCheck.setId(packCheckDto.getId());
            packCheck.setItemList(saveDetails);
        }else {
            List<PackCheckDetails> list = packCheckDetailsService.queryByCheckId(packCheck.getId());
            packCheck.setItemList(list);
        }
        return packCheck;
    }

    @Override
    public PackCheck packCheck(Long checkId, String barCode) {
        PackCheckDetails packCheckDetails = packCheckDetailsService.queryByCheckIdAndBarCode(checkId, barCode);
        if (packCheckDetails == null)
            throw new BadRequestException("此条码不属于该包裹：" + barCode);
        PackCheckDto packCheckDto = packCheckService.findById(checkId);
        if (packCheckDto != null) {
            if (packCheckDetails.getCurrentQty().intValue() < packCheckDetails.getExpectQty().intValue()) {
                packCheckDetails.setCurrentQty(packCheckDetails.getCurrentQty() + 1);
                packCheckDetailsService.update(packCheckDetails);

                PackCheck packCheck = packCheckService.queryByIdWithDetails(packCheckDto.getId());
                return packCheck;
            }else {
                throw new BadRequestException("此商品扫描数量已超过订单数量：" + packCheckDetails.getExpectQty());
            }
        }else {
            throw new BadRequestException("系统异常，请返回重新扫描运单");
        }
    }

    @Override
    public void packCheckSubmit(Long checkId) {
        List<PackCheckDetails> list = packCheckDetailsService.queryByCheckId(checkId);
        for (PackCheckDetails details : list) {
            if (details.getCurrentQty().intValue() != details.getExpectQty().intValue()) {
                throw new BadRequestException("抽检差异完成");
            }
        }
    }

    @Override
    public List<StockDto> queryStock(String barCode) {
        List<StockDto> result = new ArrayList<>();
        List<StockDto> locationStockDtos = baseSkuService.queryDetailStockByLocation(barCode);
        if (CollectionUtils.isNotEmpty(locationStockDtos))
            return locationStockDtos;
        List<BaseSku> baseSkus = baseSkuService.queryListByBarcode(barCode);
        if (CollectionUtils.isEmpty(baseSkus))
            throw new BadRequestException("条码不存在或在erp系统未维护、或库位无库存:" + barCode);
        for (BaseSku baseSku : baseSkus) {
            List<StockDto> stockDtos = baseSkuService.queryDetailStock(baseSku.getGoodsNo());
            result.addAll(stockDtos);
        }
        return result;
    }
}
