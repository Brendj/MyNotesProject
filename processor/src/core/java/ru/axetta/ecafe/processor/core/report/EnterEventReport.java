/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
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

    private final List<EnterEventItem> enterEventItems;

    public static class Builder {

        public EnterEventReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {

            Date generateTime = new Date();
            List<EnterEventItem> enterEventItems = new LinkedList<EnterEventItem>();
            if(!idOfOrgList.isEmpty()){
                Criteria criteria = session.createCriteria(EnterEvent.class);
                criteria.createAlias("org","o").add(in("o.idOfOrg",idOfOrgList));
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
                        /* Отчество клиента*/
                        // stringBuilder.append(enterEvent.getClient().getPerson().getSecondName());
                        docId=String.valueOf(enterEvent.getClient().getContractId());
                    }else{
                        stringBuilder.append(" ");
                        docId="";
                    }
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
                            enterEvent.getEventCode(),docId,
                            personFullName, enterEvent.getEvtDateTime(), color);
                    enterEventItems.add(enterEventItem);
                }
            }
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