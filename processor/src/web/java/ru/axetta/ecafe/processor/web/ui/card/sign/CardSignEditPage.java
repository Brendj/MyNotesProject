/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by i.semenov on 28.09.2017.
 */
@Component
@Scope("session")
public class CardSignEditPage extends CardSignDataBasicPage {
    private static final Logger logger = LoggerFactory.getLogger(CardSignEditPage.class);
    private Integer idOfCardSign;

    @Autowired
    CardSignGroupPage groupPage;

    public String getPageFilename() {
        return "card/sign/edit";
    }

    @Override
    public void onShow() throws Exception {
        fill();
    }

    private void fill() {
        idOfCardSign = groupPage.getCurrentCard().getIdOfCardSign();
        signTypeCard = CardSignItem.getSignTypeFromString(groupPage.getCurrentCard().getSignType());
        if (groupPage.getCurrentCard().getSignTypeProvider() != null)
            signTypeProvider = CardSignItem.getSignTypeFromString(groupPage.getCurrentCard().getSignTypeProvider());
        else
            signTypeProvider = null;
        manufacturerCode = groupPage.getCurrentCard().getManufacturerCode();
        manufacturerName = groupPage.getCurrentCard().getManufacturerName();
        signData = groupPage.getCurrentCard().getSignData();
        newProvider = groupPage.getCurrentCard().getNewProvider();
    }

    public Object save(boolean newProvider) {
        if (signData == null || manufacturerCode == null || manufacturerCode == 0 || StringUtils.isEmpty(manufacturerName)) {
            printError("Все поля на форме обязательны для заполнения. Файл с данными ключа также должен быть загружен");
            return null;
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardSign cardSign = (CardSign)session.load(CardSign.class, groupPage.getCurrentCard().getIdOfCardSign());
            cardSign.setSignType(new Integer(signTypeCard));
            cardSign.setManufacturerCode(manufacturerCode);
            cardSign.setManufacturerName(manufacturerName);
            if (newProvider) {
                cardSign.setSigntypeprov(new Integer(signTypeProvider));
                cardSign.setPublickeyprovider(signData);
            }
            else
                cardSign.setSignData(signData);
            session.update(cardSign);
            transaction.commit();
            transaction = null;
            printMessage("Запись сохранена");
        } catch (Exception e) {
            logger.error("Error in cardSign edit page: ", e);
            printError("При сохранении записи произошла ошибка с текстом: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return null;
    }

    public Integer getIdOfCardSign() {
        return idOfCardSign;
    }

    public void setIdOfCardSign(Integer idOfCardSign) {
        this.idOfCardSign = idOfCardSign;
    }

}
