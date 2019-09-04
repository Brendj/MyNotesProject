/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.payment.IPaymentNotificator;
import ru.axetta.ecafe.processor.core.persistence.AtolCompany;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.ClientPaymentAddon;
import ru.axetta.ecafe.processor.core.persistence.Contragent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by i.semenov on 26.08.2019.
 */
@Component
@Scope("singleton")
public class AtolPaymentNotificator implements IPaymentNotificator {

    private static final Logger logger = LoggerFactory.getLogger(IPaymentNotificator.class);

    public final static Integer ATOL_NEW = 0;
    public final static Integer ATOL_SENT = 1;
    public final static Integer LIMIT_PER_RUN = 500;
    private List<Long> contragentsToSave = null;

    @Override
    public boolean isToSave(ClientPayment clientPayment) {
        try {
            if (contragentsToSave == null) {
                initContragentsToSave();
            }
            return contragentsToSave.contains(clientPayment.getContragent().getIdOfContragent());
        } catch (Exception e) {
            logger.error("Error in save client payment to additional notifications table", e);
        }
        return false;
    }

    private void initContragentsToSave() throws Exception {
        AtolCompany atolCompany = AtolDAOService.getInstance().getAtolCompany();
        contragentsToSave = new ArrayList<>();
        for (Contragent contragent : atolCompany.getContragents()) {
            contragentsToSave.add(contragent.getIdOfContragent());
        }
    }

    @Override
    public void sendNotifications() {
        logger.info("Start ATOL payment notifications task");
        List<ClientPaymentAddon> list = AtolDAOService.getInstance().getPaymentsToSend();
        for (ClientPaymentAddon clientPaymentAddon : list) {
            sendNotification(clientPaymentAddon);
        }
        logger.info("End ATOL payment notifications task");
    }

    private void sendNotification(ClientPaymentAddon clientPaymentAddon) {
        RuntimeContext.getAppContext().getBean(AtolService.class).sendPayment(clientPaymentAddon);
    }

    @Override
    public void addNotificatorValues(ClientPaymentAddon clientPaymentAddon, ClientPayment clientPayment) {
        clientPaymentAddon.setAtolStatus(isToSave(clientPayment) ? ATOL_NEW : ATOL_SENT);
        clientPaymentAddon.setAtolUpdate(new Date());
    }
}
