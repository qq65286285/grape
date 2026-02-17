package com.grape.grape.service.biz;


import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.CaseVersions;
import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.entity.User;
import com.grape.grape.model.Resp;
import com.grape.grape.model.dict.ResultEnumI18n;
import com.grape.grape.service.CaseVersionsService;
import com.grape.grape.service.CasesService;
import com.grape.grape.service.TestCaseStepService;
import com.grape.grape.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class CaseBizServiceImpl implements CaseBizService {

    private static final Logger log = LoggerFactory.getLogger(CaseBizServiceImpl.class);

    @Resource
    CasesService casesService;
    @Resource
    CaseVersionsService caseVersionsService;
    @Resource
    TestCaseStepService testCaseStepService;
    @Autowired
    private UserService userService;

    @Override
    public Resp updateCase(Cases cases){
        //当前版本做记录
        Cases caseOri = casesService.getById(cases.getId());
        
        // 获取当前时间戳和当前登录用户ID
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : "system";
        
        if(null == caseOri){
            // 测试用例不存在，创建新的测试用例
            log.info("测试用例不存在，创建新的测试用例，ID: {}", cases.getId());
            
            // 设置创建时间和创建人
            cases.setCreatedAt(currentTime);
            cases.setCreatedBy(userId);
            cases.setUpdatedAt(currentTime);
            cases.setUpdatedBy(userId);
            
            // 保存测试用例
            casesService.save(cases);
            
            return Resp.info(ResultEnumI18n.SUCCESS);
        }

        // 获取测试用例的步骤信息
        String stepsJson = null;
        try {
            List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(cases.getId());
            if (steps != null && !steps.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                stepsJson = objectMapper.writeValueAsString(steps);
            }
        } catch (Exception e) {
            log.error("转换步骤信息为JSON失败: {}", e.getMessage());
        }
        
        // 创建版本记录
        CaseVersions caseVersions = new CaseVersions().getByCaseDao(caseOri, stepsJson);
        
        // 设置版本记录的创建人和更新人
        caseVersions.setCreatedBy(userId);
        caseVersions.setUpdatedBy(userId);
        
        // 设置版本记录的创建时间和更新时间
        caseVersions.setCreatedAt(currentTime);
        caseVersions.setUpdatedAt(currentTime);
        
        //入库用例版本，历史记录
        caseVersionsService.save(caseVersions);
        
        //更新用例
        cases.setVersion(caseOri.getVersion() + 1);
        
        // 设置更新时间和更新人
        cases.setUpdatedAt(currentTime);
        cases.setUpdatedBy(userId);
        
        // 记录日志
        if (currentUserId != null) {
            log.info("设置测试用例更新人: {}，版本号: {}", currentUserId, cases.getVersion());
        } else {
            log.warn("无法获取当前登录用户，使用默认值 'system' 作为更新人");
        }
        
        casesService.updateById(cases);
        return Resp.info(ResultEnumI18n.SUCCESS);
    }

    @Override
    public Resp rollbackToVersion(Integer caseId, Integer versionId) {
        // 获取当前时间戳和当前登录用户ID
        long currentTime = System.currentTimeMillis();
        String currentUserId = UserUtils.getCurrentLoginUserId(userService);
        String userId = (currentUserId != null) ? currentUserId : "system";
        
        // 获取版本信息
        CaseVersions caseVersions = caseVersionsService.getById(versionId);
        if (caseVersions == null) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 验证版本是否属于指定的测试用例
        if (!caseVersions.getTestCaseId().equals(caseId)) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 获取当前测试用例
        Cases currentCase = casesService.getById(caseId);
        if (currentCase == null) {
            return Resp.info(ResultEnumI18n.NOT_FOUND);
        }
        
        // 保存当前版本作为历史记录
        String stepsJson = null;
        try {
            List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(caseId);
            if (steps != null && !steps.isEmpty()) {
                ObjectMapper objectMapper = new ObjectMapper();
                stepsJson = objectMapper.writeValueAsString(steps);
            }
        } catch (Exception e) {
            log.error("转换步骤信息为JSON失败: {}", e.getMessage());
        }
        
        // 创建当前版本的历史记录
        CaseVersions currentVersionRecord = new CaseVersions().getByCaseDao(currentCase, stepsJson);
        currentVersionRecord.setCreatedBy(userId);
        currentVersionRecord.setUpdatedBy(userId);
        currentVersionRecord.setCreatedAt(currentTime);
        currentVersionRecord.setUpdatedAt(currentTime);
        caseVersionsService.save(currentVersionRecord);
        
        // 回滚测试用例信息
        Cases rolledBackCase = new Cases();
        rolledBackCase.setId(caseId);
        rolledBackCase.setCaseNumber(caseVersions.getCaseNumber());
        rolledBackCase.setTitle(caseVersions.getTitle());
        rolledBackCase.setDescription(caseVersions.getDescription());
        rolledBackCase.setPriority(caseVersions.getPriority());
        rolledBackCase.setStatus(caseVersions.getCaseState());
        rolledBackCase.setVersion(currentCase.getVersion() + 1);
        rolledBackCase.setEnvironmentId(caseVersions.getEnvironmentId());
        rolledBackCase.setExpectedResult(caseVersions.getExpectedResult());
        rolledBackCase.setModule(caseVersions.getModule());
        rolledBackCase.setFolderId(caseVersions.getFolderId());
        rolledBackCase.setRemark(caseVersions.getRemark());
        rolledBackCase.setUpdatedAt(currentTime);
        rolledBackCase.setUpdatedBy(userId);
        
        // 更新测试用例
        casesService.updateById(rolledBackCase);
        
        // 回滚测试用例步骤
        log.info("开始回滚测试用例步骤，caseId: {}, stepsJson: {}", caseId, caseVersions.getStepsJson());
        if (caseVersions.getStepsJson() != null) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                log.info("解析步骤JSON: {}", caseVersions.getStepsJson());
                List<TestCaseStep> rolledBackSteps = objectMapper.readValue(caseVersions.getStepsJson(), new TypeReference<List<TestCaseStep>>() {});
                log.info("解析得到步骤列表: {}", rolledBackSteps);
                boolean saveResult = testCaseStepService.saveSteps(caseId, rolledBackSteps);
                log.info("步骤回滚结果: {}", saveResult);
            } catch (Exception e) {
                log.error("回滚步骤信息失败: {}", e.getMessage());
                e.printStackTrace();
            }
        } else {
            // 如果版本中没有步骤信息，删除当前步骤
            log.info("版本中没有步骤信息，删除当前步骤");
            testCaseStepService.removeByTestCaseId(caseId);
        }
        log.info("步骤回滚完成");
        
        return Resp.info(ResultEnumI18n.SUCCESS);
    }
}
