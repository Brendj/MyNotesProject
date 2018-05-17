/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static org.hibernate.criterion.Restrictions.between;
import static org.hibernate.criterion.Restrictions.eq;

/**
 * Created by IntelliJ IDEA.
 * User: Kadyrov
 * Date: 21.12.2011
 * Time: 17:05:00
 * To change this template use File | Settings | File Templates.
 */

public class EnterEventReport extends BasicReportForOrgJob {

    private final static Logger logger = LoggerFactory.getLogger(EnterEventReport.class);

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new EnterEventReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    public EnterEventReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime) {
        super(generateTime, generateDuration, jasperPrint, startTime, null, null);
    }

    public static class Builder extends BasicReportForOrgJob.Builder {
        private final String templateFilename;
        private Long idOfOrg;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startDate, Date endDate, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            calendar.setTime(startDate);
            Org org = (Org) session.load(Org.class, idOfOrg);
            parameterMap.put("startDate", CalendarUtils.dateToString(startDate));
            parameterMap.put("endDate", CalendarUtils.dateToString(endDate));
            parameterMap.put("orgName", org.getShortNameInfoService());

            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, startDate, endDate, idOfOrg));
            long generateDuration = generateTime.getTime();
            return new EnterEventReport(generateTime, generateDuration, jasperPrint, startDate);
        }

        private JRDataSource createDataSource(Session session, Date startDate, Date endDate, Long idOfOrg)
                throws Exception {
            Date generateTime = new Date();
            List<EnterEventItem> enterEventItems = new LinkedList<EnterEventItem>();
            Criteria criteria = session.createCriteria(EnterEvent.class);
            criteria.createAlias("org","o").add(eq("o.idOfOrg",idOfOrg));
            criteria.add((between("evtDateTime", startDate, endDate)));
            List<EnterEvent> enterEventList = criteria.list();
            for(EnterEvent enterEvent: enterEventList){
                long id=enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent();
                enterEvent.getOrg().getOfficialName();
                StringBuilder stringBuilder = new StringBuilder();
                String docId = null;
                if (enterEvent.getClient() != null){
                    /* Фамилия клиента */
                    stringBuilder.append(enterEvent.getClient().getPerson().getSurname());
                    stringBuilder.append(" ");
                    /* Имя клиента */
                    stringBuilder.append(enterEvent.getClient().getPerson().getFirstName());
                    stringBuilder.append(" ");
                    /* Отчество клиента*/
                    stringBuilder.append(enterEvent.getClient().getPerson().getSecondName());
                    docId=String.valueOf(enterEvent.getClient().getContractId());
                }else{
                    stringBuilder.append(" ");
                    docId="";
                }
                String passdirection, color="black", personFullName=stringBuilder.toString();
                switch (enterEvent.getPassDirection()){
                    case EnterEvent.ENTRY: passdirection="вход"; color="green"; break;
                    case EnterEvent.EXIT: passdirection="выход"; color="red"; break;
                    case EnterEvent.PASSAGE_IS_FORBIDDEN: passdirection="проход запрещен"; break;
                    case EnterEvent.TURNSTILE_IS_BROKEN: passdirection="взлом турникета"; break;
                    case EnterEvent.EVENT_WITHOUT_PASSAGE: passdirection="событие без прохода"; break;
                    case EnterEvent.PASSAGE_RUFUSAL: passdirection="отказ от прохода"; break;
                    case EnterEvent.RE_ENTRY: passdirection="повторный вход"; color="green"; break;
                    case EnterEvent.RE_EXIT: passdirection="повторный выход"; color="red"; break;
                    case EnterEvent.DETECTED_INSIDE: passdirection="обнаружен на подносе карты внутри здания"; break;
                    case EnterEvent.CHECKED_BY_TEACHER_EXT: passdirection="отмечен в классном журнале через внешнюю систему"; break;
                    case EnterEvent.CHECKED_BY_TEACHER_INT: passdirection="отмечен учителем внутри здания"; break;
                    case EnterEvent.QUERY_FOR_ENTER: passdirection="запрос на вход"; break;
                    case EnterEvent.QUERY_FOR_EXIT: passdirection="запрос на выход"; break;
                    default: passdirection="Ошибка обратитесь администратору";
                    /*
                    TwicePassEnter = 6,     //повторный вход
                    TwicePassExit = 7,       //повторный выход
                    DetectedInside=100 // обнаружен на подносе карты внутри здания
                    * */
                }
                EnterEventItem  enterEventItem = new EnterEventItem(enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent(),
                        enterEvent.getOrg().getIdOfOrg(), enterEvent.getOrg().getOfficialName(),
                        enterEvent.getEnterName(), enterEvent.getTurnstileAddr(), passdirection,
                        enterEvent.getEventCode(),docId,
                        personFullName, enterEvent.getEvtDateTime(), color);
                enterEventItems.add(enterEventItem);
            }
            return new JRBeanCollectionDataSource(enterEventItems);
        }

        public String getTemplateFilename() {
            return templateFilename;
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }
    }

    public EnterEventReport() {
        super();
    }

    public static class EnterEventItem {

        private Long idofenterevent;   //№ Идентификатор
        private Long idoforg;  // Номер учреждения
        private String officialname; // Название учреждения
        private String entername; // Наименование входа
        private String turnstileaddr;// Адрес турникета
        private String passdirection; // Направление прохода
        private Integer eventCode; // Код события
        private String docserialnum;// Номер договора
        private String personFullName;// Фамилия и Имя учащегося
        private Date evtdatetime;// Дата события
        private String color;

        public EnterEventItem(Long idofenterevent, Long idoforg, String officialname, String entername,
                String turnstileaddr, String passdirection, Integer eventCode, String docserialnum,
                String personFullName, Date evtdatetime, String color) {
            this.idofenterevent = idofenterevent;
            this.idoforg = idoforg;
            this.officialname = officialname;
            this.entername = entername;
            this.turnstileaddr = turnstileaddr;
            this.passdirection = passdirection;
            this.eventCode = eventCode;
            this.docserialnum = docserialnum;
            this.personFullName = personFullName;
            this.evtdatetime = evtdatetime;
            this.color = color;
        }

        public Long getIdofenterevent() {
            return idofenterevent;
        }

        public void setIdofenterevent(Long idofenterevent) {
            this.idofenterevent = idofenterevent;
        }

        public Long getIdoforg() {
            return idoforg;
        }

        public void setIdoforg(Long idoforg) {
            this.idoforg = idoforg;
        }

        public String getOfficialname() {
            return officialname;
        }

        public void setOfficialname(String officialname) {
            this.officialname = officialname;
        }

        public String getEntername() {
            return entername;
        }

        public void setEntername(String entername) {
            this.entername = entername;
        }

        public String getTurnstileaddr() {
            return turnstileaddr;
        }

        public void setTurnstileaddr(String turnstileaddr) {
            this.turnstileaddr = turnstileaddr;
        }

        public String getPassdirection() {
            return passdirection;
        }

        public void setPassdirection(String passdirection) {
            this.passdirection = passdirection;
        }

        public Integer getEventCode() {
            return eventCode;
        }

        public void setEventCode(Integer eventCode) {
            this.eventCode = eventCode;
        }

        public String getDocserialnum() {
            return docserialnum;
        }

        public void setDocserialnum(String docserialnum) {
            this.docserialnum = docserialnum;
        }

        public String getPersonFullName() {
            return personFullName;
        }

        public void setPersonFullName(String personFullName) {
            this.personFullName = personFullName;
        }

        public Date getEvtdatetime() {
            return evtdatetime;
        }

        public void setEvtdatetime(Date evtdatetime) {
            this.evtdatetime = evtdatetime;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }


}