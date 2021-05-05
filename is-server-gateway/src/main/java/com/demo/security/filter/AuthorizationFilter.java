package com.demo.security.filter;

import com.demo.security.pojo.TokenInfo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
public class AuthorizationFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.sendZuulResponse();
    }

    @Override
    public Object run() throws ZuulException {
        logger.info("authorization start");

        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        if(isNeedAuth(request)) {
            TokenInfo tokenInfo = (TokenInfo) request.getAttribute("tokenInfo");

            if(tokenInfo != null && tokenInfo.isActive()) {
                if(!hasPermission(tokenInfo, request)) {
                    handleError(HttpStatus.FORBIDDEN, requestContext);
                }
            } else {
                handleError(HttpStatus.UNAUTHORIZED, requestContext);
            }
        }

        return null;
    }

    private boolean hasPermission(TokenInfo tokenInfo, HttpServletRequest request) {
//        return RandomUtils.nextBoolean();
        return true;
    }

    private void handleError(HttpStatus status, RequestContext requestContext) {
        //TODO update auditLog
        logger.info("audit log update{}", status.value());

        requestContext.getResponse().setContentType("application/json");
        requestContext.setResponseStatusCode(status.value());
        requestContext.setResponseBody("{\"message\":\"auth fail\"}");
        //这里设置 false 过滤器就返回了 就不会往后走了
        requestContext.setSendZuulResponse(false);
    }

    private boolean isNeedAuth(HttpServletRequest request) {
        if(StringUtils.startsWith(request.getRequestURI(), "/token")) {
            return false;
        }
        return true;
    }
}
