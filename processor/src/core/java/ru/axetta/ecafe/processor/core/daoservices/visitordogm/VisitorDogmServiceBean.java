/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.visitordogm;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static ru.axetta.ecafe.processor.core.persistence.Person.isEmptyFullNameFields;
import static ru.axetta.ecafe.processor.core.persistence.Visitor.isEmptyDocumentParams;
import static ru.axetta.ecafe.processor.core.utils.CalendarUtils.isDateEqLtCurrentDate;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 12.10.16
 * Time: 10:37
 */
@Repository
public class VisitorDogmServiceBean {

    final String FIND_ALL_VISITORDOGM_ITEMS = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType=2 order by v.idOfVisitor";
    final String FIND_ALL_VISITORDOGM_ITEMS_BY_DELETED = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType = 2 and v.deleted = :deleted order by v.idOfVisitor";
    final String FIND_ALL_VISITORDOGM_ITEMS_ORDER_BY_NAME = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType=2 order by v.person.firstName";
    final String FIND_ALL_TEMP_CARD_BY_VISITORDOGM = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem(ct) from CardTemp ct where ct.visitor.idOfVisitor=:idOfVisitor and ct.visitorType=3 order by ct.createDate desc";
    final String FIND_ALL_TEMP_CARD_BY_VISITORDOGM_TYPE = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem(ct, ct.visitor) from CardTemp ct where ct.visitorType=3 order by ct.createDate desc";
    final String FIND_ENTER_EVENT_BY_VISITORDOGM ="select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardEventOperationItem(ee.evtDateTime, ee.passDirection, ee.org.idOfOrg, ee.org.shortName, ee.org.refectoryType) from EnterEvent ee where ee.idOfVisitor=:idOfVisitor and ee.evtDateTime between :beginDate and :endDate order by ee.evtDateTime desc, ee.idOfVisitor asc";

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    public List<VisitorItem> findAllVisitorsDogm(){
        TypedQuery<VisitorItem> query = entityManager.createQuery(FIND_ALL_VISITORDOGM_ITEMS, VisitorItem.class);
        return query.getResultList();
    }

    public List<VisitorItem> findAllVisitorsDogm(boolean deletedVisitorsDogm) {
        TypedQuery<VisitorItem> query = entityManager.createQuery(FIND_ALL_VISITORDOGM_ITEMS_BY_DELETED, VisitorItem.class)
                .setParameter("deleted", deletedVisitorsDogm);
        return query.getResultList();
    }

