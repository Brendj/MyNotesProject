/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.RequestDTO;

import ru.axetta.ecafe.processor.web.partner.schoolapi.Response.Result;

import org.codehaus.jackson.annotate.JsonProperty;

public class JwtLoginDTO extends Result {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("refresh_token")
    private String refreshToken;

    @JsonProperty("need_change_password")
    private Boolean needChangePassword;

    @JsonProperty("need_enter_sms_code")
    private Boolean needEnterSmsCode;

    public JwtLoginDTO(String accessToken, String refreshToken,
            Boolean needChangePassword, Boolean needEnterSmsCode){
        super(0, "OK");
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.needChangePassword = needChangePassword;
        this.needEnterSmsCode = needEnterSmsCode;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Boolean getNeedChangePassword() {
        return needChangePassword;
    }

    public void setNeedChangePassword(Boolean needChangePassword) {
        this.needChangePassword = needChangePassword;
    }

    public Boolean getNeedEnterSmsCode() {
        return needEnterSmsCode;
    }

    public void setNeedEnterSmsCode(Boolean needEnterSmsCode) {
        this.needEnterSmsCode = needEnterSmsCode;
    }
}
