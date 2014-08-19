/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import com.sun.xml.internal.ws.client.BindingProviderProperties;
import com.sun.xml.internal.ws.developer.JAXWSProperties;
import generated.nsiws2.com.rstyle.nsi.beans.*;
import generated.nsiws2.com.rstyle.nsi.services.NSIService;
import generated.nsiws2.com.rstyle.nsi.services.NSIServiceService;
import generated.nsiws2.com.rstyle.nsi.services.in.NSIRequestType;
import generated.nsiws2.com.rstyle.nsi.services.out.NSIResponseType;
import generated.nsiws2.ru.gosuslugi.smev.rev110801.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.*;

/*import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;*/

@Component
@Scope("singleton")
public class MskNSIService {

    public static final String TYPE_STRING = "STRING";
    private static final Logger logger = LoggerFactory.getLogger(MskNSIService.class);
    public static final String COMMENT_MANUAL_IMPORT = "{Ручной импорт из Реестров}";
    public static final String COMMENT_AUTO_IMPORT = "{Импорт из Реестров %s}";
    public static final String COMMENT_AUTO_MODIFY = "{Изменено из Реестров %s}";
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

    public static class OrgInfo {

        public String guid, number, shortName, address;

        public String getGuid() {
            return guid;
        }

        public String getNumber() {
            return number;
        }

        public String getShortName() {
            return shortName;
        }

