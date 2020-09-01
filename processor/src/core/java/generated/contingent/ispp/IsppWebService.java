
/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package generated.contingent.ispp;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.ws.Action;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "IsppWebService", targetNamespace = "urn:contingent.mos.ru:ws:ispp")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
        generated.contingent.ispp.ObjectFactory.class,
        generated.contingent.service.ObjectFactory.class
})
public interface IsppWebService {


    /**
     * 
     * @param serviceHeader
     * @param parameters
     * @return
     *     returns ru.mos.contingent.ws.ispp.SetBenefitsResponseSmall
     */
    @WebMethod(action = "urn:setBenefits")
    @WebResult(name = "setBenefitsResponseSmall", targetNamespace = "urn:contingent.mos.ru:ws:ispp", partName = "result")
    @Action(input = "urn:setBenefits", output = "urn:contingent.mos.ru:ws:ispp:IsppWebService:setBenefitsResponse")
    public SetBenefitsResponseSmall setBenefits(
            @WebParam(name = "setBenefits", targetNamespace = "urn:contingent.mos.ru:ws:ispp", partName = "parameters") SetBenefits parameters,
            @WebParam(name = "ServiceHeader", targetNamespace = "urn:contingent.mos.ru:ws:ispp", header = true, partName = "ServiceHeader") ServiceHeader serviceHeader);

}