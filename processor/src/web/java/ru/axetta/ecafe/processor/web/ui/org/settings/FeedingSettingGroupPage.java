/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.settings;

import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class FeedingSettingGroupPage extends BasicWorkspacePage {

    private String title;

    public String getTitle() {
        return title;
    }

    public void fill(Session session, Long idOfFeedingSetting) throws Exception {
        FeedingSetting setting = (FeedingSetting) session.load(FeedingSetting.class, idOfFeedingSetting);
        if (null == setting) {
            this.title = null;
        } else {
            this.title = setting.getSettingName();
        }
    }

}