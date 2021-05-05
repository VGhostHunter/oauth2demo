package com.demo.security;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Component
public class CookieTokenFilter extends ZuulFilter {

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        return StringUtils.isBlank(requestContext.getZuulRequestHeaders().get("authorization"));
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        HttpServletResponse response = requestContext.getResponse();

        String accessToken = getCookie("demo_access_token");
        if(StringUtils.isNotBlank(accessToken)) {
            requestContext.addZuulRequestHeader("Authorization", "bearer " + accessToken);
        } else {
            String refreshToken = getCookie("demo_refresh_token");
            if(StringUtils.isNotBlank(refreshToken)) {
                //刷新令牌 刷新令牌的时候会同时刷新refresh_token
                String oauthServiceUrl = "http://gateway.demo.com:9070/token/oauth/token";

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
                headers.setBasicAuth("admin", "123456");
                MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
                paramMap.add("grant_type", "refresh_token");
                //需要和第一次申请code的redirect_url一致 并且需要是client允许跳转的url
                paramMap.add("refresh_token", refreshToken);
                HttpEntity<MultiValueMap> entity = new HttpEntity<>(paramMap, headers);

                try {
                    ResponseEntity<TokenInfo> newToken = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
                    requestContext.addZuulRequestHeader("Authorization", "bearer " + newToken.getBody().getAccess_token());

                    Cookie accessTokenCookie = new Cookie("demo_access_token", newToken.getBody().getAccess_token());
                    accessTokenCookie.setMaxAge(newToken.getBody().getExpires_in().intValue());
                    accessTokenCookie.setDomain("demo.com");
                    accessTokenCookie.setPath("/");
                    response.addCookie(accessTokenCookie);

                    Cookie refreshTokenCookie = new Cookie("demo_refresh_token", newToken.getBody().getRefresh_token());
                    refreshTokenCookie.setMaxAge(2592000);
                    refreshTokenCookie.setDomain("demo.com");
                    refreshTokenCookie.setPath("/");
                    response.addCookie(refreshTokenCookie);
                } catch (Exception e) {
                    requestContext.setSendZuulResponse(false);
                    requestContext.setResponseStatusCode(500);
                    requestContext.setResponseBody("{\"message\":\"refresh fail\"}");
                    requestContext.getResponse().setContentType("application/json");
                }
            }
        }
        return null;
    }

    private String getCookie(String token) {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();

        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        return Arrays.stream(cookies)
            .filter(x -> StringUtils.equals(token, x.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
    }
}
