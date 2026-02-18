package com.grape.grape.service;

import com.grape.grape.entity.TestScenario;
import com.grape.grape.enums.ScenarioTypeEnum;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 文档分割器服务接口
 * 用于解析测试场景文档，提取场景信息，支持脑图格式
 */
public interface DocumentSplitterService {

    /**
     * 解析测试场景文档
     * @param doc 测试场景文档文件
     * @return 解析后的测试场景列表
     * @throws IOException 读取文件时可能发生的异常
     */
    List<TestScenario> parseTestScenarios(File doc) throws IOException;

    /**
     * 解析单个测试场景
     * @param scenarioText 场景文本
     * @return 解析后的测试场景对象
     */
    TestScenario parseSingleScenario(String scenarioText);

    /**
     * 提取场景名称
     * @param scenarioText 场景文本
     * @return 场景名称
     */
    String extractScenarioName(String scenarioText);

    /**
     * 提取场景类型列表
     * @param scenarioText 场景文本
     * @return 场景类型列表
     */
    List<ScenarioTypeEnum> extractScenarioTypes(String scenarioText);

    /**
     * 从字符串中提取多个类型
     * @param typeStr 类型字符串，如"正常场景, 异常场景"
     * @param types 类型列表
     */
    void addTypesFromString(String typeStr, List<ScenarioTypeEnum> types);

    /**
     * 根据场景内容推断场景类型
     * @param scenarioText 场景文本
     * @return 推断的场景类型
     */
    ScenarioTypeEnum inferScenarioType(String scenarioText);

    /**
     * 提取场景内容列表
     * @param scenarioText 场景文本
     * @return 场景内容列表
     */
    List<com.grape.grape.entity.ScenarioContent> extractScenarioContents(String scenarioText);
}
