/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
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
            if(RuntimeContext.getInstance().getSmsService() instanceof EMPSmsServiceImpl) {
                if(!NumberUtils.isNumber(address)) {
                    printError("Для ЕМП адрес должен быть ссылкой на клиента (idofclient). Подано: " + address);
                    return;
                }
                long idofclient = NumberUtils.toLong(address);
                Client client = DAOService.getInstance().findClientById(idofclient);
                if(client == null) {
                    printError("Клиент с идентификатором " + idofclient + " не найден");
                }

                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
                String empTime = df.format(new Date(System.currentTimeMillis()));
                String [] vals = new String[]{"empTime", empTime};

                RuntimeContext.getAppContext().getBean(EventNotificationService.class).
                        sendSMS(client, EventNotificationService.NOTIFICATION_ENTER_EVENT, vals, true,
                                EnterEvent.ENTRY);
            } else {
                sentMessageId = RuntimeContext.getInstance().getSmsService().sendTextMessage(null, address, text).getMessageId();
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
