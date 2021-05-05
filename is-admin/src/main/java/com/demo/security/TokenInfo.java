package com.demo.security;

import java.time.LocalDateTime;

public class TokenInfo {

    private String access_token;
    private String refresh_token;
    private String token_type;
    private Long expires_in;
    private String scope;
    private LocalDateTime expireTime;

    public TokenInfo init() {
        expireTime = LocalDateTime.now().plusSeconds(expires_in - 3);
        return this;
    }

    public boolean isExpired() {
        return expireTime.isBefore(LocalDateTime.now());
    }

    public String getAccess_token() {
      return access_token;
    }

    public void setAccess_token(String access_token) {
      this.access_token = access_token;
    }

    public String getToken_type() {
      return token_type;
    }

    public void setToken_type(String token_type) {
      this.token_type = token_type;
    }

    public Long getExpires_in() {
      return expires_in;
    }

    public void setExpires_in(Long expires_in) {
      this.expires_in = expires_in;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
      this.scope = scope;
    }

    public String getRefresh_token() {
      return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
      this.refresh_token = refresh_token;
    }

    public LocalDateTime getExpireTime() {
      return expireTime;
    }

    public void setExpireTime(LocalDateTime expireTime) {
      this.expireTime = expireTime;
    }
}
