
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package generated.spb.meal;

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
@WebService(name = "pushMealPort", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface PushMealPort {

    @WebMethod(action = "pushZipFile")
    @WebResult(name = "pushResponse", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl", partName = "result")
    public PushResponse pushZipFile(
        @WebParam(name = "pushZipFileRequest", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl", partName = "data")
        String data);


    @WebMethod(action = "pushData")
    @WebResult(name = "pushResponse", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl", partName = "result")
    public PushResponse pushData(
        @WebParam(name = "mealData", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl", partName = "data")
        MealData data);

}
