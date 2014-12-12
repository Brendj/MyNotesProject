/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.DailyFormationRegistries;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.12.14
 * Time: 15:33
 */

public class DailyFormationOfRegistriesDBExport {

    private static final Logger logger = LoggerFactory.getLogger(DailyFormationOfRegistriesDBExport.class);

    public void reportToDatabaseExport(
            List<DailyFormationOfRegistries.DailyFormationOfRegistriesModel> dailyFormationOfRegistriesModelList,
            Session session,  Transaction persistenceTransaction) {

        for (DailyFormationOfRegistries.DailyFormationOfRegistriesModel dailyFormation : dailyFormationOfRegistriesModelList) {
            //Transaction persistenceTransaction = null;
            try {

                persistenceTransaction = session.beginTransaction();

                for (DailyFormationOfRegistries.OrgItem orgItem : dailyFormation.getOrgItemList()) {

                    DailyFormationRegistries dailyFormationRegistries = new DailyFormationRegistries();

                    dailyFormationRegistries.setGeneratedDate(dailyFormation.getGeneratedDate());
                    Contragent contragent = (Contragent) session
                            .load(Contragent.class, dailyFormation.getContragentId());
                    dailyFormationRegistries.setIdOfContragent(contragent);
                    dailyFormationRegistries.setContragentName(dailyFormation.getContragentName());

                    dailyFormationRegistries.setOrgNum(orgItem.getOrgNum());

                    Org org = (Org) session.load(Org.class, orgItem.getIdOfOrg());
                    dailyFormationRegistries.setIdOfOrg(org);
                    dailyFormationRegistries.setOfficialName(orgItem.getOfficialName());
                    dailyFormationRegistries.setAddress(orgItem.getAddress());
                    dailyFormationRegistries.setTotalBalance(orgItem.getTotalBalance());
                    dailyFormationRegistries.setRechargeAmount(orgItem.getRechargeAmount());
                    dailyFormationRegistries.setSalesAmount(orgItem.getSalesAmount());

                    session.save(dailyFormationRegistries);
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                }
            } catch (Exception e) {
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(session, logger);
            }
        }
    }
}
