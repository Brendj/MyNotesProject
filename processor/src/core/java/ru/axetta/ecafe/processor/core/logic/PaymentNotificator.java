/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianNotificationSetting;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadExternalsService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CurrencyStringUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.List;

/**
 * Created by i.semenov on 22.02.2018.
 */
@Component
@Scope("singleton")
public class PaymentNotificator {
    private static final Logger logger = LoggerFactory.getLogger(PaymentNotificator.class);

    public void sendNotification(ClientPayment clientPayment, Client client, Integer subBalanceNum) {
        try {
            String contractId;
            Long balance;
            if (subBalanceNum == null || subBalanceNum.equals(0)) {
                contractId = String.valueOf(client.getContractId());
                balance = client.getBalance();
            } else {
                contractId = client.getContractId() + "01";
                balance = client.getSubBalance1() == null ? 0L : client.getSubBalance1();
            }
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
            String empTime = df.format(clientPayment.getCreateTime());
            Person person = DAOReadExternalsService.getInstance().findPerson(client.getPerson().getIdOfPerson());

            String[] values = new String[]{
                    "paySum", CurrencyStringUtils.copecksToRubles(clientPayment.getPaySum()), "balance",
                    CurrencyStringUtils.copecksToRubles(balance), "contractId", contractId, "surname", person.getSurname(),
                    "firstName", person.getFirstName(), "empTime", empTime};
            values = EventNotificationService.attachTargetIdToValues(clientPayment.getIdOfClientPayment(), values);

            final EventNotificationService eventNotificationService = RuntimeContext.getAppContext().getBean(EventNotificationService.class);
            eventNotificationService
                    .sendNotificationAsync(client, null, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values,
                            clientPayment.getCreateTime());

            List<Client> guardians = DAOReadExternalsService.getInstance()
                    .findGuardiansByClient(client.getIdOfClient(), null);

            if (!(guardians == null || guardians.isEmpty())) {
                for (Client destGuardian : guardians) {
                    if (DAOReadExternalsService.getInstance()
                            .allowedGuardianshipNotification(destGuardian.getIdOfClient(), client.getIdOfClient(),
                                    ClientGuardianNotificationSetting.Predefined.SMS_NOTIFY_REFILLS.getValue())) {
                        eventNotificationService.sendNotificationAsync(destGuardian, client, EventNotificationService.NOTIFICATION_BALANCE_TOPUP, values,
                                clientPayment.getCreateTime());
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error send payment notification: ", e);
        }
    }
}
