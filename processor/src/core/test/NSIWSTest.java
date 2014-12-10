/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

import com.gemplus.opentools.util.RegExp;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.junit.Test;

import javax.xml.bind.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.sun.xml.internal.ws.developer.JAXWSProperties;
import generated.nsiws2.com.rstyle.nsi.beans.Context;
import generated.nsiws2.com.rstyle.nsi.beans.QueryResult;
import generated.nsiws2.com.rstyle.nsi.services.NSIService;
import generated.nsiws2.com.rstyle.nsi.services.NSIServiceService;
import generated.nsiws2.com.rstyle.nsi.services.in.NSIRequestType;
import generated.nsiws2.com.rstyle.nsi.services.out.NSIResponseType;
import generated.nsiws2.ru.gosuslugi.smev.rev110801.*;

public class NSIWSTest {

    @Test 
    public void testListCatalogues() throws Exception {
        /*
        JAXBContext jc = JAXBContext.newInstance("generated.nsiws");
        Unmarshaller um = jc.createUnmarshaller();
        Object x= um.unmarshal(new FileInputStream("C:\\xxxxx"));
        NSIResponseType r = (NSIResponseType)((JAXBElement)x).getValue();
        r.getMessageData().getAppData().setGeneralResponse(new GeneralResponse());
        Marshaller m  = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(r, System.out);
        System.out.println(x);
        */



        String url = "http://10.126.216.2:4422/em/nsiws/v2/services/NSIService";
        String wsdl = "http://10.126.216.2:4422/em/nsiws/v2/services/NSIService/WEB-INF/wsdl/NSIService.wsdl";
        NSIServiceService nsiServicePort = new NSIServiceService(new URL(wsdl.toLowerCase().contains("wsdl")?wsdl:(wsdl + "?wsdl")),
                new QName("http://rstyle.com/nsi/services", "NSIServiceService"));
        NSIService nsiService = nsiServicePort.getNSIService();
        NSIRequestType request = new NSIRequestType();
        BindingProvider provider = (BindingProvider)nsiService;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        provider.getRequestContext().put("set-jaxb-validation-event-handler", false);
        provider.getRequestContext().put("schema-validation-enabled", false);

        System.setProperty("set-jaxb-validation-event-handler", "false");
        System.setProperty("schema-validation-enabled", "false");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.ws.assembler.client", "true");

        OrgExternalType recipient = new OrgExternalType();
        recipient.setName("NSI");
        recipient.setCode("NSI");
        request.setMessage(new MessageType());
        request.getMessage().setRecipient(recipient);
        OrgExternalType originator = new OrgExternalType();
        originator.setName("ISPP");
        originator.setCode("ISPP");
        request.getMessage().setOriginator(originator);
        request.getMessage().setTypeCode(TypeCodeType.GSRV);
        request.getMessage().setStatus(StatusType.REQUEST);
        request.getMessage().setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
        request.getMessage().setExchangeType("test_ex_type");
        request.getMessage().setServiceCode("test service code");
        request.setMessageData(new ExtMessageDataType());
        request.getMessageData().setAppData(new ExtAppDataType());
        request.getMessageData().getAppData().setContext(new Context());
        request.getMessageData().getAppData().getContext().setUser("PP_SOAP");
        request.getMessageData().getAppData().getContext().setPassword("6Iport87a");
        request.getMessageData().getAppData().getContext().setCompany("dogm_nsi");
        request.getMessageData().getAppData().setFrom(new Long(1 + 300 * (1 - 1)));
        request.getMessageData().getAppData().setLimit(300);
        request.getMessageData().getAppData().setQuery(
                "select item['Реестр обучаемых линейный/Фамилия'], "
                    + "item['Реестр обучаемых линейный/Имя'], "
                    + "item['Реестр обучаемых линейный/Отчество'], "
                    + "item['Реестр обучаемых линейный/GUID'], "
                    + "item['Реестр обучаемых линейный/Дата рождения'], "
                    + "item['Реестр обучаемых линейный/Класс или группа зачисления'], "
                    + "item['Реестр обучаемых линейный/Дата зачисления'], "
                    + "item['Реестр обучаемых линейный/Дата отчисления'], "
                    + "item['Реестр обучаемых линейный/Класс/Название'], "
                    + "item['Реестр обучаемых линейный/GUID образовательного учреждения'], "
                    + "item['Реестр обучаемых линейный/Статус записи'] "
                + "from catalog('Реестр обучаемых') "
                + "where  item['Реестр обучаемых линейный/Статус записи'] not like 'Удален%' and "
                        + "(item['Реестр обучаемых линейный/GUID образовательного учреждения']='1636AFC2-3235-466D-B640-E3BFBC0034A9')");

        NSIResponseType response = nsiService.getQueryResults(request);
        List<QueryResult> queryResults = response.getMessageData().getAppData().getGeneralResponse().getQueryResult();
        for (QueryResult qr : queryResults) {
            List<String> values = qr.getQrValue();
            for (String s : values) {
                System.out.println(s);
            }
        }
    }
}
