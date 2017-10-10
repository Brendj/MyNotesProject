/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by i.semenov on 28.09.2017.
 */
@Component
@Scope("session")
public class CardSignViewPage extends BasicWorkspacePage {
    private Integer idOfCardSign;
    private String signType;
    private byte[] signData;
    private Integer manufacturerCode;
    private String manufacturerName;

    @Autowired
    CardSignGroupPage groupPage;

    public String getPageFilename() {
        return "card/sign/view";
    }

    @Override
    public void onShow() throws Exception {
        fill();
    }

    private void fill() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            idOfCardSign = groupPage.getCurrentCard().getIdOfCardSign();
            CardSign cardSign = (CardSign)session.get(CardSign.class, idOfCardSign);
            signType = CardSign.CARDSIGN_TYPES[cardSign.getSignType()];
            signData = cardSign.getSignData();
            manufacturerCode = cardSign.getManufacturerCode();
            manufacturerName = cardSign.getManufacturerName();
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, getLogger());
            HibernateUtils.close(session, getLogger());
        }
    }

    public String getSignDataSize() {
        if (signData == null) return "{нет}";
        return String.format("{Двоичные данные, размер %s байт}", signData.length);
    }

    public Integer getIdOfCardSign() {
        return idOfCardSign;
    }

    public void setIdOfCardSign(Integer idOfCardSign) {
        this.idOfCardSign = idOfCardSign;
    }

    public String getSignType() {
        return signType;
    }

    public void setSignType(String signType) {
        this.signType = signType;
    }

    public byte[] getSignData() {
        return signData;
    }

    public void setSignData(byte[] signData) {
        this.signData = signData;
    }

    public Integer getManufacturerCode() {
        return manufacturerCode;
    }

    public void setManufacturerCode(Integer manufacturerCode) {
        this.manufacturerCode = manufacturerCode;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }
}
