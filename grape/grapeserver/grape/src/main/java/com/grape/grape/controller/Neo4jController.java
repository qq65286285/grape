package com.grape.grape.controller;

import com.grape.grape.model.Resp;
import org.springframework.web.bind.annotation.*;

/**
 * Neo4j 控制器
 * 暂时注释掉，因为 GraphTransformer 类不存在
 */
// @RestController
// @RequestMapping("/api/neo4j")
public class Neo4jController {
    
    // private final GraphTransformer graphTransformer;
    
    // public Neo4jController(@Value("${spring.neo4j.uri:bolt://localhost:7687}") String neo4jUri,
    //                        @Value("${spring.neo4j.authentication.username:neo4j}") String neo4jUser,
    //                        @Value("${spring.neo4j.authentication.password:password}") String neo4jPassword) {
    //     this.graphTransformer = new GraphTransformer(neo4jUri, neo4jUser, neo4jPassword);
    // }
    
    // // 保存测试场景
    // @PostMapping("/scenario")
    // public Resp saveTestScenario(@RequestBody DocumentSplitter.TestScenario scenario) {
    //     graphTransformer.saveTestScenario(scenario);
    //     return Resp.ok("测试场景保存成功");
    // }
    // 
    // // 批量保存测试场景
    // @PostMapping("/scenarios")
    // public Resp saveTestScenarios(@RequestBody List<DocumentSplitter.TestScenario> scenarios) {
    //     graphTransformer.saveTestScenarios(scenarios);
    //     return Resp.ok("测试场景批量保存成功");
    // }
    // 
    // // 根据名称查询测试场景
    // @GetMapping("/scenario/{name}")
    // public Resp getScenarioByName(@PathVariable String name) {
    //     DocumentSplitter.TestScenario scenario = graphTransformer.getScenarioByName(name);
    //     return Resp.ok(scenario);
    // }
    // 
    // // 根据场景类型查询测试场景
    // @GetMapping("/scenarios/type/{type}")
    // public Resp getScenariosByType(@PathVariable String type) {
    //     List<DocumentSplitter.TestScenario> scenarios = graphTransformer.getScenariosByType(type);
    //     return Resp.ok(scenarios);
    // }
    // 
    // // 查询所有测试场景
    // @GetMapping("/scenarios")
    // public Resp getAllScenarios() {
    //     List<DocumentSplitter.TestScenario> scenarios = graphTransformer.getAllScenarios();
    //     return Resp.ok(scenarios);
    // }
    // 
    // // 删除测试场景
    // @DeleteMapping("/scenario/{name}")
    // public Resp deleteScenario(@PathVariable String name) {
    //     graphTransformer.deleteScenario(name);
    //     return Resp.ok("测试场景删除成功");
    // }
    // 
    // // 获取场景类型统计
    // @GetMapping("/scenarios/stats")
    // public Resp getScenarioTypeStats() {
    //     Map<String, Object> stats = graphTransformer.getScenarioTypeStats();
    //     return Resp.ok(stats);
    // }
}

