/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import generated.nsiws2.com.rstyle.nsi.beans.Context;
import generated.nsiws2.com.rstyle.nsi.beans.Item;
import generated.nsiws2.com.rstyle.nsi.beans.SearchPredicate;
import generated.nsiws2.com.rstyle.nsi.services.NSIService;
import generated.nsiws2.com.rstyle.nsi.services.NSIServiceService;
import generated.nsiws2.com.rstyle.nsi.services.in.NSIRequestType;
import generated.nsiws2.com.rstyle.nsi.services.out.NSIResponseType;
import generated.nsiws2.ru.gosuslugi.smev.rev110801.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

/*import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;*/


public class MskNSIService {

    public static final String TYPE_STRING = "STRING";
    private static final Logger logger = LoggerFactory.getLogger(MskNSIService.class);
    public static final String COMMENT_MANUAL_IMPORT = "{Ручной импорт из Реестров}";
    public static final String COMMENT_AUTO_IMPORT = "{Импорт из Реестров %s}";
    public static final String COMMENT_AUTO_MODIFY = "{Изменено из Реестров %s}";
    public static final String COMMENT_AUTO_CREATE = "{Создано из Реестров %s}";
    public static final String COMMENT_AUTO_DELETED = "{Исключен по Реестру %s}";
    public static final String REPLACEMENT_REGEXP = "\\{[^}]* Реестр[^}]*\\}";
    public static int SERVICE_ROWS_LIMIT = 300;

    public static class Config {

        public static String getUrl() {
            return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_URL);
        }

        public static String getWsdl() {
            return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_WSDL_URL);
        }

        public static String getUser() {
            return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_USER);
        }

        public static String getPassword() {
            return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_PASSWORD);
        }

        public static String getCompany() {
            return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_MSK_NSI_COMPANY);
        }

    }

    NSIServiceService nsiServicePort;
    NSIService nsiService;

    public void init() throws Exception {
        if (nsiService != null) {
            return;
        }
        String wsdl = Config.getWsdl();
        logger.info("Trying NSI service: " + wsdl);
        nsiServicePort = new NSIServiceService(new URL(wsdl.toLowerCase().contains("wsdl")?wsdl:(wsdl + "?wsdl")),
                new QName("http://rstyle.com/nsi/services", "NSIServiceService"));
        nsiService = nsiServicePort.getNSIService();
    }

    public List<Item> executeQuery(SearchPredicateInfo searchPredicateInfo) throws Exception {
        return executeQuery(searchPredicateInfo, -1);
    }

    public List<Item> executeQuery(SearchPredicateInfo searchPredicateInfo, int importIteration) throws Exception {
        init();

        String url = Config.getUrl();
        NSIRequestType request = new NSIRequestType();
        BindingProvider provider = (BindingProvider) nsiService;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        provider.getRequestContext().put("set-jaxb-validation-event-handler", false);
        provider.getRequestContext().put("schema-validation-enabled", false);

        System.setProperty("set-jaxb-validation-event-handler", "false");
        System.setProperty("schema-validation-enabled", "false");
        System.setProperty("com.sun.xml.internal.ws.transport.http.HttpAdapter.dump", "true");
        System.setProperty("com.sun.xml.ws.assembler.client", "true");
        /*provider.getRequestContext().put("com.sun.xml.ws.request.timeout", 15000);
        setTimeouts (provider, new Long (60000), new Long (180000));*/
        //provider.getRequestContext().put("jaxb-validation-event-handle", null);
        Client client = ClientProxy.getClient(nsiService);
        HTTPConduit conduit = (HTTPConduit) client.getConduit();
        HTTPClientPolicy policy = conduit.getClient();
        policy.setReceiveTimeout(15 * 60 * 1000);
        policy.setConnectionTimeout(15 * 60 * 1000);

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
        request.getMessageData().getAppData().getContext().setUser(Config.getUser());
        request.getMessageData().getAppData().getContext().setPassword(Config.getPassword());
        request.getMessageData().getAppData().getContext().setCompany(Config.getCompany());
        buildSearchPredicate(request, searchPredicateInfo);
        if (importIteration >= 0) {
            request.getMessageData().getAppData().setFrom(new Long(1 + SERVICE_ROWS_LIMIT * (importIteration - 1)));
            request.getMessageData().getAppData().setLimit(SERVICE_ROWS_LIMIT);
        }
        //request.getMessageData().getAppData().setQuery(queryText);
        //Если нужно логирование запросов
        final SOAPLoggingHandler soapLoggingHandler = new SOAPLoggingHandler();
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(soapLoggingHandler);
        ((BindingProvider) nsiService).getBinding().setHandlerChain(handlerChain);
        NSIResponseType response = nsiService.searchItemsInCatalog(request); //.getQueryResults(request);
        if (response.getMessageData().getAppData() != null
                && response.getMessageData().getAppData().getGeneralResponse() != null &&
                response.getMessageData().getAppData().getGeneralResponse().getQueryResult() != null) {
            if (response.getMessageData().getAppData().getGeneralResponse().getError() != null) {
                logger.error("Error got from nsi, try again");
                return null;
            }
            return response.getMessageData().getAppData().getGeneralResponse().getItem();
        } else {
            JAXBContext jc = JAXBContext.newInstance(NSIResponseType.class.getPackage().getName());
            Marshaller m = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            m.marshal(response, bos);
            throw new Exception("Ошибка при получении данных из сервиса НСИ. Ответ: " + bos.toString());
        }
    }



    public static String getNSIWorkTable() {
        boolean isTestingService = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE);
        return !isTestingService ? "Реестр обучаемых линейный" : "Реестр обучаемых спецификация";
    }


    public void buildSearchPredicate(NSIRequestType request, SearchPredicateInfo searchPredicateInfo) {
        request.getMessageData().getAppData().setCatalogName(searchPredicateInfo.getCatalogName());
        List<SearchPredicate> searchList = request.getMessageData().getAppData().getSearchPredicate();

        if(searchPredicateInfo.getSearchPredicates() != null) {
            searchList.addAll(searchPredicateInfo.getSearchPredicates());
        }
    }

    protected class SearchPredicateInfo {
        private String catalogName;
        private List<SearchPredicate> searchPredicates;

        public String getCatalogName() {
            return catalogName;
        }

        public void setCatalogName(String catalogName) {
            this.catalogName = catalogName;
        }

        public List<SearchPredicate> getSearchPredicates() {
            return searchPredicates;
        }

        public void addSearchPredicate(SearchPredicate searchPredicate) {
            if(searchPredicates == null) {
                searchPredicates = new ArrayList<SearchPredicate>();
            }
            searchPredicates.add(searchPredicate);
        }
    }
}
