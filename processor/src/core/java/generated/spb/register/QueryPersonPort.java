
package generated.spb.register;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * 
 *             ������ ���������� � ������� �� �������
 *         
 * 
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebService(name = "queryPersonPort", targetNamespace = "http://85.143.161.170:8080/webservice/food_benefits_full/wsdl")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface QueryPersonPort {


    /**
     * 
     * @param query
     * @return
     *     returns generated.spb.register2.Schools
     */
    @WebMethod
    @WebResult(name = "schools", targetNamespace = "http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", partName = "result")
    public Schools pushData(
        @WebParam(name = "query", targetNamespace = "http://85.143.161.170:8080/webservice/food_benefits_full/wsdl", partName = "query")
        Query query);

}
