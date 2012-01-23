/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.12.2009
 * Time: 11:45:54
 * To change this template use File | Settings | File Templates.
 */
public class ProtocolResponse {

    private final int responseCode;

    public ProtocolResponse(int responseCode) {
        this.responseCode = responseCode;
    }

    public static ProtocolResponse success() {
        return new ProtocolResponse(0);
    }

    public static ProtocolResponse fail() {
        return new ProtocolResponse(1);
    }

    public static ProtocolResponse badRequest() {
        return new ProtocolResponse(2);
    }

    public void writeTo(HttpServletResponse response) throws IOException {
        switch (responseCode) {
            case 0:
                response.getOutputStream().write("OK".getBytes("ASCII"));
                break;
            case 2:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
            default:
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
