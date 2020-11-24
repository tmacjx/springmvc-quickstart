package com.bokecc.filter;

import com.alibaba.fastjson.JSON;
import com.bokecc.config.PropertiesObtainConfig;
import com.bokecc.constant.Constant;
import com.bokecc.util.Base64Util;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
public class SwaggerAuthenticationFilter implements Filter {

    private static final String AUTHORIZATION_PROPERTY = "Authorization";

    private static final String ACTIVE = "spring.profiles.active";

    private static final String ACTIVE_FLAG = "testing";

    private static final String ACTIVE_FLAG_DEV = "dev";

    private static final String AUTH_URL = "swagger.json";

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

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String url = request.getRequestURL().toString();

        if(!url.equals(AUTH_URL)){

            return;
        }

        Map<String, List<String>> headers = Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(),
                        h -> Collections.list(request.getHeaders(h))
                ));

        final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);

        if(authorization == null || authorization.isEmpty())
        {

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"input username and password\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        log.debug(JSON.toJSONString(authorization));

        if(!auth(authorization.get(0))){

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"input username and password\"");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }


        log.debug("通过");
    }

    @Override
    public void destroy() {

    }
}
