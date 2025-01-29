package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.Cases;
import com.grape.grape.mapper.CasesMapper;
import com.grape.grape.service.CasesService;
import org.springframework.stereotype.Service;

/**
 * 测试用例表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CasesServiceImpl extends ServiceImpl<CasesMapper, Cases>  implements CasesService{

}
