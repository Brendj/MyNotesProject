/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.soap;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PosPayment;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.PosResPaymentRegistry;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 23.10.13
 * Time: 17:46
 * To change this template use File | Settings | File Templates.
 */
@WebService
public interface POSPaymentController {

    @WebMethod(operationName = "test")
    public String test(@WebParam(name = "orgId") Long orgId);

    @WebMethod(operationName = "createOrder")
    public PosResPaymentRegistry createOrder(@WebParam(name = "idOfOrg") Long idOfOrg, @WebParam(name = "payment") List<PosPayment> payment);
}
