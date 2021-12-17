/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportServiceParam;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringUtils;
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
    public static final String PREORDER_CHECK_EMAIL_NOTIFICATION_PROPERTY = "ecafe.processor.preorder.check.email";

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
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).checkPreorderClientGroups(params);
            logger.info("Successful end process relevance preorders");
        } catch(Exception e) {
            logger.error("Error in process relevance preorders ", e);
        }
    }

    public void dailyCheckPreorders() {
        logger.info("Start preorders daily check process");
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).dailyCheck();
            sendPreorderCheckNotifications(RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreorderCheckListForNotification());
        } catch (Exception e) {
            logger.error("Error in preorders daily check process:", e);
        }
        logger.info("End preorders daily check process");
    }

    public void runRelevancePreordersToMenu(PreorderRequestsReportServiceParam params) {
        logger.info("Start relevancePreordersToMenu process");
        long nextVersion;
        List<PreorderComplex> list = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).getPreorderComplexListForRelevanceToMenu(params);
        int counter = 0;
        List<ModifyMenu> modifyMenuList = new ArrayList<>();
        String message;
        Set<Long> complexes = new HashSet<>();
        for (PreorderComplex preorderComplex : list) {
            try {
                boolean found = complexes.contains(preorderComplex.getIdOfPreorderComplex());
                message = String.format("Start processing record %s from %s, idOfPreorderComplex=%s", ++counter, list.size(), preorderComplex.getIdOfPreorderComplex());
                if (found) message += ". Record skipped";
                logger.info(message);
                if (found) {
                    continue;
                } else {
                    complexes.add(preorderComplex.getIdOfPreorderComplex());
                }
                // проверка с логированием перенесена в relevancePreordersToMenu
                //if (preorderComplex.getIdOfGoodsRequestPosition() != null)
                //    continue;
                nextVersion = RuntimeContext.getAppContext().getBean(PreorderDAOService.class).nextVersionByPreorderComplex();
                Org org = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                        .getOrgByContractId(preorderComplex.getClient().getContractId());
                if (!org.getUseWebArm()) {
                    List<ModifyMenu> mmList = RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                            .relevancePreordersToMenu(preorderComplex, nextVersion);
                    if (mmList != null) {
                        modifyMenuList.addAll(mmList);
                    }
                } else {
                    RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                            .relevancePreordersToWtMenu(preorderComplex, nextVersion);
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
                    RuntimeContext.getAppContext().getBean(PreorderDAOService.class)
                                .createPreordersFromRegular(regularPreorder, false);
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
        Set<Long> complexes = new HashSet<>();
        for (Object obj : list) {
            Object[] row = (Object[]) obj;
            PreorderComplex preorderComplex = (PreorderComplex) row[0];
            if (complexes.contains(preorderComplex.getIdOfPreorderComplex())) {
                continue;
            } else {
                complexes.add(preorderComplex.getIdOfPreorderComplex());
            }
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

    private void sendPreorderCheckNotifications(List<PreorderCheck> list) {
        String email = getEmailForPreorderCheckNotification();
        if (list.size() == 0 || StringUtils.isEmpty(email)) return;
        String html = "<html><body><table><tbody><tr><td>Дата</td><td>Количество в предзаказе</td><td>Количество в заявках</td><td>Изменено</td>%s</tr></tbody></table></body></html>";
        String text = "";
        for (PreorderCheck preorderCheck : list) {
            text += String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>", CalendarUtils.dateToString(preorderCheck.getDate()),
                    preorderCheck.getPreorderAmount(), preorderCheck.getGoodRequestAmount(), CalendarUtils.dateTimeToString(preorderCheck.getLastUpdate()));
        }
        html = String.format(html, text);
        try {
            RuntimeContext.getInstance().getPostman()
                    .postNotificationEmail(email, "Уведомление об изменениях в предзаказах", html);
        } catch (Exception e) {
            logger.error("Failed to send email notification", e);
        }
    }

    private String getEmailForPreorderCheckNotification() {
        return RuntimeContext.getInstance().getConfigProperties().getProperty(PREORDER_CHECK_EMAIL_NOTIFICATION_PROPERTY, "");
    }
}
