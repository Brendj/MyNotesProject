/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.nsi;

import generated.nsiws.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;

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
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

@Component
@Scope("singleton")
public class MskNSIService {
    private static final Logger logger = LoggerFactory.getLogger(MskNSIService.class);

    public static class Config {
        private static final String PARAM_BASE = ".nsi.";
        private static final String PARAM_SYNC_SCHEDULE ="sync.schedule";
        private static final String PARAM_URL ="url";
        private static final String PARAM_USER = "user";
        private static final String PARAM_PASSWORD = "pass";
        private static final String PARAM_COMPANY = "company";

        public Config(Properties properties, String paramBaseName) throws Exception {
            paramBaseName+=PARAM_BASE;
            url = properties.getProperty(paramBaseName+PARAM_URL, url);
            user = properties.getProperty(paramBaseName+PARAM_USER, user);
            password = properties.getProperty(paramBaseName+PARAM_PASSWORD, password);
            company = properties.getProperty(paramBaseName+PARAM_COMPANY, company);
            syncEnabled = properties.containsKey(paramBaseName+ PARAM_SYNC_SCHEDULE);
            if (syncEnabled) {
                syncSchedule = properties.getProperty(paramBaseName+ PARAM_SYNC_SCHEDULE, "");
            }
        }

        public boolean syncEnabled; String syncSchedule;
        public String url="http://localhost:2000/nsiws/services/NSIService", user="UEK_SOAP", password="la0d6xxw", company="dogm_nsi";
    }
    
    public static class OrgInfo {
        public String guid, number, shortName, longName;

        public String getGuid() {
            return guid;
        }

        public String getNumber() {
            return number;
        }

        public String getShortName() {
            return shortName;
        }

        public String getLongName() {
            return longName;
        }
    }
    
    public static class PupilInfo {
        public String familyName, firstName, secondName, guid, group;
        public String birthDate;

        public String getFamilyName() {
            return familyName;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getGuid() {
            return guid;
        }

        public String getGroup() {
            return group;
        }

        public String getBirthDate() {
            return birthDate;
        }

        public void setBirthDate(String birthDate) {
            this.birthDate = birthDate;
        }
        
        public void copyFrom(PupilInfo pi) {
            this.birthDate = pi.birthDate;
            this.firstName = pi.firstName;
            this.secondName = pi.secondName;
            this.familyName = pi.familyName;
            this.guid = pi.guid;
            this.group = pi.group;
        }
    }

    
    NSIServiceService nsiServicePort;
    NSIService nsiService;
    public void init() throws Exception {
        if (nsiService!=null) return;
        Config config = RuntimeContext.getInstance().getNsiServiceConfig(); //http://10.126.216.2:2000/nsiws/services/NSIService
        String url = config.url;
        logger.info("Trying NSI service: "+url);
        nsiServicePort = new NSIServiceService(new URL(url+"?wsdl"), new QName("http://rstyle.com/nsi/services", "NSIServiceService"));
        nsiService = nsiServicePort.getNSIService();

    }
    
    public List<QueryResult> executeQuery(String queryText) throws Exception {
        init();
        
        Config config = RuntimeContext.getInstance().getNsiServiceConfig();
        String url = config.url;
        NSIRequestType request = new NSIRequestType();
        BindingProvider provider = (BindingProvider)nsiService;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, url);
        provider.getRequestContext().put("set-jaxb-validation-event-handler", false);
        //provider.getRequestContext().put("jaxb-validation-event-handle", null);


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
        request.setMessageData(new MessageDataType());
        request.getMessageData().setAppData(new AppDataType());
        request.getMessageData().getAppData().setContext(new Context());
        request.getMessageData().getAppData().getContext().setUser(config.user);
        request.getMessageData().getAppData().getContext().setPassword(config.password);
        request.getMessageData().getAppData().getContext().setCompany(config.company);
        request.getMessageData().getAppData().setQuery(queryText);

