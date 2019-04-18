/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.kzn;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.KznClientsStatistic;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("session")
public class KznClientsStatisticEditPage extends OnlineReportPage {

    private Logger logger = LoggerFactory.getLogger(KznClientsStatisticEditPage.class);

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
        if (null == idOfOrg) {
            printError("Выберите организацию");
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            KznClientsStatistic kznClientsStatistic = DAOUtils.getKznClientStatisticByOrg(session, idOfOrg);

            if (null == kznClientsStatistic) {
                printError("Ошибка при сохранении данных");
                return;
            }

            boolean modified = false;
            if (!kznClientsStatistic.getStudentsCountTotal().equals(studentsCountTotal)) {
                kznClientsStatistic.setStudentsCountTotal(studentsCountTotal);
                modified = true;
            }

            if (!kznClientsStatistic.getStudentsCountYoung().equals(studentsCountYoung)) {
                kznClientsStatistic.setStudentsCountYoung(studentsCountYoung);
                modified = true;
            }

            if (!kznClientsStatistic.getStudentsCountMiddle().equals(studentsCountMiddle)) {
                kznClientsStatistic.setStudentsCountMiddle(studentsCountMiddle);
                modified = true;
            }

            if (!kznClientsStatistic.getStudentsCountOld().equals(studentsCountOld)) {
                kznClientsStatistic.setStudentsCountOld(studentsCountOld);
                modified = true;
            }

            if (!kznClientsStatistic.getBenefitStudentsCountYoung().equals(benefitStudentsCountYoung)) {
                kznClientsStatistic.setBenefitStudentsCountYoung(benefitStudentsCountYoung);
                modified = true;
            }

            if (!kznClientsStatistic.getBenefitStudentsCountMiddle().equals(benefitStudentsCountMiddle)) {
                kznClientsStatistic.setBenefitStudentsCountMiddle(benefitStudentsCountMiddle);
                modified = true;
            }

            if (!kznClientsStatistic.getBenefitStudentsCountOld().equals(benefitStudentsCountOld)) {
                kznClientsStatistic.setBenefitStudentsCountOld(benefitStudentsCountOld);
                modified = true;
            }

            if (!kznClientsStatistic.getBenefitStudentsCountTotal().equals(benefitStudentsCountTotal)) {
                kznClientsStatistic.setBenefitStudentsCountTotal(benefitStudentsCountTotal);
                modified = true;
            }

            if (!kznClientsStatistic.getEmployeeCount().equals(employeeCount)) {
                kznClientsStatistic.setEmployeeCount(employeeCount);
                modified = true;
            }

            if (modified) {
                printMessage("Сохранено");
            } else {
                printMessage("Данные не изменились");
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in save KznClientsStatisticEditPage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void find() {
        if (null == idOfOrg) {
            printError("Выберите организацию");
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            KznClientsStatistic kznClientsStatistic = DAOUtils.getKznClientStatisticByOrg(session, idOfOrg);
            if (null == kznClientsStatistic) {
                printError("Не удалось найти данные для выбранной организации");
                return;
            }

            studentsCountTotal = kznClientsStatistic.getStudentsCountTotal();
            studentsCountYoung = kznClientsStatistic.getStudentsCountYoung();
            studentsCountMiddle = kznClientsStatistic.getStudentsCountMiddle();
            studentsCountOld = kznClientsStatistic.getBenefitStudentsCountOld();
            benefitStudentsCountYoung = kznClientsStatistic.getBenefitStudentsCountYoung();
            benefitStudentsCountMiddle = kznClientsStatistic.getBenefitStudentsCountMiddle();
            benefitStudentsCountOld = kznClientsStatistic.getBenefitStudentsCountOld();
            benefitStudentsCountTotal = kznClientsStatistic.getBenefitStudentsCountTotal();
            employeeCount = kznClientsStatistic.getEmployeeCount();

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in find KznClientsStatisticEditPage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void delete() {
        if (null == idOfOrg) {
            printError("Выберите организацию");
            return;
        }

        Session session = null;
        Transaction transaction = null;

        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            DAOUtils.deleteFromKznClientStatisticByOrgId(session, idOfOrg);
            completeOrgSelection(session, null);

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in reload KznClientsStatisticReportPage: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void clear() {
        studentsCountTotal = null;
        studentsCountYoung = null;
        studentsCountMiddle = null;
        studentsCountOld = null;
        benefitStudentsCountYoung = null;
        benefitStudentsCountMiddle = null;
        benefitStudentsCountOld = null;
        benefitStudentsCountTotal = null;
        employeeCount = null;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        this.idOfOrg = idOfOrg;
        if (this.idOfOrg == null) {
            filter = "Не выбрано";
        } else {
            Org org = (Org)session.load(Org.class, this.idOfOrg);
            filter = org.getShortName();
        }
        clear();
    }

    public void showOrgSelectPage() {
        MainPage.getSessionInstance().showOrgSelectPage();
    }

    @Override
    public String getPageFilename() {
        return "service/kzn/statistic/edit";
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
