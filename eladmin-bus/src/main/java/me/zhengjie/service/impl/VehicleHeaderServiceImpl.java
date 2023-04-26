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

import me.zhengjie.domain.VehicleDetail;
import me.zhengjie.domain.VehicleHeader;
import me.zhengjie.domain.WmsOutstock;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.service.WmsOutstockService;
import me.zhengjie.support.zhuozhi.ZhuozhiSupport;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.VehicleHeaderRepository;
import me.zhengjie.service.VehicleHeaderService;
import me.zhengjie.service.dto.VehicleHeaderDto;
import me.zhengjie.service.dto.VehicleHeaderQueryCriteria;
import me.zhengjie.service.mapstruct.VehicleHeaderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
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
* @author wangm
* @date 2021-04-01
**/
@Service
@RequiredArgsConstructor
public class VehicleHeaderServiceImpl implements VehicleHeaderService {

    private final VehicleHeaderRepository vehicleHeaderRepository;
    private final VehicleHeaderMapper vehicleHeaderMapper;
    @Autowired
    private WmsOutstockService wmsOutstockService;

    @Autowired
    private ZhuozhiSupport zhuozhiSupport;

    @Override
    public Map<String,Object> queryAll(VehicleHeaderQueryCriteria criteria, Pageable pageable){
        Page<VehicleHeader> page = vehicleHeaderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(vehicleHeaderMapper::toDto));
    }

    @Override
    public List<VehicleHeaderDto> queryAll(VehicleHeaderQueryCriteria criteria){
        return vehicleHeaderMapper.toDto(vehicleHeaderRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public VehicleHeaderDto findById(Long id) {
        VehicleHeader vehicleHeader = vehicleHeaderRepository.findById(id).orElseGet(VehicleHeader::new);
        ValidationUtil.isNull(vehicleHeader.getId(),"VehicleHeader","id",id);
        return vehicleHeaderMapper.toDto(vehicleHeader);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public VehicleHeaderDto create(VehicleHeader resources) {
        //当装车类型为托盘装载时打托数和拆托数都不能为空
        if (StringUtil.equals(resources.getLoadType(),"1")&&((resources.getBuildLpnQty()==null&&resources.getSplitLpnQty()==null)||(resources.getBuildLpnQty()==0&&resources.getSplitLpnQty()==0)))
            throw new BadRequestException("拆托数和打托数至少一个都不能为空或0");
        WmsOutstock wmsOutstock=wmsOutstockService.queryByLoadNo(resources.getLoadNo());
        if (CollectionUtils.isEmpty(resources.getVehicleDetails()))
            throw new BadRequestException("请填写车辆信息");
        for (VehicleDetail vehicleDetail : resources.getVehicleDetails()) {
            vehicleDetail.setVehHeader(resources);
        }
        resources.setLoadDetails(wmsOutstock.getOutOrderSn());
        resources.setStatus("0");
        resources.setTenantCode("zhuozhi");
        resources.setWarehouseId("3302461510");
        VehicleHeaderDto headerDto=vehicleHeaderMapper.toDto(vehicleHeaderRepository.save(resources));
        wmsOutstock.setOutStatus("35");
        wmsOutstockService.update(wmsOutstock);
        return headerDto;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(VehicleHeader resources) {
        VehicleHeader vehicleHeader = vehicleHeaderRepository.findById(resources.getId()).orElseGet(VehicleHeader::new);
        ValidationUtil.isNull( vehicleHeader.getId(),"VehicleHeader","id",resources.getId());
        vehicleHeader.copy(resources);
        vehicleHeaderRepository.save(vehicleHeader);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            vehicleHeaderRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<VehicleHeaderDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (VehicleHeaderDto vehicleHeader : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("租户编码", vehicleHeader.getTenantCode());
            map.put("交接单号", vehicleHeader.getLoadNo());
            map.put("托盘数", vehicleHeader.getLpnQty());
            map.put("打托数", vehicleHeader.getBuildLpnQty());
            map.put("拆托数", vehicleHeader.getSplitLpnQty());
            map.put("交接类型 WL：一般订单 TT：调拨 RTV：退供", vehicleHeader.getLoadType());
            map.put("装车类型", vehicleHeader.getEntruckType());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public List<String> querySoNoLike(String soNo) {
        List<VehicleHeader>list=vehicleHeaderRepository.findByLoadDetailsLike("%"+soNo+"%");
        List<String>resList=new ArrayList<>();
        for (VehicleHeader header : list) {
            resList.add(header.getLoadNo());
        }
        return resList;
    }

    @Override
    public String pushVeh(Long id) {
        VehicleHeader vehicleHeader=vehicleHeaderRepository.findById(id).orElse(null);
        if (vehicleHeader==null)
            return "失败，数据不存在";
        if (!StringUtil.equals("0",vehicleHeader.getStatus())&&!StringUtil.equals("1F",vehicleHeader.getStatus()))
            return "失败，状态不正确";
        WmsOutstock wmsOutstock=wmsOutstockService.queryByLoadNo(vehicleHeader.getLoadNo());
        if (wmsOutstock==null)
            throw new BadRequestException("报错:根据装载单号"+vehicleHeader.getLoadNo()+"未找到出库通知单");
        if (!(StringUtil.equals(wmsOutstock.getOutStatus(),"30")
                ||StringUtil.equals(wmsOutstock.getOutStatus(),"35")
                ||StringUtil.equals(wmsOutstock.getOutStatus(),"40")))
            throw new BadRequestException("出库单"+wmsOutstock.getOutOrderSn()+"状态不是待上传车辆信息、待出库、已出库状态");
        try {
            zhuozhiSupport.tpVehInfoUpload(vehicleHeader);
        }catch (Exception e){
            return "失败:"+e.getMessage();
        }
        vehicleHeader.setStatus("1");
        update(vehicleHeader);
        return "成功";
    }
}
