package com.grape.grape.controller;

import com.grape.grape.component.UserUtils;
import com.grape.grape.entity.User;
import com.grape.grape.model.PageResp;
import com.grape.grape.model.Resp;
import com.grape.grape.service.UserService;
import com.grape.grape.service.biz.CaseBizService;
import com.mybatisflex.core.paginate.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import com.grape.grape.entity.Cases;
import com.grape.grape.entity.TestCaseFolder;
import com.grape.grape.entity.TestCaseStep;
import com.grape.grape.service.CasesService;
import com.grape.grape.service.TestCaseFolderService;
import com.grape.grape.service.TestCaseStepService;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.Data;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 测试用例表 控制层。
 *
 * @author Administrator
 * @since 2025-01-29
 */
@RestController
@RequestMapping("/cases")
public class CasesController {

    private static final Logger log = LoggerFactory.getLogger(CasesController.class);

    @Autowired
    private CasesService casesService;
    @Resource
    CaseBizService caseBizService;
    @Autowired
    private UserService userService;
    @Autowired
    private TestCaseFolderService testCaseFolderService;
    @Autowired
    private TestCaseStepService testCaseStepService;
    /**
     * 测试用例请求体，包含基本信息和步骤列表
     */
    @Data
    public static class CaseRequest {
        private Integer id;
        private String caseNumber;
        private String title;
        private String description;
        private Integer priority;
        private int status;
        private Integer version;
        private Integer environmentId;
        private String module;
        private Integer folderId;
        private String remark;
        private List<TestCaseStep> steps;
        private String createdBy;
        private Long createdAt;
        private Long updatedAt;
        private String updatedBy;
    }

    /**
     * 添加测试用例表。
     *
     * @param caseRequest 测试用例请求体
     * @return {@code true} 添加成功，{@code false} 添加失败
     */
    @PostMapping("save")
    public Resp save(@RequestBody CaseRequest caseRequest) {
        if (caseRequest == null) {
            return Resp.info(400, "请求参数不能为空");
        }
        
        // 构建测试用例对象
        Cases cases = new Cases();
        cases.setCaseNumber(caseRequest.getCaseNumber());
        cases.setTitle(caseRequest.getTitle());
        cases.setDescription(caseRequest.getDescription());
        cases.setPriority(caseRequest.getPriority());
        cases.setStatus(caseRequest.getStatus());
        cases.setVersion(caseRequest.getVersion() != null ? caseRequest.getVersion() : 1);
        cases.setEnvironmentId(caseRequest.getEnvironmentId());
        cases.setExpectedResult("");
        cases.setModule(caseRequest.getModule());
        cases.setFolderId(caseRequest.getFolderId());
        cases.setRemark(caseRequest.getRemark());
        
        // 设置创建和更新时间
        long currentTime = System.currentTimeMillis();
        cases.setCreatedAt(currentTime);
        cases.setUpdatedAt(currentTime);
        
        // 设置创建人和更新人
        String currentUser = UserUtils.getCurrentUsername();
        cases.setCreatedBy(currentUser);
        cases.setUpdatedBy(currentUser);
        
        // 保存测试用例
        boolean saveResult = casesService.save(cases);
        if (saveResult && caseRequest.getSteps() != null && !caseRequest.getSteps().isEmpty()) {
            // 保存测试用例步骤
            testCaseStepService.saveSteps(cases.getId(), caseRequest.getSteps());
        }
        
        return Resp.ok(saveResult);
    }

    /**
     * 根据主键删除测试用例表。
     *
     * @param id 主键
     * @return {@code true} 删除成功，{@code false} 删除失败
     */
    @DeleteMapping("remove/{id}")
    public Resp remove(@PathVariable Integer id) {
        return Resp.ok(casesService.removeById(id));
    }

