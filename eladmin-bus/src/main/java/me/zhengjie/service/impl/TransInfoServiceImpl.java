/*
*  Copyright 2019-2020 Zheng Jie
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*  http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*/
package me.zhengjie.service.impl;

import cn.hutool.core.date.DatePattern;
import me.zhengjie.domain.*;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.TransDetailsService;
import me.zhengjie.service.TransLogService;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.TransInfoRepository;
import me.zhengjie.service.TransInfoService;
import me.zhengjie.service.dto.TransInfoDto;
import me.zhengjie.service.dto.TransInfoQueryCriteria;
import me.zhengjie.service.mapstruct.TransInfoMapper;
import me.zhengjie.utils.enums.ClearTransStatusEnum;
import me.zhengjie.utils.enums.HeZhuInfoStatusEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author luob
* @date 2021-09-04
**/
@Service
@RequiredArgsConstructor
public class TransInfoServiceImpl implements TransInfoService {

    private final TransInfoRepository transInfoRepository;
    private final TransInfoMapper transInfoMapper;

    @Autowired
    private TransLogService transLogService;

    @Autowired
    private TransDetailsService transDetailsService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Override
    public Map<String,Object> queryAll(TransInfoQueryCriteria criteria, Pageable pageable){
        Page<TransInfo> page = transInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(transInfoMapper::toDto));
    }

    @Override
    public List<TransInfoDto> queryAll(TransInfoQueryCriteria criteria){
        return transInfoMapper.toDto(transInfoRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public TransInfoDto findById(Long id) {
        TransInfo transInfo = transInfoRepository.findById(id).orElseGet(TransInfo::new);
        ValidationUtil.isNull(transInfo.getId(),"TransInfo","id",id);
        return transInfoMapper.toDto(transInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TransInfoDto create(TransInfo resources) {
        return transInfoMapper.toDto(transInfoRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(TransInfo resources) {
        TransInfo transInfo = transInfoRepository.findById(resources.getId()).orElseGet(TransInfo::new);
        ValidationUtil.isNull( transInfo.getId(),"TransInfo","id",resources.getId());
        transInfo.copy(resources);
        transInfoRepository.save(transInfo);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            transInfoRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<TransInfoDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (TransInfoDto transInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("清关ID", transInfo.getClearId());
            map.put("单据编号", transInfo.getOrderNo());
            map.put("清关单号", transInfo.getClearNo());
            map.put("状态", transInfo.getStatus());
            map.put("贸易类型", transInfo.getTradeType());
            map.put("客户ID", transInfo.getCustomersId());
            map.put("店铺ID", transInfo.getShopId());
            map.put("预估SKU数量", transInfo.getSkuNum());
            map.put("预估件数", transInfo.getTotalNum());
            map.put("打包方式", transInfo.getPackWay());
            map.put("打包数量", transInfo.getPackNum());
            map.put("排车方", transInfo.getPlanCarType());
            map.put("是否拼车", transInfo.getShareFlag());
            map.put("创建人", transInfo.getCreateBy());
            map.put("创建时间", transInfo.getCreateTime());
            map.put("更新者", transInfo.getUpdateBy());
            map.put("更新时间", transInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void createTransByClear(ClearInfo clearInfo) {
        TransInfo exist = queryByClearNo(clearInfo.getClearNo());
        if (exist != null)
            throw new BadRequestException("运输单已生成："+ exist.getOrderNo());
        TransInfo transInfo = new TransInfo();
        transInfo.setClearId(clearInfo.getId());
        transInfo.setClearNo(clearInfo.getClearNo());
        transInfo.setOrderNo(genOrderNo());
        transInfo.setStatus(ClearTransStatusEnum.STATUS_200.getCode());
        transInfo.setTradeType(clearInfo.getTradeType());
        transInfo.setCustomersId(clearInfo.getCustomersId());
        transInfo.setShopId(clearInfo.getShopId());
        transInfo.setSkuNum(clearInfo.getSkuNum());
        transInfo.setTotalNum(clearInfo.getTotalNum());
        transInfo.setOrderSource(clearInfo.getOrderSource());
        TransInfoDto transInfoDto = create(transInfo);

        TransLog log = new TransLog(
                transInfoDto.getId(),
                transInfo.getOrderNo(),
                transInfo.getStatus().toString(),
                "",
                ""
        );
        transLogService.create(log);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOptNode(TransLog log) {
        TransInfo transInfo = queryById(log.getOrderId());
        if (transInfo == null)
            throw new BadRequestException("单据不存在");
        if (StringUtils.equals(transInfo.getStatus().toString(), ClearTransStatusEnum.STATUS_230.getCode().toString()))
            throw new BadRequestException("服务完成不能再上传状态");
        if (StringUtils.equals(log.getOptNode(), ClearTransStatusEnum.STATUS_200.getCode().toString()))
            throw new BadRequestException("不用上传待排车状态");
        if (StringUtils.equals(log.getOptNode(), ClearTransStatusEnum.STATUS_215.getCode().toString())
                && transInfo.getDetails() == null)
            throw new BadRequestException("上传已排车节点请先维护车辆信息");
        if (StringUtils.equals(log.getOptNode(), ClearTransStatusEnum.STATUS_230.getCode().toString())
                && transInfo.getDetails() == null)
            throw new BadRequestException("上传服务完成节点请先维护车辆信息");
        transInfo.setStatus(Integer.valueOf(log.getOptNode()));
        update(transInfo);
        transLogService.create(log);
        if (StringUtils.equals(transInfo.getOrderSource(), "1")) {
            // 卓志单据
            zhuozhiSupport.noticeTransStatus(transInfo);
        }
    }

    @Override
    public TransInfo queryById(Long id) {
        TransInfo transInfo = transInfoRepository.findById(id).orElseGet(TransInfo::new);
        List<TransDetails> details = transDetailsService.queryByOrderId(id);
        transInfo.setDetails(details);
        return transInfo;
    }

    @Override
    public TransInfo queryByClearNo(String clearNo) {
        return transInfoRepository.findByClearNo(clearNo);
    }

    @Override
    public TransInfo queryByOrderNo(String orderNo) {
        return transInfoRepository.findByOrderNo(orderNo);
    }

    private synchronized String genOrderNo() {
        int tryCount = 0;
        String qz = "YS";
        String time = DateUtils.format(new Date(), DatePattern.PURE_DATE_FORMAT);
        String result = qz + time;
        Random random = new Random();
        for (int i=0;i<4; i++) {
            result += random.nextInt(10);
        }
        TransInfo order = queryByOrderNo(result);
        if (order != null && tryCount < 4) {
            genOrderNo();
            tryCount++;
        }
        return result;
    }


}