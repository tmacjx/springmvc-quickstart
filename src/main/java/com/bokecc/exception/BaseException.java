package com.bokecc.exception;

import com.bokecc.supports.CommonErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 异常基类
 * </p>
 *
 * @description: 异常基类
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BaseException extends RuntimeException {
	private Integer code;
	private String message;

	public BaseException(CommonErrorCode result) {
		super(result.getMessage());
		this.code = result.getCode();
		this.message = result.getMessage();
	}

	public BaseException(Integer code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}
}
