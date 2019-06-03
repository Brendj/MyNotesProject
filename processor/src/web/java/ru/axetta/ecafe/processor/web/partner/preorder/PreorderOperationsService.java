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
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nuc on 02.04.2019.
 */
@Component
@Scope("singleton")
public class PreorderOperationsService {
    private static final Logger logger = LoggerFactory.getLogger(PreorderOperationsService.class);

    public void relevancePreorders() {
        try {
            logger.info("Start process relevance preorders");
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToOrgs();
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToMenu();
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).relevancePreordersToOrgFlag();
            logger.info("Successful end process relevance preorders");
        } catch(Exception e) {
            logger.error("Error in process relevance preorders");
        }
    }

    public void generatePreordersBySchedule() {
        List<RegularPreorder> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getRegularPreorders();
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

    public void additionalTasksForPreorders() {
        Date dateTo = CalendarUtils.addDays(new Date(), PreorderComplex.getDaysOfRegularPreorders()-1);
        List<ProductionCalendar> productionCalendar = DAOReadonlyService.getInstance().getProductionCalendar(new Date(), dateTo);
        List list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getAllActualPreorders();
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

    public void additionalTasksForRegulars() {
        List<RegularPreorder> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getExpiredRegularPreorders();
        for (RegularPreorder regularPreorder : list) {
            try {
                RuntimeContext.getAppContext().getBean(PreorderDAOService.class).deleteExpiredRegularPreorder(regularPreorder);
            } catch (Exception e) {
                logger.error("Error in delete expired regular preorder: ", e);
            }
        }
    }
}