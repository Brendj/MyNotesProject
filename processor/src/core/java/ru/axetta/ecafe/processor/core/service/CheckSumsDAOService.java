/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CheckSums;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 31.05.16
 * Time: 11:53
 */

@Service
public class CheckSumsDAOService {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CheckSumsDAOService.class);

    /*public List<ServiceCheckSumsPageItems> getCheckSums() {

        List<ServiceCheckSumsPageItems> sumsPageItemsList = new ArrayList<ServiceCheckSumsPageItems>();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();

            Criteria criteria = persistenceSession.createCriteria(CheckSums.class);
            criteria.addOrder(Order.desc("checkSumsDate"));
            List<CheckSums> checkSumsList = criteria.list();

            for (CheckSums checkSumsItem : checkSumsList) {
                String checkSumsDate = CalendarUtils
                        .toStringFullDateTimeWithLocalTimeZone(checkSumsItem.getCheckSumsDate());
                String distributionVersion = checkSumsItem.getDistributionVersion();
                String checkSumsMd5 = checkSumsItem.getCheckSumsMd5();
                String checkSumOnSettings = checkSumsItem.getCheckSumOnSettings();

                ServiceCheckSumsPageItems serviceCheckSumsPageItems = new ServiceCheckSumsPageItems(checkSumsDate,
                        distributionVersion, checkSumsMd5, checkSumOnSettings);

                sumsPageItemsList.add(serviceCheckSumsPageItems);
            }
        } catch (Exception e) {
            logger.warn("Failed to get CheckSums from persistence :" + e);
        }

        return sumsPageItemsList;
    }*/

    public List<ServiceCheckSumsPageItems> getCheckSums() {
        List<ServiceCheckSumsPageItems> sumsPageItemsList = new ArrayList<ServiceCheckSumsPageItems>();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            org.hibernate.Query query = persistenceSession.createSQLQuery("SELECT checksumsdate, distributionversion, checksumsmd5, checksumonsettings, "
                    + "(SELECT checksumsmd5 FROM cf_checksums ww WHERE ww.idofchecksums = (SELECT max(idofchecksums) FROM cf_checksums WHERE idofchecksums < rr.idofchecksums)) as cs1, "
                    + "(SELECT checksumonsettings FROM cf_checksums ww WHERE ww.idofchecksums = (SELECT max(idofchecksums) FROM cf_checksums WHERE idofchecksums < rr.idofchecksums)) as cs2, "
                    + "(select distributionversion from cf_checksums ww where ww.idofchecksums = (select max(idofchecksums) from cf_checksums where idofchecksums < rr.idofchecksums)) as cs3 "
                    + " FROM cf_checksums rr ORDER BY checksumsdate DESC");
            List list = query.list();
            for (Object o : list ){
                Object e[] = (Object[]) o;
                String checkSumsDate = CalendarUtils
                        .toStringFullDateTimeWithLocalTimeZone(new Date(((BigInteger) e[0]).longValue()));
                String distributionVersion = (String) e[1];
                String checkSumsMd5 = (String) e[2];
                String checkSumOnSettings = (String) e[3];
                String checkSumsMd5Prev = (String) e[4];
                String checkSumOnSettingsPrev = (String) e[5];
                String versionPrev = (String) e[6];

                ServiceCheckSumsPageItems serviceCheckSumsPageItems = new ServiceCheckSumsPageItems(checkSumsDate,
                        distributionVersion, checkSumsMd5, checkSumOnSettings, checkSumsMd5Prev, checkSumOnSettingsPrev, versionPrev);

                sumsPageItemsList.add(serviceCheckSumsPageItems);
            }
        } catch (Exception e) {
            logger.warn("Failed to get CheckSums from persistence :" + e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }

        return sumsPageItemsList;
    }

    public void saveCheckSums(CheckSums checkSums) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceSession.save(checkSums);
        } catch (Exception e) {
            logger.warn("Failed to get CheckSums from persistence :" + e);
        } finally {
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public class ServiceCheckSumsPageItems {

        private String checkSumsDate;
        private String distributionVersion;
        private String checkSumsMd5;
        private String checkSumsOnSettings;
        private String checkSumsMd5Prev;
        private String checkSumsOnSettingsPrev;
        private String versionPrev;

        public ServiceCheckSumsPageItems(String checkSumsDate, String distributionVersion, String checkSumsMd5,
                String checkSumsOnSettings, String checkSumsMd5Prev, String checkSumsOnSettingsPrev, String versionPrev) {
            this.checkSumsDate = checkSumsDate;
            this.distributionVersion = distributionVersion;
            this.checkSumsMd5 = checkSumsMd5 == null ? "" : checkSumsMd5;
            this.checkSumsOnSettings = checkSumsOnSettings == null ? "" : checkSumsOnSettings;
            this.checkSumsMd5Prev = checkSumsMd5Prev == null ? "" : checkSumsMd5Prev;
            this.checkSumsOnSettingsPrev = checkSumsOnSettingsPrev == null ? "" : checkSumsOnSettingsPrev;
            this.versionPrev = versionPrev == null ? "" : versionPrev;
        }

        public boolean getRedMd5() {
            return !checkSumsMd5.equals(checkSumsMd5Prev) && distributionVersion.equals(versionPrev);
        }

        public boolean getRedSettings() {
            return !checkSumsOnSettings.equals(checkSumsOnSettingsPrev) && distributionVersion.equals(versionPrev);
        }

        public String getCheckSumsDate() {
            return checkSumsDate;
        }

        public void setCheckSumsDate(String checkSumsDate) {
            this.checkSumsDate = checkSumsDate;
        }

        public String getDistributionVersion() {
            return distributionVersion;
        }

        public void setDistributionVersion(String distributionVersion) {
            this.distributionVersion = distributionVersion;
        }

        public String getCheckSumsMd5() {
            return checkSumsMd5;
        }

        public void setCheckSumsMd5(String checkSumsMd5) {
            this.checkSumsMd5 = checkSumsMd5;
        }

        public String getCheckSumsOnSettings() {
            return checkSumsOnSettings;
        }

        public void setCheckSumsOnSettings(String checkSumsOnSettings) {
            this.checkSumsOnSettings = checkSumsOnSettings;
        }

        public String getCheckSumsMd5Prev() {
            return checkSumsMd5Prev;
        }

        public void setCheckSumsMd5Prev(String checkSumsMd5Prev) {
            this.checkSumsMd5Prev = checkSumsMd5Prev;
        }

        public String getCheckSumsOnSettingsPrev() {
            return checkSumsOnSettingsPrev;
        }

        public void setCheckSumsOnSettingsPrev(String checkSumsOnSettingsPrev) {
            this.checkSumsOnSettingsPrev = checkSumsOnSettingsPrev;
        }
    }

}
