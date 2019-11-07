/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import java.util.*;

@WebService
public class EZDControllerSOAP extends HttpServlet {

    @WebMethod(operationName = "requestscomplex")
    public List<ResponseToEZDResult> requestscomplex(@WebParam(name = "orders") List<ResponseFromEzd> orders) {
        Result result;
        List<ResponseToEZDResult> responseToEZDResults = new ArrayList<>();
        for (ResponseFromEzd responseFromEzd : orders) {
            result = GeneralRequestMetod
                    .requestsComplexForOne(responseFromEzd.getGuidOrg(), responseFromEzd.getGroupName(),
                            responseFromEzd.getDate(), responseFromEzd.getUserName(), responseFromEzd.getIdOfComplex(),
                            responseFromEzd.getComplexName(), responseFromEzd.getCount());
            if (result.getErrorCode().equals(ResponseCodes.RC_OK.getCode().toString()) && result.getErrorMessage()
                    .equals(ResponseCodes.RC_OK.toString()))
            {
                ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();
                responseToEZDResult.setErrorCode(ResponseCodes.RC_OK.getCode().toString());
                responseToEZDResult.setErrorMessage(ResponseCodes.RC_OK.toString());
                responseToEZDResults.add(responseToEZDResult);
            }
            else
            {
                ResponseToEZDResult responseToEZDResult = new ResponseToEZDResult();

                responseToEZDResult.setGuidOrg(responseFromEzd.getGuidOrg());
                responseToEZDResult.setGroupName(responseFromEzd.getGroupName());
                responseToEZDResult.setDate(responseFromEzd.getDate());
                responseToEZDResult.setUserName(responseFromEzd.getUserName());
                responseToEZDResult.setIdOfComplex(responseFromEzd.getIdOfComplex());
                responseToEZDResult.setComplexName(responseFromEzd.getComplexName());
                responseToEZDResult.setCount(responseFromEzd.getCount());
                responseToEZDResult.setErrorCode(result.getErrorCode());
                responseToEZDResult.setErrorMessage(result.getErrorMessage());
                responseToEZDResults.add(responseToEZDResult);
            }
        }
        return responseToEZDResults;
    }
}



