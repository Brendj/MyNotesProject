/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.PreorderComplex;
import ru.axetta.ecafe.processor.core.persistence.ProductionCalendar;
import ru.axetta.ecafe.processor.core.persistence.RegularPreorder;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportServiceParam;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by nuc on 02.04.2019.
 */
@Component
@Scope("singleton")
public class PreorderOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderOperationsService.class);

    public void relevancePreorders(PreorderRequestsReportServiceParam params) {
        logger.info("Start process relevance preorders");
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToOrgs(params);
        } catch (Exception e) {
            logger.error("Error in relevancePreordersToOrgs: ", e);
        }
        //RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToMenu(params);
        try {
            runRelevancePreordersToMenu(params);
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToOrgFlag(params);
            logger.info("Successful end process relevance preorders");
        } catch(Exception e) {
            logger.error("Error in process relevance preorders");
        }
    }

    public void dailyCheckPreorders() {
        logger.info("Start preorders daily check process");
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).dailyCheck();
        } catch (Exception e) {
            logger.error("Error in preorders daily check process");
        }
        logger.info("End preorders daily check process");
    }

    public void runRelevancePreordersToMenu(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToMenu process");
        long nextVersion;
        List<PreorderComplex> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreorderComplexListForRelevanceToMenu(params);
        int counter = 0;
        List<ModifyMenu> modifyMenuList = new ArrayList<>();
        for (PreorderComplex preorderComplex : list) {
            try {
                logger.info(String.format("Start processing record %s from %s", ++counter, list.size()));
                if (preorderComplex.getIdOfGoodsRequestPosition() != null)
                    continue;
                nextVersion = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).nextVersionByPreorderComplex();
                List<ModifyMenu> mmList = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .relevancePreordersToMenu(preorderComplex, nextVersion);
                if (mmList != null) {
                    modifyMenuList.addAll(mmList);
                }
            } catch (Exception e) {
                logger.error("Error in runRelevancePreordersToMenu: ", e);
            }
        }
        nextVersion = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).nextVersionByPreorderComplex();
        RuntimeContext.getAppContext().getBean(PreorderDAOService.class).changeLocalIdOfMenu(modifyMenuList, nextVersion);
        logger.info("End relevancePreordersToMenu process");
    }

    public void generatePreordersBySchedule(PreorderRequestsReportServiceParam params) {
        List<RegularPreorder> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getRegularPreorders(params);
        for (RegularPreorder regularPreorder : list) {
            if (regularPreorder.getIdOfComplex() != null) {
                try {
                    RuntimeContext.getAppContext().getBean(PreorderDAOService.class).createPreordersFromRegular(regularPreorder, false);
                } catch (Exception e) {
                    logger.error("Error in generate preorders by schedule: ", e);
                }
            }
        }
    }

    public void additionalTasksForPreorders(PreorderRequestsReportServiceParam params) {
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(new Date(), dateTo);
        List list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getAllActualPreorders(params);
        Date currentDate = CalendarUtils.startOfDay(new Date());
        Map<Long, List<SpecialDate>> specialDatesMap = new HashMap<Long, List<SpecialDate>>();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            PreorderComplex preorderComplex = (PreorderComplex) row[0];
            Long idOfClientGroup = (Long) row[1];
            try {
                List<SpecialDate> specialDates = specialDatesMap.get(preorderComplex.getIdOfOrgOnCreate());
                if (specialDates == null) {
                    specialDates = DAOReadonlyService.getInstance().getSpecialDates(currentDate, dateTo, preorderComplex.getIdOfOrgOnCreate());
                    specialDatesMap.put(preorderComplex.getIdOfOrgOnCreate(), specialDates);
                }
                RuntimeContext.getAppContext().getBean(PreorderDAOService.class).processAdditionalTaskForPreorder(preorderComplex, productionCalendar, specialDates, idOfClientGroup);
            } catch (Exception e) {
                logger.error(String.format("Error processing additional task for preorder %s: ", preorderComplex), e);
            }
        }
    }

    public void additionalTasksForRegulars(PreorderRequestsReportServiceParam params) {
        List<RegularPreorder> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getExpiredRegularPreorders(params);
        for (RegularPreorder regularPreorder : list) {
            try {
                RuntimeContext.getAppContext().getBean(PreorderDAOService.class).deleteExpiredRegularPreorder(regularPreorder);
            } catch (Exception e) {
                logger.error("Error in delete expired regular preorder: ", e);
            }
        }
    }
}
