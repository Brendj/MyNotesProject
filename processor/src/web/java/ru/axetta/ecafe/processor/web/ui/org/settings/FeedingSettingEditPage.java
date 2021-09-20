/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by i.semenov on 15.09.2017.
 */
public class FeedingSettingEditPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private Long idOfSetting;
    private String settingName;
    private Long limit;
    private Long discount;
    private Boolean useDiscount;
    private Boolean useDiscountBuffet;
    private Date lastUpdate;
    private Set<Org> orgs;
    private String userName;
    private String orgName;
    private static final Logger logger = LoggerFactory.getLogger(FeedingSettingEditPage.class);
    private List<Long> idOfOrgList = new ArrayList<Long>();
    private String filter = "Не выбрано";

    public void fill(Session session) {
        FeedingSetting setting = (FeedingSetting)session.load(FeedingSetting.class, idOfSetting);
        this.settingName = setting.getSettingName();
        this.limit = setting.getLimit() == null ? null : setting.getLimit();
        this.discount = setting.getDiscount() == null ? null : setting.getDiscount();
        this.useDiscount = setting.getUseDiscount();
        this.useDiscountBuffet = setting.getUseDiscountBuffet();
        this.lastUpdate = setting.getLastUpdate();
        this.userName = setting.getUser().getUserName();
        idOfOrgList.clear();
        filter = "Не выбрано";
        if ((setting.getOrgsInternal() == null || setting.getOrgsInternal().size() == 0)) return;
        filter = "";
        for (Org org : setting.getOrgsInternal()) {
            idOfOrgList.add(org.getIdOfOrg());
            filter += org.getShortName() + ", ";
        }
        filter = filter.substring(0, filter.length()-2);
    }

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 1);
            }
        }
    }

    public void updateSetting() {
        if (limit == 0L) limit = null;
        if (discount == 0L) discount = null;
        if (limit == null && discount == null) {
            printError("Сумма лимита или сумма скидки обязательно должны быть заполнены.");
            return;
        }
        if (discount == null) useDiscount = false;

        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            FeedingSetting setting = (FeedingSetting)persistenceSession.load(FeedingSetting.class, idOfSetting);
            setting.setSettingName(settingName);
            setting.setLimit(limit);
            setting.setDiscount(discount);
            setting.setUseDiscount(useDiscount);
            setting.setUseDiscountBuffet(useDiscountBuffet);
            Set<Org> set = new HashSet<Org>();
            for (Long id : idOfOrgList) {
                Org org = (Org) persistenceSession.load(Org.class, id);
                set.add(org);
            }
            setting.setOrgsInternal(set);
            if (!setting.isValidByOrg()) {
                printError("Одна или несколько выбранных организаций уже участвуют в других настройках");
                return;
            }
            setting.setLastUpdate(new Date());
            setting.setUser(DAOReadonlyService.getInstance().getUserFromSession());
            persistenceSession.update(setting);

            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage("Запись сохранена в БД");
        } catch (Exception e) {
            printError("Ошибка при подготовке страницы редактирования настроек платного питания: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public String getPageFilename() {
        return "org/settings/feeding_edit";
    }

    public String getPageTitle() {
        return settingName;
    }

    public String getEntityName() {
        return settingName;
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

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
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

    public Boolean getUseDiscountBuffet() {
        return useDiscountBuffet;
    }

    public void setUseDiscountBuffet(Boolean useDiscountBuffet) {
        this.useDiscountBuffet = useDiscountBuffet;
    }
}
