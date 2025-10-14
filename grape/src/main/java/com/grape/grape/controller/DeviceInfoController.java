package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.DeviceInfo;
import com.grape.grape.service.DeviceInfoService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 设备信息表 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/deviceInfo")
public class DeviceInfoController {

    @Autowired
    private DeviceInfoService deviceInfoService;

    /**
     * 保存设备信息表。
     *
     * @param deviceInfo 设备信息表
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.save(deviceInfo);
    }

    /**
     * 根据主键删除设备信息表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return deviceInfoService.removeById(id);
    }

    /**
     * 根据主键更新设备信息表。
     *
     * @param deviceInfo 设备信息表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody DeviceInfo deviceInfo) {
        return deviceInfoService.updateById(deviceInfo);
    }

    /**
     * 查询所有设备信息表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<DeviceInfo> list() {
        return deviceInfoService.list();
    }

    /**
     * 根据主键获取设备信息表。
     *
     * @param id 设备信息表主键
     * @return 设备信息表详情
     */
    @GetMapping("getInfo/{id}")
    public DeviceInfo getInfo(@PathVariable Integer id) {
        return deviceInfoService.getById(id);
    }

    /**
     * 分页查询设备信息表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @PostMapping("page")
    public PageResp page(@RequestBody  Page<DeviceInfo> page) {
        return new PageResp().pageInfoOk(deviceInfoService.pageInfo(page));
    }

}
