package com.grape.grape.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.mapper.CaseVersionsMapper;
import com.grape.grape.service.CaseVersionsService;
import org.springframework.stereotype.Service;

/**
 * 测试用例版本备份表 服务层实现。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@Service
public class CaseVersionsServiceImpl extends ServiceImpl<CaseVersionsMapper, CaseVersions>  implements CaseVersionsService{

}
