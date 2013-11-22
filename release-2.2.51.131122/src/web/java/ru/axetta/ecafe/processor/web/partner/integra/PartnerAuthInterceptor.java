/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class PartnerAuthInterceptor extends HandlerInterceptorAdapter {
    List<String> partnerList;

    public void setPartnerList(List<String> partnerList) {
        this.partnerList = partnerList;
    }
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        String authToken = request.getParameter("authtoken");
        if (!checkToken(authToken)) {
            response.sendError(401);
            return false;
        }
        else {
            return true;
        }
    }

    private boolean checkToken(String authToken) {
        if (authToken==null) return false;
        for (String s : partnerList) {
            if (s.equals(authToken)) return true;
        }
        return false;
    }
}