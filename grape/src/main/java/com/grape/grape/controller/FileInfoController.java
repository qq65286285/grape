package com.grape.grape.controller;

import com.grape.grape.model.PageResp;
import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.FileInfo;
import com.grape.grape.service.FileInfoService;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.List;

/**
 *  控制层。
 *
 * @author Administrator
 * @since 2025-08-31
 */
@RestController
@RequestMapping("/fileInfo")
public class FileInfoController {

    @Autowired
    private FileInfoService fileInfoService;

    /**
     * 添加。
     *
     * @param fileInfo 
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody FileInfo fileInfo) {
        return fileInfoService.save(fileInfo);
    }

    /**
     * 根据主键删除。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable BigInteger id) {
        return fileInfoService.removeById(id);
    }

    /**
     * 根据主键更新。
     *
     * @param fileInfo 
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody FileInfo fileInfo) {
        return fileInfoService.updateById(fileInfo);
    }

    /**
     * 查询所有。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<FileInfo> list() {
        return fileInfoService.list();
    }

    /**
     * 根据主键获取详细信息。
     *
     * @param id 主键
     * @return 详情
     */
    @GetMapping("getInfo/{id}")
    public FileInfo getInfo(@PathVariable BigInteger id) {
        return fileInfoService.getById(id);
    }

    /**
     * 分页查询。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public PageResp page(Page<FileInfo> page) {
        return new PageResp().pageInfoOk(fileInfoService.page(page));
    }

}
