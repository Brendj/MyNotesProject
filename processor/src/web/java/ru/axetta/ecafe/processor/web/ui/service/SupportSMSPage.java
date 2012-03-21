/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.stereotype.Component;

@Component
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
            sentMessageId = RuntimeContext.getInstance().getSmsService().sendTextMessage(null, address, text).getMessageId();
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
