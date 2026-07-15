package cn.zenshop.common.result;

import lombok.Data;

/**
 * 统一响应结果封装类
 *
 * @param <T> 数据泛型
 */
@Data
public class Result<T> {

    /** 成功状态码 */
    private static final Integer SUCCESS_CODE = 1;
    /** 默认失败状态码 */
    private static final Integer ERROR_CODE = 0;

    /** 状态码：1 成功，其他为失败 */
    private Integer code;

    /** 提示信息 */
    private String msg;

    /** 响应数据 */
    private T data;

    private Result() {
    }

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ==================== 成功 ====================

    /**
     * 成功（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(SUCCESS_CODE, "success", null);
    }

    /**
     * 成功（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(SUCCESS_CODE, "success", data);
    }

    /**
     * 成功（自定义消息 + 数据）
     */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(SUCCESS_CODE, msg, data);
    }

    // ==================== 失败 ====================

    /**
     * 失败（默认状态码 0）
     */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ERROR_CODE, msg, null);
    }

    /**
     * 失败（自定义状态码 + 消息）
     */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /**
     * 失败（自定义状态码 + 消息 + 数据）
     */
    public static <T> Result<T> error(Integer code, String msg, T data) {
        return new Result<>(code, msg, data);
    }
}
