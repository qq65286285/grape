package com.grape.grape;

import com.grape.grape.entity.ScenarioContent;
import com.grape.grape.entity.ScenarioTypeContent;
import com.grape.grape.entity.TestScenario;
import com.grape.grape.enums.ScenarioTypeEnum;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class ExcelToTestScenarioTest {
    
    private static PrintStream out;
    private static PrintStream err;
    
    static {
        // 设置控制台编码为UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("sun.jnu.encoding", "UTF-8");
        
        // 尝试重新设置标准输出流为UTF-8编码
        try {
            out = new PrintStream(System.out, true, "UTF-8");
            err = new PrintStream(System.err, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            out = System.out;
            err = System.err;
        }
    }

    @Test
    public void testExcelToTestScenario() {
        // 读取Excel文件
        String excelPath = "src/main/resources/excelTemplate/caseExcel/-导入验证.xlsx";
        File excelFile = new File(excelPath);
        
        if (!excelFile.exists()) {
            err.println("Excel文件不存在: " + excelPath);
            return;
        }

        // 使用Hutool读取Excel
        ExcelReader reader = ExcelUtil.getReader(excelFile);
        
        // 读取所有行，第一行为标题
        List<List<Object>> rows = reader.read();
        
        if (rows.isEmpty()) {
            err.println("Excel文件为空");
            return;
        }

        // 创建映射来分组场景和类型
        Map<String, Map<ScenarioTypeEnum, List<ScenarioContent>>> scenarioMap = new HashMap<>();
        
        // 跳过标题行，处理数据行
        for (int i = 1; i < rows.size(); i++) {
            List<Object> row = rows.get(i);
            if (row.isEmpty()) {
                continue;
            }

            // 获取场景名称
            String scenarioName = row.get(0) != null ? row.get(0).toString().trim() : "";
            if (scenarioName.isEmpty()) {
                continue;
            }

            // 获取场景类型
            String typeStr = row.get(1) != null ? row.get(1).toString().trim() : "";
            ScenarioTypeEnum scenarioType = ScenarioTypeEnum.getByValue(typeStr);
            if (scenarioType == null) {
                // 如果无法匹配枚举值，使用推断的类型
                scenarioType = ScenarioTypeEnum.inferType(typeStr);
            }

            // 获取内容标题和描述
            String contentTitle = row.size() >= 3 && row.get(2) != null ? row.get(2).toString().trim() : "";
            String contentDesc = row.size() >= 4 && row.get(3) != null ? row.get(3).toString().trim() : "";
            
            if (!contentTitle.isEmpty()) {
                // 确保场景映射存在
                scenarioMap.computeIfAbsent(scenarioName, k -> new HashMap<>());
                // 确保类型映射存在
                scenarioMap.get(scenarioName).computeIfAbsent(scenarioType, k -> new ArrayList<>());
                // 添加内容
                scenarioMap.get(scenarioName).get(scenarioType).add(new ScenarioContent(contentTitle, contentDesc));
            }
        }
        
        // 处理分组后的数据，创建测试场景
        for (Map.Entry<String, Map<ScenarioTypeEnum, List<ScenarioContent>>> scenarioEntry : scenarioMap.entrySet()) {
            String scenarioName = scenarioEntry.getKey();
            Map<ScenarioTypeEnum, List<ScenarioContent>> typeMap = scenarioEntry.getValue();
            
            // 创建测试场景
            TestScenario scenario = new TestScenario(scenarioName, new ArrayList<>());
            
            // 添加场景类型内容
            for (Map.Entry<ScenarioTypeEnum, List<ScenarioContent>> typeEntry : typeMap.entrySet()) {
                ScenarioTypeEnum type = typeEntry.getKey();
                List<ScenarioContent> contents = typeEntry.getValue();
                scenario.typeContents.add(new ScenarioTypeContent(type, contents));
            }
            
            // 打印测试场景
            out.println("解析到测试场景: " + scenario.name);
            out.println("场景类型数量: " + scenario.typeContents.size());
            
            // 打印场景类型及其内容
            for (ScenarioTypeContent typeContent : scenario.typeContents) {
                out.println("  场景类型: " + typeContent.type.getValue());
                out.println("  内容数量: " + typeContent.contents.size());
                
                // 打印前2个内容（避免输出过多）
                for (int j = 0; j < Math.min(2, typeContent.contents.size()); j++) {
                    ScenarioContent content = typeContent.contents.get(j);
                    out.println("    内容" + (j + 1) + ": " + content.title);
                    if (!content.description.isEmpty()) {
                        out.println("      描述: " + content.description);
                    }
                }
                if (typeContent.contents.size() > 2) {
                    out.println("    ... 等" + typeContent.contents.size() + "个内容");
                }
            }
            out.println();
        }

        // 关闭reader
        reader.close();
    }
}
