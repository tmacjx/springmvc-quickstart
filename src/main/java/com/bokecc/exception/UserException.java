package com.bokecc.exception;

import com.bokecc.supports.CommonErrorCode;

public class UserException extends BaseException{

    public UserException(CommonErrorCode result){
        super(result);
    }

    public UserException(Integer code, String message){
        super(code, message);
    }

}
