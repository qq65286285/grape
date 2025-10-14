package com.grape.grape.service;

import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.User;

/**
 * 核心用户信息表，存储系统用户基础数据 服务层。
 *
 * @author wb-ginshi
 * @since 2025-10-13
 */
public interface UserService extends MyBaseService<User> {

    User register(User user);

    User findByUsername(String username);
}
