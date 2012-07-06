/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.chronopay;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.12.2009
 * Time: 11:45:54
 * To change this template use File | Settings | File Templates.
 */
public class ChronopayProtocolResponse {

    private final int responseCode;

    public ChronopayProtocolResponse(int responseCode) {
        this.responseCode = responseCode;
    }

    public static ChronopayProtocolResponse success() {
        return new ChronopayProtocolResponse(0);
    }

    public static ChronopayProtocolResponse fail() {
        return new ChronopayProtocolResponse(1);
    }

    public static ChronopayProtocolResponse badRequest() {
        return new ChronopayProtocolResponse(2);
    }

    public void writeTo(HttpServletResponse response) throws IOException {
        switch (responseCode) {
            case 0:
                response.getOutputStream().write("200 OK".getBytes("ASCII"));
                break;
            case 2:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                break;
            default:
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
