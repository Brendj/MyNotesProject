/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.sms.emp.EMPProcessor;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 26.03.15
 * Time: 18:41
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class EmpInfoPage extends BasicWorkspacePage {

    private String clientMobile = "";
    private List<DataLine> result = new ArrayList<DataLine>();

    public Object populateEntryAttributes() throws Exception {
        if (clientMobile != null) {
            EMPProcessor processor = RuntimeContext.getAppContext().getBean(EMPProcessor.class);
            HashMap<String, List<String>> entryAttributesMap = processor.getEntryAttributesByMobile(clientMobile);
            if (entryAttributesMap.size() > 0) {
                result.clear();
                for (String key : entryAttributesMap.keySet()) {
                    List<String> attributeValues = entryAttributesMap.get(key);
                    for (String value : attributeValues) {
                        if (key.equals("SUBSCRIPTION_ID")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                        if (key.equals("SSOID")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                        if (key.equals("MSISDN")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                        if (key.equals("SMS_SEND")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                        if (key.equals("PUSH_SEND")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                        if (key.equals("EMAIL_SEND")) {
                            DataLine dataLine = new DataLine(key, value);
                            result.add(dataLine);
                        }
                    }
                }
                printMessage(
                        "Данные в ЕМП для записи по номеру телефона {clientMobile: " + clientMobile + "} получены");
            } else {
                printError(
                        "Данные в ЕМП для записи по номеру телефона {clientMobile: " + clientMobile + "} не найдены");
            }
        } else {
            printWarn("Неверно указан номер телефона для поиска");
        }
        return null;
    }

    public String getPageFilename() {
        return "service/emp_info";
    }

    public String getClientMobile() {
        return clientMobile;
    }

    public void setClientMobile(String clientMobile) {
        this.clientMobile = Client.checkAndConvertMobile(clientMobile);
    }

    public List<DataLine> getResult() {
        return result;
    }

    public class DataLine {
        String name;
        String value;

        public DataLine(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }
    }
}
