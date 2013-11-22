/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

//import generated.nsiws.NSIResponseType;
import generated.opc.ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import junit.framework.TestCase;

//import ru.axetta.soap.smev.*;
import ru.axetta.soap.smev.pfr.*;
import ru.axetta.soap.test.ClientSummary;
import ru.axetta.soap.test.Data;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;

public class TestSMEVTest extends TestCase {
/*    public void testStore() throws Exception {
        //String serviceUrl= "http://188.254.16.92:7777/gateway/services/SID0003022";
        String serviceUrl= "http://127.0.0.1:7777/gateway/services/SID0003022";


        MsgExampleService service = new MsgExampleService(new URL(serviceUrl+"?wsdl"), new QName("http://smev.gosuslugi.ru/MsgExample/", "MsgExampleService"));
        MsgExamplePort port = service.getMsgExamplePort();

        BindingProvider provider = (BindingProvider)port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

        List<Handler> handlerChain = new ArrayList<Handler>();
              handlerChain.add(new SignHandlerTest.LogMessageHandler());

        ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);

        HeaderType smevHeader = new HeaderType();
        //smevHeader.setMessageId("123");
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        //smevHeader.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        SyncRequestType parameters = new SyncRequestType();
        MessageType messageType = new MessageType();
        OrgExternalType sender = new OrgExternalType();
        sender.setCode("1"); sender.setName("Foiv1");
        messageType.setSender(sender);
        OrgExternalType recipient = new OrgExternalType();
        recipient.setCode("2"); recipient.setName("Foiv2");
        OrgExternalType originator = new OrgExternalType();
        originator.setCode("3"); originator.setName("Foiv3");
        messageType.setSender(sender);
        messageType.setRecipient(recipient);
        messageType.setOriginator(originator);
        messageType.setTypeCode("1");
        messageType.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        parameters.setMessage(messageType);
        MessageDataType messageDataType = new MessageDataType();
        AppDataType appDataType = new AppDataType();
        appDataType.getAny().add(getContentDocument());
        appDataType.getAny().add(getContentDocument2());
        messageDataType.setAppData(appDataType);
        parameters.setMessageData(messageDataType);
        port.syncReq(smevHeader, parameters);
    }*/

    public void testPfr() throws Exception {
        //String serviceUrl= "http://188.254.16.92:7777/gateway/services/SID0003022";
        String serviceUrl= "http://127.0.0.1:7777/gateway/services/SID0003324";


        ServicePFRService service = new ServicePFRService(new URL(serviceUrl+"?wsdl"), new QName("http://service.pfr.socit.ru", "ServicePFRService"));
        ServicePFR port = service.getServicePFR();

        BindingProvider provider = (BindingProvider)port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceUrl);

        List<Handler> handlerChain = new ArrayList<Handler>();
              handlerChain.add(new SignHandlerTest.LogMessageHandler());

        ((BindingProvider)port).getBinding().setHandlerChain(handlerChain);

        MessageType messageType = new MessageType();
        messageType.setTypeCode("GSRV");
        messageType.setStatus(StatusType.REQUEST);
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTime(new Date());
        messageType.setDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        messageType.setExchangeType("2");
        OrgExternalType sender = new OrgExternalType();
        sender.setCode("PFRF01001"); sender.setName("Пенсионный фонд РФ");
        OrgExternalType recipient = new OrgExternalType();
        recipient.setCode("PFRF01001"); recipient.setName("Пенсионный фонд РФ");
        OrgExternalType originator = new OrgExternalType();
        originator.setCode("PFRF01001"); originator.setName("Пенсионный фонд РФ");
        messageType.setSender(sender);
        messageType.setOriginator(originator);
        messageType.setRecipient(recipient);
        //smevHeader.setMessageId("123");
        //smevHeader.setTimeStamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
        MessageDataType messageDataType = new MessageDataType();
        AppDataType appDataType = new AppDataType();
        
