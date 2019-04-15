/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.kzn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.KznClientsStatistic;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.report.kzn.KznClientsStatisticReportItem;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class KznClientsStatisticCreatePage extends OnlineReportPage {

    Logger logger = LoggerFactory.getLogger(KznClientsStatisticCreatePage.class);

    private Long studentsCountTotal;
    private Long studentsCountYoung;
    private Long studentsCountMiddle;
    private Long studentsCountOld;
    private Long benefitStudentsCountYoung;
    private Long benefitStudentsCountMiddle;
    private Long benefitStudentsCountOld;
    private Long benefitStudentsCountTotal;
    private Long employeeCount;

    public void save() {
        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Org org = (Org) session.load(Org.class, idOfOrg);

            KznClientsStatistic kznClientsStatistic = new KznClientsStatistic(org, studentsCountTotal,
                    studentsCountYoung, studentsCountMiddle, studentsCountOld, benefitStudentsCountYoung,
                    benefitStudentsCountMiddle, benefitStudentsCountOld, benefitStudentsCountTotal, employeeCount);
            session.save(kznClientsStatistic);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in reload KznClientsStatisticReportPage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void showOrgSelectPage() {
        MainPage.getSessionInstance().showOrgSelectPage();
    }

    public Object showAddingModalPage() {
        MainPage mainPage = MainPage.getSessionInstance();
        mainPage.getModalPages().push(this);
        return null;
    }

    @Override
    public String getPageFilename() {
        return "service/kzn/statistic/create";
    }

    public Long getStudentsCountTotal() {
        return studentsCountTotal;
    }

    public void setStudentsCountTotal(Long studentsCountTotal) {
        this.studentsCountTotal = studentsCountTotal;
    }

    public Long getStudentsCountYoung() {
        return studentsCountYoung;
    }

    public void setStudentsCountYoung(Long studentsCountYoung) {
        this.studentsCountYoung = studentsCountYoung;
    }

    public Long getStudentsCountMiddle() {
        return studentsCountMiddle;
    }

    public void setStudentsCountMiddle(Long studentsCountMiddle) {
        this.studentsCountMiddle = studentsCountMiddle;
    }

    public Long getStudentsCountOld() {
        return studentsCountOld;
    }

    public void setStudentsCountOld(Long studentsCountOld) {
        this.studentsCountOld = studentsCountOld;
    }

    public Long getBenefitStudentsCountYoung() {
        return benefitStudentsCountYoung;
    }

    public void setBenefitStudentsCountYoung(Long benefitStudentsCountYoung) {
        this.benefitStudentsCountYoung = benefitStudentsCountYoung;
    }

    public Long getBenefitStudentsCountMiddle() {
        return benefitStudentsCountMiddle;
    }

    public void setBenefitStudentsCountMiddle(Long benefitStudentsCountMiddle) {
        this.benefitStudentsCountMiddle = benefitStudentsCountMiddle;
    }

    public Long getBenefitStudentsCountOld() {
        return benefitStudentsCountOld;
    }

    public void setBenefitStudentsCountOld(Long benefitStudentsCountOld) {
        this.benefitStudentsCountOld = benefitStudentsCountOld;
    }

    public Long getBenefitStudentsCountTotal() {
        return benefitStudentsCountTotal;
    }

    public void setBenefitStudentsCountTotal(Long benefitStudentsCountTotal) {
        this.benefitStudentsCountTotal = benefitStudentsCountTotal;
    }

    public Long getEmployeeCount() {
        return employeeCount;
    }

    public void setEmployeeCount(Long employeeCount) {
        this.employeeCount = employeeCount;
    }
}
