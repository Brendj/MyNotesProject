/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.classic.Session;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ContragentCreatePage extends BasicWorkspacePage {

    public static class PersonItem {

        private String firstName;
        private String surname;
        private String secondName;
        private String idDocument;

        public Person buildPerson() {
            Person person = new Person(firstName, surname, secondName);
            person.setIdDocument(idDocument);
            return person;
        }

        public PersonItem() {
            this.firstName = null;
            this.surname = null;
            this.secondName = null;
            this.idDocument = null;
        }

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
    }

    private PersonItem contactPerson = new PersonItem();
    private Integer parentId;
    private String contragentName;
    private Integer classId;
    private String title;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String fax;
    private String remarks;
    private String inn;
    private String bank;
    private String bic;
    private String corrAccount;
    private String account;
    private String publicKey;
    private boolean needAccountTranslate;
    private final ContragentClassMenu contragentClassMenu = new ContragentClassMenu();

    public String getPageFilename() {
        return "contragent/create";
    }

    public ContragentClassMenu getContragentClassMenu() {
        return contragentClassMenu;
    }

    public PersonItem getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(PersonItem contactPerson) {
        this.contactPerson = contactPerson;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getContragentName() {
        return contragentName;
    }

    public void setContragentName(String contragentName) {
        this.contragentName = contragentName;
    }

    public Integer getClassId() {
        return classId;
    }

    public void setClassId(Integer classId) {
        this.classId = classId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getInn() {
        return inn;
    }

    public void setInn(String inn) {
        this.inn = inn;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }

    public String getCorrAccount() {
        return corrAccount;
    }

    public void setCorrAccount(String corrAccount) {
        this.corrAccount = corrAccount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getShortName() {
        return contragentName;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public boolean isNeedAccountTranslate() {
        return needAccountTranslate;
    }

    public void setNeedAccountTranslate(boolean needAccountTranslate) {
        this.needAccountTranslate = needAccountTranslate;
    }

    public void fill(Session session) throws Exception {
    }

    public void createContragent(Session session) throws Exception {
        // Проверять, чтобы для типов Оператор, Бюждет и Клиент не было создано более одного контрагента
        if (this.classId.equals(3) || this.classId.equals(4) || this.classId.equals(5))
            if (DAOUtils.existContragentWithClass(session, this.classId))
                throw new ContragentWithClassExistsException("Can't create more then 1 contragent for \"Operator\", \"Budget\" and \"Client\" types");
        Person contactPerson = this.contactPerson.buildPerson();
        session.save(contactPerson);
        Date currentTime = new Date();
        Contragent contragent = new Contragent(contactPerson, this.contragentName, this.classId, 1, this.title,
                this.address, currentTime, currentTime, this.publicKey, this.needAccountTranslate);
        contragent.setContactPerson(contactPerson);
        contragent.setParentId(this.parentId);
        contragent.setPhone(this.phone);
        contragent.setMobile(this.mobile);
        contragent.setEmail(this.email);
        contragent.setFax(this.fax);
        contragent.setRemarks(this.remarks);
        contragent.setInn(this.inn);
        contragent.setBank(this.bank);
        contragent.setBic(this.bic);
        contragent.setCorrAccount(this.corrAccount);
        contragent.setAccount(this.account);
        session.save(contragent);
    }

    public class ContragentWithClassExistsException extends Exception {
        public ContragentWithClassExistsException(String e) {
            super(e);
        }
    }

}