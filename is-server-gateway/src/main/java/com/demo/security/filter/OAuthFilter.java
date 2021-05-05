package com.demo.security.filter;

import com.demo.security.pojo.TokenInfo;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;

@Component
public class OAuthFilter extends ZuulFilter {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public String filterType() {
        //pre post error route
        return "pre";
    }

    @Override
    public int filterOrder() {
        //执行顺序 这里是认证 前面应该有个限流
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        //写一段判断逻辑 判断过滤器是否起作用
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //认证逻辑
        logger.info("oauth start");

        //获取请求和响应
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        //发往认证服务器的请求不需要做身份认证
        if(StringUtils.startsWith(request.getRequestURI(), "/token")) {
            return null;
        }

        String authHeader = request.getHeader("Authorization");
        if(StringUtils.isBlank(authHeader)) {
            return null;
        }

        //这个过滤器只负责Oauth的认证 如果不是bearer则跳过
        if(! StringUtils.startsWithIgnoreCase(authHeader, "bearer ")) {
            return null;
        }

        try {
            TokenInfo info = getTokenInfo(authHeader);
            request.setAttribute("tokenInfo", info);
        } catch (Exception e) {
            logger.error("get token Info fail{}", e.getMessage());
        }

        return null;
    }

    private TokenInfo getTokenInfo(String authHeader) {
        String token = StringUtils.substringAfter(authHeader, "bearer ");
        String oauthServiceUrl = "http://auth.demo.com:9090/oauth/check_token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("gateway", "123456");
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("token", token);
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(paramMap, headers);

        ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);

        logger.info("token info: " + response.getBody());

        return response.getBody();
    }
}
