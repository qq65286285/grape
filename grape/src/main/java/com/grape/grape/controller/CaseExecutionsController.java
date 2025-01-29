package com.grape.grape.controller;

import com.mybatisflex.core.paginate.Page;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.CaseExecutions;
import com.grape.grape.service.CaseExecutionsService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用例执行表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/caseExecutions")
public class CaseExecutionsController {

    @Autowired
    private CaseExecutionsService caseExecutionsService;

    /**
     * 添加用例执行表。
     *
     * @param caseExecutions 用例执行表
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public boolean save(@RequestBody CaseExecutions caseExecutions) {
        return caseExecutionsService.save(caseExecutions);
    }

    /**
     * 根据主键删除用例执行表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public boolean remove(@PathVariable Integer id) {
        return caseExecutionsService.removeById(id);
    }

    /**
     * 根据主键更新用例执行表。
     *
     * @param caseExecutions 用例执行表
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public boolean update(@RequestBody CaseExecutions caseExecutions) {
        return caseExecutionsService.updateById(caseExecutions);
    }

    /**
     * 查询所有用例执行表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public List<CaseExecutions> list() {
        return caseExecutionsService.list();
    }

    /**
     * 根据用例执行表主键获取详细信息。
     *
     * @param id 用例执行表主键
     * @return 用例执行表详情
     */
    @GetMapping("getInfo/{id}")
    public CaseExecutions getInfo(@PathVariable Integer id) {
        return caseExecutionsService.getById(id);
    }

    /**
     * 分页查询用例执行表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public Page<CaseExecutions> page(Page<CaseExecutions> page) {
        return caseExecutionsService.page(page);
    }

}
