package com.bokecc.filter;

import com.alibaba.fastjson.JSON;
import com.bokecc.config.PropertiesObtainConfig;
import com.bokecc.constant.Constant;
import com.bokecc.util.Base64Util;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ContainerRequest;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@Slf4j
public class SwaggerAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";

    private static final String ACTIVE = "spring.profiles.active";

    private static final String ACTIVE_FLAG = "testing";

    private static final String ACTIVE_FLAG_DEV = "dev";

    private static final String AUTH_URL = "swagger.json";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        ContainerRequest request = (ContainerRequest) requestContext.getRequest();

        String url = request.getPath(false);

        if(!url.equals(AUTH_URL)){

            return;
        }

        final MultivaluedMap<String, String> headers = requestContext.getHeaders();

        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if(authorization == null || authorization.isEmpty())
        {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"input username and password\"").build());
            return;
        }

        log.debug(JSON.toJSONString(authorization));

        if(!auth(authorization.get(0))){

            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .header("WWW-Authenticate", "Basic realm=\"input username and password\"")
                    .entity("认证失败")
                    .build());
            return;
        }

        log.debug("通过");
    }

    /**
     * 认证方法体
     * @param authHeadValue 认证头的值
     * @return true：通过；false：未通过
     */
    private boolean auth(String authHeadValue) throws IOException {

        String[] authArray = authHeadValue.split(" ");

        if(authArray.length < 2){

            return false;
        }

        String[] userArray = Base64Util.decodeAsString(authArray[1]).split(":");

        log.info(Arrays.toString(userArray));

        if(userArray.length < 2){

            return false;
        }

        String userName = userArray[0];

        String pw = userArray[1];

        String active = PropertiesObtainConfig.env.getProperty(ACTIVE);

        if (ACTIVE_FLAG.equals(active) || ACTIVE_FLAG_DEV.equals(active)) {

            return Constant.BASIC_AUTH_USER.equals(userName)
                    && Constant.BASIC_AUTH_PW_TESTING.equals(pw);
        }

        return Constant.BASIC_AUTH_USER.equals(userName)
                && Constant.BASIC_AUTH_PW_ONLINE.equals(pw);
    }
}
