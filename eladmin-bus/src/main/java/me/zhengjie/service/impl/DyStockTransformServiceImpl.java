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

import cn.hutool.core.collection.CollectionUtil;
import me.zhengjie.domain.BaseSku;
import me.zhengjie.domain.DyStockTakingDetail;
import me.zhengjie.domain.DyStockTransform;
import me.zhengjie.domain.DyStockTransformDetail;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.BaseSkuService;
import me.zhengjie.service.DouyinService;
import me.zhengjie.service.DyStockTransformDetailService;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.DyStockTransformRepository;
import me.zhengjie.service.DyStockTransformService;
import me.zhengjie.service.dto.DyStockTransformDto;
import me.zhengjie.service.dto.DyStockTransformQueryCriteria;
import me.zhengjie.service.mapstruct.DyStockTransformMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.*;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author 王淼
* @date 2021-09-26
**/
@Service
@RequiredArgsConstructor
public class DyStockTransformServiceImpl implements DyStockTransformService {

    private final DyStockTransformRepository dyStockTransformRepository;
    private final DyStockTransformMapper dyStockTransformMapper;

    @Autowired
    private DyStockTransformDetailService dyStockTransformDetailService;

    @Autowired
    private DouyinService douyinService;

    @Autowired
    private BaseSkuService baseSkuService;

