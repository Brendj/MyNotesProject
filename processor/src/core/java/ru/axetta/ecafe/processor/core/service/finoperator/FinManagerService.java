/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.finoperator;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.service.SummaryDownloadBaseService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by i.semenov on 25.10.2017.
 */
@Component
public class FinManagerService extends SummaryDownloadBaseService {
    private static final Logger logger = LoggerFactory.getLogger(FinManagerService.class);
    public static final String NODE_OPTION = "ecafe.processor.finmanager.node";
    public static final String FOLDER_OPTION = "ecafe.processor.finmanager.folder";

    protected String getNode() {
        return NODE_OPTION;
    }

    public void run(Date startDate, Date endDate) throws RuntimeException {
        String filename = RuntimeContext.getInstance().getPropertiesValue(FOLDER_OPTION, null);
        if (filename == null) {
            logger.error(String.format("Not found property %s in application config", FOLDER_OPTION));
            throw new RuntimeException(String.format("Не найдена опция %s в конфигурации", FOLDER_OPTION));
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        filename += "/ispp_transactions_" + df.format(startDate) + ".csv";
        FinManager.getInstance().run(startDate, endDate, filename);
    }

}
