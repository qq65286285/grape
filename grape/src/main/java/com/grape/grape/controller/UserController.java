package com.grape.grape.controller;

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
import com.grape.grape.entity.User;
import com.grape.grape.service.UserService;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * 核心用户信息表，存储系统用户基础数据 控制层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 保存核心用户信息表，存储系统用户基础数据。
     *
     * @param user 核心用户信息表，存储系统用户基础数据
     * @return {@code true} 保存成功，{@code false} 保存失败
     */
//    @PostMapping("save")
//    public boolean save(@RequestBody User user) {
//        return userService.save(user);
//    }


    /**
     * 保存核心用户信息表，存储系统用户基础数据。
     *
     */
    @PostMapping("register")
    public Resp register(@RequestBody User user) {
        return Resp.ok(userService.register(user));
    }

    /**
     * 根据主键删除核心用户信息表，存储系统用户基础数据。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
//    @DeleteMapping("remove/{id}")
//    public boolean remove(@PathVariable String id) {
//        return userService.removeById(id);
//    }

    /**
     * 根据主键更新核心用户信息表，存储系统用户基础数据。
     *
     * @param user 核心用户信息表，存储系统用户基础数据
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
//    @PutMapping("update")
//    public boolean update(@RequestBody User user) {
//        return userService.updateById(user);
//    }

    /**
     * 查询所有核心用户信息表，存储系统用户基础数据。
     *
     * @return 所有数据
     */
//    @GetMapping("list")
//    public List<User> list() {
//        return userService.list();
//    }

    /**
     * 根据主键获取核心用户信息表，存储系统用户基础数据。
     *
     * @param id 核心用户信息表，存储系统用户基础数据主键
     * @return 核心用户信息表，存储系统用户基础数据详情
     */
//    @GetMapping("getInfo/{id}")
//    public User getInfo(@PathVariable String id) {
//        return userService.getById(id);
//    }

    /**
     * 分页查询核心用户信息表，存储系统用户基础数据。
     *
     * @param page 分页对象
     * @return 分页对象
     */
//    @GetMapping("page")
//    public Page<User> page(Page<User> page) {
//        return userService.page(page);
//    }

}
