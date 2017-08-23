/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.spb;

import ru.axetta.ecafe.processor.core.service.ImportRegisterSpbClientsService;
import ru.axetta.ecafe.processor.web.ui.service.msk.NSIOrgRegistrySynchPageBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 10.04.17
 * Time: 10:37
 */
@Component("SpbRegistrySynchPageBase")
@Scope("session")
public class SpbRegistrySynchPageBase extends NSIOrgRegistrySynchPageBase {
    Logger logger = LoggerFactory.getLogger(SpbRegistrySynchPageBase.class);

    public String getPageFilename() {
        return "service/spb/spb_registry_sync_page";
    }

    public void createCards() {
        long idOfOrg = getIdOfOrg();
        if (idOfOrg == -1) {
            errorMessages = "Выберите организацию";
            return;
        }
        try {
            int amount = ImportRegisterSpbClientsService.createSpbCards(idOfOrg);
            infoMessages = String.format("Операция выполнена, создано карт: %s", amount);
        } catch (Exception e) {
            errorMessages = "Не удалось сгенерировать карты клиентам ОО. Ошибка: " + e.getMessage();
        }
    }

}