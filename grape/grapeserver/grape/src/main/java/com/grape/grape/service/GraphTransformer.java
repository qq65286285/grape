package com.grape.grape.service;

import com.grape.grape.entity.ScenarioContent;
import com.grape.grape.entity.ScenarioTypeContent;
import com.grape.grape.entity.TestScenario;
import com.grape.grape.enums.ScenarioTypeEnum;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Values;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.neo4j.driver.Values.parameters;

/**
 * 测试场景图结构转换器
 * 专注于测试场景数据与图形数据库的交互
 */
public class GraphTransformer implements AutoCloseable {

    private final Driver driver;

    /**
     * 构造函数，初始化Neo4j连接
     * @param uri Neo4j连接URI
     * @param user 用户名
     * @param password 密码
     */
    public GraphTransformer(String uri, String user, String password) {
        this.driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
    }

    /**
     * 将测试场景保存到Neo4j
     * @param scenario 测试场景对象
     */
    public void saveTestScenario(TestScenario scenario) {
        try (Session session = driver.session()) {
            // 创建场景节点
            session.run(
                    "MERGE (s:TestScenario {name: $name})",
                    parameters("name", scenario.name)
            );

            // 处理场景类型及其对应内容
            for (ScenarioTypeContent typeContent : scenario.typeContents) {
                String type = typeContent.type.getValue();
                List<ScenarioContent> contents = typeContent.contents;
                
                // 创建类型节点
                session.run(
                        "MERGE (t:ScenarioType {name: $type})",
                        parameters("type", type)
                );
                // 建立场景与类型的关系
                session.run(
                        "MATCH (s:TestScenario {name: $scenarioName}), (t:ScenarioType {name: $typeName}) MERGE (s)-[r:HAS_TYPE]->(t)",
                        parameters("scenarioName", scenario.name, "typeName", type)
                );

                // 处理该类型对应的场景内容
                saveScenarioContents(session, scenario.name, type, contents, null);
            }
        }
    }

    /**
     * 递归保存场景内容到Neo4j
     * @param session Neo4j会话
     * @param scenarioName 场景名称
     * @param scenarioType 场景类型
     * @param contents 场景内容列表
     * @param parentContentId 父内容ID
     */
    private void saveScenarioContents(Session session, String scenarioName, String scenarioType, List<ScenarioContent> contents, Long parentContentId) {
        for (ScenarioContent content : contents) {
            // 创建内容节点
            Result result = session.run(
                    "CREATE (c:ScenarioContent {title: $title, description: $description}) RETURN id(c) as id",
                    parameters("title", content.title, "description", content.description)
            );

            Long contentId = null;
            if (result.hasNext()) {
                // 直接从结果中获取值，避免使用 Record 类型
                contentId = result.next().get("id").asLong();
            }

            if (contentId != null) {
                if (parentContentId == null) {
                    // 内容直接属于场景
                    session.run(
                            "MATCH (s:TestScenario {name: $scenarioName}), (c:ScenarioContent) WHERE id(c) = $contentId MERGE (s)-[r:HAS_CONTENT]->(c)",
                            parameters("scenarioName", scenarioName, "contentId", contentId)
                    );
                    // 建立内容与场景类型的关系
                    session.run(
                            "MATCH (c:ScenarioContent) WHERE id(c) = $contentId MATCH (t:ScenarioType {name: $typeName}) MERGE (c)-[r:BELONGS_TO_TYPE]->(t)",
                            parameters("contentId", contentId, "typeName", scenarioType)
                    );
                } else {
                    // 内容属于另一个内容（子内容）
                    session.run(
                            "MATCH (p:ScenarioContent), (c:ScenarioContent) WHERE id(p) = $parentId AND id(c) = $contentId MERGE (p)-[r:HAS_SUB_CONTENT]->(c)",
                            parameters("parentId", parentContentId, "contentId", contentId)
                    );
                }

                // 递归保存子内容
                if (content.subContents != null && !content.subContents.isEmpty()) {
                    saveScenarioContents(session, scenarioName, scenarioType, content.subContents, contentId);
                }
            }
        }
    }

    /**
     * 批量保存测试场景到Neo4j
     * @param scenarios 测试场景列表
     */
    public void saveTestScenarios(List<TestScenario> scenarios) {
        try (Session session = driver.session()) {
            for (TestScenario scenario : scenarios) {
                saveTestScenario(scenario);
            }
        }
    }

    /**
     * 根据场景类型查询测试场景
     * @param type 场景类型
     * @return 符合条件的测试场景列表
     */
    public List<TestScenario> getScenariosByType(String type) {
        List<TestScenario> scenarios = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (s:TestScenario)-[:HAS_TYPE]->(t:ScenarioType {name: $type}) RETURN s.name as name",
                    parameters("type", type)
            );

