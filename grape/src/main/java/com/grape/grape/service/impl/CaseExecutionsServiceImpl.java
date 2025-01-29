package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.CaseExecutions;
import com.grape.grape.mapper.CaseExecutionsMapper;
import com.grape.grape.service.CaseExecutionsService;
import org.springframework.stereotype.Service;

/**
 * 用例执行表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CaseExecutionsServiceImpl extends ServiceImpl<CaseExecutionsMapper, CaseExecutions>  implements CaseExecutionsService{

}
