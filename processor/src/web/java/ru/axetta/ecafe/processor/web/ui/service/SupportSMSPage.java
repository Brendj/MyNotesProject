/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.sms.emp.EMPSmsServiceImpl;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.math.NumberUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;

@Component
@Scope("session")
public class SupportSMSPage extends BasicWorkspacePage {
    String address, text;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSent() {
        return sentMessageId!=null;
    }

    public void send() {
        try {
            if(!NumberUtils.isNumber(address)) {
                printError("Для ЕМП адрес должен быть ссылкой на клиента (номер лицевого счета). Подано: " + address);
                return;
            }
            long contractId = NumberUtils.toLong(address);
            Client client = DAOReadonlyService.getInstance().getClientByContractId(contractId);
            if(client == null) {
                printError("Клиент с номером лицевого счета " + contractId + " не найден");
                return;
            }

            if(RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl) {
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                String empTime = df.format(new Date(System.currentTimeMillis()));
                String [] values = new String[]{"empTime", empTime};
                values = EventNotificationService.attachGuardianIdToValues(client.getIdOfClient(), values);
                values = EventNotificationService.attachEventDirectionToValues(EnterEvent.ENTRY, values);
                values = EventNotificationService.attachTargetIdToValues(1L, values);

                RuntimeContext.getAppContext().getBean(EventNotificationService.class).
                        sendSMS(client, null, EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN, values, true,
                                EnterEvent.ENTRY, client, new Date());
            } else {
                String phone = client.getMobile();
                sentMessageId = RuntimeContext.getInstance().getSmsService().sendTextMessage(null, phone, text).getMessageId();
            }
            printMessage("Сообщение отправлено");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при отправке", e);
        }
    }
    public void updateStatus() {
        try {
            deliveryStatus = RuntimeContext.getInstance().getSmsService().getDeliveryStatus(sentMessageId).getStatusMessage();
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при получении статуса", e);
        }
    }

    String sentMessageId;
    String deliveryStatus;
    public String getDeliveryStatus() {
        return deliveryStatus;
    }



    @Override
    public void onShow() throws Exception {
        sentMessageId = null;
    }

    public String getPageFilename() {
        return "service/support_sms";
    }


}
