/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.report.GoodRequestsReport;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 25.04.13
 * Time: 16:47
 * To change this template use File | Settings | File Templates.
 */
public class GoodRequestsReportPage extends OnlineReportWithContragentPage {
    private GoodRequestsReport goodRequests;
    private Boolean hideMissedColumns;
    private boolean showAll = true;
    private int requestsFilter = 1;
    private String goodName;

    public String getPageFilename() {
        return "report/online/good_requests_report";
    }

    public String getGoodName() {
        return goodName;
    }

    public void setGoodName(String goodName) {
        this.goodName = goodName;
    }

    public int getRequestsFilter() {
        return requestsFilter;
    }

    public void setRequestsFilter(int requestsFilter) {
        this.requestsFilter = requestsFilter;
    }

    public GoodRequestsReport getGoodRequestsReport() {
        return goodRequests;
    }

    public Boolean getHideMissedColumns() {
        return hideMissedColumns;
    }

    public void setHideMissedColumns(Boolean hideMissedColumns) {
        this.hideMissedColumns = hideMissedColumns;
    }

    public GoodRequestsReport getGoodRequests() {
        return goodRequests;
    }

    public void setGoodRequests(GoodRequestsReport goodRequests) {
        this.goodRequests = goodRequests;
    }

    public boolean isShowAll() {
        return showAll;
    }

    public void setShowAll(boolean showAll) {
        this.showAll = showAll;
    }

    public void showOrgListSelectPage () {
        setSelectIdOfOrgList(true);
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public void showContragentListSelectPage () {
        setSelectIdOfOrgList(false);
        MainPage.getSessionInstance().showOrgListSelectPage();
    }

    public void buildReport(Session session) throws Exception {
        GoodRequestsReport.Builder reportBuilder = new GoodRequestsReport.Builder();
        this.goodRequests = reportBuilder.build(session, hideMissedColumns, startDate, endDate,
                                                idOfOrgList, idOfContragentOrgList, requestsFilter, goodName);
    }

}