    /**
     * 根据主键更新测试用例表。
     *
     * @param caseRequest 测试用例请求体
     * @return {@code true} 更新成功，{@code false} 更新失败
     */
    @PutMapping("update")
    public Resp update(@RequestBody CaseRequest caseRequest) {
        if (caseRequest == null || caseRequest.getId() == null) {
            return Resp.info(400, "请求参数不能为空，且必须包含测试用例ID");
        }
        
        // 构建测试用例对象
        Cases cases = new Cases();
        cases.setId(caseRequest.getId());
        cases.setCaseNumber(caseRequest.getCaseNumber());
        cases.setTitle(caseRequest.getTitle());
        cases.setDescription(caseRequest.getDescription());
        cases.setPriority(caseRequest.getPriority());
        cases.setStatus(caseRequest.getStatus());
        cases.setVersion(caseRequest.getVersion() != null ? caseRequest.getVersion() : 1);
        cases.setEnvironmentId(caseRequest.getEnvironmentId());
        cases.setExpectedResult("");
        cases.setModule(caseRequest.getModule());
        cases.setFolderId(caseRequest.getFolderId());
        cases.setRemark(caseRequest.getRemark());
        
        // 设置更新时间
        cases.setUpdatedAt(System.currentTimeMillis());
        cases.setUpdatedBy(UserUtils.getCurrentUsername());
        
        // 更新测试用例
        Resp updateResult = caseBizService.updateCase(cases);
        
        // 更新测试用例步骤
        System.out.println("=== 开始更新测试用例步骤 ===");
        System.out.println("caseId: " + cases.getId());
        System.out.println("steps: " + caseRequest.getSteps());
        System.out.println("updateResult.code: " + updateResult.getCode());
        if (updateResult.getCode() == 0 && caseRequest.getSteps() != null) {
            System.out.println("执行步骤更新");
            boolean stepsUpdated = testCaseStepService.saveSteps(cases.getId(), caseRequest.getSteps());
            System.out.println("步骤更新结果: " + stepsUpdated);
        } else {
            System.out.println("跳过步骤更新，原因: updateResult.code = " + updateResult.getCode() + ", caseRequest.getSteps() = " + caseRequest.getSteps());
        }
        System.out.println("=== 测试用例步骤更新完成 ===");
        
        return updateResult;
    }

    /**
     * 查询所有测试用例表。
     *
     * @return 所有数据
     */
    @GetMapping("list")
    public Resp list() {
        return Resp.ok(casesService.list());
    }

    /**
     * 测试用例步骤响应，只包含前端需要的字段
     */
    @Data
    public static class StepResponse {
        private Integer id;
        private Integer stepNumber;
        private String step;
        private String expectedResult;
    }

    /**
     * 测试用例详情响应，包含基本信息和步骤列表
     */
    @Data
    public static class CaseDetailResponse {
        private Cases cases;
        private List<StepResponse> steps;
    }

