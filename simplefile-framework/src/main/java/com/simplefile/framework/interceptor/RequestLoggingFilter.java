package com.simplefile.framework.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Enumeration;

@Slf4j
@Component
public class RequestLoggingFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        log.info("Received request: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());

        // 打印请求头（包含Authorization头）
        Enumeration<String> headers = httpRequest.getHeaderNames();
        while (headers.hasMoreElements()) {
            String headerName = headers.nextElement();
            log.info("Header: {} = {}", headerName, httpRequest.getHeader(headerName));
        }

        chain.doFilter(request, response);
    }
}