        public String getAddress() {
            return address;
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
        policy.setReceiveTimeout(10 * 60 * 1000);
        policy.setConnectionTimeout(10 * 60 * 1000);

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

        NSIResponseType response = nsiService.searchItemsInCatalog(request); //.getQueryResults(request);
        if (response.getMessageData().getAppData() != null
                && response.getMessageData().getAppData().getGeneralResponse() != null &&
                response.getMessageData().getAppData().getGeneralResponse().getQueryResult() != null) {
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

    public List<OrgInfo> getOrgByNameAndGuid(String orgName, String orgGuid) throws Exception {
        if (StringUtils.isEmpty(orgName) && StringUtils.isEmpty(orgGuid)) {
            throw new Exception("Не указано название организации и GUID");
        }
        /*
       От Козлова
       "select item['РОУ XML/GUID Образовательного учреждения'], "+
       "item['РОУ XML/Номер учреждения'], "+
       "item['РОУ XML/Краткое наименование учреждения'], "+
       "item['РОУ XML/Официальный адрес'], "+
       "item['РОУ XML/Дата изменения (число)'] "+
       "from catalog('Реестр образовательных учреждений') "+
       "where "+
       "item['РОУ XML/Статус записи']!='Удаленный' and "+
       "item['РОУ XML/Краткое наименование учреждения'] like '"+orgGuid+"'
        */
        /*String query = "select \n" + "item['РОУ XML/GUID Образовательного учреждения'],\n"
                + "item['РОУ XML/Краткое наименование учреждения'],\n" + "item['РОУ XML/Официальный адрес'],\n"
                + "item['РОУ XML/Дата изменения (число)']\n"
                + "from catalog('Реестр образовательных учреждений') where \n"
                + "item['РОУ XML/Статус записи'] not like 'Удален%'";
        if (StringUtils.isNotEmpty(orgName)) {
            query += " and item['РОУ XML/Краткое наименование учреждения'] like '%" + orgName + "%'";
        }
        if (StringUtils.isNotEmpty(orgGuid)) {
            query += " and item['РОУ XML/GUID Образовательного учреждения']='" + orgGuid + "'";
        }*/


        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр образовательных учреждений");
        if (!StringUtils.isBlank(orgName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Краткое наименование учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue("%" + orgName + "%");
            search.setAttributeOp("like");
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(orgGuid)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("GUID Образовательного учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(orgGuid);
            search.setAttributeOp("=");
            searchPredicateInfo.addSearchPredicate(search);
        }


        List<Item> queryResults = executeQuery(searchPredicateInfo);
        LinkedList<OrgInfo> list = new LinkedList<OrgInfo>();
        for (Item i : queryResults) {
            OrgInfo orgInfo = new OrgInfo();
            for(Attribute attr : i.getAttribute()) {
                if(attr.getName().equals("Краткое наименование учреждения")) {
                    orgInfo.shortName = attr.getValue().get(0).getValue();
                    orgInfo.number = Org.extractOrgNumberFromName(orgInfo.shortName);
                }
                if (attr.getName().equals("GUID Образовательного учреждения")) {
                    orgInfo.guid = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Адрес")) {
                    orgInfo.address = attr.getValue().get(0).getValue();
                }
            }

            orgInfo.guid = orgInfo.guid == null ? null : orgInfo.guid.trim();
            orgInfo.shortName = orgInfo.shortName == null ? null : orgInfo.shortName.trim();
            orgInfo.address = orgInfo.address == null ? null : orgInfo.address.trim();

            list.add(orgInfo);
        }
        return list;
    }

    private String getGroup(String currentGroup, String initialGroup) {
        String group = currentGroup;
        if (group == null || group.trim().length() == 0) {
            group = initialGroup;
        }
        if (group != null) {
            group = group.replaceAll("[ -]", "");
        }
        return group;
    }

    public List<ImportRegisterClientsService.ExpandedPupilInfo> getPupilsByOrgGUID(Set<String> orgGuids,
            String familyName, String firstName, String secondName) throws Exception {
        List<ImportRegisterClientsService.ExpandedPupilInfo> pupils = new ArrayList<ImportRegisterClientsService.ExpandedPupilInfo>();
        int importIteration = 1;
        while (true) {
            List<ImportRegisterClientsService.ExpandedPupilInfo> iterationPupils = null;
            iterationPupils = getClientsForOrgs(orgGuids, familyName, firstName, secondName, importIteration);
            if (iterationPupils.size() > 0) {
                pupils.addAll(iterationPupils);
            } else {
                break;
            }
            importIteration++;
        }
        /// удалить неимпортируемые группы
        for (Iterator<ImportRegisterClientsService.ExpandedPupilInfo> i = pupils.iterator(); i.hasNext(); ) {
            ImportRegisterClientsService.ExpandedPupilInfo p = i.next();
            if (ImportRegisterClientsService.isPupilIgnoredFromImport(p.getGuid(), p.getGroup())) {
                i.remove();
            }
        }
        return pupils;
    }

    /*    public List<ImportRegisterClientsService.PupilInfo> getPupilsByOrgGUID(String orgGuid, String familyName, int iteration) throws Exception {
        String tbl = getNSIWorkTable ();
        String select = "select item['" + tbl + "/Фамилия'], "
        + "item['" + tbl + "/Имя'], item['" + tbl + "/Отчество'], "
        + "item['" + tbl + "/GUID'], item['" + tbl + "/Дата рождения'], "
        + "item['" + tbl + "/Текущий класс или группа'], "
        + "item['" + tbl + "/Класс или группа зачисления'] "
        + "from catalog('Реестр обучаемых') "
        + "where item['" + tbl + "/Статус записи'] not like 'Удален%' and "
        + "item['" + tbl + "/GUID образовательного учреждения'] like '"+orgGuid+"'";
        if (familyName != null && familyName.length() > 0) {
            select += " and item['" + tbl + "/Фамилия'] like '%" + familyName + "%'";
        }
        //if (updateTime != null) {
        //    select += " and  item['" + tbl + "/Дата изменения (число)']  &gt; " + (updateTime / 1000);
        //}
        List<QueryResult> queryResults = executeQuery(select, iteration);
        LinkedList<ImportRegisterClientsService.PupilInfo> list = new LinkedList<ImportRegisterClientsService.PupilInfo>();
        for (QueryResult qr : queryResults) {
            ImportRegisterClientsService.PupilInfo pupilInfo = new ImportRegisterClientsService.PupilInfo();
            pupilInfo.familyName = qr.getQrValue().get(0);
            pupilInfo.firstName = qr.getQrValue().get(1);
            pupilInfo.secondName = qr.getQrValue().get(2);
            pupilInfo.guid = qr.getQrValue().get(3);
            pupilInfo.birthDate = qr.getQrValue().get(4);
            pupilInfo.group = getGroup(qr.getQrValue().get(5), qr.getQrValue().get(6));

            pupilInfo.familyName = pupilInfo.familyName == null ? null : pupilInfo.familyName.trim();
            pupilInfo.firstName = pupilInfo.firstName == null ? null : pupilInfo.firstName.trim();
            pupilInfo.secondName = pupilInfo.secondName == null ? null : pupilInfo.secondName.trim();
            pupilInfo.guid = pupilInfo.guid == null ? null : pupilInfo.guid.trim();
            pupilInfo.group = pupilInfo.group == null ? null : pupilInfo.group.trim();

            list.add(pupilInfo);
        }
        return list;
    }*/


    public List<ImportRegisterClientsService.ExpandedPupilInfo> getClientsForOrgs(Set<String> guids, String familyName,
            String firstName, String secondName, int importIteration) throws Exception {
        /*
        От Козлова
        */
        /*String tbl = getNSIWorkTable();
        String orgFilter = "";
        if (guids != null && guids.size() > 0) {
            for (String guid : guids) {
                if (orgFilter.length() > 0) {
                    orgFilter += " or ";
                }
                orgFilter += "item['" + tbl + "/GUID образовательного учреждения']='" + guid + "'";
            }
        }

        String query = "select " +
                "item['" + tbl + "/Фамилия'], " +
                "item['" + tbl + "/Имя'], " +
                "item['" + tbl + "/Отчество'], " +
                "item['" + tbl + "/GUID'], " +
                "item['" + tbl + "/Дата рождения'], " +
                "item['" + tbl + "/Класс или группа зачисления'], " +
                "item['" + tbl + "/Дата зачисления'], " +
                "item['" + tbl + "/Дата отчисления'], " +
                "item['" + tbl + "/Текущий класс или группа'], " +
                "item['" + tbl + "/GUID образовательного учреждения'], " +
                "item['" + tbl + "/Статус записи'] " +
                "from catalog('Реестр обучаемых') " +
                "where ";
        if (orgFilter.length() > 0) {
            query += " item['" + tbl + "/Статус записи'] not like 'Удален%'";
            query += " and (" + orgFilter + ")";
            if (familyName != null && familyName.length() > 0) {
                query += " and item['" + tbl + "/Фамилия'] like '%" + familyName + "%'";
            }
            if (firstName != null && firstName.length() > 0) {
                query += " and item['" + tbl + "/Имя'] like '%" + firstName + "%'";
            }
            if (secondName != null && secondName.length() > 0) {
                query += " and item['" + tbl + "/Отчество'] like '%" + secondName + "%'";
            }
        } else { // при поиске по ФИО используем для быстроты только полное совпадение
            if (familyName != null && familyName.length() > 0) {
                query += " item['" + tbl + "/Фамилия'] = '" + familyName + "'";
            }
            if (firstName != null && firstName.length() > 0) {
                query += " and item['" + tbl + "/Имя'] = '" + firstName + "'";
            }
            if (secondName != null && secondName.length() > 0) {
                query += " and item['" + tbl + "/Отчество'] = '" + secondName + "'";
            }
        }*/

        //  Ограничение по guid'ам
        SearchPredicateInfo searchPredicateInfo = new SearchPredicateInfo();
        searchPredicateInfo.setCatalogName("Реестр обучаемых");
        String guidCase = "";
        if (guids != null && guids.size() > 0) {
            for (String guid : guids) {
                if(guidCase.length() > 0) {
                    guidCase += ", ";
                }
                guidCase += guid;
            }
        }
        if(guidCase.length() > 0) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("GUID образовательного учреждения");
            search.setAttributeType(TYPE_STRING);
            search.setAttributeValue(guidCase);
            search.setAttributeOp("in");
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  ФИО ограничения
        if(!StringUtils.isBlank(familyName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Фамилия");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + familyName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(familyName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(firstName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Имя");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + firstName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(firstName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }
        if(!StringUtils.isBlank(secondName)) {
            SearchPredicate search = new SearchPredicate();
            search.setAttributeName("Отчество");
            search.setAttributeType(TYPE_STRING);
            if(guids != null && guids.size() > 0) {
                search.setAttributeValue("%" + secondName + "%");
                search.setAttributeOp("like");
            } else {
                search.setAttributeValue(secondName);
                search.setAttributeOp("=");
            }
            searchPredicateInfo.addSearchPredicate(search);
        }

        //  Запрет на удаленных
        SearchPredicate search = new SearchPredicate();
        search.setAttributeName("Статус записи");
        search.setAttributeType(TYPE_STRING);
        search.setAttributeValue("Удален%");
        search.setAttributeOp("not like");
        searchPredicateInfo.addSearchPredicate(search);

        List<Item> queryResults = executeQuery(searchPredicateInfo, importIteration);
        LinkedList<ImportRegisterClientsService.ExpandedPupilInfo> list = new LinkedList<ImportRegisterClientsService.ExpandedPupilInfo>();
        for(Item i : queryResults) {
            ImportRegisterClientsService.ExpandedPupilInfo pupilInfo = new ImportRegisterClientsService.ExpandedPupilInfo();
            for(Attribute attr : i.getAttribute()) {
                if (attr.getName().equals("Фамилия")) {
                    pupilInfo.familyName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Имя")) {
                    pupilInfo.firstName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Отчество")) {
                    pupilInfo.secondName = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("GUID")) {
                    pupilInfo.guid = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Дата рождения")) {
                    pupilInfo.birthDate = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("Текущий класс или группа")) {
                    pupilInfo.group = attr.getValue().get(0).getValue();
                }
                /*if (attr.getName().equals("Дата зачисления")) {
                    pupilInfo.created = attr.getValue().get(0).getValue();
                }
                if (attr.getName().equals("")) {
                    pupilInfo.deleted = attr.getValue().get(0).getValue();
                }*/
                if (attr.getName().equals("GUID образовательного учреждения")) {
                    pupilInfo.guidOfOrg = attr.getValue().get(0).getValue();
                }
                /*if (attr.getName().equals("")) {
                    pupilInfo.recordState = attr.getValue().get(0).getValue();
                }*/

            }

            pupilInfo.familyName = pupilInfo.familyName == null ? null : pupilInfo.familyName.trim();
            pupilInfo.firstName = pupilInfo.firstName == null ? null : pupilInfo.firstName.trim();
            pupilInfo.secondName = pupilInfo.secondName == null ? null : pupilInfo.secondName.trim();
            pupilInfo.guid = pupilInfo.guid == null ? null : pupilInfo.guid.trim();
            pupilInfo.group = pupilInfo.group == null ? null : pupilInfo.group.trim();

            list.add(pupilInfo);
        }
        /*for (QueryResult qr : queryResults) {
            ImportRegisterClientsService.ExpandedPupilInfo pupilInfo = new ImportRegisterClientsService.ExpandedPupilInfo();
            pupilInfo.familyName = qr.getQrValue().get(0);
            pupilInfo.firstName = qr.getQrValue().get(1);
            pupilInfo.secondName = qr.getQrValue().get(2);
            pupilInfo.guid = qr.getQrValue().get(3);
            pupilInfo.birthDate = qr.getQrValue().get(4);
            pupilInfo.group = getGroup(qr.getQrValue().get(8), qr.getQrValue().get(5));
            pupilInfo.created = qr.getQrValue().get(6) != null && !qr.getQrValue().get(6).equals("");
            pupilInfo.deleted = qr.getQrValue().get(7) != null && !qr.getQrValue().get(7).equals("");
            pupilInfo.guidOfOrg = qr.getQrValue().get(9);
            pupilInfo.recordState = qr.getQrValue().get(10);

            pupilInfo.familyName = pupilInfo.familyName == null ? null : pupilInfo.familyName.trim();
            pupilInfo.firstName = pupilInfo.firstName == null ? null : pupilInfo.firstName.trim();
            pupilInfo.secondName = pupilInfo.secondName == null ? null : pupilInfo.secondName.trim();
            pupilInfo.guid = pupilInfo.guid == null ? null : pupilInfo.guid.trim();
            pupilInfo.group = pupilInfo.group == null ? null : pupilInfo.group.trim();

            list.add(pupilInfo);
        }*/
        return list;
    }


    public static String getNSIWorkTable() {
        boolean isTestingService = RuntimeContext.getInstance()
                .getOptionValueBool(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE);
        return !isTestingService ? "Реестр обучаемых линейный" : "Реестр обучаемых спецификация";
    }


    public static void setTimeouts(BindingProvider bindingProvider, Long connectTimeout, Long requestTimeout) {
        // from Java SE 6
        final String keyInternalConnectTimeout = com.sun.xml.internal.ws.developer.JAXWSProperties.CONNECT_TIMEOUT;
        final String keyInternalRequestTimeout = com.sun.xml.internal.ws.developer.JAXWSProperties.REQUEST_TIMEOUT;
        // from Java EE 6
        final String keyConnectTimeout = JAXWSProperties.CONNECT_TIMEOUT;
        final String keyRequestTimeout = JAXWSProperties.REQUEST_TIMEOUT;

        final Map<String, Object> requestContext = bindingProvider.getRequestContext();
        if (connectTimeout != null) {
            requestContext.put(keyInternalConnectTimeout, connectTimeout);
            requestContext.put(keyConnectTimeout, (int) connectTimeout.longValue());
            requestContext.put(BindingProviderProperties.CONNECT_TIMEOUT, (int) connectTimeout.longValue());
        }
        if (requestTimeout != null) {
            requestContext.put(keyInternalRequestTimeout, requestTimeout);
            requestContext.put(keyRequestTimeout, (int) requestTimeout.longValue());
            requestContext.put(BindingProviderProperties.REQUEST_TIMEOUT, (int) requestTimeout.longValue());
        }
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
