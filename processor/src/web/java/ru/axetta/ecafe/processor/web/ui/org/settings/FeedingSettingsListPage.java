/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by i.semenov on 15.09.2017.
 */

public class FeedingSettingsListPage extends BasicWorkspacePage {

    private List<FeedingSettingsItem> items;
    private static final Logger logger = LoggerFactory.getLogger(FeedingSettingsListPage.class);
    private Long selectedSetting;

    public void fill(Session session) throws Exception {
        if (items == null) {
            items = new ArrayList<FeedingSettingsItem>();
        }
        items.clear();
        //todo todo todo
        User user = DAOReadonlyService.getInstance().getUserFromSession();
        user = (User)session.merge(user);
        Criteria criteria = session.createCriteria(FeedingSetting.class);
        criteria.addOrder(Order.asc("lastUpdate"));
        if (!user.hasFunction(Function.FUNC_FEEDING_SETTINGS_ADMIN)) {
            criteria.add(Restrictions.eq("user", user));
        }
        List<FeedingSetting> list = criteria.list();
        for (FeedingSetting setting : list) {
            items.add(new FeedingSettingsItem(setting));
        }
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
            printError("Ошибка при подготовке страницы настроек платного питания: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public String getPageFilename() {
        return "org/settings/feeding_list";
    }

    public String getPageTitle() {
        return "Настройки платного питания";
    }


    public List<FeedingSettingsItem> getItems() {
        return items;
    }

    public void setItems(List<FeedingSettingsItem> items) {
        this.items = items;
    }

    public Long getSelectedSetting() {
        return selectedSetting;
    }

    public void setSelectedSetting(Long selectedSetting) {
        this.selectedSetting = selectedSetting;
    }
}
