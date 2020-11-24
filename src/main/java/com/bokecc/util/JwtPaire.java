package com.bokecc.util;


import lombok.*;

/**
 * jwt token验证返回对象类
 **/

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtPaire {

    private int left;

    private String right;

    @Override
    public String toString() {

        return "JwtPaire{" +
                "left=" + left +
                ", right='" + right + '\'' +
                '}';
    }
}
