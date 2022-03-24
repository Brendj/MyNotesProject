/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.daoservices.visitordogm;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.*;

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

    final String FIND_ALL_VISITORDOGM_ITEMS = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType=2 %s order by v.idOfVisitor";
    final String FIND_ALL_VISITORDOGM_ITEMS_BY_DELETED = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType = 2 and v.deleted = :deleted %s order by v.idOfVisitor";
    final String FIND_ALL_VISITORDOGM_ITEMS_ORDER_BY_NAME = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.visitorType=2 order by v.person.firstName";
    final String FIND_ALL_TEMP_CARD_BY_VISITORDOGM = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem(ct) from CardTemp ct where ct.visitor.idOfVisitor=:idOfVisitor and ct.visitorType=3 order by ct.createDate desc";
    final String FIND_ALL_TEMP_CARD_BY_VISITORDOGM_TYPE = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardItem(ct, ct.visitor) from CardTemp ct where ct.visitorType=3 order by ct.createDate desc";
    final String FIND_ENTER_EVENT_BY_VISITORDOGM ="select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.CardEventOperationItem(ee.idOfVisitor, ee.evtDateTime, ee.passDirection, ee.org.idOfOrg, ee.org.shortName, ee.org.refectoryType) from EnterEvent ee where ee.idOfVisitor in :idOfVisitors and ee.evtDateTime between :beginDate and :endDate order by ee.evtDateTime desc, ee.idOfVisitor asc";
    final String FIND_VISITORDOGM_ITEMS_BY_IDOFVISITOR = "select new ru.axetta.ecafe.processor.core.daoservices.visitordogm.VisitorItem(v) from Visitor v where v.idOfVisitor = :idOfVisitor";

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;

    private String filter;

    private String getCondition() {
        return StringUtils.isEmpty(filter) ? "" : " and ("
                + "LOWER(v.person.surname) like :filter "
                + "or LOWER(v.person.firstName) like :filter "
                + "or LOWER(v.person.secondName) like :filter "
                + "or LOWER(v.position) like :filter "
                + "or LOWER(v.passportNumber) like :filter "
                + "or LOWER(v.driverLicenceNumber) like :filter "
                + "or LOWER(v.warTicketNumber) like :filter"
                + ") ";
    }

    public VisitorItem findVisitorsDogmByIdOfVisitor(Long idOfVisitor){
        TypedQuery<VisitorItem> query = entityManager.createQuery(FIND_VISITORDOGM_ITEMS_BY_IDOFVISITOR, VisitorItem.class);
        query.setParameter("idOfVisitor", idOfVisitor);
        return query.getSingleResult();
    }

    public List<VisitorItem> findAllVisitorsDogm(){
        String condition = getCondition();
        TypedQuery<VisitorItem> query = entityManager.createQuery(String.format(FIND_ALL_VISITORDOGM_ITEMS, condition), VisitorItem.class);
        if (!StringUtils.isEmpty(filter)) query.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");
        return query.getResultList();
    }

    public List<VisitorItem> findAllVisitorsDogm(boolean deletedVisitorsDogm) {
        String condition = getCondition();
        TypedQuery<VisitorItem> query = entityManager.createQuery(String.format(FIND_ALL_VISITORDOGM_ITEMS_BY_DELETED, condition), VisitorItem.class)
                .setParameter("deleted", deletedVisitorsDogm);
        if (!StringUtils.isEmpty(filter)) query.setParameter("filter", "%" + filter.trim().toLowerCase() + "%");
        return query.getResultList();
    }

    public List<VisitorItem> generateVisitorDogmHistoryReport(Date startDate, Date endDate){
        TypedQuery<VisitorItem> visitorItemTypedQuery = entityManager.createQuery(FIND_ALL_VISITORDOGM_ITEMS_ORDER_BY_NAME, VisitorItem.class);
        List<VisitorItem> visitorItemList = visitorItemTypedQuery.getResultList();
        List<Long> visitorIds = new ArrayList<Long>();
        Map<Long, VisitorItem> visitorMap = new HashMap<Long, VisitorItem>();
        for(VisitorItem visitorItem : visitorItemList) {
            visitorIds.add(visitorItem.getIdOfVisitor());
            visitorMap.put(visitorItem.getIdOfVisitor(), visitorItem);
        }
        TypedQuery<CardEventOperationItem> cardEventOperationItemTypedQuery = entityManager.createQuery(FIND_ENTER_EVENT_BY_VISITORDOGM,
                CardEventOperationItem.class);
        cardEventOperationItemTypedQuery.setParameter("idOfVisitors", visitorIds);
        cardEventOperationItemTypedQuery.setParameter("beginDate", startDate);
        cardEventOperationItemTypedQuery.setParameter("endDate", endDate);
        List<CardEventOperationItem> cardEventOperationItems = cardEventOperationItemTypedQuery.getResultList();
        for(CardEventOperationItem eventOperationItem : cardEventOperationItems) {
            visitorMap.get(eventOperationItem.getIdOfVisitor()).getOperationItemList().add(eventOperationItem);
        }
        List<VisitorItem> result = new ArrayList<VisitorItem>();
        for(VisitorItem visitorItem : visitorMap.values()) {
            if(visitorItem.getOperationItemList().size() > 0) {
                result.add(visitorItem);
            }
        }
        return result;
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
            throw new Exception("клиент не является сотрудником.");
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
                    throw new Exception("карта не предназначена для сотрудников.");
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
        if(StringUtils.isEmpty(visitorItem.getSurname()) || StringUtils.isEmpty(visitorItem.getFirstName()) ) {
            throw new  Exception("Фамилия и имя должны быть заполнены.");
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
            visitor.setPassportNumber(visitorItem.getPassportNumber().replace(" ", ""));
            visitor.setPassportDate(visitorItem.getPassportDate());
            visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber().replace(" ", ""));
            visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
            visitor.setWarTicketNumber(visitorItem.getWarTicketNumber().replace(" ", ""));
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
                visitor.setPassportNumber(visitorItem.getPassportNumber().replace(" ", ""));
                visitor.setPassportDate(visitorItem.getPassportDate());
                visitor.setDriverLicenceNumber(visitorItem.getDriverLicenceNumber().replace(" ", ""));
                visitor.setDriverLicenceDate(visitorItem.getDriverLicenceDate());
                visitor.setWarTicketNumber(visitorItem.getWarTicketNumber().replace(" ", ""));
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}
