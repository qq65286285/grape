package com.grape.grape.service.impl;

import com.grape.grape.model.vo.DeviceInfoVo;
import com.grape.grape.service.MinioService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.mapper.DeviceInfoMapper;
import com.grape.grape.service.DeviceInfoService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 设备信息表 服务层实现。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@Service
public class DeviceInfoServiceImpl extends ServiceImpl<DeviceInfoMapper, DeviceInfo>  implements DeviceInfoService{

    @Resource
    MinioService minioService;

    @Override
    public Page<DeviceInfoVo> pageInfo(Page<DeviceInfo> page){
        Page<DeviceInfo> daoPage = this.page(page);
        Page<DeviceInfoVo> voPage = new Page<>();
        voPage.setRecords(new DeviceInfoVo().toVoList(daoPage.getRecords(), minioService));
        voPage.setPageNumber(daoPage.getPageNumber());
        voPage.setPageSize(daoPage.getPageSize());
        voPage.setTotalPage(daoPage.getTotalPage());
        voPage.setTotalRow(daoPage.getTotalRow());
        voPage.setOptimizeCountQuery(daoPage.needOptimizeCountQuery());
        return voPage;

    }
}
