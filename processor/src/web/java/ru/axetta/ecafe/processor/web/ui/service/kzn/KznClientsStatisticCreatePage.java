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
public class KznClientsStatisticCreatePage extends KznClientsStatisticPage {

    private Logger logger = LoggerFactory.getLogger(KznClientsStatisticCreatePage.class);

    public void save() {
        if (null == idOfOrg) {
            printError("Выберите организацию");
            clear();
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

            if (null != DAOUtils.getKznClientStatisticByOrg(session, idOfOrg)) {
                printError("Для выбранной школы уже имеются данные");
                return;
            }

            Org org = (Org) session.load(Org.class, idOfOrg);

            KznClientsStatistic kznClientsStatistic = new KznClientsStatistic(org,
                    stringAsLong(getStudentsCountTotal()), stringAsLong(getStudentsCountYoung()),
                    stringAsLong(getStudentsCountMiddle()), stringAsLong(getStudentsCountOld()),
                    stringAsLong(getBenefitStudentsCountYoung()), stringAsLong(getBenefitStudentsCountMiddle()),
                    stringAsLong(getBenefitStudentsCountOld()), stringAsLong(getBenefitStudentsCountTotal()),
                    stringAsLong(getEmployeeCount()));
            session.save(kznClientsStatistic);

            transaction.commit();
            transaction = null;
            printMessage("Сохранено");
            completeOrgSelection(session, null);
            clear();
        } catch (Exception e) {
            logger.error("Error in reload KznClientsStatisticReportPage: ", e);
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
        return "service/kzn/statistic/create";
    }
}
