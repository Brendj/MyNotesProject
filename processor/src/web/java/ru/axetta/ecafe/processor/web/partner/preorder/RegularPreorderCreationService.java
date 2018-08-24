/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by i.semenov on 11.07.2018.
 */
@Component
@Scope("singleton")
@Deprecated
//Генерация встроена в метод отправки заявок
public class RegularPreorderCreationService {
    private static final Logger logger = LoggerFactory.getLogger(RegularPreorderCreationService.class);

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.preorder.regular.node", "1");
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim().equals(
                reqInstance.trim())) {
            return false;
        }
        return true;
    }

    public void run() {
        if (!isOn()) return;
        logger.info("Start of generating regular preorders");
        try {
            RuntimeContext.getAppContext().getBean(PreorderDAOService.class).generatePreordersBySchedule();
            logger.info("Successful end of generating regular preorders");
        } catch (Exception e) {
            logger.error("Error in generating regular preorders: ", e);
        }
    }
}