    /**
     * 根据测试用例表主键获取详细信息。
     *
     * @param id 测试用例表主键
     * @return 测试用例表详情，包含步骤列表
     */
    @GetMapping("getInfo/{id}")
    public Resp getInfo(@PathVariable Integer id) {
        try {
            Cases cases = casesService.getById(id);
            if (cases != null) {
                List<TestCaseStep> steps = testCaseStepService.getByTestCaseId(id);
                CaseDetailResponse response = new CaseDetailResponse();
                response.setCases(cases);
                
                // 转换步骤列表为只包含前端需要的字段
                List<StepResponse> stepResponses = new ArrayList<>();
                if (steps != null) {
                    for (TestCaseStep step : steps) {
                        StepResponse stepResponse = new StepResponse();
                        stepResponse.setId(step.getId());
                        stepResponse.setStepNumber(step.getStepNumber()); // 设置步骤序号
                        stepResponse.setStep(step.getStep());
                        stepResponse.setExpectedResult(step.getExpectedResult());
                        stepResponses.add(stepResponse);
                    }
                }
                response.setSteps(stepResponses);
                
                return Resp.ok(response);
            } else {
                return Resp.info(404, "测试用例不存在");
            }
        } catch (Exception e) {
            return Resp.info(500, "获取测试用例详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询测试用例表。
     *
     * @param page 分页对象
     * @return 分页对象
     */
    @GetMapping("page")
    public PageResp page(Page<Cases> page) {
        return new PageResp().pageInfoOk(casesService.page(page));
    }

    /**
     * 获取测试用例树状结构
     */
    @GetMapping("tree")
    public Resp getTestCaseTree() {
        try {
            // 这里需要实现测试用例树状结构的构建
            // 暂时返回一个示例结构
            return Resp.ok(buildTestCaseTree());
        } catch (Exception e) {
            return Resp.info(500, "获取测试用例树状结构失败: " + e.getMessage());
        }
    }

    /**
     * 构建测试用例树状结构
     */
    private Object buildTestCaseTree() {
        try {
            // 1. 获取所有文件夹
            List<TestCaseFolder> allFolders = testCaseFolderService.list();
            
            // 2. 获取所有测试用例
            List<Cases> allCases = casesService.list();
            
            // 3. 构建文件夹映射
            Map<Integer, TestCaseFolder> folderMap = new HashMap<>();
            for (TestCaseFolder folder : allFolders) {
                folderMap.put(folder.getId(), folder);
            }
            
            // 4. 按文件夹分组测试用例
            Map<Integer, List<Cases>> casesByFolder = new HashMap<>();
            for (Cases testCase : allCases) {
                int folderId = testCase.getFolderId() != null ? testCase.getFolderId() : 0;
                casesByFolder.computeIfAbsent(folderId, k -> new ArrayList<>()).add(testCase);
            }
            
            // 5. 构建树状结构
            List<Map<String, Object>> treeNodes = new ArrayList<>();
            
            // 6. 构建根文件夹
            for (TestCaseFolder folder : allFolders) {
                if (folder.getParentId() == 0) {
                    Map<String, Object> folderNode = buildFolderNode(folder, allFolders, casesByFolder);
                    treeNodes.add(folderNode);
                }
            }
            
            return treeNodes;
        } catch (Exception e) {
            log.error("构建测试用例树状结构失败", e);
            return new ArrayList<>();
        }
    }
    
    /**
     * 构建文件夹节点
     */
    private Map<String, Object> buildFolderNode(TestCaseFolder folder, List<TestCaseFolder> allFolders, Map<Integer, List<Cases>> casesByFolder) {
        Map<String, Object> node = new HashMap<>();
        node.put("id", folder.getId());
        node.put("name", folder.getName());
        node.put("type", "folder");
        node.put("parentId", folder.getParentId());
        
        // 构建子文件夹
        List<Map<String, Object>> children = new ArrayList<>();
        
        // 添加子文件夹
        for (TestCaseFolder childFolder : allFolders) {
            if (childFolder.getParentId().equals(folder.getId())) {
                Map<String, Object> childNode = buildFolderNode(childFolder, allFolders, casesByFolder);
                children.add(childNode);
            }
        }
        
        // 添加测试用例
        List<Cases> folderCases = casesByFolder.get(folder.getId());
        if (folderCases != null) {
            for (Cases testCase : folderCases) {
                Map<String, Object> caseNode = new HashMap<>();
                caseNode.put("id", testCase.getId());
                caseNode.put("name", testCase.getTitle());
                caseNode.put("type", "case");
                caseNode.put("caseNumber", testCase.getCaseNumber());
                caseNode.put("status", testCase.getStatus());
                caseNode.put("priority", testCase.getPriority());
                children.add(caseNode);
            }
        }
        
        node.put("children", children);
        return node;
    }

    /**
     * 测试用例查询条件实体类
     */
    @Data
    public static class CaseQueryRequest {
        private Integer folderId;
        private String title;
        private String description;
        private Integer priority;
        private List<String> createdByList;
    }

    /**
     * 测试用例回滚请求实体类
     */
    @Data
    public static class CaseRollbackRequest {
        private Integer caseId;
        private Integer versionId;
    }

    /**
     * 根据文件夹ID查询测试用例列表，包括该文件夹及其子文件夹下的所有用例，支持条件筛选
     *
     * @param queryRequest 查询条件
     * @return 测试用例列表
     */
    @PostMapping("listByFolderId")
    public Resp listByFolderId(@RequestBody CaseQueryRequest queryRequest) {
        try {
            Integer folderId = queryRequest.getFolderId();
            if (folderId == null) {
                return Resp.info(500, "文件夹ID不能为空");
            }
            
            // 获取该文件夹及其所有子文件夹的ID
            List<Integer> folderIds = new ArrayList<>();
            folderIds.add(folderId);
            getChildFolderIds(folderId, folderIds);
            
            // 构建查询条件
            QueryWrapper queryWrapper = new QueryWrapper();
            if (folderId == 0) {
                // 根节点，查询所有folderId为null的测试用例
                queryWrapper.and("folder_id IS NULL");
            } else {
                // 非根节点，查询指定文件夹及其子文件夹下的测试用例
                queryWrapper.and("folder_id IN (" + String.join(",", folderIds.stream().map(String::valueOf).toArray(String[]::new)) + ")");
            }
            
            // 添加标题模糊查询
            String title = queryRequest.getTitle();
            if (title != null && !title.isEmpty()) {
                queryWrapper.and("title LIKE '%" + title + "%'");
            }
            
            // 添加描述模糊查询
            String description = queryRequest.getDescription();
            if (description != null && !description.isEmpty()) {
                queryWrapper.and("description LIKE '%" + description + "%'");
            }
            
            // 添加优先级筛选
            Integer priority = queryRequest.getPriority();
            if (priority != null) {
                queryWrapper.and("priority = " + priority);
            }
            
            // 添加创建人多选筛选
            List<String> createdByList = queryRequest.getCreatedByList();
            if (createdByList != null && !createdByList.isEmpty()) {
                queryWrapper.and("created_by IN (" + String.join(",", createdByList.stream().map(c -> "'" + c + "'").toArray(String[]::new)) + ")");
            }
            
            // 排序
            queryWrapper.orderBy("id ASC");
            
            // 查询测试用例列表
            List<Cases> casesList = casesService.list(queryWrapper);
            return Resp.ok(casesList);
        } catch (Exception e) {
            log.error("根据文件夹ID查询测试用例失败", e);
            return Resp.info(500, "根据文件夹ID查询测试用例失败: " + e.getMessage());
        }
    }

    /**
     * 回滚测试用例到指定版本
     *
     * @param rollbackRequest 回滚请求
     * @return 回滚结果
     */
    @PostMapping("rollback")
    public Resp rollbackToVersion(@RequestBody CaseRollbackRequest rollbackRequest) {
        try {
            Integer caseId = rollbackRequest.getCaseId();
            Integer versionId = rollbackRequest.getVersionId();
            
            if (caseId == null || versionId == null) {
                return Resp.info(400, "测试用例ID和版本ID不能为空");
            }
            
            // 执行回滚操作
            Resp rollbackResult = caseBizService.rollbackToVersion(caseId, versionId);
            return rollbackResult;
        } catch (Exception e) {
            log.error("回滚测试用例版本失败", e);
            return Resp.info(500, "回滚测试用例版本失败: " + e.getMessage());
        }
    }
    
    /**
     * 递归获取子文件夹ID
     *
     * @param parentId 父文件夹ID
     * @param folderIds 文件夹ID列表，用于存储结果
     */
    private void getChildFolderIds(Integer parentId, List<Integer> folderIds) {
        try {
            // 查询所有子文件夹
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.and("parent_id = ?", parentId);
            List<TestCaseFolder> childFolders = testCaseFolderService.list(queryWrapper);
            
            // 递归处理子文件夹
            for (TestCaseFolder folder : childFolders) {
                folderIds.add(folder.getId());
                getChildFolderIds(folder.getId(), folderIds);
            }
        } catch (Exception e) {
            log.error("获取子文件夹ID失败", e);
        }
    }

}
