package com.grape.grape.service;

import com.mybatisflex.core.service.IService;
import com.grape.grape.entity.CaseVersions;

import java.util.List;

/**
 * 测试用例版本备份表 服务层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
public interface CaseVersionsService extends IService<CaseVersions> {

    List<CaseVersions> listByCaseId(int caseId);
}
