package com.grape.grape.entity;

import com.grape.grape.enums.ScenarioTypeEnum;
import java.util.ArrayList;
import java.util.List;

/**
 * 场景类型内容类，存储场景类型及其对应的内容
 */
public class ScenarioTypeContent {
    public ScenarioTypeEnum type; // 场景类型
    public List<ScenarioContent> contents; // 该类型对应的场景内容列表

    public ScenarioTypeContent(ScenarioTypeEnum type, List<ScenarioContent> contents) {
        this.type = type;
        this.contents = contents != null ? contents : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ScenarioTypeContent{" +
                "type='" + type.getValue() + '\'' +
                ", contents.size()=" + contents.size() +
                '}';
    }
}
