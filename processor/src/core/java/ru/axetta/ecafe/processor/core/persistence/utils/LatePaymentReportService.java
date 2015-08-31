/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.persistence.Org;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 28.08.15
 * Time: 13:39
 */

public class LatePaymentReportService {

    private static final Logger logger = LoggerFactory.getLogger(LatePaymentReportService.class);

    public LatePaymentReportService() {
    }

    // Запрос подсчета количества льготников по организациям
    public Long getCountOfBeneficiariesByOrganization (Set<Org> friendlyOrganizationsSet) {
        return null;
    }
}