        appDataType.setType(Type.REQUEST);
        Properties properties = new Properties();
        properties.getProperty().add(createProperty("TYPE_QUERY", "ЗАПРОС_ДАННЫХ_О_ЗЛ_ПО_СНИЛС"));
        properties.getProperty().add(createProperty("SNILS", "133-463-281 42"));
        //properties.getProperty().add(createProperty("TYPE_QUERY", "СВЕДЕНИЯ_О_СООТВЕТСТВИИ_СНИЛС_ФАМИЛЬНО_ИМЕННОЙ_ГРУППЕ"));
        /*properties.getProperty().add(createProperty("SECOND_NAME", "МОЧАЛОВ"));
        properties.getProperty().add(createProperty("FIRST_NAME", "АНТОН"));
        properties.getProperty().add(createProperty("PATRONYMIC", "ПАВЛОВИЧ"));
        properties.getProperty().add(createProperty("SNILS", "124-893-747 93"));
        /*properties.getProperty().add(createProperty("SECOND_NAME", "ДАДАГОВ"));
        properties.getProperty().add(createProperty("FIRST_NAME", "РАМЗАН"));
        properties.getProperty().add(createProperty("PATRONYMIC", "АДНАНОВИЧ"));
        properties.getProperty().add(createProperty("SNILS", "156-954-660 14"));

        /*properties.getProperty().add(createProperty("SECOND_NAME", "ПЕТИНА"));
        properties.getProperty().add(createProperty("FIRST_NAME", "ЕЛЕНА"));
        properties.getProperty().add(createProperty("PATRONYMIC", "ВЛАДИМИРОВНА"));
        properties.getProperty().add(createProperty("SNILS", "027-733-198 62"));           */
        appDataType.setProperties(properties);
        messageDataType.setAppData(appDataType);
        port.process(new Holder<MessageType>(messageType), new Holder<MessageDataType>(messageDataType));
    }

    private Property createProperty(String n, String v) {
        Property p = new Property();
        p.setPropertyName(n);
        p.setPropertyValue(v);
        return p;
    }


    Element getContentDocument() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Data.class.getPackage().getName());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final StringWriter stringWriter = new StringWriter();
        Data d = new Data();
        ClientSummary cs  =new ClientSummary();
        cs.setBalance(100L);
        cs.setOverdraftLimit(11L);
        d.setClientSummary(cs);

        marshaller.marshal(d, stringWriter);
        
        final String xmlContents = stringWriter.toString();
        System.out.println(String.format("Content:\n%s", xmlContents));
        
        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document document = documentBuilder.parse(new InputSource(new StringReader(xmlContents)));

        final Document document1 = documentBuilder.newDocument();
        //final Element test = document1.createElement("test");
        //test.appendChild(document1.adoptNode(document.getDocumentElement()));
        document1.appendChild(document1.adoptNode(document.getDocumentElement()));

        return document1.getDocumentElement();
    }
    Element getContentDocument2() throws Exception {
        JAXBContext jc = JAXBContext.newInstance(Data.class.getPackage().getName());
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        final StringWriter stringWriter = new StringWriter();
        Data d = new Data();
        ClientSummary cs  =new ClientSummary();
        cs.setBalance(200L);
        cs.setOverdraftLimit(22L);
        d.setClientSummary(cs);

        marshaller.marshal(d, stringWriter);

        final String xmlContents = stringWriter.toString();
        System.out.println(String.format("Content:\n%s", xmlContents));

        final DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        final Document document = documentBuilder.parse(new InputSource(new StringReader(xmlContents)));

        final Document document1 = documentBuilder.newDocument();
        //final Element test = document1.createElement("test");
        //test.appendChild(document1.adoptNode(document.getDocumentElement()));
        document1.appendChild(document1.adoptNode(document.getDocumentElement()));

        return document1.getDocumentElement();
    }
}
