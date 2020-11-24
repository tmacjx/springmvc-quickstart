package com.bokecc.supports;

import com.bokecc.exception.BaseException;
import lombok.Data;

/**
 * <p>
 * 通用的 API 接口封装
 * </p>
 *
 * @description: 通用的 API 接口封装
 */
@Data
public class RestResponse {
	/**
	 * 状态码
	 */
	private Integer code;

	/**
	 * 返回内容
	 */
	private String message;

	/**
	 * 返回数据
	 */
	private Object data;

	private Long ts = System.currentTimeMillis();

	/**
	 * 无参构造函数
	 */
	private RestResponse() {

	}

	/**
	 * 全参构造函数
	 *
	 * @param code    状态码
	 * @param message 返回内容
	 * @param data    返回数据
	 */
	public RestResponse(Integer code, String message, Object data) {
		this.code = code;
		this.message = message;
		this.data = data;
	}

	/**
	 * 构造一个自定义的API返回
	 *
	 * @param code    状态码
	 * @param message 返回内容
	 * @param data    返回数据
	 * @return ApiResponse
	 */
	public static RestResponse of(Integer code, String message, Object data) {
		return new RestResponse(code, message, data);
	}

	/**
	 * 构造一个成功且带数据的API返回
	 *
	 * @param data 返回数据
	 * @return ApiResponse
	 */
	public static RestResponse ofSuccess(Object data) {
		return ofStatus(CommonErrorCode.OK, data);
	}

	/**
	 * 构造一个成功API返回
	 *
	 * @return ApiResponse
	 */
	public static RestResponse ofSuccess() {
		return ofStatus(CommonErrorCode.OK, null);
	}

	/**
	 * 构造一个成功且自定义消息的API返回
	 *
	 * @param message 返回内容
	 * @return ApiResponse
	 */
	public static RestResponse ofMessage(String message) {
		return of(CommonErrorCode.OK.getCode(), message, null);
	}


	/**
	 * 构造一个失败API返回
	 *
	 * @return ApiResponse
	 */
	public static RestResponse ofFail(){
		return of(CommonErrorCode.FAIL.getCode(), CommonErrorCode.FAIL.getMessage(), null);
	}

	/**
	 * 构造一个有状态的API返回
	 *
	 * @param result 状态 {@link IErrorCode}
	 * @return ApiResponse
	 */
	public static RestResponse ofStatus(IErrorCode result) {
		return ofStatus(result, null);
	}

	/**
	 * 构造一个有状态且带数据的API返回
	 *
	 * @param result 状态 {@link IErrorCode}
	 * @param data   返回数据
	 * @return ApiResponse
	 */
	public static RestResponse ofStatus(IErrorCode result, Object data) {
		return of(result.getCode(), result.getMessage(), data);
	}

	/**
	 * 构造一个异常且带数据的API返回
	 *
	 * @param t    异常
	 * @param data 返回数据
	 * @param <T>  {@link BaseException} 的子类
	 * @return ApiResponse
	 */
	public static <T extends BaseException> RestResponse ofException(T t, Object data) {
		return of(t.getCode(), t.getMessage(), data);
	}

	/**
	 * 构造一个异常API返回
	 *
	 * @param t   异常
	 * @param <T> {@link BaseException} 的子类
	 * @return ApiResponse
	 */
	public static <T extends BaseException> RestResponse ofException(T t) {
		return ofException(t, null);
	}
}
