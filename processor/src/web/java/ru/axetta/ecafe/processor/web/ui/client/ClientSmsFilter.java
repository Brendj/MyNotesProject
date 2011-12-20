/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.client.ContractIdFormat;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.utils.AbbreviationUtils;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.Criteria;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 17.07.2009
 * Time: 12:30:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientSmsFilter {

    public static class ClientItem {

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

            public PersonItem() {
                this.firstName = null;
                this.surname = null;
                this.secondName = null;
                this.idDocument = null;
            }

            public PersonItem(Person person) {
                this.firstName = person.getFirstName();
                this.surname = person.getSurname();
                this.secondName = person.getSecondName();
                this.idDocument = person.getIdDocument();
            }
        }

        private Long idOfClient;
        private String orgShortName;
        private PersonItem person;
        private PersonItem contractPerson;
        private Long contractId;
        private Date contractTime;
        private Integer contractState;

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }

        public String getOrgShortName() {
            return orgShortName;
        }

        public void setOrgShortName(String orgShortName) {
            this.orgShortName = orgShortName;
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

        public Long getContractId() {
            return contractId;
        }

        public void setContractId(Long contractId) {
            this.contractId = contractId;
        }

        public Date getContractTime() {
            return contractTime;
        }

        public void setContractTime(Date contractTime) {
            this.contractTime = contractTime;
        }

        public Integer getContractState() {
            return contractState;
        }

        public void setContractState(Integer contractState) {
            this.contractState = contractState;
        }

        public String getShortName() {
            if (null == contractId) {
                return "";
            }
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(ContractIdFormat.format(contractId)).append(" (")
                    .append(AbbreviationUtils.buildAbbreviation(contractPerson.getFirstName(),
                            contractPerson.getSurname(), contractPerson.getSecondName())).append("): ")
                    .append(AbbreviationUtils.buildAbbreviation(person.getFirstName(), person.getSurname(),
                            person.getSecondName()));
            return stringBuilder.toString();
        }

        public ClientItem() {
            this.idOfClient = null;
            this.orgShortName = null;
            this.person = new PersonItem();
            this.contractPerson = new PersonItem();
            this.contractId = null;
            this.contractTime = null;
            this.contractState = null;
        }

        public boolean isEmty() {
            return null == idOfClient;
        }

        public ClientItem(Client client) {
            this.idOfClient = client.getIdOfClient();
            this.orgShortName = client.getOrg().getShortName();
            this.person = new PersonItem(client.getPerson());
            this.contractPerson = new PersonItem(client.getContractPerson());
            this.contractId = client.getContractId();
            this.contractTime = client.getContractTime();
            this.contractState = client.getContractState();
        }
    }

    private String idOfSms;
    private ClientItem client = new ClientItem();
    private int deliveryStatus;
    private final SmsDeliveryFilterMenu smsDeliveryFilterMenu = new SmsDeliveryFilterMenu();
    private Date startTime;
    private Date endTime;

    public ClientItem getClient() {
        return client;
    }

    public void setClient(ClientItem client) {
        this.client = client;
    }

    public Integer getDeliveryStatus() {
        return deliveryStatus;
    }

    public void setDeliveryStatus(Integer deliveryStatus) {
        this.deliveryStatus = deliveryStatus;
    }

    public SmsDeliveryFilterMenu getSmsDeliveryFilterMenu() {
        return smsDeliveryFilterMenu;
    }

    public String getIdOfSms() {
        return idOfSms;
    }

    public void setIdOfSms(String idOfSms) {
        this.idOfSms = idOfSms;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public ClientSmsFilter() {
        this.endTime = new Date();
        this.startTime = DateUtils.addHours(this.endTime, -1);
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(idOfSms) && client.isEmty() && 0 == deliveryStatus && null == startTime
                && null == endTime;
    }

    public String getStatus() {
        if (isEmpty()) {
            return "нет";
        }
        return "установлен";
    }

    public void clear() {
        idOfSms = null;
        client = new ClientItem();
        deliveryStatus = 0;
        startTime = null;
        endTime = null;
    }

    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        if (null != idOfClient) {
            Client client = (Client) session.load(Client.class, idOfClient);
            this.client = new ClientItem(client);
        }
    }

    public List retrieveClientSms(Session session) throws Exception {
        Criteria criteria = session.createCriteria(ClientSms.class);
        if (StringUtils.isNotEmpty(this.idOfSms)) {
            criteria.add(Restrictions.eq("idOfSms", this.idOfSms));
        }
        if (!this.client.isEmty()) {
            Client client = (Client) session.get(Client.class, this.client.getIdOfClient());
            criteria.add(Restrictions.eq("client", client));
        }
        if (0 != deliveryStatus) {
            criteria.add(Restrictions.eq("deliveryStatus", this.deliveryStatus - 1));
        }
        if (null != this.startTime) {
            criteria.add(Restrictions.ge("serviceSendTime", this.startTime));
        }
        if (null != this.endTime) {
            criteria.add(Restrictions.lt("serviceSendTime", this.endTime));
        }
        return criteria.list();
    }
}