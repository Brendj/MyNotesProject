
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.contingent;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "IsppWebService", targetNamespace = "urn:contingent.mos.ru:ws:ispp")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface IsppWebService {


    /**
     * 
     * @param serviceHeader
     * @param parameters
     * @return
     *     returns ru.mos.contingent.ws.ispp.SetBenefitsResponse1
     */
    @WebMethod(action = "urn:setBenefits")
    @WebResult(name = "setBenefitsResponse", targetNamespace = "urn:contingent.mos.ru:ws:ispp", partName = "result")
    public SetBenefitsResponse1 setBenefits(
            @WebParam(name = "setBenefits", targetNamespace = "urn:contingent.mos.ru:ws:ispp", partName = "parameters") SetBenefits parameters,
            @WebParam(name = "ServiceHeader", targetNamespace = "urn:contingent.mos.ru:ws:ispp", header = true, partName = "ServiceHeader") IsppHeaders serviceHeader);

}
