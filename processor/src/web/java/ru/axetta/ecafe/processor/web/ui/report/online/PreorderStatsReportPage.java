/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope(value = "session")
public class PreorderStatsReportPage extends OnlineReportPage {

    @Override
    public String getPageFilename() {
        return "report/online/preorder_stats";
    }

    public Object showOrgListSelectPage() {
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }

    public void exportToHtml() {
        if (validateFormData()) {
            return;
        }
        preorderStatsReport =(PreorderStatsReport)makeReport();
        htmlReport = preorderStatsReport.getHtmlReport();
    }

}
