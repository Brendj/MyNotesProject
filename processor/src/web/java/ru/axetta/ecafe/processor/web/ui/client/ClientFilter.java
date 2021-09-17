/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sms.PhoneNumberCanonicalizator;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.client.items.ClientGroupMenu;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientFilter {


    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getFilterClientMESHGUID() {
        return filterClientMESHGUID;
    }

    public void setFilterClientMESHGUID(String filterClientMESHGUID) {
        this.filterClientMESHGUID = filterClientMESHGUID;
    }

    public static class OrgItem {

        private final Long idOfOrg;
        private final String shortName;
        private final String officialName;

        public OrgItem() {
            this.idOfOrg = null;
            this.shortName = null;
            this.officialName = null;
        }

        public boolean isEmpty() {
            return null == idOfOrg;
        }

        public OrgItem(Org org) {
            this.idOfOrg = org.getIdOfOrg();
            this.shortName = org.getShortName();
            this.officialName = org.getOfficialName();
        }

        public Long getIdOfOrg() {
            return idOfOrg;
        }

        public String getShortName() {
            return shortName;
        }

        public String getOfficialName() {
            return officialName;
        }
    }

    public static class PersonItem {

        private String firstName;
        private String surname;
        private String secondName;
        private String idDocument;

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public void setSecondName(String secondName) {
            this.secondName = secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }

        public void setIdDocument(String idDocument) {
            this.idDocument = idDocument;
        }

        public boolean isEmpty() {
            return StringUtils.isEmpty(firstName) && StringUtils.isEmpty(surname) && StringUtils.isEmpty(secondName)
                    && StringUtils.isEmpty(idDocument);
        }

        public Person buildPerson() {
            Person person = new Person(firstName.trim(), surname.trim(), secondName.trim());
            person.setIdDocument(idDocument);
            return person;
        }
    }

    private OrgItem org = new OrgItem();
    private String contractId;
    private PersonItem person = new PersonItem();
    private PersonItem contractPerson = new PersonItem();
    private final ClientCardOwnMenu clientCardOwnMenu = new ClientCardOwnMenu();
    private int clientCardOwnCondition = ClientCardOwnMenu.NO_CONDITION;
    private final ClientBalanceFilter clientBalanceMenu = new ClientBalanceFilter();
    private Integer clientBalanceCondition =  ClientBalanceFilter.NO_CONDITION;
    private String filterClientId;
    private String filterClientGUID;
    private String filterClientMESHGUID;
    private Map<String, Long> clientGroupItems = ClientGroupMenu.getItems();
    private Map<String, Long> clientGroupsCustomItems = ClientGroupMenu.getCustomItems();
    private Long clientGroupId = ClientGroupMenu.CLIENT_ALL;
    private boolean showDeleted;
    private boolean includeFriendlyOrg = true;
    private String mobileNumber;
    private String email;
    private Long permanentOrgId;
    private Integer limit = 0;
    private Integer offset = 0;

    public ClientBalanceFilter getClientBalanceMenu() {
        return clientBalanceMenu;
    }

    public int getClientBalanceCondition() {
        return clientBalanceCondition;
    }

    public void setClientBalanceCondition(int clientBalanceCondition) {
        this.clientBalanceCondition = clientBalanceCondition;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFilterClientId() {
        return filterClientId;
    }

    public void setFilterClientId(String filterClientId) {
        this.filterClientId = filterClientId;
    }

    public int getClientCardOwnCondition() {
        return clientCardOwnCondition;
    }

    public void setClientCardOwnCondition(int clientCardOwnCondition) {
        this.clientCardOwnCondition = clientCardOwnCondition;
    }

    public ClientCardOwnMenu getClientCardOwnMenu() {
        return clientCardOwnMenu;
    }

    public OrgItem getOrg() {
        return org;
    }

    public void setOrg(OrgItem org) {
        this.org = org;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public PersonItem getPerson() {
        return person;
    }

    public void setPerson(PersonItem person) {
        this.person = person;
    }

    public PersonItem getContractPerson() {
        return contractPerson;
    }

    public void setContractPerson(PersonItem contractPerson) {
        this.contractPerson = contractPerson;
    }

    public String getFilterClientGUID() {
        return filterClientGUID;
    }

    public void setFilterClientGUID(String filterClientGUID) {
        this.filterClientGUID = filterClientGUID;
    }

    public boolean isIncludeFriendlyOrg() {
        return includeFriendlyOrg;
    }

    public void setIncludeFriendlyOrg(boolean includeFriendlyOrg) {
        this.includeFriendlyOrg = includeFriendlyOrg;
    }

    public boolean isShowDeleted() {
        return showDeleted;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }

    public Long getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(Long clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public Map<String, Long> getClientGroupItems() {
        return clientGroupItems;
    }

    public Map<String, Long> getClientGroupsCustomItems() {
        return clientGroupsCustomItems;
    }

    public Long getPermanentOrgId() {
        return permanentOrgId;
    }

    public void setPermanentOrgId(Long permanentOrgId) {
        this.permanentOrgId = permanentOrgId;
    }

    public boolean isEmpty() {
        if (ClientCardOwnMenu.NO_CONDITION == clientCardOwnCondition && StringUtils.isEmpty(contractId)
                && StringUtils.isEmpty(filterClientId) && person.isEmpty() && contractPerson.isEmpty()
                && StringUtils.isEmpty(mobileNumber) && StringUtils.isEmpty(email) && StringUtils.isEmpty(filterClientGUID)
                && StringUtils.isEmpty(filterClientMESHGUID)) {
            if (!org.isEmpty() && org.getIdOfOrg().equals(getPermanentOrgId()) || org.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void clear() {
        if (null == permanentOrgId || null == org || !permanentOrgId.equals(org.getIdOfOrg())) {
            org = new OrgItem();
            permanentOrgId = null;
        }
        contractId = null;
        filterClientId = null;
        person = new PersonItem();
        contractPerson = new PersonItem();
        mobileNumber = null;
        clientCardOwnCondition = ClientCardOwnMenu.NO_CONDITION;
        clientBalanceCondition = ClientBalanceFilter.NO_CONDITION;
        clientGroupId = ClientGroupMenu.CLIENT_ALL;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws HibernateException {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
        else {
            this.org = new OrgItem();
        }
    }

    public List retrieveClients(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Client.class);
        criteria.createAlias("person","p", JoinType.INNER_JOIN);
        addRestrictions(session, criteria);
        return criteria.list();
    }

    public void addRestrictions(Session session, Criteria criteria) throws Exception {
        //  Ограничение на отображение только тех клиентов, которые доступны пользователю
       try {
            criteria.createAlias("org", "o");
            Long idOfUser = MainPage.getSessionInstance().getCurrentUser().getIdOfUser();
            ContextDAOServices.getInstance().buildOrgRestrictionWithAlias(idOfUser, "o.idOfOrg", criteria);
        } catch (Exception e) {
        }
        if (!this.org.isEmpty()) {
            if (includeFriendlyOrg) {
                List<Long> orgIds = DAOUtils.findFriendlyOrgIds(session, org.getIdOfOrg());
                criteria.add(orgIds.isEmpty() ? Restrictions.eq("o.idOfOrg", org.getIdOfOrg())
                        : Restrictions.in("o.idOfOrg", orgIds));
            } else {
                criteria.add(Restrictions.eq("o.idOfOrg", org.getIdOfOrg()));
            }
        }
        if (ClientCardOwnMenu.NO_CONDITION != clientCardOwnCondition) {
            switch (clientCardOwnCondition) {
                case ClientCardOwnMenu.HA_NO_CARD:
                    criteria.add(Restrictions.isEmpty("cardsInternal"));
                    break;
                case ClientCardOwnMenu.HAS_CARD:
                    criteria.add(Restrictions.isNotEmpty("cardsInternal"));
                    break;
            }
        }
        if(ClientBalanceFilter.NO_CONDITION != clientBalanceCondition){
            switch (clientBalanceCondition){
                case ClientBalanceFilter.LT_ZERO:
                    criteria.add(Restrictions.lt("balance",0L));
                    break;
                case ClientBalanceFilter.EQ_ZERO:
                    criteria.add(Restrictions.eq("balance",0L));
                    break;
                case ClientBalanceFilter.GT_ZERO:
                    criteria.add(Restrictions.gt("balance",0L));
                    break;
                case ClientBalanceFilter.NE_ZERO:
                    criteria.add(Restrictions.ne("balance", 0L));
                    break;
            }
        }

        //todo - add more conditions
        if (StringUtils.isNotEmpty(this.contractId)) {
            criteria.add(Restrictions.eq("contractId", Long.parseLong(this.contractId.replaceAll("\\s", ""))));
        }
        if (!this.person.isEmpty()) {
            Person examplePerson = this.person.buildPerson();
            if (StringUtils.isNotEmpty(examplePerson.getSurname())) {
                criteria.add(Restrictions.ilike("p.surname", examplePerson.getSurname(), MatchMode.ANYWHERE));
            }
            if (StringUtils.isNotEmpty(examplePerson.getFirstName())) {
                criteria.add(Restrictions.ilike("p.firstName", examplePerson.getFirstName(), MatchMode.ANYWHERE));
            }
            if (StringUtils.isNotEmpty(examplePerson.getSecondName())) {
                criteria.add(Restrictions.ilike("p.secondName", examplePerson.getSecondName(), MatchMode.ANYWHERE));
            }
            if (StringUtils.isNotEmpty(examplePerson.getIdDocument())) {
                criteria.add(Restrictions.ilike("p.idDocument", examplePerson.getIdDocument(), MatchMode.ANYWHERE));
            }
        }
        if (!this.contractPerson.isEmpty()) {
            Person examplePerson = this.person.buildPerson();
            criteria.add(Restrictions.ilike("p.surname", examplePerson.getSurname(), MatchMode.START));
        }
        if (StringUtils.isNotEmpty(this.filterClientId)) {
            criteria.add(Restrictions.eq("idOfClient", Long.parseLong(this.filterClientId.replaceAll("\\s", ""))));
        }
        if (StringUtils.isNotEmpty(this.mobileNumber)) {
            criteria.add(Restrictions.ilike("mobile", PhoneNumberCanonicalizator.canonicalize(mobileNumber), MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotEmpty(this.email)) {
            criteria.add(Restrictions.ilike("email", email, MatchMode.ANYWHERE));
        }
        if (StringUtils.isNotEmpty(filterClientGUID)) {
            criteria.add(Restrictions.eq("clientGUID", filterClientGUID.trim()));
        }
        if (StringUtils.isNotEmpty(filterClientMESHGUID)) {
            criteria.add(Restrictions.eq("meshGUID", filterClientMESHGUID.trim()));
        }
        criteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
        String cgFieldName = "cg.compositeIdOfClientGroup.idOfClientGroup";
        if (!clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
            if (clientGroupId.equals(ClientGroupMenu.CLIENT_STUDENTS)) {
                criteria.add(Restrictions.not(Restrictions.in(cgFieldName, ClientGroupMenu.getNotStudent())));
            } else {
                criteria.add(Restrictions.eq(cgFieldName, clientGroupId));
            }
        }
        //Для роли оператора карт разрешаем включение клиентов из группы выбывших
        User user = DAOReadonlyService.getInstance().getUserFromSession();
        Object[] condition;
        if (user != null && user.getIdOfRole() == User.DefaultRole.CARD_OPERATOR.ordinal()) {
            condition = new Long[] {ClientGroupMenu.CLIENT_DELETED};
        } else {
            condition = new Long[]{ClientGroupMenu.CLIENT_DELETED, ClientGroupMenu.CLIENT_LEAVING};
        }
        if (!showDeleted && clientGroupId.equals(ClientGroupMenu.CLIENT_ALL)) {
            criteria.add(Restrictions.or(Restrictions.not(Restrictions
                    .in(cgFieldName, condition)),
                    Restrictions.isNull(cgFieldName)));
        }
        if (limit > 0) {
            criteria.setMaxResults(limit);
            criteria.setFirstResult(offset);
        }
       criteria.addOrder(Order.asc("contractId"));
    }
}
