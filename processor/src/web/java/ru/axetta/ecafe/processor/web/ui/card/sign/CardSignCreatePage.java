/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.sign;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CryptoSign;
import ru.axetta.ecafe.processor.core.persistence.CardSign;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.List;

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

    @Override
    public void onShow() throws Exception {
        this.setSignData(null);
        this.setSignData(null);
    }

    public void createCardSign(Long type) throws Exception {
        logger.info("Старт создания поставщика карт");
        if (signData == null || manufacturerCode == null || manufacturerCode == 0 || StringUtils.isEmpty(manufacturerName)) {
            printError("Все поля на форме обязательны для заполнения. Файл с данными ключа также должен быть загружен");
            return;
        }
        if (getManufacturerCode() < 1 || getManufacturerCode() > 256)
        {
            printError("Код поставщика карты должен быть не более 256");
            return;
        }
        //Только для нового типа поставщика
        if (newProvider) {
            List<CardSign> cardsignList = DAOReadonlyService.getInstance().findCardsignByManufactureCodeForNewTypeProvider(manufacturerCode);
            //Если есть хоть одна запись....
            if (!cardsignList.isEmpty()) {
                printError("Поставщик с данным кодом производителя уже зарегистрирован");
                return;
            }
        }

        Session session = null;
        Transaction transaction = null;
        //Генерация ключей для подписи и проверки карт
        KeyPair pair = CryptoSign.keyPairGen();
        PrivateKey privateKey = pair.getPrivate();
        PublicKey publicKey = pair.getPublic();
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            CardSign cardSign = new CardSign();
            cardSign.setSignType(new Integer(signTypeCard));
            cardSign.setManufacturerCode(getManufacturerCode());
            cardSign.setManufacturerName(getManufacturerName());

            //type = 1 только у поставщиков нового типа
            if (type == 1) {
                cardSign.setSignData(publicKey.getEncoded());
                cardSign.setPublickeyprovider(signData);
                cardSign.setSigntypeprov(new Integer(signTypeProvider));
                cardSign.setPrivatekeycard(privateKey.getEncoded());
                cardSign.setNewtypeprovider(true);
            }
            else {
                cardSign.setSignData(signData);
                cardSign.setNewtypeprovider(false);
            }
            session.save(cardSign);
            transaction.commit();
            transaction = null;
            logger.info(String.format("Создан поставщик карт %s", cardSign.getIdOfCardSign()));
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
