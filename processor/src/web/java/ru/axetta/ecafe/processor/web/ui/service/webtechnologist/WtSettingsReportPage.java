/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class WtSettingsReportPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(WtSettingsReportPage.class);

    private Integer daysForbid;

    public String getPageFilename() {
        return "service/webtechnologist/wt_options_page";
    }

    public void onShow() throws Exception {
        daysForbid = DAOReadonlyService.getInstance().getWtDaysForbid();
    }

    public void save() {
        try {
            DAOService.getInstance().setOnlineOptionValue(daysForbid.toString(), Option.OPTION_WT_DAYS_FORBID);
            printMessage("Настройка сохранена");
        } catch (Exception e) {
            printError("Ошибка при сохранении настройки: " + e.getMessage());
            logger.error("Error in save WtSettingsReportPage: ", e);
        }
    }

    public Integer getDaysForbid() {
        return daysForbid;
    }

    public void setDaysForbid(Integer daysForbid) {
        this.daysForbid = daysForbid;
    }
}
