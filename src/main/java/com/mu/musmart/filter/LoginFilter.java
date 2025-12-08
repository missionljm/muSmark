package com.mu.musmart.filter;

import cn.hutool.core.date.StopWatch;
import com.mu.musmart.context.ReqInfoContext;
import com.mu.musmart.mdc.MdcUtil;
import com.mu.musmart.service.GlobalInitService;
import com.mu.musmart.service.LoginService;
import com.mu.musmart.util.CrossUtil;
import com.mu.musmart.util.EnvUtil;
import com.mu.musmart.util.IpUtil;
import com.mu.musmart.util.SessionUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@WebFilter("/*")
@Slf4j
public class LoginFilter implements Filter {

    @Resource
    private GlobalInitService globalInitService;

    /**
     * 返回给前端的traceId，用于日志追踪
     */
    private static final String GLOBAL_TRACE_ID_HEADER = "g-trace-id";



    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        if (isStaticUri(req)){
            filterChain.doFilter(req, servletResponse);
        }
        StopWatch stopWatch = new StopWatch("请求耗时");
        //放过登录页面
        if (!req.getRequestURI().contains("/login")){
            try {
                if (req.getCookies() == null){
                    req.getRequestDispatcher("/login").forward(req, servletResponse);
                }else {
                    stopWatch.start("请求参数构建");
                    this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
                    stopWatch.stop();
                    stopWatch.start("跨域请求");
                    CrossUtil.buildCors(req , (HttpServletResponse) servletResponse);
                    filterChain.doFilter(req, servletResponse);
//                    ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
//                    Optional.ofNullable(SessionUtil.findCookieByName(req, LoginService.SESSION_KEY))
//                            .ifPresent(cookie -> globalInitService.initLoginUser(reqInfo));
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }finally {
                if (stopWatch.isRunning()) {
                    // 避免doFitler执行异常，导致上面的 stopWatch无法结束，这里先首当结束一下上次的计数
                    stopWatch.stop();
                }
//                stopWatch.start("输出请求日志");
//                buildRequestLog(ReqInfoContext.getReqInfo(), request, System.currentTimeMillis() - start);
//                // 一个链路请求完毕，清空MDC相关的变量(如GlobalTraceId，用户信息)
//                MdcUtil.clear();
//                ReqInfoContext.clear();
//                stopWatch.stop();
//
//                if (!isStaticURI(request) && !EnvUtil.isPro()) {
//                    log.info("{} - cost:\n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
//                }
            }
        }

    }

    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {

        StopWatch stopWatch = new StopWatch("请求参数构建");
        try {
            stopWatch.start("traceId");
            // 添加全链路的traceId
            MdcUtil.addTraceId();
            stopWatch.stop();

            stopWatch.start("请求基本信息");
            // 手动写入一个session，借助 OnlineUserCountListener 实现在线人数实时统计
            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());

            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
            reqInfo.setHost(request.getHeader("host"));
            reqInfo.setPath(request.getPathInfo());
            if (reqInfo.getPath() == null) {
                String url = request.getRequestURI();
                int index = url.indexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                reqInfo.setPath(url);
            }
            reqInfo.setReferer(request.getHeader("referer"));
            reqInfo.setClientIp(IpUtil.getClientIp(request));
            reqInfo.setUserAgent(request.getHeader("User-Agent"));
            reqInfo.setDeviceId(getOrInitDeviceId(request, response));

            request = this.wrapperRequest(request, reqInfo);
            stopWatch.stop();

            stopWatch.start("登录用户信息");
            // 初始化登录信息
            globalInitService.initLoginUser(reqInfo);
            stopWatch.stop();

            ReqInfoContext.addReqInfo(reqInfo);
//            stopWatch.start("pv/uv站点统计");
//            // 更新uv/pv计数
//            AsyncUtil.execute(() -> SpringUtil.getBean(SitemapServiceImpl.class).saveVisitInfo(reqInfo.getClientIp(), reqInfo.getPath()));
//            stopWatch.stop();

            stopWatch.start("回写traceId");
            // 返回头中记录traceId
            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
            stopWatch.stop();
        } catch (Exception e) {
            log.error("init reqInfo error!", e);
        } finally {
            if (!EnvUtil.isPro()) {
                log.info("{} -> 请求构建耗时: \n{}", request.getRequestURI(), stopWatch.prettyPrint(TimeUnit.MILLISECONDS));
            }
        }

        return request;
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

    /**
     * 初始化设备id
     *
     * @return
     */
    private String getOrInitDeviceId(HttpServletRequest request, HttpServletResponse response) {
        String deviceId = request.getParameter("deviceId");
        if (StringUtils.isNotBlank(deviceId) && !"null".equalsIgnoreCase(deviceId)) {
            return deviceId;
        }

        Cookie device = SessionUtil.findCookieByName(request, LoginService.USER_DEVICE_KEY);
        if (device == null) {
            deviceId = UUID.randomUUID().toString();
            if (response != null) {
                response.addCookie(SessionUtil.newCookie(LoginService.USER_DEVICE_KEY, deviceId));
            }
            return deviceId;
        }
        return device.getValue();
    }

    private HttpServletRequest wrapperRequest(HttpServletRequest request, ReqInfoContext.ReqInfo reqInfo) {
        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
            return request;
        }

        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        reqInfo.setPayload(requestWrapper.getBodyString());
        return requestWrapper;
    }
}
