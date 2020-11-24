package com.bokecc.filter;

import com.bokecc.constant.Constant;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;


/**
 *  过滤器 设置请求TraceId
 */
@Slf4j
@Order(10)
@WebFilter(filterName = "mdc-filter", urlPatterns = "/*")
public class AMDCFilter implements Filter {


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        String uuid = UUID.randomUUID().toString().replaceAll("-", "");

        MDC.put(Constant.MDC_KEY, uuid);
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
