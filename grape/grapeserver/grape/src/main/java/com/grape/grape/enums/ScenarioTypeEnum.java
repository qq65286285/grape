package com.grape.grape.enums;

/**
 * 场景类型枚举
 */
public enum ScenarioTypeEnum {
    /**
     * 正常场景
     */
    NORMAL("正常场景"),
    
    /**
     * 异常场景
     */
    EXCEPTION("异常场景"),
    
    /**
     * 逆向场景
     */
    REVERSE("逆向场景"),
    
    /**
     * 猴子场景
     */
    MONKEY("猴子场景");
    
    private final String value;
    
    ScenarioTypeEnum(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
    
    /**
     * 根据值获取枚举
     * @param value 场景类型值
     * @return 对应的枚举，不存在则返回null
     */
    public static ScenarioTypeEnum getByValue(String value) {
        for (ScenarioTypeEnum type : values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        return null;
    }
    
    /**
     * 根据文本推断场景类型
     * @param text 场景文本
     * @return 推断的场景类型
     */
    public static ScenarioTypeEnum inferType(String text) {
        if (text == null) {
            return NORMAL;
        }
        
        String lowerText = text.toLowerCase();
        if (lowerText.contains("异常") || lowerText.contains("错误") || lowerText.contains("失败")) {
            return EXCEPTION;
        } else if (lowerText.contains("逆向") || lowerText.contains("反向") || lowerText.contains("逆序")) {
            return REVERSE;
        } else if (lowerText.contains("猴子") || lowerText.contains("随机") || lowerText.contains("monkey")) {
            return MONKEY;
        } else {
            return NORMAL;
        }
    }
}