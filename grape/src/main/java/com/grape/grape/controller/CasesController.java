package com.grape.grape.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.Cases;
import com.grape.grape.service.CasesService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 测试用例表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/cases")
public class CasesController {

    @Autowired
    private CasesService casesService;

    /**
     * 添加测试用例表。
     *
     * @param cases 测试用例表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody Cases cases) {
        return casesService.save(cases);
    }

    /**
     * 根据主键删除测试用例表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return casesService.removeById(id);
    }

    /**
     * 根据主键更新测试用例表。
     *
     * @param cases 测试用例表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody Cases cases) {
        return casesService.updateById(cases);
    }

    /**
     * 查询所有测试用例表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<Cases> list() {
        return casesService.list();
    }

    /**
     * 根据测试用例表主键获取详细信息。
     *
     * @param id 测试用例表主键
     * @return 测试用例表详情
     */
    @GetMapping("getInfo/{id}")
    public Cases getInfo(@PathVariable Integer id) {
        return casesService.getById(id);
    }

    /**
     * 分页查询测试用例表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<Cases> page(Page<Cases> page) {
        return casesService.page(page);
    }

}
