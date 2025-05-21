package com.kenanai.common.constant;

/**
 * 通用常量定义
 */
public interface CommonConstants {

    /**
     * 成功标记
     */
    Integer SUCCESS = 0;

    /**
     * 失败标记
     */
    Integer FAIL = 1;

    /**
     * 当前页码
     */
    String CURRENT = "current";

    /**
     * 每页数量
     */
    String SIZE = "size";

    /**
     * 排序字段
     */
    String SORT = "sort";

    /**
     * 排序方向
     */
    String ORDER = "order";

    /**
     * 升序
     */
    String ASC = "asc";

    /**
     * Token前缀
     */
    String TOKEN_PREFIX = "Bearer ";

    /**
     * Token头部
     */
    String AUTHORIZATION_HEADER = "Authorization";

    /**
     * 用户ID键名
     */
    String USER_ID = "userId";

    /**
     * 用户名键名
     */
    String USERNAME = "username";
} 