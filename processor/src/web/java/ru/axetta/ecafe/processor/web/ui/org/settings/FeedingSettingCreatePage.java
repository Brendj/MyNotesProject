/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
public class FeedingSettingCreatePage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private String settingName;
    private Long limit;
    private Long discount;
    private Boolean useDiscount;
    private Set<Org> orgs;
    private static final Logger logger = LoggerFactory.getLogger(FeedingSettingCreatePage.class);
    private List<Long> idOfOrgList = new ArrayList<Long>();
    private String filter = "Не выбрано";

    public String getPageFilename() {
        return "org/settings/feeding_create";
    }

    public String getPageTitle() {
        return "Создание";
    }

    public void createFeedingSetting() {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            if (limit == 0L) limit = null;
            if (discount == 0L) discount = null;
            if (limit == null && discount == null) {
                printError("Сумма лимита или сумма скидки обязательно должны быть заполнены.");
                return;
            }
            FeedingSetting setting = new FeedingSetting(settingName, limit, discount, useDiscount, new HashSet<Org>());
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
            persistenceSession.save(setting);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            printMessage("Настройка сохранена");
            clear();
        } catch (Exception e) {
            printError("Ошибка при сохранении настройки платного питания: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void clear() {
        settingName = "";
        limit = null;
        idOfOrgList.clear();
        filter = "Не выбрано";
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
}
