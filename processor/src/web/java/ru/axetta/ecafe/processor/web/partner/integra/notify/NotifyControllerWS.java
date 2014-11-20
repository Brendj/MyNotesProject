/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.notify;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ResultConst;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;

/**
 * User: shamil
 * Date: 20.11.14
 * Time: 17:11
 */
@WebService
public class NotifyControllerWS extends HttpServlet implements NotifyController {
    @Override
    public NotifyResult notify(
            @WebParam(name = "accountN") long accountNumber,
            @WebParam(name = "eventCode") int eventCode) {
        NotifyResult result = new NotifyResult();
        result.resultCode = ResultConst.CODE_OK;
        result.description = ResultConst.DESCR_OK;

        return result;
    }
}
