/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

@Component
@Scope("session")
public class UosStopListPage extends BasicWorkspacePage {
    @PersistenceContext
    EntityManager em;

    String lastUpdate;
    Long stopListSize;


    @Override
    public String getPageFilename() {
        return "service/msk/stop_list_update";
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getStopListSize() {
        return stopListSize;
    }

    public void setStopListSize(Long stopListSize) {
        this.stopListSize = stopListSize;
    }

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(UosStopListPage.class).loadData();
    }

    @Transactional
    public void loadData() {
        lastUpdate  = DAOUtils.getOptionValue(em, Option.OPTION_STOP_LIST_LAST_UPDATE, "");
        stopListSize = DAOUtils.getBlockedCardsCount(em);
    }

    @Transactional
    public void updateStopList() {
        lastUpdate = CalendarUtils.dateTimeToString(new Date());
        DAOUtils.setOptionValue(em, Option.OPTION_STOP_LIST_LAST_UPDATE, lastUpdate);

        printMessage("Стоп-лист успешно обновлен");
    }
}
