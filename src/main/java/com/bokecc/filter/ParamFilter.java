package com.bokecc.filter;

import com.alibaba.fastjson.JSON;
import com.bokecc.config.ConfigProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * 请求参数过滤器
 */

@Slf4j
@Order(11)
@WebFilter(filterName = "param-filter", urlPatterns = "/*")
public class ParamFilter implements Filter {

    public static final List<String> IGNORE_URL_LIST = new ArrayList<String>() {{
        add("/api/swagger.json");
        add("/api/health");
    }};

    public static final String OPTIONS = "OPTIONS";

    ConfigProperties configProperties;

    @Override
    public void init(FilterConfig filterConfig) {
        ApplicationContext context = WebApplicationContextUtils.getWebApplicationContext(filterConfig.getServletContext());
        configProperties = (ConfigProperties) context.getBean("configProperties");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 不需要打印OPTIONS 预检
        if (req.getMethod().equals(OPTIONS)) {
            filterChain.doFilter(req, response);
            return;
        }

        // 不需要打印的路径
        if (IGNORE_URL_LIST.contains(req.getRequestURI())) {
            filterChain.doFilter(req, response);
            return;
        }

        RequestWrapper requestWrapper = new RequestWrapper(req);
        ResponseWrapper responseWrapper = new ResponseWrapper(response);
        String body;
        if (("POST".equals(req.getMethod()) || "PUT".equals(req.getMethod())) && StringUtils.isNotBlank(req.getContentType()) && "application/json".equals(req.getContentType())) {
            body = this.getJsonParam(requestWrapper);
        } else {
            body = JSON.toJSONString(requestWrapper.getParameterMap());
        }

        log.info("请求地址: {} 请求参数:{} 请求方法:{} 请求类型为{} 请求头为:{}", requestWrapper.getRequestURI(), body, req.getMethod(), req.getContentType(), getHeadersInfo(req));
        // 请求开始时间
        long begin = System.currentTimeMillis();
        filterChain.doFilter(requestWrapper, responseWrapper);
        String content = responseWrapper.getTextContent();

        log.info("返回数据 url: {} time: {} Response: {} 请求参数:{}", req.getRequestURI(), System.currentTimeMillis() - begin, content, body);
        response.getOutputStream().write(content.getBytes());

    }

    @Override
    public void destroy() {

    }

    /**
     * 获取Json数据
     *
     */
    private String getJsonParam(HttpServletRequest request) {
        String jsonParam = "";
        ServletInputStream inputStream = null;
        try {
            int contentLength = request.getContentLength();
            if (!(contentLength < 0)) {
                byte[] buffer = new byte[contentLength];
                inputStream = request.getInputStream();
                for (int i = 0; i < contentLength; ) {
                    int len = inputStream.read(buffer, i, contentLength);
                    if (len == -1) {
                        break;
                    }
                    i += len;
                }
                jsonParam = new String(buffer, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            log.error("参数转换成json异常", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("参数转换成json异常", e);
                }
            }
        }
        return jsonParam;
    }

    private Map<String, String> getHeadersInfo(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            map.put(key, value);
        }
        return map;
    }
}