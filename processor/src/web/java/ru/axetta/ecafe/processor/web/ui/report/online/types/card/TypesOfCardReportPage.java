/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online.types.card;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.report.TypesOfCardReport;
import ru.axetta.ecafe.processor.web.ui.client.ClientListPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Алмаз
 * Date: 03.11.14
 * Time: 14:04
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TypesOfCardReportPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(TypesOfCardReportPage.class);
    private TypesOfCardReport report;

    private final ClientListPage clientListPage = new ClientListPage();

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;


    public String getPageFilename() {
        return "report/online/types_of_card_report";
    }

    public TypesOfCardReport getReport() {
        return report;
    }

    public void doGenerate() {
        RuntimeContext.getAppContext().getBean(TypesOfCardReportPage.class).generate();
    }

    public void doGenerateXLS(ActionEvent actionEvent) {
        RuntimeContext.getAppContext().getBean(TypesOfCardReportPage.class).generateXLS();
    }

    @Transactional
    public void generate() {
    }

    @Transactional
    public void generateXLS() {
    }

    public void generateReport(Session session, String templateFile) throws Exception {
    }

    public void generateXLS(Session session) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
    }

    public ClientListPage getClientListPage() {
        return clientListPage;
    }

    public Date getStartDate() {
        return new Date();
    }

}
