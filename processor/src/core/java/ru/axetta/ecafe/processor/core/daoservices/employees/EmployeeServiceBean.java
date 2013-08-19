/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.employees;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.Person.isEmptyFullNameFields;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyDocumentParams;
import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.isDateEqLtCurrentDate;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 13.08.13
 * Time: 15:04
 * To change this template use File | Settings | File Templates.
 */
@Repository
@Transactional
public class EmployeeServiceBean {

    final String FIND_ALL_EMPLOYEE_ITEMS = "select new ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem(v) from Visitor v where v.VisitorType=1 order by v.idOfVisitor";
    final String FIND_ALL_EMPLOYEE_ITEMS_ORDER_BY_NAME = "select new ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem(v) from Visitor v where v.VisitorType=1 order by v.person.firstName";
    final String GET_EMPLOYEE_ITEMS_BY_ID = "select new ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem(v) from Visitor v where v.VisitorType=1 and v.idOfVisitor=:idOfVisitor order by v.idOfVisitor";
    //final String EXTRACT_EMPLOYEE_ID = "select new ru.axetta.ecafe.processor.core.daoservices.employees.VisitorItem(v) from Visitor v where v.VisitorType=1 order by v.idOfVisitor";
    final String FIND_ALL = "from Visitor v left join v.cards card order by v.idOfVisitor";
    final String FIND_ALL_TEMP_CARD = "from CardTemp ct";
    final String FIND_ALL_TEMP_CARD_BY_EMPLOYEE = "select new ru.axetta.ecafe.processor.core.daoservices.employees.CardItem(ct) from CardTemp ct where ct.visitor.idOfVisitor=:idOfVisitor and ct.clientTypeEnum=2 order by ct.createDate desc";
    final String FIND_ALL_TEMP_CARD_BY_EMPLOYEE_TYPE = "select new ru.axetta.ecafe.processor.core.daoservices.employees.CardItem(ct, ct.visitor) from CardTemp ct where ct.clientTypeEnum=2 order by ct.createDate desc";
    final String FIND_ENTER_EVENT_BY_EMPLOYEE ="select new ru.axetta.ecafe.processor.core.daoservices.employees.CardEventOperationItem(ee.evtDateTime, ee.passDirection, ee.org.idOfOrg, ee.org.shortName, ee.org.refectoryType) from EnterEvent ee where ee.idOfVisitor=:idOfVisitor and ee.evtDateTime between :beginDate and :endDate order by ee.evtDateTime desc, ee.idOfVisitor asc";

    @PersistenceContext
    private EntityManager entityManager;

    @PostConstruct
    public void init(){
    }

    @Transactional(readOnly = true)
    public List<VisitorItem> findAllEmployees(){
        Query query = entityManager.createQuery(FIND_ALL_EMPLOYEE_ITEMS);
        return query.getResultList();
    }

    //@Transactional(readOnly = true)
    //public List<CardEventOperationItem> generateEmployeeReport(Date beginDate, Date endDate){
    //    List<CardEventOperationItem> cardEventOperationItems = new ArrayList<CardEventOperationItem>();
    //    Calendar calendar = Calendar.getInstance();
    //    calendar.setTime(beginDate);
    //    Query query = entityManager.createQuery(FIND_ALL_EMPLOYEE_ITEMS_ORDER_BY_NAME);
    //    List<VisitorItem> visitorItemList = query.getResultList();
    //    if (visitorItemList!=null && !visitorItemList.isEmpty()){
    //        for (VisitorItem visitorItem: visitorItemList){
    //            while (endDate.getTime()>calendar.getTimeInMillis()){
    //                query = entityManager.createQuery(FIND_ENTER_EVENT_BY_EMPLOYEE);
    //                query.setParameter("idOfVisitor", visitorItem.getIdOfVisitor());
    //                query.setParameter("beginDate",beginDate);
    //                query.setParameter("endDate",endDate);
    //                cardEventOperationItems = query.getResultList();
    //                int cardEventOperationItemsSize = cardEventOperationItems.size();
    //                for (int i = 0;i < cardEventOperationItemsSize; i++) {
    //                    CardEventOperationItem item = cardEventOperationItems.get(i);
    //                    item.setEmployee(visitorItem);
    //                    cardEventOperationItems.set(i, item);
    //                }
    //                calendar.add(Calendar.DATE,1);
    //            }
    //        }
    //    }
    //    return cardEventOperationItems;
    //}

