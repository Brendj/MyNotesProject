/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.complex.schedule;

import ru.axetta.ecafe.processor.core.persistence.ComplexSchedule;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.reestr.taloon.approval.ResReestrTaloonApproval;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 16.02.16
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class ComplexScheduleProcessor extends AbstractProcessor<ResReestrTaloonApproval> {

    private static final Logger logger = LoggerFactory.getLogger(ComplexScheduleProcessor.class);
    private final ListComplexSchedules complexSchedules;
    private final List<ComplexScheduleItem> resComplexScheduleItems;

    public ComplexScheduleProcessor(Session persistenceSession, ListComplexSchedules complexSchedules) {
        super(persistenceSession);
        this.complexSchedules = complexSchedules;
        resComplexScheduleItems = new ArrayList<ComplexScheduleItem>();
    }

    @Override
    public ResComplexSchedules process() throws Exception {
        ResComplexSchedules result = new ResComplexSchedules();
        List<ResComplexScheduleItem> items = new ArrayList<ResComplexScheduleItem>();
        try {
            ResComplexScheduleItem resItem = null;
            Long nextVersion = DAOUtils.nextVersionByComplexSchedule(session);
            for (ComplexScheduleItem item : complexSchedules.getItems()) {
                if (item.getResCode() > 0) {
                    resItem = new ResComplexScheduleItem(item.getGuid(), null, item.getResCode(), item.getErrorMessage());
                    items.add(resItem);
                } else {
                    String guid = item.getGuid();
                    Long idOfOrg = item.getIdOfOrg();
                    Long idOfComplex = item.getIdOfComplex();
                    Integer intervalFrom = item.getIntervalFrom();
                    Integer intervalTo = item.getIntervalTo();
                    Long idOfOrgCreated = item.getIdOfOrgCreated();
                    String groupsIds = item.getGroupsIds();

                    ComplexSchedule complexSchedule = DAOReadonlyService.getInstance().findComplexSchedule(guid);
                    if (complexSchedule == null)
                        complexSchedule = new ComplexSchedule(guid);
                    complexSchedule.setIdOfOrg(idOfOrg);
                    complexSchedule.setIdOfComplex(idOfComplex);
                    complexSchedule.setIntervalFrom(intervalFrom);
                    complexSchedule.setIntervalTo(intervalTo);
                    complexSchedule.setIdOfOrgCreated(idOfOrgCreated);
                    complexSchedule.setVersion(nextVersion);
                    complexSchedule.setGroupsIds(groupsIds);
                    session.saveOrUpdate(complexSchedule);
                    resItem = new ResComplexScheduleItem(guid, nextVersion, 0, null);
                    items.add(resItem);
                }
            }
        } catch (Exception e) {
            logger.error("Error saving ComplexSchedule", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    public ComplexScheduleData processData() throws Exception {
        ComplexScheduleData result = new ComplexScheduleData();
        List<ComplexScheduleItem> items = new ArrayList<ComplexScheduleItem>();
        ComplexScheduleItem resItem;
        List<ComplexSchedule> list = DAOUtils.getComplexSchedulesForOrgSinceVersion(session, complexSchedules.getIdOfOrgOwner(), complexSchedules.getMaxVersion());
        for (ComplexSchedule schedule : list) {
            if (schedule != null) {
                resItem = new ComplexScheduleItem(schedule);
                items.add(resItem);
            }
        }

        result.setItems(items);
        return result;
    }

    public List<ComplexScheduleItem> getResComplexScheduleItems() {
        return resComplexScheduleItems;
    }
}
