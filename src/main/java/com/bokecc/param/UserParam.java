package com.bokecc.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserParam {

    @ApiModelProperty(required = true, value = "用户id")
    private String userid;

    @ApiModelProperty(required = true, value = "用户名")
    private String username;
}