        NSIResponseType response = nsiService.getQueryResults(request);
        if (response.getMessageData().getAppData()!=null && response.getMessageData().getAppData().getGeneralResponse()!=null &&
                response.getMessageData().getAppData().getGeneralResponse().getQueryResult()!=null) {
            return response.getMessageData().getAppData().getGeneralResponse().getQueryResult();
        }
        else {
            JAXBContext jc = JAXBContext.newInstance(NSIResponseType.class.getPackage().getName());
            Marshaller m  = jc.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            m.marshal(response, bos);
            throw new Exception("Ошибка при получении данных из сервиса НСИ. Ответ: "+bos.toString());
        }
    }
    
    public List<OrgInfo> getOrgByName(String orgName) throws Exception {
        List<QueryResult> queryResults = executeQuery("select \n"
                + "item['РОУ XML/GUID Образовательного учреждения'],\n"
                + "item['РОУ XML/Номер  учреждения'], \n"
                + "item['РОУ XML/Краткое наименование учреждения'],\n"
                + "item['РОУ XML/Краткое наименование учреждения'],\n"
                + "item['РОУ XML/Дата изменения (число)']\n"
                + "from catalog('Реестр образовательных учреждений') where \n"
                + "item['РОУ XML/Краткое наименование учреждения'] like '%"+orgName+"%'");
        LinkedList<OrgInfo> list = new LinkedList<OrgInfo>();
        for (QueryResult qr : queryResults) {
            OrgInfo orgInfo = new OrgInfo();
            orgInfo.guid = qr.getQrValue().get(0);
            orgInfo.number = qr.getQrValue().get(1);
            orgInfo.shortName = qr.getQrValue().get(2);
            orgInfo.longName = qr.getQrValue().get(3);
            list.add(orgInfo);
        }
        return list;
    }
    public List<PupilInfo> getPupilsByOrgGUID(String orgName, String familyName, Long updateTime) throws Exception {
        String select = "select \n" + "item['Реестр обучаемых линейный/Фамилия'],\n"
                        + "item['Реестр обучаемых линейный/Имя'], \n" + "item['Реестр обучаемых линейный/Отчество'],\n"
                        + "item['Реестр обучаемых линейный/GUID'],\n"
                        + "item['Реестр обучаемых линейный/Дата рождения'], \n"
                        + "item['Реестр обучаемых линейный/Текущий класс или группа']\n" +
                        "from catalog('Реестр обучаемых')\n"
                        + "where\n"
                        //+ "item['Реестр обучаемых линейный/GUID образовательного учреждения']='"+orgGuid+"'";Полное наименование учреждения
                        + "item['Реестр обучаемых линейный/ID Образовательного учреждения']\n"
                        + "in (select item['РОУ XML/Первичный ключ'] from catalog('Реестр образовательных учреждений') "
                        + "where  item['РОУ XML/Краткое наименование учреждения']='" + orgName + "')\n";
        if (familyName!=null && familyName.length()>0) {
            select += " and item['Реестр обучаемых линейный/Фамилия'] like '%"+familyName+"%'";
        }
        if (updateTime!=null) {
            select += " and  item['Реестр обучаемых линейный/Дата изменения (число)']  &gt; "+(updateTime/1000);
        }
        //select += " order by item['Реестр обучаемых линейный/Фамилия'], item['Реестр обучаемых линейный/Имя'], item['Реестр обучаемых линейный/Отчество']";
        List<QueryResult> queryResults = executeQuery(select);
        LinkedList<PupilInfo> list = new LinkedList<PupilInfo>();
        for (QueryResult qr : queryResults) {
            PupilInfo pupilInfo = new PupilInfo();
            pupilInfo.familyName = qr.getQrValue().get(0);
            pupilInfo.firstName = qr.getQrValue().get(1);
            pupilInfo.secondName = qr.getQrValue().get(2);
            pupilInfo.guid = qr.getQrValue().get(3);
            pupilInfo.birthDate = qr.getQrValue().get(4);
            pupilInfo.group = qr.getQrValue().get(5);
list.add(pupilInfo);
}
        return list;
}
        }
