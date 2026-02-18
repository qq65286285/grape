package com.grape.grape.service.impl;

import com.grape.grape.entity.ScenarioContent;
import com.grape.grape.entity.ScenarioTypeContent;
import com.grape.grape.entity.TestScenario;
import com.grape.grape.enums.ScenarioTypeEnum;
import com.grape.grape.service.DocumentSplitter;
import com.grape.grape.service.DocumentSplitterService;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档分割器服务实现
 * 用于解析测试场景文档，提取场景信息，支持脑图格式
 */
@Service
public class DocumentSplitterServiceImpl implements DocumentSplitterService {

    /**
     * 解析测试场景文档
     * @param doc 测试场景文档文件
     * @return 解析后的测试场景列表
     * @throws IOException 读取文件时可能发生的异常
     */
    @Override
    public List<TestScenario> parseTestScenarios(File doc) throws IOException {
        List<TestScenario> scenarios = new ArrayList<>();
        String content = Files.readString(doc.toPath());
        
        // 按场景分割文档
        // 假设每个场景以"场景名称:"或类似的标记开始
        String[] scenarioTexts = content.split("(?=场景名称:|Scenario:|场景\\s*[0-9]+:)");
        
        for (String scenarioText : scenarioTexts) {
            scenarioText = scenarioText.trim();
            if (scenarioText.isEmpty()) {
                continue;
            }
            
            TestScenario scenario = parseSingleScenario(scenarioText);
            if (scenario != null) {
                scenarios.add(scenario);
            }
        }
        
        return scenarios;
    }

    /**
     * 解析单个测试场景（支持脑图结构）
     * @param scenarioText 场景文本
     * @return 解析后的测试场景对象
     */
    @Override
    public TestScenario parseSingleScenario(String scenarioText) {
        String name = extractScenarioName(scenarioText);
        List<ScenarioTypeEnum> types = extractScenarioTypes(scenarioText);
        List<ScenarioContent> contents = extractScenarioContents(scenarioText);
        
        if (name == null || types.isEmpty()) {
            return null;
        }
        
        // 构建场景类型及其对应内容的列表
        List<ScenarioTypeContent> typeContents = new ArrayList<>();
        for (ScenarioTypeEnum type : types) {
            // 为每个场景类型创建对应的内容列表
            typeContents.add(new ScenarioTypeContent(type, contents));
        }
        
        return new TestScenario(name, typeContents);
    }

    /**
     * 提取场景名称
     * @param scenarioText 场景文本
     * @return 场景名称
     */
    @Override
    public String extractScenarioName(String scenarioText) {
        // 从文本中提取场景名称
        // 支持多种格式："场景名称: xxx", "Scenario: xxx", "场景1: xxx"等
        String[] lines = scenarioText.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("场景名称:")) {
                return line.substring(5).trim();
            } else if (line.startsWith("Scenario:")) {
                return line.substring(9).trim();
            } else if (line.matches("场景\\s*[0-9]+:\\s*.+")) {
                return line.replaceAll("场景\\s*[0-9]+:\\s*", "").trim();
            }
        }
        return null;
    }

    /**
     * 提取场景类型列表
     * @param scenarioText 场景文本
     * @return 场景类型列表
     */
    @Override
    public List<ScenarioTypeEnum> extractScenarioTypes(String scenarioText) {
        List<ScenarioTypeEnum> types = new ArrayList<>();
        // 从文本中提取场景类型
        // 支持多种格式："场景类型: xxx", "Type: xxx"等
        String[] lines = scenarioText.split("\\n");
        for (String line : lines) {
            line = line.trim();
            if (line.startsWith("场景类型:")) {
                String typeStr = line.substring(5).trim();
                // 处理多个类型，如"场景类型: 正常场景, 异常场景"
                addTypesFromString(typeStr, types);
            } else if (line.startsWith("Type:")) {
                String typeStr = line.substring(5).trim();
                addTypesFromString(typeStr, types);
            } else if (line.contains("场景类型") || line.contains("Type")) {
                // 尝试从包含场景类型的行中提取
                String[] parts = line.split(":");
                if (parts.length > 1) {
                    String typeStr = parts[1].trim();
                    addTypesFromString(typeStr, types);
                }
            }
        }
        
        // 如果没有明确指定类型，根据内容推断
        if (types.isEmpty()) {
            types.add(inferScenarioType(scenarioText));
        }
        
        return types;
    }

    /**
     * 从字符串中提取多个类型
     * @param typeStr 类型字符串，如"正常场景, 异常场景"
     * @param types 类型列表
     */
    @Override
    public void addTypesFromString(String typeStr, List<ScenarioTypeEnum> types) {
        // 支持多种分隔符
        String[] typeArray = typeStr.split("[,，;；]\\s*");
        for (String type : typeArray) {
            type = type.trim();
            if (!type.isEmpty()) {
                ScenarioTypeEnum typeEnum = ScenarioTypeEnum.getByValue(type);
                if (typeEnum != null) {
                    types.add(typeEnum);
                } else {
                    // 如果无法匹配枚举值，使用推断的类型
                    types.add(ScenarioTypeEnum.inferType(type));
                }
            }
        }
    }

    /**
     * 根据场景内容推断场景类型
     * @param scenarioText 场景文本
     * @return 推断的场景类型
     */
    @Override
    public ScenarioTypeEnum inferScenarioType(String scenarioText) {
        // 直接使用枚举类中的推断方法
        return ScenarioTypeEnum.inferType(scenarioText);
    }

    /**
     * 提取场景内容列表
     * @param scenarioText 场景文本
     * @return 场景内容列表
     */
    @Override
    public List<ScenarioContent> extractScenarioContents(String scenarioText) {
        List<ScenarioContent> contents = new ArrayList<>();
        
        // 简单的脑图结构解析
        // 假设脑图使用缩进或标记来表示层级关系
        String[] lines = scenarioText.split("\\n");
        List<ScenarioContent> currentLevel = contents;
        List<List<ScenarioContent>> levelStack = new ArrayList<>();
        levelStack.add(currentLevel);
        
        int previousIndent = 0;
        
        for (String line : lines) {
            // 跳过场景名称和类型行
            if (line.trim().startsWith("场景名称:") || line.trim().startsWith("Scenario:") || 
                line.trim().startsWith("场景类型:") || line.trim().startsWith("Type:")) {
                continue;
            }
            
            // 计算缩进级别
            int indent = line.length() - line.trim().length();
            String trimmedLine = line.trim();
            
            if (trimmedLine.isEmpty()) {
                continue;
            }
            
            // 处理层级关系
            if (indent > previousIndent) {
                // 进入下一层级
                if (!currentLevel.isEmpty()) {
                    ScenarioContent lastContent = currentLevel.get(currentLevel.size() - 1);
                    currentLevel = lastContent.subContents;
                    levelStack.add(currentLevel);
                }
            } else if (indent < previousIndent) {
                // 返回上一层级
                while (indent < previousIndent && levelStack.size() > 1) {
                    levelStack.remove(levelStack.size() - 1);
                    currentLevel = levelStack.get(levelStack.size() - 1);
                    previousIndent = indent;
                }
            }
            
            // 创建内容节点
            // 假设格式为"标题: 描述"
            String[] parts = trimmedLine.split(":", 2);
            String title = parts[0].trim();
            String description = parts.length > 1 ? parts[1].trim() : "";
            
            ScenarioContent content = new ScenarioContent(title, description);
            currentLevel.add(content);
            
            previousIndent = indent;
        }
        
        return contents;
    }
}
