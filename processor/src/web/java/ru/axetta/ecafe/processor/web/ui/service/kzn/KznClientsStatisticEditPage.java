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

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


@Component
@Scope("session")
public class KznClientsStatisticEditPage extends KznClientsStatisticPage {

    private Logger logger = LoggerFactory.getLogger(KznClientsStatisticEditPage.class);

    public void save() {
        if (null == idOfOrg) {
            printError("Выберите организацию");
            return;
        }

        if (!validate()) {
            printError("Заполните все поля");
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
            Long studentsCountTotalLong = stringAsLong(getStudentsCountTotal());
            if (!kznClientsStatistic.getStudentsCountTotal().equals(studentsCountTotalLong)) {
                kznClientsStatistic.setStudentsCountTotal(studentsCountTotalLong);
                modified = true;
            }

            Long studentsCountYoungLong = stringAsLong(getStudentsCountYoung());
            if (!kznClientsStatistic.getStudentsCountYoung().equals(studentsCountYoungLong)) {
                kznClientsStatistic.setStudentsCountYoung(studentsCountYoungLong);
                modified = true;
            }

            Long studentsCountMiddleLong = stringAsLong(getStudentsCountMiddle());
            if (!kznClientsStatistic.getStudentsCountMiddle().equals(studentsCountMiddleLong)) {
                kznClientsStatistic.setStudentsCountMiddle(studentsCountMiddleLong);
                modified = true;
            }

            Long studentsCountOldLong = stringAsLong(getStudentsCountOld());
            if (!kznClientsStatistic.getStudentsCountOld().equals(studentsCountOldLong)) {
                kznClientsStatistic.setStudentsCountOld(studentsCountOldLong);
                modified = true;
            }

            Long benefitStudentsCountYoungLong = stringAsLong(getBenefitStudentsCountYoung());
            if (!kznClientsStatistic.getBenefitStudentsCountYoung().equals(benefitStudentsCountYoungLong)) {
                kznClientsStatistic.setBenefitStudentsCountYoung(benefitStudentsCountYoungLong);
                modified = true;
            }

            Long benefitStudentsCountMiddleLong = stringAsLong(getBenefitStudentsCountMiddle());
            if (!kznClientsStatistic.getBenefitStudentsCountMiddle().equals(benefitStudentsCountMiddleLong)) {
                kznClientsStatistic.setBenefitStudentsCountMiddle(benefitStudentsCountMiddleLong);
                modified = true;
            }

            Long benefitStudentsCountOldLong = stringAsLong(getBenefitStudentsCountOld());
            if (!kznClientsStatistic.getBenefitStudentsCountOld().equals(benefitStudentsCountOldLong)) {
                kznClientsStatistic.setBenefitStudentsCountOld(benefitStudentsCountOldLong);
                modified = true;
            }

            Long benefitStudentsCountTotalLong = stringAsLong(getBenefitStudentsCountTotal());
            if (!kznClientsStatistic.getBenefitStudentsCountTotal().equals(benefitStudentsCountTotalLong)) {
                kznClientsStatistic.setBenefitStudentsCountTotal(benefitStudentsCountTotalLong);
                modified = true;
            }

            Long employeeCountLong = stringAsLong(getEmployeeCount());
            if (!kznClientsStatistic.getEmployeeCount().equals(employeeCountLong)) {
                kznClientsStatistic.setEmployeeCount(employeeCountLong);
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
            clear();
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
                clear();
                return;
            }

            setStudentsCountTotal(kznClientsStatistic.getStudentsCountTotal().toString());
            setStudentsCountYoung(kznClientsStatistic.getStudentsCountYoung().toString());
            setStudentsCountMiddle(kznClientsStatistic.getStudentsCountMiddle().toString());
            setStudentsCountOld(kznClientsStatistic.getBenefitStudentsCountOld().toString());
            setBenefitStudentsCountYoung(kznClientsStatistic.getBenefitStudentsCountYoung().toString());
            setBenefitStudentsCountMiddle(kznClientsStatistic.getBenefitStudentsCountMiddle().toString());
            setBenefitStudentsCountOld(kznClientsStatistic.getBenefitStudentsCountOld().toString());
            setBenefitStudentsCountTotal(kznClientsStatistic.getBenefitStudentsCountTotal().toString());
            setEmployeeCount(kznClientsStatistic.getEmployeeCount().toString());

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

            if (DAOUtils.deleteFromKznClientStatisticByOrgId(session, idOfOrg)) {
                completeOrgSelection(session, null);
                printMessage("Удалено");
            } else {
                printMessage("Нет данных для выбранной организации");
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in reload KznClientsStatisticReportPage: ", e);
            printError("Ошибка при удалении");
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
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
}
