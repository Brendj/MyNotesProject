/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.cxf.common.util.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by i.semenov on 27.03.2017.
 */
@Component
@Scope("session")
public class NSIOrgsRegistrySynchSettingsPage extends BasicWorkspacePage {

    protected String regionsString;
    private String founder;
    private String industry;

    public String getPageFilename() {
        return "service/msk/nsi_orgs_registry_sync_settings_page";
    }

    public String getPageTitle() {
        return "Параметры сверки организаций с Реестрами";
    }

    public NSIOrgsRegistrySynchSettingsPage() {
        regionsString = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_REGIONS_FROM_NSI);
        founder = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_FOUNDER_FROM_NSI);
        industry = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_INDUSTRY_FROM_NSI);
    }

    public void saveSettings() {
        try {
            String[] a = regionsString.split(",");
            if (a.length > 0 && StringUtils.isEmpty(a[0])) throw new Exception("regions");
        } catch (Exception e) {
            printError("Список округов ожидается в виде перечисления названий через запятую и не должен быть пустым!");
            return;
        }

        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_REGIONS_FROM_NSI, regionsString);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_FOUNDER_FROM_NSI, founder);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_INDUSTRY_FROM_NSI, industry);
        RuntimeContext.getInstance().setOptionValueWithSave(Option.OPTION_LAST_CHANGE_FROM_NSI, getLastChange());
    }

    public String getWho() {
        String s = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_LAST_CHANGE_FROM_NSI);
        try {
            String[] arr = s.split("\\|");
            return "* последнее изменение: " + arr[0] + " | " + arr[1];
        } catch (Exception e) {
            return "";
        }
    }

    public String getFounder() {
        return founder;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    private String getLastChange() {
        User user = DAOReadonlyService.getInstance().getUserFromSession();
        String username = user.getUserName();
        return username + "|" + CalendarUtils.dateTimeToString(new Date());
    }

    public String getRegionsString() {
        return regionsString;
    }

    public void setRegionsString(String regionsString) {
        this.regionsString = regionsString;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }
}
