/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.RegularPreorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

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
}
