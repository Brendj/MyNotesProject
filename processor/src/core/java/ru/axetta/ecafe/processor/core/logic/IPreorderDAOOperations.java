/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.service.PreorderRequestsReportServiceParam;

/**
 * Created by i.semenov on 01.08.2018.
 */
public interface IPreorderDAOOperations {
    void generatePreordersBySchedule(PreorderRequestsReportServiceParam params);
    void relevancePreorders(PreorderRequestsReportServiceParam params);
    void dailyCheckPreorders(PreorderRequestsReportServiceParam params);
}
