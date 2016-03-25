/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.interactive.report.data;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfInteractiveReportData;
import ru.axetta.ecafe.processor.core.persistence.InteractiveReportDataEntity;
import ru.axetta.ecafe.processor.core.persistence.Org;
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
 * Date: 24.03.16
 * Time: 16:12
 */

public class InteractiveReportProcessor extends AbstractProcessor<InteractiveReport> {

    private final Logger logger = LoggerFactory.getLogger(InteractiveReportProcessor.class);

    private final Long idOfOrg;
    private final InteractiveReport interactiveReport;
    private final List<InteractiveReportItem> interactiveReportItems;

    public InteractiveReportProcessor(Session session, Long idOfOrg, InteractiveReport interactiveReport) {
        super(session);
        this.idOfOrg = idOfOrg;
        this.interactiveReport = interactiveReport;
        interactiveReportItems = new ArrayList<InteractiveReportItem>();
    }

    @Override
    public InteractiveReport process() throws Exception {
        InteractiveReport result = new InteractiveReport();
        List<InteractiveReportItem> items = new ArrayList<InteractiveReportItem>();
        try {
            InteractiveReportItem reportItem = null;
            for (InteractiveReportItem item: interactiveReport.getItems()) {

                if (item.getResCode().equals(InteractiveReportItem.ERROR_CODE_ALL_OK)) {
                    CompositeIdOfInteractiveReportData compositeIdOfInteractiveReportData = new CompositeIdOfInteractiveReportData(idOfOrg, item.getIdOfRecord());
                    InteractiveReportDataEntity interactiveReportDataEntity = DAOUtils.findInteractiveDataReport(session, compositeIdOfInteractiveReportData);

                    if (interactiveReportDataEntity == null) {
                        Org org = (Org) session.load(Org.class, idOfOrg);
                        interactiveReportDataEntity = new InteractiveReportDataEntity(
                                compositeIdOfInteractiveReportData, item.getIdOfRecord(), org, item.getValue());
                    } else {
                        DAOUtils.deleteInteractiveReportDataEntity(session, interactiveReportDataEntity);
                        Org org = (Org) session.load(Org.class, idOfOrg);
                        interactiveReportDataEntity = new InteractiveReportDataEntity(
                                compositeIdOfInteractiveReportData, item.getIdOfRecord(), org, item.getValue());
                    }

                    session.saveOrUpdate(interactiveReportDataEntity);
                     reportItem = new InteractiveReportItem(item.getIdOfRecord(), item.getValue(), "");
                }
                items.add(reportItem);
            }
            session.flush();
        } catch (Exception e) {
            logger.error("Error saving InteractiveReport", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public List<InteractiveReportItem> getInteractiveReportItems() {
        return interactiveReportItems;
    }
}
