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
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by i.semenov on 27.09.2017.
 */
@Component
@Scope("session")
public class CardSignCreatePage extends CardSignDataBasicPage {
    private static final Logger logger = LoggerFactory.getLogger(CardSignCreatePage.class);


    public String getPageFilename() {
        return "card/sign/create";
    }

    public void createCardSign() {
        if (signData == null || manufacturerCode == null || manufacturerCode == 0 || StringUtils.isEmpty(manufacturerName)) {
            printError("Все поля на форме обязательны для заполнения. Файл с данными ключа также должен быть загружен");
            return;
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardSign cardSign = new CardSign();
            cardSign.setSignType(new Integer(signType));
            cardSign.setManufacturerCode(getManufacturerCode());
            cardSign.setManufacturerName(getManufacturerName());
            cardSign.setSignData(signData);
            session.save(cardSign);
            transaction.commit();
            transaction = null;
            printMessage("Запись сохранена");
        } catch (Exception e) {
            logger.error("Error in cardSign create page: ", e);
            printError("При сохранении записи произошла ошибка с текстом: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

}
