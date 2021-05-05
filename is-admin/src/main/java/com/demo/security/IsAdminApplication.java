package com.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@EnableZuulProxy
@SpringBootApplication
public class IsAdminApplication {

    private RestTemplate restTemplate = new RestTemplate();
    private Logger logger = LoggerFactory.getLogger(getClass());

    public static void main(String[] args) {
        SpringApplication.run(IsAdminApplication.class, args);
    }

    @GetMapping("/oauth/callback")
    public void callback(@RequestParam(required = true) String code, String state, HttpServletRequest request, HttpServletResponse servletResponse) throws IOException {
        logger.info(state);

        String oauthServiceUrl = "http://gateway.demo.com:9070/token/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBasicAuth("admin", "123456");
        MultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("code", code);
        paramMap.add("grant_type", "authorization_code");
        //需要和第一次申请code的redirect_url一致 并且需要是client允许跳转的url
        paramMap.add("redirect_uri", "http://admin.demo.com:8080/oauth/callback");
        HttpEntity<MultiValueMap> entity = new HttpEntity<>(paramMap, headers);

        ResponseEntity<TokenInfo> response = restTemplate.exchange(oauthServiceUrl, HttpMethod.POST, entity, TokenInfo.class);
        request.getSession().setAttribute("token", response.getBody().init());

        //这里应该根据state进行跳转
        servletResponse.sendRedirect("/");
    }

    @PostMapping("/logout")
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @GetMapping("/me")
    public TokenInfo me(HttpServletRequest request) {
        return (TokenInfo) request.getSession().getAttribute("token");
    }
}