    @Transactional(readOnly = true)
    public List<VisitorItem> generateEmployeeHistoryReport(Date beginDate, Date endDate){
        List<VisitorItem> reportResult = new ArrayList<VisitorItem>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        Query query = entityManager.createQuery(FIND_ALL_EMPLOYEE_ITEMS_ORDER_BY_NAME);
        List<VisitorItem> visitorItemList = query.getResultList();
        while (endDate.getTime()>calendar.getTimeInMillis()){
            VisitorItem currentVisitor = null;
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DATE,1);
            Date finishDate = calendar.getTime();
            for (VisitorItem item: visitorItemList){
                query = entityManager.createQuery(FIND_ENTER_EVENT_BY_EMPLOYEE);
                query.setParameter("idOfVisitor", item.getIdOfVisitor());
                query.setParameter("beginDate",startDate);
                query.setParameter("endDate",finishDate);
                List<CardEventOperationItem> cardEventOperationItems = query.getResultList();
                if(cardEventOperationItems!=null && !cardEventOperationItems.isEmpty()){
                    currentVisitor = new VisitorItem(item);
                    currentVisitor.setOperationDate(startDate);
                    currentVisitor.addOperationItem(cardEventOperationItems);
                }
            }
            if(currentVisitor!=null) {
                reportResult.add(currentVisitor);
            }

        }
        //if (visitorItemList!=null && !visitorItemList.isEmpty()){
        //    for (VisitorItem visitorItem: visitorItemList){
        //        while (endDate.getTime()>calendar.getTimeInMillis()){
        //            query = entityManager.createQuery(FIND_ENTER_EVENT_BY_EMPLOYEE);
        //            query.setParameter("idOfVisitor", visitorItem.getIdOfVisitor());
        //            query.setParameter("beginDate",beginDate);
        //            query.setParameter("endDate",endDate);
        //            cardEventOperationItems = query.getResultList();
        //            int cardEventOperationItemsSize = cardEventOperationItems.size();
        //            for (int i = 0;i < cardEventOperationItemsSize; i++) {
        //                CardEventOperationItem item = cardEventOperationItems.get(i);
        //                item.setEmployee(visitorItem);
        //                cardEventOperationItems.set(i, item);
        //            }
        //            calendar.add(Calendar.DATE,1);
        //        }
        //    }
        //}
        return reportResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveEmployeeCard(CardItem cardItem, Long idOfEmployer) throws Exception{
        Card c = DAOUtils.findCardByCardNo((Session) entityManager.getDelegate(), cardItem.getCardNo());
        if (c != null) {
            final String format = "Карта уже зарегистрирована на клиента: %d";
            final String message = String.format(format, c.getClient().getIdOfClient());
            throw new Exception(message);
        }
        Visitor visitor = entityManager.find(Visitor.class, idOfEmployer);
        if(visitor.getVisitorType()!=VisitorType.EMPLOYEE){
            throw new Exception("Клиент не является инженером");
        }
        CardTemp cardTemp = DAOUtils.findCardTempByCardNo((Session) entityManager.getDelegate(), cardItem.getCardNo());
        String cardPrintedNo;
        if(StringUtils.isEmpty(cardItem.getCardPrintedNo())){
            cardPrintedNo = Long.toString(cardItem.getCardNo());
        } else {
            cardPrintedNo = cardItem.getCardPrintedNo();
        }
        if (cardTemp != null) {
            if(cardTemp.getClientTypeEnum()!= ClientTypeEnum.EMPLOYEE) {
                throw new Exception("Карта не предназначена для сотрудников");
            }
            if(cardTemp.getCardPrintedNo()!=null && !cardTemp.getCardPrintedNo().equals(cardPrintedNo)){
                cardTemp.setCardPrintedNo(cardPrintedNo);
            } else {
                throw new Exception("Временная карта уже зарегистрирована на временная");
            }
        } else {
            if(cardItem.getId()==null){
                cardTemp = new CardTemp(cardItem.getCardNo(), cardPrintedNo, cardItem.getCardStation(),  ClientTypeEnum.EMPLOYEE);
                cardTemp.setValidDate(cardItem.getValidDate());
                cardTemp.setVisitor(visitor);
                entityManager.persist(cardTemp);
            } else {
                cardTemp = entityManager.find(CardTemp.class, cardItem.getId());
                cardTemp.setCardPrintedNo(cardItem.getCardPrintedNo());
                cardTemp.setCardNo(cardItem.getCardNo());
                cardTemp.setValidDate(cardItem.getValidDate());
                cardTemp.setCardStation(cardItem.getCardStation());
                cardTemp.setVisitor(visitor);
                entityManager.merge(cardTemp);
            }
        }
        return cardTemp.getIdOfCartTemp();
    }

