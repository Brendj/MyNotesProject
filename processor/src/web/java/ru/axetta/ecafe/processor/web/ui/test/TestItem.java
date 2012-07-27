/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.test;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.web.ui.paging.AbstractItem;
import ru.axetta.ecafe.processor.web.ui.paging.NarrowProperty;
import ru.axetta.ecafe.processor.web.ui.paging.WideProperty;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 26.07.12
 * Time: 23:49
 * To change this template use File | Settings | File Templates.
 */
public class TestItem extends AbstractItem {


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

    @WideProperty(name = "Идентификатор")
    @NarrowProperty(name = "Идентификатор")
    protected final Long idOfContragent;
    protected final PersonItem contactPerson;
    protected final Integer parentId;
    @WideProperty(name = "Имя контрагента")
    @NarrowProperty(name = "Имя контрагента")
    protected final String contragentName;
    protected final Integer classId;
    protected final Integer flags;
    @WideProperty(name = "Title")
    protected final String title;
    @NarrowProperty(name = "Адрес")
    protected final String address;
    @NarrowProperty(name = "Телефон")
    protected final String phone;
    @WideProperty(name = "Мобильный телефон")
    protected final String mobile;
    @NarrowProperty(name = "Эл. почта")
    protected final String email;
    protected final String fax;
    protected final String remarks;
    protected final String inn;
    protected final String bank;
    protected final String bic;
    protected final String corrAccount;
    protected final String account;
    protected final Date createTime;
    protected final Date updateTime;

    public TestItem(Contragent contragent) {
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

    @Override
    public Long getId() {
        return idOfContragent;
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
