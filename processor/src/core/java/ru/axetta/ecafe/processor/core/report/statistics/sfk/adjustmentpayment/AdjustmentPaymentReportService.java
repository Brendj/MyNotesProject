/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.sfk.adjustmentpayment;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.09.15
 * Time: 15:21
 */

public class AdjustmentPaymentReportService {

    private static final Logger logger = LoggerFactory.getLogger(AdjustmentPaymentReportBuilder.class);

    public AdjustmentPaymentReportService() {
    }

    public List<AdjustmentPaymentReportModel> getMainData(Session session, Long idOfOrg, Date StartDate, Date endDate, Boolean showReverse) throws Exception {

        List<AdjustmentPaymentReportModel> adjustmentPaymentReportModelList = new ArrayList<AdjustmentPaymentReportModel>();

        AdjustmentPaymentReportModel adjustmentPaymentReportModel = new AdjustmentPaymentReportModel(1L, "100", "Большая крассная", 5L, 10L, 15L);

        adjustmentPaymentReportModelList.add(adjustmentPaymentReportModel);

        return adjustmentPaymentReportModelList;
    }
}
