/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.persistence.CategoryDiscount;
import ru.axetta.ecafe.processor.core.report.AllOrgsDiscountsReport;

import org.hibernate.Session;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.02.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class AllOrgsDiscountsReportPage extends OnlineReportPage {
    private AllOrgsDiscountsReport allOrgsDiscountsReport;


    public String getPageFilename() {
        return "report/online/orgs_discounts_report";
    }

    public AllOrgsDiscountsReport getAllOrgsDiscountsReport() {
        return allOrgsDiscountsReport;
    }

    public void buildReport(Session session) throws Exception {
        //allOrgsDiscountsReport = new AllOrgsDiscountsReport();
        AllOrgsDiscountsReport.Builder builder = new AllOrgsDiscountsReport.Builder();
        allOrgsDiscountsReport = builder.build(session);
    }


}
