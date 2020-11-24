package com.bokecc.supports;

import lombok.Getter;
import lombok.ToString;

/**
 * <p>
 * 状态码封装
 * </p>
 *
 * @description: 状态码封装
 */
@Getter
@ToString
public enum CommonErrorCode implements IErrorCode{
	/**
	 * 操作成功
	 */
	OK(0, "操作成功"),
	FAIL(-1, "操作失败"),
	UNKNOWN_ERROR(-100, "服务器出错啦"),
	AUTH_FAILURE(-101, "认证失败"),
	PARAM_INVALID(11, "参数错误");

	/**
	 * 状态码
	 */
	private Integer code;
	/**
	 * 内容
	 */
	private String message;

	CommonErrorCode(Integer code, String message) {
		this.code = code;
		this.message = message;
	}

}
