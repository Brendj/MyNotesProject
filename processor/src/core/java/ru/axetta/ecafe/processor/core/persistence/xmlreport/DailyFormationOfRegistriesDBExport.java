/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.xmlreport;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.DailyFormationRegistries;
import ru.axetta.ecafe.processor.core.persistence.DailyOrgRegistries;
import ru.axetta.ecafe.processor.core.persistence.Org;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 10.12.14
 * Time: 15:33
 */
@Deprecated
public class DailyFormationOfRegistriesDBExport {

    private static final Logger logger = LoggerFactory.getLogger(DailyFormationOfRegistriesDBExport.class);

    public void reportToDatabaseExport(
            List<DailyFormationOfRegistriesService.DailyFormationOfRegistriesModel> dailyFormationOfRegistriesModelList,
            Session session) throws Exception {

       // session.clear();

        for (DailyFormationOfRegistriesService.DailyFormationOfRegistriesModel dailyFormation : dailyFormationOfRegistriesModelList) {

            DailyFormationRegistries dailyFormationRegistries = new DailyFormationRegistries();
            dailyFormationRegistries.setGeneratedDate(dailyFormation.getGeneratedDate());

            Contragent contragent = (Contragent) session.load(Contragent.class, dailyFormation.contragentId);

            dailyFormationRegistries.setIdOfContragent(contragent);
            dailyFormationRegistries.setContragentName(dailyFormation.getContragentName());

            Set<DailyOrgRegistries> dailyOrgRegistriesSet = new HashSet<DailyOrgRegistries>();

            for (DailyFormationOfRegistriesService.OrgItem dayForm : dailyFormation.getOrgItemList()) {

                DailyOrgRegistries dailyOrgRegistries = new DailyOrgRegistries();
                dailyOrgRegistries.setOrgNum(dayForm.getOrgNum());

                Org org = (Org) session.load(Org.class, dayForm.getIdOfOrg());

                dailyOrgRegistries.setIdOfOrg(org);
                dailyOrgRegistries.setOfficialName(dayForm.getOfficialName());
                dailyOrgRegistries.setAddress(dayForm.getAddress());
                dailyOrgRegistries.setTotalBalance(dayForm.getTotalBalance());
                dailyOrgRegistries.setRechargeAmount(dayForm.getRechargeAmount());
                dailyOrgRegistries.setSalesAmount(dayForm.getSalesAmount());
                dailyOrgRegistries.setDailyFormationRegistries(dailyFormationRegistries);
                dailyOrgRegistries.setCreatedDate(new Date());

                dailyOrgRegistriesSet.add(dailyOrgRegistries);
            }

            dailyFormationRegistries.setDailyOrgRegistriesSet(dailyOrgRegistriesSet);
            session.save(dailyFormationRegistries);
        }
    }
}
