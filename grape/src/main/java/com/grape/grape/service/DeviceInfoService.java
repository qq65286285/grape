package com.grape.grape.service;

import com.grape.grape.model.vo.DeviceInfoVo;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.DeviceInfo;

/**
 * 设备信息表 服务层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
public interface DeviceInfoService extends MyBaseService<DeviceInfo> {

    Page<DeviceInfoVo> pageInfo(Page<DeviceInfo> page);
}
