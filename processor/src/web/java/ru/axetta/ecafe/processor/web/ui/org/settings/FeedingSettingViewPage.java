/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Set;

/**
 * Created by i.semenov on 15.09.2017.
 */
public class FeedingSettingViewPage extends BasicWorkspacePage {

    private Long idOfSetting;
    private String settingName;
    private Long limit;
    private Long discount;
    private Boolean useDiscount;
    private Date lastUpdate;
    private Set<Org> orgs;
    private String userName;
    private String orgName;
    private static final Logger logger = LoggerFactory.getLogger(FeedingSettingViewPage.class);

    public void fill(Session session) {
        FeedingSetting setting = (FeedingSetting)session.load(FeedingSetting.class, idOfSetting);
        this.settingName = setting.getSettingName();
        this.limit = setting.getLimit();
        this.discount = setting.getDiscount();
        this.useDiscount = setting.getUseDiscount();
        this.lastUpdate = setting.getLastUpdate();
        this.userName = setting.getUser().getUserName();
        orgName = "";
        if (setting.getOrgsInternal() == null || setting.getOrgsInternal().size() == 0) return;
        for (Org org : setting.getOrgsInternal()) {
            orgName += org.getShortName() + ", ";
        }
        orgName = orgName.substring(0, orgName.length()-2);
    }

    public void onShow() throws Exception {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            fill(persistenceSession);
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            printError("Ошибка при подготовке страницы просмотра настроек платного питания: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public String getPageFilename() {
        return "org/settings/feeding_view";
    }

    public String getPageTitle() {
        return "Настройки платного питания";
    }


    public Long getIdOfSetting() {
        return idOfSetting;
    }

    public void setIdOfSetting(Long idOfSetting) {
        this.idOfSetting = idOfSetting;
    }

    public String getSettingName() {
        return settingName;
    }

    public void setSettingName(String settingName) {
        this.settingName = settingName;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public Long getDiscount() {
        return discount;
    }

    public void setDiscount(Long discount) {
        this.discount = discount;
    }

    public Boolean getUseDiscount() {
        return useDiscount;
    }

    public void setUseDiscount(Boolean useDiscount) {
        this.useDiscount = useDiscount;
    }
}
