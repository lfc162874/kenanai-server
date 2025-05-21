package com.kenanai.common.entity;

import com.kenanai.common.constant.CommonConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 通用响应信息主体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 返回标记：0 - 成功， 1 - 失败
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String msg;

    /**
     * 数据
     */
    private T data;

    public static <T> R<T> ok() {
        return ok(null);
    }

    public static <T> R<T> ok(T data) {
        return new R<>(CommonConstants.SUCCESS, "操作成功", data);
    }

    public static <T> R<T> ok(T data, String msg) {
        return new R<>(CommonConstants.SUCCESS, msg, data);
    }

    public static <T> R<T> failed() {
        return new R<>(CommonConstants.FAIL, "操作失败", null);
    }

    public static <T> R<T> failed(String msg) {
        return new R<>(CommonConstants.FAIL, msg, null);
    }

    public static <T> R<T> failed(T data) {
        return new R<>(CommonConstants.FAIL, "操作失败", data);
    }

    public static <T> R<T> failed(T data, String msg) {
        return new R<>(CommonConstants.FAIL, msg, data);
    }
} 