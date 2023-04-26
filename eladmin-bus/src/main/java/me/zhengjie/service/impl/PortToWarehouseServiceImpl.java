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

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUnit;
import me.zhengjie.domain.PortToWarehouse;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.exception.EntityExistException;
import me.zhengjie.utils.*;
import lombok.RequiredArgsConstructor;
import me.zhengjie.repository.PortToWarehouseRepository;
import me.zhengjie.service.PortToWarehouseService;
import me.zhengjie.service.dto.PortToWarehouseDto;
import me.zhengjie.service.dto.PortToWarehouseQueryCriteria;
import me.zhengjie.service.mapstruct.PortToWarehouseMapper;
import org.apache.poi.util.StringUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import cn.hutool.core.date.DateUtil;

import java.util.Date;
import java.io.File;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
* @website https://el-admin.vip
* @description 服务实现
* @author lh
* @date 2021-01-10
**/
@Service
@RequiredArgsConstructor
public class PortToWarehouseServiceImpl implements PortToWarehouseService {

    private final PortToWarehouseRepository portToWarehouseRepository;
    private final PortToWarehouseMapper portToWarehouseMapper;

    @Override
    public Map<String,Object> queryAll(PortToWarehouseQueryCriteria criteria, Pageable pageable){
        Page<PortToWarehouse> page = portToWarehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder),pageable);
        return PageUtil.toPage(page.map(portToWarehouseMapper::toDto));
    }

    @Override
    public List<PortToWarehouseDto> queryAll(PortToWarehouseQueryCriteria criteria){
        return portToWarehouseMapper.toDto(portToWarehouseRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root,criteria,criteriaBuilder)));
    }

    @Override
    @Transactional
    public PortToWarehouseDto findById(Long id) {
        PortToWarehouse portToWarehouse = portToWarehouseRepository.findById(id).orElseGet(PortToWarehouse::new);
        ValidationUtil.isNull(portToWarehouse.getId(),"PortToWarehouse","id",id);
        return portToWarehouseMapper.toDto(portToWarehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PortToWarehouseDto create(PortToWarehouse resources) {
        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        resources.setId(snowflake.nextId());
        if(portToWarehouseRepository.findByQgCode(resources.getQgCode()) != null){
            throw new EntityExistException(PortToWarehouse.class,"已存在QG号",resources.getQgCode());
        }
        //获取系统时间
        Timestamp date = new Timestamp(System.currentTimeMillis());
        resources.setCreateUser(SecurityUtils.getCurrentUsername());
        resources.setCreateTime(date);
        resources.setPortToWarehouseName("富立");
        resources.setCpName("富立");
        resources.setShopType("平台");
        //新增 修改人时间与创建人一样
        resources.setModifyUser(SecurityUtils.getCurrentUsername());
        resources.setModifyTime(date);
        resources.setStatus("00");
        return portToWarehouseMapper.toDto(portToWarehouseRepository.save(resources));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(PortToWarehouse resources) {
        PortToWarehouse portToWarehouse = portToWarehouseRepository.findById(resources.getId()).orElseGet(PortToWarehouse::new);
        ValidationUtil.isNull( portToWarehouse.getId(),"PortToWarehouse","id",resources.getId());
        portToWarehouse = portToWarehouseRepository.findByQgCode(resources.getQgCode());
        if(portToWarehouse != null && !portToWarehouse.getId().equals(portToWarehouse.getId())){
            throw new EntityExistException(PortToWarehouse.class,"qg_code",resources.getQgCode());
        }
        portToWarehouse.copy(resources);
        portToWarehouseRepository.save(portToWarehouse);
    }

    @Override
    public void deleteAll(Long[] ids) {
        for (Long id : ids) {
            portToWarehouseRepository.deleteById(id);
        }
    }

    @Override
    public void download(List<PortToWarehouseDto> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (PortToWarehouseDto portToWarehouse : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("港到仓CP", portToWarehouse.getPortToWarehouseName());
            map.put("仓CP", portToWarehouse.getCpName());
            map.put("QG号", portToWarehouse.getQgCode());
            map.put("商家性质", portToWarehouse.getShopType());
            map.put("店铺名称", portToWarehouse.getShopName());
            map.put("LBX号", portToWarehouse.getLbxCode());
            map.put("总数量", portToWarehouse.getTotalNum());
            map.put("品种数", portToWarehouse.getSkuNum());
            map.put("提单号", portToWarehouse.getBlCode());
            map.put("合同号", portToWarehouse.getContractCode());
            map.put("箱型", portToWarehouse.getContainerType());
            map.put("报关行", portToWarehouse.getCustomsBroker());
            map.put("报关单号", portToWarehouse.getCustomDeclareCode());
            map.put("报检单号", portToWarehouse.getInspectionSingleCode());
            map.put("入境口岸", portToWarehouse.getPortName());
            map.put("运输方式", portToWarehouse.getTransportation());
            map.put("资料路径", portToWarehouse.getDataPath());
            map.put("接单日期", portToWarehouse.getOrderTakingTime());
            map.put("预计到港日期", portToWarehouse.getExpectArrivalTime());
            map.put("实际到港日期", portToWarehouse.getActualArrivalTime());
            map.put("资料提供时间", portToWarehouse.getDataSupplyTime());
            map.put("资料审核时间", portToWarehouse.getDataAuditingTime());
            map.put("报关报检开始时间", portToWarehouse.getApplyStartTime());
            map.put("报关报检结束时间", portToWarehouse.getApplyEndTime());
            map.put("预计送货时间", portToWarehouse.getExpectDeliverTime());
            map.put("到仓时间", portToWarehouse.getArrivalWarehouseTime());
            map.put("入仓卸货完成时间", portToWarehouse.getUnloadingEndTime());
            map.put("理货完成时间", portToWarehouse.getTallyingEndTime());
            map.put("理货报告上传时间", portToWarehouse.getTallyingReportUploadTime());
            map.put("理货报告确认时间", portToWarehouse.getTallyingReportConfirmTime());
            map.put("海关上架时间", portToWarehouse.getCostomGroundingTime());
            map.put("收货时间", portToWarehouse.getWmsGroundingTime());
            map.put("资料时效", portToWarehouse.getDataTimeliness());
            map.put("资料时效是否超时", portToWarehouse.getDataTimelinessStatus());
            map.put("资料超时原因", portToWarehouse.getDataOvertimeReason());
            map.put("清关时效", portToWarehouse.getCustomDeclareTimeliness());
            map.put("清关时效是否超时", portToWarehouse.getCustomDeclareTimelinessStatus());
            map.put("清关超时原因", portToWarehouse.getCustomDeclareOvertimeReason());
            map.put("理货时效", portToWarehouse.getTallyingTimeliness());
            map.put("理货时效是否超时", portToWarehouse.getTallyingTimelinessStatus());
            map.put("理货超时原因", portToWarehouse.getTallyingOvertimeReason());
            map.put("理货确认时效", portToWarehouse.getTallyingConfirmTimeliness());
            map.put("理货确认时效是否超时", portToWarehouse.getTallyingConfirmTimelinessStatus());
            map.put("理货确认时效超时原因", portToWarehouse.getTallyingConfirmOvertimeReason());
            map.put("上架时效", portToWarehouse.getGroundingTimeliness());
            map.put("上架时效是否超时", portToWarehouse.getGroundingTimelinessStatus());
            map.put("上架超时原因", portToWarehouse.getGroundingOvertimeReason());
            map.put("备注", portToWarehouse.getRemark());
            map.put("当前状态", portToWarehouse.getStatus());
            map.put("创建时间", portToWarehouse.getCreateTime());
            map.put("创建人", portToWarehouse.getCreateUser());
            map.put("修改时间", portToWarehouse.getModifyTime());
            map.put("修改人", portToWarehouse.getModifyUser());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }

    @Override
    public void uploadfile(Long id, MultipartFile file) {
        PortToWarehouseDto ptw = findById(id);
        if (ptw!=null){
            long date = System.currentTimeMillis();
            String path="D:\\t\\"+date+file.getOriginalFilename();
            try {
                file.transferTo(new File(path));
            } catch (IOException e) {
                e.printStackTrace();
                throw new BadRequestException(e.getMessage());
            }
            PortToWarehouse port = portToWarehouseMapper.toEntity(ptw);
            port.setDataPath(path);
            portToWarehouseRepository.save(port);
        }else{
            throw new EntityExistException(PortToWarehouseDto.class,"ID不存在","500");
        }
    }

    @Override
    public void dewnload(Long id, HttpServletRequest request,HttpServletResponse response) {
        PortToWarehouseDto dto = findById(id);
        if (dto!=null){
            if (dto.getDataPath()!=null)
                try {
                    FileUtil.downloadFile(request,response,new File(dto.getDataPath()),false);
                }catch (Exception e){
                    throw new BadRequestException(e.getMessage());
                }
        }
    }

    @Override
    public void updateparam(Long id, String status, Timestamp time, String statusname) {
        String type=null;
        PortToWarehouseDto dto = findById(id);
        if (dto==null)
            throw new EntityExistException(PortToWarehouseDto.class,"id",id.toString());
        if (status.equals("00")&&statusname.equals("到港时间")){
            dto.setActualArrivalTime(time);
            type="01";
        }if(!status.equals("00")&&statusname.equals("到港时间")){
            dto.setActualArrivalTime(time);
        }else if(status.equals("01")||statusname.equals("提供资料时间")){
            dto.setDataSupplyTime(time);
            type="02";
        }else if(status.equals("02")||statusname.equals("资料审核时间")){
            type="03";
            dto.setDataAuditingTime(time);
            Integer timeLine = getDataTimeliness(dto.getDataSupplyTime(), time);
            dto.setDataTimeliness(timeLine);
            dto.setDataTimelinessStatus(timeLine < 0 ? "1" : "0");
        }else if(status.equals("03")||statusname.equals("报关报检开始时间")){
            type ="04";
            dto.setApplyStartTime(time);
        }else if(status.equals("04")||statusname.equals("报关报检结束时间")) {
            type = "05";
            dto.setApplyEndTime(time);
            Integer timeLine = getApplayTimeliness(dto.getApplyStartTime(), dto.getApplyEndTime(), dto.getTransportation());
            dto.setCustomDeclareTimeliness(timeLine);
            dto.setCustomDeclareTimelinessStatus(timeLine < 0 ? "1" : "0");
        }else if(status.equals("05")){
            if (statusname.equals("预计送货时间")) {
                dto.setExpectDeliverTime(time);
            }else {
                type="06";
                dto.setArrivalWarehouseTime(time);
            }
        }else if (status.equals("06")) {
            type = "07";
            dto.setUnloadingEndTime(time);
            dto.setTallyingEndTime(time);
            Integer timeLine = getTallyingOrGroundingTimeliness(dto.getArrivalWarehouseTime(), dto.getTallyingEndTime());
            dto.setTallyingTimeliness(timeLine);
            dto.setTallyingTimelinessStatus(timeLine < 0 ? "1" : "0");
        }else if (status.equals("07")) {
            if (statusname.equals("理货报告上传时间")) {
                dto.setTallyingReportUploadTime(time);
            }else {
                type ="08";
                dto.setTallyingReportConfirmTime(time);
            }
        }else if (status.equals("08")) {
            type ="09";
            dto.setCostomGroundingTime(time);
        }else if (status.equals("09")) {
            status = "10";
            dto.setWmsGroundingTime(time);
            Integer timeLine = getTallyingOrGroundingTimeliness(dto.getTallyingReportConfirmTime(), dto.getWmsGroundingTime());
            dto.setGroundingTimeliness(timeLine);
            dto.setTallyingTimelinessStatus(timeLine < 0 ? "1" : "0");
        }
        if (type==null){
            type=status;
            //throw new EntityExistException(PortToWarehouseDto.class,"status状态异常",status);
        }
        dto.setStatus(type);
        portToWarehouseRepository.save(portToWarehouseMapper.toEntity(dto));
    }

    @Override
    public void updateReason(Long id, String name, String text) {
        PortToWarehouseDto ptw = findById(id);
        switch (name) {
            case "资料超时原因":
                ptw.setDataOvertimeReason(text);
                break;
            case "清关超时原因":
                ptw.setCustomDeclareOvertimeReason(text);
                break;
            case "理货超时原因":
                ptw.setTallyingOvertimeReason(text);
                break;
            case "上架超时原因":
                ptw.setGroundingOvertimeReason(text);
                break;
        }
        portToWarehouseRepository.save(portToWarehouseMapper.toEntity(ptw));
    }

    /**
     * 资料时效计算
     * @param dataSupplyTime
     * @param dataAuditingTime
     * @return
     */
    private Integer getDataTimeliness(Date dataSupplyTime, Date dataAuditingTime) {
        String current14Str = DateUtil.format(dataSupplyTime, "yyyy-MM-dd") + " 14:00:00";
        //
        Date current14 = DateUtils.parse(current14Str);
        Date start;
        Date end = dataAuditingTime;
        long expire;
        if (DateUtil.between(current14, dataSupplyTime, DateUnit.MS)>0) {
            // 14点之前
            start = DateUtil.endOfDay(dataSupplyTime);
        }else {
            Date next14 = DateUtil.offset(current14, DateField.DAY_OF_MONTH, 1);
            start = next14;
        }
        expire = DateUtil.between(start, end, DateUnit.HOUR);
        Integer result = start.before(end) ? -Math.toIntExact(expire) : Math.toIntExact(expire);
        return result;
    }

    /**
     * 清关时效计算
     * @param applyStartTime
     * @param applyEndTime
     * @param transportation
     * @return
     */
    private Integer getApplayTimeliness(Date applyStartTime, Date applyEndTime, String transportation) {
        int timeline = "空运".equals(transportation) ? 3 : 5;
        Date line = DateUtil.offset(applyStartTime, DateField.DAY_OF_MONTH, timeline);
        Date start = line;
        Date end = applyEndTime;
        long expire = DateUtil.between(start, end, DateUnit.HOUR);
        Integer result = start.before(end) ? -Math.toIntExact(expire) : Math.toIntExact(expire);
        return result;
    }

    /**
     * 理货时效计算
     * @param arrivalWarehouseTime
     * @param tallyingOrWmsGroundingEndTime
     * @return
     */
    private Integer getTallyingOrGroundingTimeliness(Date arrivalWarehouseTime, Date tallyingOrWmsGroundingEndTime) {
        int timeline = 1;
        Date line = DateUtil.offset(arrivalWarehouseTime, DateField.DAY_OF_MONTH, timeline);
        Date start = line;
        Date end = tallyingOrWmsGroundingEndTime;
        long expire = DateUtil.between(start, end, DateUnit.HOUR);
        Integer result = start.before(end) ? -Math.toIntExact(expire) : Math.toIntExact(expire);
        return result;
    }
}
