/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.hibernate.criterion.Restrictions.*;

/**
 * Created by IntelliJ IDEA.
 * User: Kadyrov
 * Date: 21.12.2011
 * Time: 17:05:00
 * To change this template use File | Settings | File Templates.
 */

public class EnterEventReport extends BasicReport {

    private static final Logger logger = LoggerFactory.getLogger(EnterEventReport.class);
    private final List<EnterEventItem> enterEventItems;

    public static class Builder {

        public EnterEventReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            /*
            * Выполняем запрос:
            * SELECT
              cf_enterevents.idofenterevent,
              cf_enterevents.idoforg,
              cf_orgs.officialname,
              cf_enterevents.entername,
              cf_enterevents.turnstileaddr,
              cf_enterevents.passdirection,
              cf_enterevents.eventcode,
              cf_enterevents.docserialnum,
              concat (cf_persons.firstname, ' ' ,
              cf_persons.surname, ' ',
              cf_persons.secondname ) as personFullName,
              cf_enterevents.evtdatetime
            FROM
              public.cf_enterevents,
              public.cf_clients,
              public.cf_persons,
              public.cf_orgs
            WHERE
              cf_enterevents.idofclient = cf_clients.idofclient AND
              cf_enterevents.idoforg = cf_orgs.idoforg AND
              cf_clients.idofperson = cf_persons.idofperson;
            * */
            Date generateTime = new Date();
            List<EnterEventItem> enterEventItems = new LinkedList<EnterEventItem>();
            /* String preparedQuery = "SELECT e.idofenterevent, e.idoforg, o.officialname,"
                    + "e.entername, e.turnstileaddr,  e.passdirection,  e.eventcode,   e.docserialnum,"
                    + " p.firstname as personFullName, e.evtdatetime "
                    + "FROM CF_EnterEvents e,  CF_Clients c,  CF_Persons p, CF_Orgs o"
                    + " WHERE e.idofclient = c.idofclient AND e.idoforg = o.idoforg AND c.idofperson = p.idofperson"
                     + "  and e.evtdatetime >= :fromCreatedDate and e.evtdatetime <= :toCreatedDate";*/
             /* Для этого необходимо изменить класс EnterEvent со связями */
            try{
                Criteria criteria = session.createCriteria(EnterEvent.class);
                criteria.add((between("evtDateTime", startDate, endDate)));
                List<EnterEvent> enterEventList = criteria.list();
                for(EnterEvent enterEvent: enterEventList){
                      long id=enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent();
                    enterEvent.getOrg().getOfficialName();
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(enterEvent.getClient().getPerson().getFirstName());
                    stringBuilder.append(" ");
                    stringBuilder.append(enterEvent.getClient().getPerson().getSecondName());
                    String passdirection, color="black", personFullName=stringBuilder.toString();
                    switch (enterEvent.getPassDirection()){
                        case 0: passdirection="вход"; color="green"; break;
                        case 1: passdirection="выход"; color="red"; break;
                        case 2: passdirection="проход запрещен"; break;
                        case 3: passdirection="взлом турникета"; break;
                        case 4: passdirection="событие без прохода"; break;
                        case 5: passdirection="отказ от прохода"; break;
                        default: passdirection="Ошибка обратитесь администратору";
                    }

                    EnterEventItem  enterEventItem = new EnterEventItem(enterEvent.getCompositeIdOfEnterEvent().getIdOfEnterEvent(),
                            enterEvent.getOrg().getIdOfOrg(), enterEvent.getOrg().getOfficialName(),
                            enterEvent.getEnterName(), enterEvent.getTurnstileAddr(), passdirection,
                            enterEvent.getEventCode(), enterEvent.getClient().getPerson().getIdDocument(),
                            personFullName, enterEvent.getEvtDateTime(), color);
                    enterEventItems.add(enterEventItem);
                }
            } catch (Exception e){
                throw new Exception(e.getMessage());
            }
             /*
            List resultList = null;
            Query query = session.createSQLQuery(preparedQuery);
            long startDateLong = startDate.getTime();
            long endDateLong = endDate.getTime();
            query.setLong("fromCreatedDate", startDateLong);
            query.setLong("toCreatedDate", endDateLong);
            resultList = query.list();
            for (Object result : resultList) {
                Object[] sale = (Object[]) result;
                 Long idofenterevent = ((BigInteger) sale[0]).longValue();   //№ Идентификатор
                 Long idoforg = ((BigInteger) sale[1]).longValue();  // Номер учреждения
                 String officialname = (String) sale[2]; // Название учреждения
                 String entername = (String) sale[3]; // Наименование входа
                 String turnstileaddr = (String) sale[4];// Адрес турникета
                Integer passdirectionInt = (Integer) sale[5]; // Направление прохода
                 Integer eventCode = (Integer) sale[6]; // Код события
                 Integer docserialnum = (Integer) sale[7];// Номер договора
                 String personFullName = (String) sale[8];// Фамилия и Имя учащегося
                Date evtdatetime = new Date(((BigInteger) sale[9]).longValue());// Дата события

                //переопределение направление турникета, возможны ошибки с кодировкой.
                String passdirection, color="black";
                switch (passdirectionInt){
                    case 0: passdirection="вход"; color="green"; break;
                    case 1: passdirection="выход"; color="red"; break;
                    case 2: passdirection="проход запрещен"; break;
                    case 3: passdirection="взлом турникета"; break;
                    case 4: passdirection="событие без прохода"; break;
                    case 5: passdirection="отказ от прохода"; break;
                    default: passdirection="Ошибка обратитесь администратору";
                }

                EnterEventItem  enterEvent = new EnterEventItem(idofenterevent, idoforg, officialname, entername,
                        turnstileaddr, passdirection, eventCode, docserialnum,
                        personFullName, evtdatetime, color);
                enterEventItems.add(enterEvent);
            }   */

            return new EnterEventReport(generateTime, new Date().getTime() - generateTime.getTime(), enterEventItems);
        }

    }

    public EnterEventReport() {
        super();
        this.enterEventItems = Collections.emptyList();
    }

    public EnterEventReport(Date generateTime, long generateDuration, List<EnterEventItem> enterEventItems) {
        super(generateTime, generateDuration);
        this.enterEventItems = enterEventItems;
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

    public List<EnterEventItem> getEnterEventItems() {
        return enterEventItems;
    }

}