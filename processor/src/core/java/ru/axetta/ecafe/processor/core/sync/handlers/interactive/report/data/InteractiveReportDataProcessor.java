/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 21.03.16
 * Time: 17:30
 */

public class InteractiveReportDataProcessor extends AbstractProcessor<InteractiveReportData>{

    private final Long idOfOrg;

    public InteractiveReportDataProcessor(Session session, Long idOfOrg) {
        super(session);
        this.idOfOrg = idOfOrg;
    }

    public InteractiveReportData processData() throws Exception {
        List<InteractiveReportDataItem> interactiveReportDataItemList = DAOUtils.getInteractiveReportDatas(session, idOfOrg);
        return new InteractiveReportData(interactiveReportDataItemList);
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    @Override
    public <RES> RES process() throws Exception {
        return null;
    }
}