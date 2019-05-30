/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.contragent;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.RnipEventType;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.service.RnipDAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class ContragentViewPage extends BasicWorkspacePage {

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

    public String getPageFilename() {
        return "contragent/view";
    }

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
    private Date createTime;
    private Date updateTime;
    private String publicKey;
    private String publicKeyGOSTAlias;
    private boolean needAccountTranslate;
    private String kpp;
    private String ogrn;
    private String defaultPayContragent;
    private boolean payByCashier;

    public boolean hasRnip() {
        RNIPLoadPaymentsService rnipLoadPaymentsService = RNIPLoadPaymentsService.getRNIPServiceBean();
        return !StringUtils.isEmpty(rnipLoadPaymentsService.getRNIPIdFromRemarks (this.remarks));
    }

    public String getRnipLogEdit() {
        List<RnipEventType> list = new ArrayList<>();
        list.add(RnipEventType.CONTRAGENT_CREATE);
        list.add(RnipEventType.CONTRAGENT_EDIT);
        return RuntimeContext.getAppContext().getBean(RnipDAOService.class).getRnipInfoResultString(idOfContragent, list);
    }

    public String getRnipLogPayment() {
        List<RnipEventType> list = new ArrayList<>();
        list.add(RnipEventType.PAYMENT);
        list.add(RnipEventType.PAYMENT_MODIFIED);
        return RuntimeContext.getAppContext().getBean(RnipDAOService.class).getRnipInfoResultString(idOfContragent, list);
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

    public void setRequestNotifyMailList(String requestNotifyMailList) {
        this.requestNotifyMailList = requestNotifyMailList;
    }

    public String getRequestNotifyMailList() {
        return requestNotifyMailList;
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

    public String getPublicKey() {
        return publicKey;
    }

    public String getPublicKeyGOSTAlias() {
        return publicKeyGOSTAlias;
    }

    public boolean isNeedAccountTranslate() {
        return needAccountTranslate;
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
        this.createTime = contragent.getCreateTime();
        this.updateTime = contragent.getUpdateTime();
        this.publicKey = contragent.getPublicKey();
        this.publicKeyGOSTAlias = contragent.getPublicKeyGOSTAlias();
        this.needAccountTranslate = contragent.getNeedAccountTranslate();
        this.kpp = contragent.getKpp();
        this.ogrn = contragent.getOgrn();
        if (contragent.getDefaultPayContragent() != null) {
            this.defaultPayContragent = contragent.getDefaultPayContragent().getContragentName();
        }else {
            this.defaultPayContragent = null;
        }

        if (contragent.isPayByCashier() == null) {
            this.payByCashier = false;
        }else {
            this.payByCashier = contragent.isPayByCashier();
        }
    }

    public boolean isTSP(){
        return classId == Contragent.TSP;
    }

    public String getDefaultPayContragent() {
        return defaultPayContragent;
    }

    public boolean isPayByCashier() {
        return payByCashier;
    }
}