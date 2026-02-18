package com.grape.grape.service.biz;

import java.util.Map;

/**
 * AI业务服务接口
 * 用于处理AI相关的业务逻辑
 */
public interface AiBizService {

    /**
     * 调用科大讯飞Spark API
     * @param question 用户的问题
     * @return 响应结果
     */
    Map<String, Object> callSpark(String question);

}
