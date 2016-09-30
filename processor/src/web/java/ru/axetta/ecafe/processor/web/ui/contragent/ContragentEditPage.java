/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.service.GoodRequestsChangeAsyncNotificationService;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ContragentEditPage extends BasicWorkspacePage implements ContragentSelectPage.CompleteHandler {

    public static class PersonItem {

        private String firstName;
        private String surname;
        private String secondName;
        private String idDocument;

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

        public void copyTo(Person person) {
            person.setFirstName(firstName);
            person.setSurname(surname);
            person.setSecondName(secondName);
            person.setIdDocument(idDocument);
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

    public String getPageFilename() {
        return "contragent/edit";
    }


    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ContragentEditPage.class);
    private Long idOfContragent;
    private PersonItem contactPerson;
    private Integer parentId;
    private String contragentName;
    private Integer classId;
    private Integer flags;
    private String title;
    private String address;
    private String phone;
    private String mobile;
    private String email;
    private String requestNotifyMailList;
    private String orderNotifyMailList;
    private String fax;
    private String remarks;
    private String inn;
    private String bank;
    private String bic;
    private String okato;
    private String oktmo;
    private String corrAccount;
    private String account;
    private String publicKey;
    private String publicKeyGOSTAlias;
    private boolean needAccountTranslate;
    private final ContragentClassMenu contragentClassMenu = new ContragentClassMenu();
    private String kpp;
    private String ogrn;
    private Contragent defaultPayContragent;
    private String defaultPayContragentName;
    private Boolean payByCashier;

    public ContragentClassMenu getContragentClassMenu() {
        return contragentClassMenu;
    }

    public Long getIdOfContragent() {
        return idOfContragent;
    }

    public void setIdOfContragent(Long idOfContragent) {
        this.idOfContragent = idOfContragent;
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

    public Integer getFlags() {
        return flags;
    }

    public void setFlags(Integer flags) {
        this.flags = flags;
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

    public String getRequestNotifyMailList() {
        return requestNotifyMailList;
    }

    public void setRequestNotifyMailList(String requestNotifyMailList) {
        this.requestNotifyMailList = requestNotifyMailList;
    }

    public String getOrderNotifyMailList() {
        return orderNotifyMailList;
    }

    public void setOrderNotifyMailList(String orderNotifyMailList) {
        this.orderNotifyMailList = orderNotifyMailList;
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

    public String getPublicKeyGOSTAlias() {
        return publicKeyGOSTAlias;
    }

    public void setPublicKeyGOSTAlias(String publicKeyGOSTAlias) {
        this.publicKeyGOSTAlias = publicKeyGOSTAlias;
    }

    public String getKpp() {
        return kpp;
    }

    public void setKpp(String kpp) {
        this.kpp = kpp;
    }

    public String getOgrn() {
        return ogrn;
    }

    public void setOgrn(String ogrn) {
        this.ogrn = ogrn;
    }

    public boolean isNeedAccountTranslate() {
        return needAccountTranslate;
    }

    public void setNeedAccountTranslate(boolean needAccountTranslate) {
        this.needAccountTranslate = needAccountTranslate;
    }

    public String getOkato() {
        return okato;
    }

    public void setOkato(String okato) {
        this.okato = okato;
    }

    public String getOktmo() {
        return oktmo;
    }

    public void setOktmo(String oktmo) {
        this.oktmo = oktmo;
    }

    public void fill(Session session, Long idOfContragent) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        fill(contragent);
    }

    public void updateContragent(Session session, Long idOfContragent) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, this.idOfContragent);
        Person contractPerson = contragent.getContactPerson();
        this.contactPerson.copyTo(contractPerson);
        contragent.setParentId(this.parentId);
        contragent.setContragentName(this.contragentName.trim());
        contragent.setClassId(this.classId);
        contragent.setFlags(this.flags);
        contragent.setTitle(this.title);
        contragent.setAddress(this.address);
        contragent.setPhone(this.phone);
        contragent.setMobile(this.mobile);
        contragent.setEmail(this.email);
        contragent.setRequestNotifyMailList(requestNotifyMailList);
        contragent.setOrderNotifyMailList(orderNotifyMailList);
        contragent.setFax(this.fax);
        contragent.setRemarks(this.remarks.trim());
        contragent.setInn(this.inn.trim());
        contragent.setBank(this.bank.trim());
        contragent.setBic(this.bic.trim());
        contragent.setOkato(this.okato.trim());
        contragent.setOktmo(this.oktmo.trim());
        contragent.setCorrAccount(this.corrAccount.trim());
        contragent.setAccount(this.account.trim());
        contragent.setUpdateTime(new Date());
        contragent.setPublicKey(this.publicKey);
        contragent.setPublicKeyGOSTAlias(this.publicKeyGOSTAlias);
        contragent.setNeedAccountTranslate(this.needAccountTranslate);
        contragent.setKpp(kpp.trim());
        contragent.setOgrn(ogrn.trim());

        contragent.setDefaultPayContragent(defaultPayContragent);
        if(defaultPayContragent == null && payByCashier){
            throw new IllegalArgumentException("Укажите платежного контрагента по умолчанию");
        } else {
            contragent.setPayByCashier(payByCashier);
        }
        session.update(contragent);
        GoodRequestsChangeAsyncNotificationService.getInstance().updateContragentItem(session, contragent);
        fill(contragent);
    }

    public void fill(Contragent contragent) throws Exception {
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
        this.requestNotifyMailList = contragent.getRequestNotifyMailList();
        this.orderNotifyMailList = contragent.getOrderNotifyMailList();
        this.fax = contragent.getFax();
        this.remarks = contragent.getRemarks();
        this.inn = contragent.getInn();
        this.bank = contragent.getBank();
        this.bic = contragent.getBic();
        this.okato = contragent.getOkato();
        this.oktmo = contragent.getOktmo();
        this.corrAccount = contragent.getCorrAccount();
        this.account = contragent.getAccount();
        this.publicKey = contragent.getPublicKey();
        this.publicKeyGOSTAlias = contragent.getPublicKeyGOSTAlias();
        this.needAccountTranslate = contragent.getNeedAccountTranslate();
        this.kpp = contragent.getKpp();
        this.ogrn = contragent.getOgrn();
        if (contragent.getDefaultPayContragent() != null) {
            this.defaultPayContragentName = contragent.getDefaultPayContragent().getContragentName();
            this.defaultPayContragent = contragent.getDefaultPayContragent();
        }else {
            this.defaultPayContragentName = null;
            this.defaultPayContragent = null;
        }

        if (contragent.isPayByCashier() == null) {
            this.payByCashier = false;
        }else {
            this.payByCashier = contragent.isPayByCashier();
        }
    }


    public Boolean updateContragentRNIP (Session session, Long idOfContragent, String prevId) throws Exception {
        Contragent contragent = (Contragent) session.load(Contragent.class, this.idOfContragent);
        // Получаем id в РНИП, который был там до изменения (если он изменится или отсутствует,
        // то необходимо пересоздаваить каталог в самом РНИП)
        RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getRNIPServiceBean();
        String id = rnipLoadPaymentsService.getRNIPIdFromRemarks (this.remarks);
        if (isEmpty (id)) {
            return null;
            //throw new IllegalStateException("Необходимо указать РНИП идентификатор в примечаниях контрагента. Формат: {RNIP=идентификатор_в_РНИП}");
        }
        /*if(!isEmpty(prevId) && prevId.equals(id)) {
            return null;
        }*/
        if (isEmpty (contragent.getContragentName())) {
            throw new IllegalStateException("Необходимо указать наименование Контрагента");
        }
        if (isEmpty(contragent.getBank())) {
            throw new IllegalStateException("Необходимо указать Банк");
        }
        if (isEmpty(contragent.getAccount())) {
            throw new IllegalStateException("Необходимо указать номер счета");
        }
        if (isEmpty(contragent.getInn())) {
            throw new IllegalStateException("Необходимо указать ИНН");
        }
        if (isEmpty(contragent.getKpp())) {
            throw new IllegalStateException("Необходимо указать КПП");
        }
        if (isEmpty(contragent.getOgrn())) {
            throw new IllegalStateException("Необходимо указать ОГРН");
        }
        if (isEmpty(contragent.getOkato())) {
            throw new IllegalStateException("Необходимо указать ОКАТО");
        }
        if (isEmpty(contragent.getOktmo())) {
            throw new IllegalStateException("Необходимо указать ОКТМО");
        }
        if (isEmpty(contragent.getCorrAccount())) {
            throw new IllegalStateException("Необходимо указать номер Коррсчета");
        }
        if (isEmpty(contragent.getBic())) {
            throw new IllegalStateException("Необходимо указать БИК");
        }


        try {
            if (isEmpty(prevId)) {
                rnipLoadPaymentsService.createCatalogForContragent(contragent);
            }
            else {
                rnipLoadPaymentsService.modifyCatalogForContragent(contragent);
            }
            return Boolean.TRUE;
        } catch (IllegalStateException ise) {
            logger.error("Failed to update contragent in RNIP", ise);
            return Boolean.FALSE;
        } catch (Exception e) {
            logger.error("Failed to update contragent in RNIP", e);
            return Boolean.FALSE;
        }
    }


    public String getRNIPButtonLabel () {
        RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getRNIPServiceBean();
        String id = rnipLoadPaymentsService.getRNIPIdFromRemarks (this.remarks);
        if (id == null) {
            return "Создать каталог в РНИП";
        }
        else {
            return "Сохранить каталог в РНИП";
        }
    }

    public static final boolean isEmpty (String str) {
        if (str == null || str.length() < 1) {
            return true;
        }
        return false;
    }

    public boolean isTSP(){
        return classId == Contragent.TSP;
    }

    public void setDefaultPayContragent(Contragent defaultPayContragent) {
        this.defaultPayContragent = defaultPayContragent;
    }

    public String getDefaultPayContragentName() {
        return defaultPayContragentName;
    }

    public Boolean getPayByCashier() {
        return payByCashier;
    }

    public void setPayByCashier(Boolean payByCashier) {
        this.payByCashier = payByCashier;
    }

    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlags, String classTypes) throws Exception {
        if (null != idOfContragent) {
            this.defaultPayContragent = (Contragent) session.load(Contragent.class, idOfContragent);
            this.defaultPayContragentName = this.defaultPayContragent.getContragentName();
        }
    }

    public void cancelContragentSelection() {
        this.defaultPayContragent = null;
        this.defaultPayContragentName = null;
    }
}