/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Person;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientFilter {

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
            Person person = new Person(firstName, surname, secondName);
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
    private String filterClientId;

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

    public boolean isEmpty() {
        return ClientCardOwnMenu.NO_CONDITION == clientCardOwnCondition && StringUtils.isEmpty(contractId)
                && StringUtils.isEmpty(filterClientId) && org.isEmpty() && person.isEmpty() && contractPerson.isEmpty();
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void clear() {
        org = new OrgItem();
        contractId = null;
        filterClientId = null;
        person = new PersonItem();
        contractPerson = new PersonItem();
        clientCardOwnCondition = ClientCardOwnMenu.NO_CONDITION;
    }

    public void completeOrgSelection(Session session, Long idOfOrg) throws HibernateException {
        if (null != idOfOrg) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            this.org = new OrgItem(org);
        }
    }

    public List retrieveClients(Session session) throws Exception {
        Criteria criteria = session.createCriteria(Client.class);
        addRestrictions(session, criteria);
        return criteria.list();
    }

    public void addRestrictions(Session session, Criteria criteria) throws Exception {
        if (!this.org.isEmpty()) {
            Org org = (Org) session.load(Org.class, this.org.getIdOfOrg());
            criteria.add(Restrictions.eq("org", org));
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
        //todo - add more conditions
        if (StringUtils.isNotEmpty(this.contractId)) {
            criteria.add(Restrictions.eq("contractId", Long.parseLong(this.contractId.replaceAll("\\s", ""))));
        }
        if (!this.person.isEmpty()) {
            Person examplePerson = this.person.buildPerson();
            Criteria personCriteria = criteria.createCriteria("person");
            personCriteria
                    .add(Example.create(examplePerson).excludeZeroes().enableLike(MatchMode.ANYWHERE).ignoreCase());
        }
        if (!this.contractPerson.isEmpty()) {
            Person examplePerson = this.contractPerson.buildPerson();
            Criteria contractPersonCriteria = criteria.createCriteria("contractPerson");
            contractPersonCriteria
                    .add(Example.create(examplePerson).excludeZeroes().enableLike(MatchMode.ANYWHERE).ignoreCase());
        }
        if (StringUtils.isNotEmpty(this.filterClientId)) {
            criteria.add(Restrictions.eq("idOfClient", Long.parseLong(this.filterClientId.replaceAll("\\s", ""))));
        }
    }
}
