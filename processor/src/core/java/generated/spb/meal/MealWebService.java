
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package generated.spb.meal;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.1.6 in JDK 6
 * Generated source version: 2.1
 * 
 */
@WebServiceClient(name = "MealWebService", targetNamespace = "http://service.petersburgedu.ru/webservice/meal/wsdl", wsdlLocation = "META-INF/spb/MealWebService.wsdl")
public class MealWebService
    extends Service
{

    private final static String WSDL_LOCATION = "META-INF/spb/MealWebService.wsdl";
    private final static URL MEALWEBSERVICE_WSDL_LOCATION;
    private final static Logger logger = Logger.getLogger(MealWebService.class.getName());

    static {
        URL url = null;
        try {
            URL baseUrl;
            baseUrl = MealWebService.class.getResource(".");
            url = new URL(baseUrl, MealWebService.class.getClassLoader().getResource(WSDL_LOCATION).getPath());
        } catch (MalformedURLException e) {
            logger.warning("Failed to create URL for the wsdl Location: 'META-INF/spb/MealWebService.wsdl', retrying as a local file");
            logger.warning(e.getMessage());
        }
        MEALWEBSERVICE_WSDL_LOCATION = url;
    }

    public MealWebService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MealWebService() {
        super(MEALWEBSERVICE_WSDL_LOCATION, new QName("http://service.petersburgedu.ru/webservice/meal/wsdl", "MealWebService"));
    }

    @WebEndpoint(name = "pushMealPort")
    public PushMealPort getPushMealPort() {
        return super.getPort(new QName("http://service.petersburgedu.ru/webservice/meal/wsdl", "pushMealPort"), PushMealPort.class);
    }

    @WebEndpoint(name = "pushMealPort")
    public PushMealPort getPushMealPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://service.petersburgedu.ru/webservice/meal/wsdl", "pushMealPort"), PushMealPort.class, features);
    }

}
