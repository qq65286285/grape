package com.grape.grape.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试场景类，存储场景信息
 */
public class TestScenario {
    public String name; // 场景名称
    public List<ScenarioTypeContent> typeContents; // 场景类型及其对应的内容列表

    public TestScenario(String name, List<ScenarioTypeContent> typeContents) {
        this.name = name;
        this.typeContents = typeContents != null ? typeContents : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "TestScenario{" +
                "name='" + name + '\'' +
                ", typeContents.size()=" + typeContents.size() +
                '}';
    }
}
