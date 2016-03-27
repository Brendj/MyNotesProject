/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.awt.event.ActionEvent;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 25.03.16
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class InteractiveCardDataReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(InteractiveCardDataReportPage.class);
    private InteractiveCardDataReport report;

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    public String getPageFilename() {
        return "report/online/interactive_card_data_report";
    }

    public InteractiveCardDataReport getReport() {
        return report;
    }

    public void doGenerate() {

    }

    public void doGenerateXLS(ActionEvent actionEvent) {

    }


}