            while (result.hasNext()) {
                // 直接从结果中获取值，避免使用 Record 类型
                String name = result.next().get("name").asString();
                TestScenario scenario = getScenarioByName(name);
                if (scenario != null) {
                    scenarios.add(scenario);
                }
            }
        }
        return scenarios;
    }

    /**
     * 查询所有测试场景
     * @return 所有测试场景列表
     */
    public List<TestScenario> getAllScenarios() {
        List<TestScenario> scenarios = new ArrayList<>();
        try (Session session = driver.session()) {
            Result result = session.run(
                    "MATCH (s:TestScenario) RETURN s.name as name"
            );

            while (result.hasNext()) {
                // 直接从结果中获取值，避免使用 Record 类型
                String name = result.next().get("name").asString();
                TestScenario scenario = getScenarioByName(name);
                if (scenario != null) {
                    scenarios.add(scenario);
                }
            }
        }
        return scenarios;
    }

    /**
     * 根据场景名称查询测试场景
     * @param name 场景名称
     * @return 符合条件的测试场景
     */
    public TestScenario getScenarioByName(String name) {
        TestScenario scenario = null;
        try (Session session = driver.session()) {
            // 获取场景类型
            List<String> types = new ArrayList<>();
            Result typeResult = session.run(
                    "MATCH (s:TestScenario {name: $name})-[:HAS_TYPE]->(t:ScenarioType) RETURN t.name as type",
                    parameters("name", name)
            );
            while (typeResult.hasNext()) {
                // 直接从结果中获取值，避免使用 Record 类型
                types.add(typeResult.next().get("type").asString());
            }

            // 构建场景类型及其对应内容的列表
            List<ScenarioTypeContent> typeContents = new ArrayList<>();
            for (String type : types) {
                // 获取该类型对应的场景内容
                List<ScenarioContent> contents = new ArrayList<>();
                Result contentResult = session.run(
                        "MATCH (s:TestScenario {name: $name})-[:HAS_CONTENT]->(c:ScenarioContent)-[:BELONGS_TO_TYPE]->(t:ScenarioType {name: $type}) RETURN id(c) as id, c.title as title, c.description as description",
                        parameters("name", name, "type", type)
                );
                while (contentResult.hasNext()) {
                    // 直接从结果中获取值，避免使用 Record 类型
                    var record = contentResult.next();
                    Long contentId = record.get("id").asLong();
                    String title = record.get("title").asString();
                    String description = record.get("description").asString();
                    
                    ScenarioContent content = new ScenarioContent(title, description);
                    // 递归获取子内容
                    loadSubContents(session, content, contentId);
                    contents.add(content);
                } ScenarioTypeEnum typeEnum = ScenarioTypeEnum.getByValue(type); if (typeEnum == null) { typeEnum = ScenarioTypeEnum.NORMAL; } typeContents.add(new ScenarioTypeContent(typeEnum, contents));
            }

            scenario = new TestScenario(name, typeContents);
        }
        return scenario;
    }

    /**
     * 递归加载子内容
     * @param session Neo4j会话
     * @param parentContent 父内容
     * @param parentContentId 父内容ID
     */
    private void loadSubContents(Session session, ScenarioContent parentContent, Long parentContentId) {
        Result result = session.run(
                "MATCH (p:ScenarioContent)-[:HAS_SUB_CONTENT]->(c:ScenarioContent) WHERE id(p) = $parentId RETURN id(c) as id, c.title as title, c.description as description",
                parameters("parentId", parentContentId)
        );

        while (result.hasNext()) {
            // 直接从结果中获取值，避免使用 Record 类型
            var record = result.next();
            Long contentId = record.get("id").asLong();
            String title = record.get("title").asString();
            String description = record.get("description").asString();
            
            ScenarioContent content = new ScenarioContent(title, description);
            // 递归获取子内容
            loadSubContents(session, content, contentId);
            parentContent.subContents.add(content);
        }
    }

    /**
     * 删除测试场景
     * @param name 场景名称
     */
    public void deleteScenario(String name) {
        try (Session session = driver.session()) {
            // 删除场景相关的所有关系和节点
            session.run(
                    "MATCH (s:TestScenario {name: $name})-[r]-() DELETE r",
                    parameters("name", name)
            );
            session.run(
                    "MATCH (s:TestScenario {name: $name}) DELETE s",
                    parameters("name", name)
            );
        }
    }

    /**
     * 获取场景类型统计
     * @return 各场景类型的数量统计
     */
    public Map<String, Object> getScenarioTypeStats() {
        Map<String, Object> result = null;
        try (Session session = driver.session()) {
            Result queryResult = session.run(
                    "MATCH (s:TestScenario)-[:HAS_TYPE]->(t:ScenarioType) RETURN t.name as type, count(*) as count ORDER BY count DESC"
            );

            // 构建统计结果
            List<Map<String, Object>> stats = new ArrayList<>();
            while (queryResult.hasNext()) {
                // 直接从结果中获取值，避免使用 Record 类型
                var record = queryResult.next();
                Map<String, Object> stat = Map.of(
                        "type", record.get("type").asString(),
                        "count", record.get("count").asLong()
                );
                stats.add(stat);
            }

            result = Map.of("stats", stats);
        }
        return result;
    }

    /**
     * 关闭Neo4j连接
     */
    @Override
    public void close() {
        if (driver != null) {
            driver.close();
        }
    }
}