package com.mu.musmart.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;

import java.io.IOException;

@WebFilter("/*")
public class LoginFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (isStaticUri(req)){
            filterChain.doFilter(req, servletResponse);
        }
        //放过登录页面
        if (!req.getRequestURI().contains("/login")){
            try {
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    public boolean isStaticUri(HttpServletRequest req){
        return req.getRequestURI().matches(".*\\.(js|css|png|jpg|gif|ico|woff|woff2|ttf|svg|eot|min.js.map|min.css.map)");
    }
}
