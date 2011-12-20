/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ContragentListPage extends BasicWorkspacePage {

    public static class PersonItem {

        private final String firstName;
        private final String surname;
        private final String secondName;
        private final String idDocument;

        public PersonItem(Person person) {
            this.firstName = person.getFirstName();
            this.surname = person.getSurname();
            this.secondName = person.getSecondName();
            this.idDocument = person.getIdDocument();
        }

        public String getFirstName() {
            return firstName;
        }

        public String getSurname() {
            return surname;
        }

        public String getSecondName() {
            return secondName;
        }

        public String getIdDocument() {
            return idDocument;
        }
    }

    public static class Item {

        private final Long idOfContragent;
        private final PersonItem contactPerson;
        private final Integer parentId;
        private final String contragentName;
        private final Integer classId;
        private final Integer flags;
        private final String title;
        private final String address;
        private final String phone;
        private final String mobile;
        private final String email;
        private final String fax;
        private final String remarks;
        private final String inn;
        private final String bank;
        private final String bic;
        private final String corrAccount;
        private final String account;
        private final Date createTime;
        private final Date updateTime;

        public Item(Contragent contragent) {
            this.idOfContragent = contragent.getIdOfContragent();
            this.contactPerson = new PersonItem(contragent.getContactPerson());
            this.parentId = contragent.getParentId();
            this.contragentName = contragent.getContragentName();
            this.classId = contragent.getClassId();
            this.flags = contragent.getFlags();
            this.title = contragent.getTitle();
            this.address = contragent.getAddress();
            this.phone = contragent.getPhone();
            this.mobile = contragent.getMobile();
            this.email = contragent.getEmail();
            this.fax = contragent.getFax();
            this.remarks = contragent.getRemarks();
            this.inn = contragent.getInn();
            this.bank = contragent.getBank();
            this.bic = contragent.getBic();
            this.corrAccount = contragent.getCorrAccount();
            this.account = contragent.getAccount();
            this.createTime = contragent.getCreateTime();
            this.updateTime = contragent.getUpdateTime();
        }

        public Long getIdOfContragent() {
            return idOfContragent;
        }

        public PersonItem getContactPerson() {
            return contactPerson;
        }

        public Integer getParentId() {
            return parentId;
        }

        public String getContragentName() {
            return contragentName;
        }

        public Integer getClassId() {
            return classId;
        }

        public Integer getFlags() {
            return flags;
        }

        public String getTitle() {
            return title;
        }

        public String getAddress() {
            return address;
        }

        public String getPhone() {
            return phone;
        }

        public String getMobile() {
            return mobile;
        }

        public String getEmail() {
            return email;
        }

        public String getFax() {
            return fax;
        }

        public String getRemarks() {
            return remarks;
        }

        public String getInn() {
            return inn;
        }

        public String getBank() {
            return bank;
        }

        public String getBic() {
            return bic;
        }

        public String getCorrAccount() {
            return corrAccount;
        }

        public String getAccount() {
            return account;
        }

        public Date getCreateTime() {
            return createTime;
        }

        public Date getUpdateTime() {
            return updateTime;
        }
    }

    private List<Item> items = Collections.emptyList();

    public String getPageFilename() {
        return "contragent/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", items.size());
    }

    public List<Item> getItems() {
        return items;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
        Criteria criteria = session.createCriteria(Contragent.class);
        List contragents = criteria.list();
        for (Object object : contragents) {
            Contragent contragent = (Contragent) object;
            items.add(new Item(contragent));
        }
        this.items = items;
    }

}