    @Transactional
    public List<VisitorItem> visitorDogmHistoryReport(Date beginDate, Date endDate){
        Session session = entityManager.unwrap(Session.class);
        List<VisitorItem> reportResult = new ArrayList<VisitorItem>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        Criteria visitorCriteria = session.createCriteria(Visitor.class);
        visitorCriteria.add(Restrictions.eq("visitorType", 2));
        List<Visitor> visitors = visitorCriteria.list();
        while (endDate.getTime()>calendar.getTimeInMillis()){
            VisitorItem currentVisitor = null;
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DATE,1);
            Date finishDate = calendar.getTime();
            for (Visitor visitor: visitors){
                Criteria criteria = session.createCriteria(EnterEvent.class);
                criteria.add(Restrictions.eq("idOfVisitor", visitor.getIdOfVisitor()));
                criteria.add(Restrictions.between("evtDateTime", startDate, finishDate));
                criteria.addOrder(Order.desc("evtDateTime"));
                criteria.addOrder(Order.asc("idOfVisitor"));
                List<EnterEvent> enterEvents = criteria.list();

                //ee.evtDateTime between :beginDate and :endDate order by ee.evtDateTime desc, ee.idOfVisitor asc
                if(enterEvents!=null && !enterEvents.isEmpty()){
                    currentVisitor = new VisitorItem();
                    final Person person = visitor.getPerson();
                    currentVisitor.setFirstName(person.getFirstName());
                    currentVisitor.setSecondName(person.getSecondName());
                    currentVisitor.setSurname(person.getSurname());
                    currentVisitor.setOperationDate(startDate);
                    List<CardEventOperationItem> cardEventOperationItems = new ArrayList<CardEventOperationItem>();
                    for (EnterEvent events: enterEvents){
                        final Org org = events.getOrg();
                        cardEventOperationItems.add(new CardEventOperationItem(events.getEvtDateTime(), events.getPassDirection(), org
                                .getIdOfOrg(), org.getShortName(), org.getRefectoryType()));
                    }
                    currentVisitor.addOperationItem(cardEventOperationItems);
                }
                if(currentVisitor!=null) {
                    reportResult.add(currentVisitor);
                    currentVisitor=null;
                }
            }
        }
        return reportResult;
    }

    public List<VisitorItem> generateVisitorDogmHistoryReport(Date beginDate, Date endDate){
        List<VisitorItem> reportResult = new ArrayList<VisitorItem>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(beginDate);
        TypedQuery<VisitorItem> visitorItemTypedQuery = entityManager.createQuery(FIND_ALL_VISITORDOGM_ITEMS_ORDER_BY_NAME, VisitorItem.class);
        List<VisitorItem> visitorItemList = visitorItemTypedQuery.getResultList();
        while (endDate.getTime()>calendar.getTimeInMillis()){
            VisitorItem currentVisitor = null;
            Date startDate = calendar.getTime();
            calendar.add(Calendar.DATE,1);
            Date finishDate = calendar.getTime();
            for (VisitorItem item: visitorItemList){
                TypedQuery<CardEventOperationItem> cardEventOperationItemTypedQuery = entityManager.createQuery(FIND_ENTER_EVENT_BY_VISITORDOGM, CardEventOperationItem.class);
                cardEventOperationItemTypedQuery.setParameter("idOfVisitor", item.getIdOfVisitor());
                cardEventOperationItemTypedQuery.setParameter("beginDate",startDate);
                cardEventOperationItemTypedQuery.setParameter("endDate",finishDate);
                List<CardEventOperationItem> cardEventOperationItems = cardEventOperationItemTypedQuery.getResultList();
                if(cardEventOperationItems!=null && !cardEventOperationItems.isEmpty()){
                    currentVisitor = new VisitorItem(item);
                    currentVisitor.setOperationDate(startDate);
                    currentVisitor.addOperationItem(cardEventOperationItems);
                }
                if(currentVisitor!=null) {
                    reportResult.add(currentVisitor);
                    currentVisitor=null;
                }
            }
        }
        return reportResult;
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveVisitorDogmCard(CardItem cardItem, Long idOfVisitorDogm) throws Exception{
        Date today = CalendarUtils.truncateToDayOfMonth(new Date());
        Card c = DAOUtils.findCardByCardNo((Session) entityManager.getDelegate(), cardItem.getCardNo());
        if (c != null && c.getClient() != null) {
            final String format = "карта уже зарегистрирована на клиента: %d.";
            final String message = String.format(format, c.getClient().getIdOfClient());
            throw new Exception(message);
        }
        Visitor visitor = entityManager.find(Visitor.class, idOfVisitorDogm);
        if(!visitor.getVisitorType().equals(Visitor.VISITORDOGM_TYPE)){
            throw new Exception("клиент не является сотрудником ДОгМ.");
        }
        CardTemp cardTemp = DAOUtils.findCardTempByCardNo((Session) entityManager.getDelegate(), cardItem.getCardNo());
        String cardPrintedNo;
        if(StringUtils.isEmpty(cardItem.getCardPrintedNo())){
            cardPrintedNo = Long.toString(cardItem.getCardNo());
        } else {
            cardPrintedNo = cardItem.getCardPrintedNo();
        }
        if (cardTemp != null) {
            if(cardItem.getId()==null){
                //if(cardTemp.getClientTypeEnum()!= ClientTypeEnum.VISITORDOGM)
                if(cardTemp.getVisitorType()!= 3){
                    throw new Exception("карта не предназначена для сотрудников ДОгМ.");
                }
                if(cardTemp.getCardPrintedNo()!=null && !cardTemp.getCardPrintedNo().equals(cardPrintedNo)){
                    //cardTemp.setCardPrintedNo(cardPrintedNo);
                } else {
                    throw new Exception("карта уже зарегистрирована на временная.");
                }
            }  else {
                cardTemp.setVisitor(visitor);
                cardTemp.setCardNo(cardTemp.getCardNo());
                cardTemp.setCardPrintedNo(cardPrintedNo);
                entityManager.merge(cardTemp);
            }
        } else {
            if(cardItem.getId()==null){
                cardTemp = new CardTemp(cardItem.getCardNo(), cardPrintedNo, CardOperationStation.REGISTRATION, 3); // ClientTypeEnum.VISITORDOGM);
                cardTemp.setVisitor(visitor);
                entityManager.persist(cardTemp);
            } else {
                cardTemp = entityManager.find(CardTemp.class, cardItem.getId());
                cardTemp.setCardPrintedNo(cardItem.getCardPrintedNo());
                cardTemp.setCardNo(cardItem.getCardNo());
                cardTemp.setVisitor(visitor);
                entityManager.merge(cardTemp);
            }
        }
        return cardTemp.getIdOfCartTemp();
    }

    @Transactional(rollbackFor = Exception.class)
    public Long saveVisitorDogm(VisitorItem visitorItem) throws Exception{
        if(isEmptyFullNameFields(visitorItem.getFirstName(), visitorItem.getSurname(), visitorItem.getSecondName())) {
            throw new  Exception("все поля ФИО должны быть заполнены.");
        }

        /**
         * Если поле-массив PersonDocuments не содержит ни одного описания удостоверния личности,
         * выбрасывать исключение с сообщением «Отсутствует информация об удостоверении личности»
         * */
        if(isEmptyDocumentParams(visitorItem.getDriverLicenceNumber(), visitorItem.getDriverLicenceDate()) &&
                isEmptyDocumentParams(visitorItem.getPassportNumber(), visitorItem.getPassportDate()) &&
                isEmptyDocumentParams(visitorItem.getWarTicketNumber(), visitorItem.getWarTicketDate())) {
            throw new Exception("отсутствует информация об удостоверении личности.");
        }

        /**
         * Если в  поле-массиве PersonDocuments у какого-либо документа для даты выдачи
         * будет указана еще не наступившая дата, выбрасывать исключение с сообщением «Неверная дата выдачи документа»
         * */
        if( isDateEqLtCurrentDate(visitorItem.getDriverLicenceDate()) ||
                isDateEqLtCurrentDate(visitorItem.getPassportDate()) ||
                isDateEqLtCurrentDate(visitorItem.getWarTicketDate())){
            throw new Exception("неверная дата выдачи документа.");
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
            visitor.setVisitorType(Visitor.VISITORDOGM_TYPE);
            visitor.setPosition(visitorItem.getPosition());
            entityManager.persist(visitor);
            return visitor.getIdOfVisitor();
        } else {
            Visitor visitor = entityManager.find(Visitor.class, visitorItem.getIdOfVisitor());
            if(visitor==null){
                throw new Exception("сотрудник не найден.");
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
                visitor.setVisitorType(Visitor.VISITORDOGM_TYPE);
                visitor.setPosition(visitorItem.getPosition());
                entityManager.persist(visitor);
                return visitor.getIdOfVisitor();
            }
        }
    }

    @Transactional
    public void deleteVisitorDogm(Long idOfVisitor) {
        Visitor visitor = entityManager.getReference(Visitor.class, idOfVisitor);
        visitor.setDeleted(true);
    }

    public List<CardItem> findCardsByVisitorDogm(Long idOfVisitor) {
        TypedQuery<CardItem> query = entityManager.createQuery(FIND_ALL_TEMP_CARD_BY_VISITORDOGM, CardItem.class);
        query.setParameter("idOfVisitor",idOfVisitor);
        return query.getResultList();
    }

    public List<CardItem> findCardsByVisitorDogmTypes() {
        TypedQuery<CardItem> query = entityManager.createQuery(FIND_ALL_TEMP_CARD_BY_VISITORDOGM_TYPE, CardItem.class);
        return query.getResultList();
    }

    public VisitorItem getVisitorDogmByCard(Long id) throws Exception {
        TypedQuery<VisitorItem> query = entityManager.createQuery("select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(ct.visitor) from CardTemp ct where ct.id=:id", VisitorItem.class);
        query.setParameter("id", id);
        List<VisitorItem> items = query.getResultList();
        if(items==null || items.isEmpty()){
            return null;
        } else {
            if (items.size() == 1)
                return items.get(0);
            else {
                throw new Exception("карта имеет много владельцев.");
            }
        }
    }

}
