package me.zhengjie.service.impl;

import cn.hutool.json.JSONArray;
import me.zhengjie.domain.BaseSku;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.M3ScreensService;
import me.zhengjie.service.dto.ActTransactionLogSoAsnInvLotAtt;
import me.zhengjie.service.dto.InvLotLocIdAtt;
import me.zhengjie.service.dto.StockDto;
import me.zhengjie.support.WmsSupport;
import me.zhengjie.utils.CollectionUtils;
import me.zhengjie.utils.DateUtils;
import me.zhengjie.utils.FtpUtil;
import me.zhengjie.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class M3ScreensServiceImpl implements M3ScreensService {
    @Autowired
    private BaseSkuService baseSkuService;

    @Autowired
    private WmsSupport wmsSupport;

    @Override
    public void generatInvrptFile() {
        List<BaseSku>skuList = baseSkuService.queryByCustomerId(238L);//要换成真实的客户id
        if (CollectionUtils.isEmpty(skuList))
            return;
        StringBuilder builder=new StringBuilder();
        Date generatDate=new Date();
        String[]headers=new String[]{"H","LOGISTA","8436034150008","INVRPT"+DateUtils.format(generatDate,"yyyyMMdd"),DateUtils.format(generatDate,"yyyyMMdd"),DateUtils.format(generatDate,"MMdd"),DateUtils.format(generatDate,"000HHmm")};
        builder.append(headers[0]).
                append(";").
                append(headers[1]).
                append(";").
                append(headers[2]).
                append(";").
                append(headers[3]).
                append(";").
                append(headers[4]).
                append(";").
                append(headers[5]).
                append(";").
                append(headers[6]).append("\n");
        int totalLine=0;
        for (BaseSku baseSku : skuList) {
            List<StockDto>stockDtos=baseSkuService.queryDetailStock(baseSku.getGoodsNo());
            if (stockDtos.isEmpty()){
                builder.append("L;").append(headers[3]).append(headers[4]).append(headers[5]).append(headers[6]).append(";")
                        .append(baseSku.getBarCode())//预测是商品条码
                        .append(";")
                        //.append("");//商品编码?
                        .append(";")
                        .append(baseSku.getOuterGoodsNo())
                        .append(";")
                        //.append("")//???
                        .append(";")
                        .append(baseSku.getGoodsName())
                        .append(";")
                        .append(baseSku.getGoodsName())
                        .append(";")
                //.append("")//type？
                ;
                builder.append(";;;0;;\n");
            }else {
                for (StockDto stockDto : stockDtos) {
                    builder.append("L;").append(headers[3]).append(headers[4]).append(headers[5]).append(headers[6]).append(";")
                            .append(baseSku.getBarCode())//预测是商品条码
                            .append(";")
                            //.append("");//商品编码?
                            .append(";")
                            .append(baseSku.getOuterGoodsNo())
                            .append(";")
                            //.append("")//???
                            .append(";")
                            .append(baseSku.getGoodsName())
                            .append(";")
                            .append(baseSku.getGoodsName())
                            .append(";")
                    //.append("")//type？
                    ;
                    builder.append(stockDto.getCustomerBatch())
                            .append(";");
                    if (StringUtil.isNotEmpty(stockDto.getExpireDate())){
                        builder.append(DateUtils.format(DateUtils.parseDate(stockDto.getExpireDate()),"yyyyMMdd"));
                    }

                    builder.append(";")
                            //.append("")//单位
                            .append(";")
                            .append(stockDto.getQty())
                            .append(";")
                            .append(stockDto.getLocationId())
                            .append(";")
                            //.append("")//Item Balance Status
                            .append("\n");
                }
            }
            totalLine++;
        }
        builder.append("E;").append(totalLine).append("99END");
        FtpUtil.upload(new ByteArrayInputStream(builder.toString().getBytes()),headers[3]+".csv","./keyan");
    }

    @Override
    public void generatHandingMovementFile() {
        List<BaseSku>skuList = baseSkuService.queryByCustomerId(238L);//要换成真实的客户id
        if (CollectionUtils.isEmpty(skuList))
            return;
        StringBuilder builder=new StringBuilder();
        Date generatDate=new Date();
        String[]headers=new String[]{"H","FM","EXPANSCIENCE","MVT",DateUtils.format(generatDate,"yyyyMMdd"),DateUtils.format(generatDate,"HHmm"),DateUtils.format(generatDate,"ss")};
        builder.append(headers[0]).
                append(";").
                append(headers[1]).
                append(";").
                append(headers[2]).
                append(";").
                append(headers[3]).append(headers[4]).append(headers[5]).
                append(";").
                append(headers[4]).
                append(";").
                append(headers[5]).
                append(";").
                append(headers[6]).append("\n");
        int totalLine=0;
        List<String>skus=new ArrayList<>();
        for (BaseSku baseSku : skuList) {
            skus.add(baseSku.getGoodsNo());
        }
        List<ActTransactionLogSoAsnInvLotAtt>lotLocIdAttList=wmsSupport.queryTranscationMoveLogByM3Api(skus,DateUtils.formatDate(generatDate)+" 00:00:00",DateUtils.formatDate(generatDate)+" 23:59:59");
        BaseSku baseSku=null;
        for (ActTransactionLogSoAsnInvLotAtt invLotAtt : lotLocIdAttList) {
            if (baseSku==null||!StringUtil.equals(baseSku.getGoodsNo(),invLotAtt.getFmsku())){
                for (BaseSku sku : skuList) {
                    if (StringUtil.equals(sku.getGoodsNo(),invLotAtt.getFmsku())){
                        baseSku=sku;
                        break;
                    }
                }
                if (baseSku==null)
                    throw new BadRequestException("没有sku："+invLotAtt.getFmsku()+"的信息");
            }
            builder.append("O")
                    .append(";")
                    .append(headers[3]).append(headers[4]).append(headers[5]).append(headers[6])
                    .append(";")
                    //.append("")
                    .append(";")
                    .append(headers[4])
                    .append("\n");
            builder.append("L")
                    .append(headers[3]).append(headers[4]).append(headers[5]).append(headers[6])
                    .append(String.format("%08d",totalLine))
                    .append(";")
                    //.append("")
                    .append(";")
                    .append(baseSku.getOuterGoodsNo())
                    .append(";")
                    .append(baseSku.getGoodsName())
                    .append(";")
                    .append(invLotAtt.getLotatt09())
                    .append(";")
                    .append(invLotAtt.getLotatt02())
                    .append(";")
                    .append(invLotAtt.getFmqty())
                    .append(";")
                    .append(baseSku.getUnit())
                    .append(";")
                    .append(invLotAtt.getFmloctId())
                    .append(";")
                    .append(invLotAtt.getToloctId())
                    .append(StringUtil.equals(invLotAtt.getLotatt08(),"良品")?"AN3":"AN35")
                    .append(";")
                    //.append("")
                    .append(";")
                    //.append("")
                    .append(";")
                    //.append("")
                    .append("\n");
            totalLine++;
        }
        builder.append("E")
                .append(";")
                .append(totalLine)
                .append(";")
                .append("99END");
        FtpUtil.upload(new ByteArrayInputStream(builder.toString().getBytes()),headers[3]+headers[4]+headers[5]+".csv","./keyan");
    }
}
