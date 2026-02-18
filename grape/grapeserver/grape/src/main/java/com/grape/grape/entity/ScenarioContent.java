package com.grape.grape.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * 测试场景内容类，存储场景中的具体内容
 */
public class ScenarioContent {
    public String title; // 内容标题
    public String description; // 内容描述
    public List<ScenarioContent> subContents; // 子内容

    public ScenarioContent(String title, String description) {
        this.title = title;
        this.description = description;
        this.subContents = new ArrayList<>();
    }

    public ScenarioContent(String title, String description, List<ScenarioContent> subContents) {
        this.title = title;
        this.description = description;
        this.subContents = subContents != null ? subContents : new ArrayList<>();
    }

    @Override
    public String toString() {
        return "ScenarioContent{" +
                "title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", subContents.size()=" + subContents.size() +
                '}';
    }
}
