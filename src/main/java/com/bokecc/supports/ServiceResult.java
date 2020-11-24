package com.bokecc.supports;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * Service层结果封装类
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServiceResult<T> {

    private static final String successMsg = "成功";
    private static final String failMsg = "失败";

    private T res;
    private boolean success;
    private String message;

    public boolean isSuccess() {
        return success;
    }

    public static <T> ServiceResult<T> ofSuccess(T data) {
        return ServiceResult.<T>builder().success(true).res(data).message(successMsg).build();
    }

    public static <T> ServiceResult<T> ofSuccess() {
        return ServiceResult.<T>builder().success(true).message(successMsg).build();
    }

    public static <T> ServiceResult<T> ofSuccess(T data, String message) {
        return ServiceResult.<T>builder().success(true).res(data).message(message).build();
    }

    public static <T> ServiceResult<T> ofFAIL() {
        return ServiceResult.<T>builder().success(false).message(failMsg).build();
    }

    public static <T> ServiceResult<T> ofFAIL(T data) {
        return ServiceResult.<T>builder().success(false).res(data).message(failMsg).build();
    }

    public static <T> ServiceResult<T> ofFAIL(T data, String message) {
        return ServiceResult.<T>builder().success(false).res(data).message(message).build();
    }

    public static <T> ServiceResult<T> ofFAIL(String message) {
        return ServiceResult.<T>builder().success(false).message(message).build();
    }

}
