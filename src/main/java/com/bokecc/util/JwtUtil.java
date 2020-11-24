package com.bokecc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.jsonwebtoken.*;
import io.jsonwebtoken.impl.TextCodec;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

public class JwtUtil {

    /** 段数 **/
    private static final int TOKEN_PART_SIZE = 3;

    /** 超时时间（毫秒） **/
    private static final long EXPIRATION_TIME = 1800L * 1000L;

    /** jwt头 **/
    private static final Map<String, Object> JWT_HEAD = Collections.singletonMap("type", "JWT");

    /**
     * 验证token
     * @param token token
     * @param secret 密码
     * @return jwtPaire left 0：正常
     * 1：签名错误 2：时间过期 3：jwt格式错误
     */
    public static JwtPaire verifyToken(String token, String secret){

        JwtPaire jwtPaire = new JwtPaire();

        try{

            Claims claims = parseToken(token, secret);

            jwtPaire.setLeft(0);

            jwtPaire.setRight(claims.getIssuer());

        }catch (SignatureException signatureException){

            jwtPaire.setLeft(1);

            jwtPaire.setRight("Signature error!");

        }catch (ExpiredJwtException expiredJwtException){

            jwtPaire.setLeft(2);

            jwtPaire.setRight("time Expired error!");

        }catch (Exception e){

            jwtPaire.setLeft(3);

            jwtPaire.setRight("malformed jwt error!");
        }

        return jwtPaire;
    }

    /**
     * 解析token
     * @param token token
     * @param secret 密码
     * @return claims
     */
    public static Claims parseToken(String token, String secret){


        if(StringUtils.isEmpty(token)){

            throw new IllegalArgumentException("token must not be null or empty!");
        }

        String[] parts = token.split("\\.");

        if(parts.length < TOKEN_PART_SIZE){

            throw new IllegalArgumentException("token is invalid!");
        }

        try {

            return Jwts.parser().setSigningKey(Base64Util.encode(secret.getBytes("UTF-8"))).parseClaimsJws(token).getBody();
        }catch (Exception e){

            throw new RuntimeException("jwt decode exception");
        }
    }

    /**
     * 获取token中的appkey
     * @param token token
     * @return appkey
     */
    public static String getAppkey(String token) throws UnsupportedEncodingException {

        String[] parts = validate(token);

        String str = new String(TextCodec.BASE64URL.decode(parts[1]), "utf-8");

        JSONObject jsonObject = JSON.parseObject(str);

        return jsonObject.getString("iss");
    }

    private static String[] validate(String token){

        if(StringUtils.isEmpty(token)){

            throw new IllegalArgumentException("token must not be null or empty!");
        }

        String[] parts = token.split("\\.");

        if(parts.length < TOKEN_PART_SIZE){

            throw new IllegalArgumentException("token is invalid!");
        }

        return parts;
    }

    /**
     * 生成token
     * @param appKey 用户id
     * @param secret 密码
     * @return token
     */
    public static String createToken(String appKey, String secret) {

        long nowMillis = System.currentTimeMillis();

        Date now = new Date(nowMillis);

        Date expire = new Date(nowMillis + EXPIRATION_TIME);

        JwtBuilder builder = Jwts.builder()

                .setHeaderParam("type", "JWT")

                .setIssuedAt(now)

                .setIssuer(appKey)

                .setExpiration(expire)

                .signWith(SignatureAlgorithm.HS256, Base64Util.encode(secret.getBytes(StandardCharsets.UTF_8)));

        return builder.compact();
    }
}
