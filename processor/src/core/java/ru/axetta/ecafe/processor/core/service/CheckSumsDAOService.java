/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.CheckSums;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public List<ServiceCheckSumsPageItems> getCheckSums() {

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

                ServiceCheckSumsPageItems serviceCheckSumsPageItems = new ServiceCheckSumsPageItems(checkSumsDate,
                        distributionVersion, checkSumsMd5);

                sumsPageItemsList.add(serviceCheckSumsPageItems);
            }
        } catch (Exception e) {
            logger.warn("Failed to get CheckSums from persistence :" + e);
        }

        return sumsPageItemsList;
    }

    public void saveCheckSums(CheckSums checkSums) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession;

        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceSession.save(checkSums);
        } catch (Exception e) {
            logger.warn("Failed to get CheckSums from persistence :" + e);
        }
    }

    public class ServiceCheckSumsPageItems {

        private String checkSumsDate;
        private String distributionVersion;
        private String checkSumsMd5;

        public ServiceCheckSumsPageItems(String checkSumsDate, String distributionVersion, String checkSumsMd5) {
            this.checkSumsDate = checkSumsDate;
            this.distributionVersion = distributionVersion;
            this.checkSumsMd5 = checkSumsMd5;
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
    }

}
