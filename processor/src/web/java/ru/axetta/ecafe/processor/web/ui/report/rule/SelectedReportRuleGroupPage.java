/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.rule;

import ru.axetta.ecafe.processor.core.persistence.ReportHandleRule;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 07.06.2010
 * Time: 14:23:22
 * To change this template use File | Settings | File Templates.
 */
public class SelectedReportRuleGroupPage extends BasicWorkspacePage {

    private String title;

    public String getTitle() {
        return title;
    }

    public void fill(Session session, Long idOfReportRule) throws Exception {
        ReportHandleRule reportHandleRule = (ReportHandleRule) session.load(ReportHandleRule.class, idOfReportRule);
        if (null == reportHandleRule) {
            this.title = null;
        } else {
            this.title = ReportRuleConstants.createShortName(reportHandleRule, 50);
        }
    }

}