    @Override
    public Map<String,Object> queryAll(DyStockTransformQueryCriteria criteria, Pageable pageable){
        Page<DyStockTransform> page = dyStockTransformRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(dyStockTransformMapper::toDto));
    }

    @Override
    public List<DyStockTransformDto> queryAll(DyStockTransformQueryCriteria criteria){
        return dyStockTransformMapper.toDto(dyStockTransformRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public DyStockTransformDto findById(Long id) {
        DyStockTransform dyStockTransform = dyStockTransformRepository.findById(id).orElseGet(DyStockTransform::new);
        ValidationUtil.isNull(dyStockTransform.getId(),"DyStockTransform","id",id);
        return dyStockTransformMapper.toDto(dyStockTransform);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public DyStockTransformDto create(DyStockTransform resources) {
        resources.setOccurrenceTime(System.currentTimeMillis()/1000);
        resources.setCreateUserId(SecurityUtils.getCurrentUserId());
        resources.setIsSuccess("0");
        resources.setStatus("0");
        resources.setIdempotentNo("TRANS"+DateUtils.format(new Date(),"yyyyMMddHHmmssSSS"));
        
        DyStockTransform dyStockTransform=dyStockTransformRepository.save(resources);
        String warehoursNo=null;
        if (CollectionUtil.isNotEmpty(resources.getItemList())){
            for (DyStockTransformDetail detail : resources.getItemList()) {
                BaseSku baseSku=baseSkuService.queryByGoodsNo(detail.getGoodsNo());
                if (warehoursNo==null){
                    warehoursNo=baseSku.getWarehouseCode();
                }else {
                    if (!StringUtil.equals(warehoursNo,baseSku.getWarehouseCode()))
                        throw new BadRequestException("货号："+detail.getGoodsNo()+"与其他货号的仓编码不统一");
                }
                if (StringUtil.isBlank(detail.getEvidence())){
                    detail.setEvidence("[]");
                }
                if (detail.getRemark()==null)
                    detail.setRemark("");
                detail.setMainId(dyStockTransform.getId());
                dyStockTransformDetailService.create(detail);
            }
        }
        return dyStockTransformMapper.toDto(dyStockTransform);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DyStockTransform resources) {
        DyStockTransform dyStockTransform = dyStockTransformRepository.findById(resources.getId()).orElseGet(DyStockTransform::new);
        ValidationUtil.isNull( dyStockTransform.getId(),"DyStockTransform","id",resources.getId());
        if (CollectionUtil.isNotEmpty(resources.getItemList())){
            for (DyStockTransformDetail detail : resources.getItemList()) {
                if (StringUtil.isBlank(detail.getEvidence())){
                    detail.setEvidence("[]");
                }
                if (detail.getRemark()==null)
                    detail.setRemark("");
                if (detail.getId()!=null)
                    dyStockTransformDetailService.update(detail);
                else {
                    detail.setMainId(dyStockTransform.getId());
                    dyStockTransformDetailService.create(detail);
                }
            }
        }
        dyStockTransform.copy(resources);
        dyStockTransformRepository.save(dyStockTransform);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            dyStockTransformRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<DyStockTransformDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DyStockTransformDto dyStockTransform : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("库存变动类型 1：良转次 2：次转良", dyStockTransform.getTransformType());
            map.put("店铺id", dyStockTransform.getShopId());
            map.put("操作时间", dyStockTransform.getOccurrenceTime());
            map.put("备注", dyStockTransform.getRemark());
            map.put("创建人", dyStockTransform.getCreateUserId());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    /**
     * 推送库存变动通知
     */
    @Override
    public void push() {
        DyStockTransform wrapper=new DyStockTransform();
        wrapper.setIsSuccess("0");
        List<DyStockTransform>list=dyStockTransformRepository.findAll(Example.of(wrapper));
        if (CollectionUtil.isEmpty(list))
            return;
        for (DyStockTransform dyStockTransform : list) {
            List<DyStockTransformDetail>detailList=dyStockTransformDetailService.queryByMainId(dyStockTransform.getId());
            dyStockTransform.setItemList(detailList);
            try {
                douyinService.pushStockTransform(dyStockTransform);
                update(dyStockTransform);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public DyStockTransform queryByIdempotentNo(String idempotentNo) {
        return dyStockTransformRepository.findByIdempotentNo(idempotentNo);
    }

    @Override
    public void pushProcess(Long id) throws Exception {
        DyStockTransform dyStockTransform = queryById(id);
        dyStockTransform.setItemList(dyStockTransformDetailService.queryByMainId(id));
        if (StringUtil.equals(dyStockTransform.getStatus(),"0")){
            //必须是已创建状态才能推送审核
            douyinService.pushStockTransform(dyStockTransform);
            dyStockTransform.setStatus("1");
            update(dyStockTransform);
        }else
            throw new BadRequestException("非创建状态不能推送调整单审核");
    }

    @Override
    public DyStockTransform queryById(Long id) {
        DyStockTransform dyStockTransform = dyStockTransformRepository.findById(id).orElseGet(DyStockTransform::new);
        ValidationUtil.isNull(dyStockTransform.getId(),"DyStockTransform","id",id);
        return dyStockTransform;
    }

    @Override
    public void pushSuccess(Long id) throws Exception {
        DyStockTransform dyStockTransform = queryById(id);
        dyStockTransform.setItemList(dyStockTransformDetailService.queryByMainId(id));
        if (StringUtil.equals(dyStockTransform.getStatus(),"2")){
            //必须是审核通过状态才能推送调整单完成
            douyinService.pushStockTransform(dyStockTransform);
            dyStockTransform.setStatus("5");
            dyStockTransform.setIsSuccess("1");
            dyStockTransform.setSucTime(new Timestamp(System.currentTimeMillis()));
            update(dyStockTransform);
        }else
            throw new BadRequestException("非审核通过状态不能推送调整单完成");
    }

    @Override
    public void pushCancel(Long id) throws Exception{
        DyStockTransform dyStockTransform = queryById(id);
        dyStockTransform.setItemList(dyStockTransformDetailService.queryByMainId(id));
        dyStockTransform.setStatus("8");
        douyinService.pushStockTransform(dyStockTransform);
        dyStockTransform.setIsSuccess("0");
        update(dyStockTransform);
    }

    @Override
    public DyStockTransform queryByOccurrenceTimeAndLotNumAndLoction(String batchNo, String location,String goodsNo) {
        List<DyStockTransformDetail>details = dyStockTransformDetailService.queryByBatchNoAndLocation(batchNo,location,goodsNo);
        if (CollectionUtils.isNotEmpty(details)){
            long mainId = -1;
            for (DyStockTransformDetail detail : details) {
                if (mainId!=-1&&mainId!=detail.getMainId()){
                    break;
                }
                mainId = detail.getMainId();
            }
            DyStockTransform transform = queryById(mainId);
            List<DyStockTransformDetail>itemList = dyStockTransformDetailService.queryByMainId(mainId);
            transform.setItemList(itemList);
            return transform;
        }
        return null;
    }
}