    public Long saveEmployee(VisitorItem visitorItem) throws Exception{
        if(isEmptyFullNameFields(visitorItem.getFirstName(), visitorItem.getSurname(), visitorItem.getSecondName())) {
            throw new  Exception("Все поля ФИО должны быть заполнены");
        }

        /**
         * Если поле-массив PersonDocuments не содержит ни одного описания удостоверния личности,
         * выбрасывать исключение с сообщением «Отсутствует информация об удостоверении личности»
         * */
        if(isEmptyDocumentParams(visitorItem.getDriverLicenceNumber(), visitorItem.getDriverLicenceDate()) &&
                isEmptyDocumentParams(visitorItem.getPassportNumber(), visitorItem.getPassportDate()) &&
                isEmptyDocumentParams(visitorItem.getWarTicketNumber(), visitorItem.getWarTicketDate())) {
            throw new Exception("Отсутствует информация об удостоверении личности");
        }

        /**
         * Если в  поле-массиве PersonDocuments у какого-либо документа для даты выдачи
         * будет указана еще не наступившая дата, выбрасывать исключение с сообщением «Неверная дата выдачи документа»
         * */
        if( isDateEqLtCurrentDate(visitorItem.getDriverLicenceDate()) ||
                isDateEqLtCurrentDate(visitorItem.getPassportDate()) ||
                isDateEqLtCurrentDate(visitorItem.getWarTicketDate())){
            throw new Exception("Неверное значение даты окончания действия карты");
        }

        if(visitorItem.getIdOfVisitor()==null){
            Person person = new Person(visitorItem.getFirstName().trim(), visitorItem.getSurname().trim(), visitorItem.getSecondName().trim());
            entityManager.persist(person);
            Visitor visitor = new Visitor(person);
            visitor.setPassportNumber(visitorItem.getPassportNumber());
            visitor.setPassportDate(visitorItem.getPassportDate());
            visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber());
            visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
            visitor.setWarTicketNumber(visitorItem.getWarTicketNumber());
            visitor.setWarTicketDate(visitorItem.getWarTicketDate());
            visitor.setVisitorType(VisitorType.EMPLOYEE);
            entityManager.persist(visitor);
            return visitor.getIdOfVisitor();
        } else {
            Visitor visitor = entityManager.find(Visitor.class, visitorItem.getIdOfVisitor());
            if(visitor==null){
                throw new Exception("Сотрудник не найден");
            } else {
                Person person = visitor.getPerson();
                person.setFirstName(visitorItem.getFirstName());
                person.setSecondName(visitorItem.getSecondName());
                person.setSurname(visitorItem.getSurname());
                entityManager.persist(person);
                visitor.setPassportNumber(visitorItem.getPassportNumber());
                visitor.setPassportDate(visitorItem.getPassportDate());
                visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber());
                visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
                visitor.setWarTicketNumber(visitorItem.getWarTicketNumber());
                visitor.setWarTicketDate(visitorItem.getWarTicketDate());
                visitor.setVisitorType(VisitorType.EMPLOYEE);
                entityManager.persist(visitor);
                return visitor.getIdOfVisitor();
            }
        }
    }

    @Transactional(readOnly = true)
    public List<CardItem> findCardsByEmployee(Long idOfVisitor) {
        Query query = entityManager.createQuery(FIND_ALL_TEMP_CARD_BY_EMPLOYEE);
        query.setParameter("idOfVisitor",idOfVisitor);
        return query.getResultList();
    }

    @Transactional(readOnly = true)
    public List<CardItem> findCardsByEmployeeTypes() {
        Query query = entityManager.createQuery(FIND_ALL_TEMP_CARD_BY_EMPLOYEE_TYPE);
        return query.getResultList();
    }
}
