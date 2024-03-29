/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.special.dates;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 14:01
 */

public class SpecialDatesProcessor extends AbstractProcessor<ResSpecialDates>{
    private static final Logger logger = LoggerFactory.getLogger(SpecialDatesProcessor.class);
    private final SpecialDates specialDates;
    private final List<ResSpecialDatesItem> resSpecialDatesItems;

    public SpecialDatesProcessor(Session persistenceSession, SpecialDates specialDates) {
        super(persistenceSession);
        this.specialDates = specialDates;
        resSpecialDatesItems = new ArrayList<ResSpecialDatesItem>();
    }

    @Override
    public ResSpecialDates process() throws Exception {
        ResSpecialDates result = new ResSpecialDates();
        List<ResSpecialDatesItem> items = new ArrayList<ResSpecialDatesItem>();
        try{
            ResSpecialDatesItem resItem;
            Long nextVersion = DAOUtils.nextVersionBySpecialDate(session);
            for (SpecialDatesItem item : specialDates.getItems()) {
                //
                OrgSync orgSync = (OrgSync)session.load(OrgSync.class, item.getIdOfOrg());
                if (orgSync != null && orgSync.getClientVersion() != null) {
                    if (!orgSync.getClientVersion().trim().isEmpty()
                            && (item.getGroupName() == null || item.getGroupName().trim().isEmpty())) {
                        if (SyncRequest.versionIsAfter(orgSync.getClientVersion(), "2.7.93.1")) {
                            item.setResCode(SpecialDatesItem.ERROR_CODE_NOT_VALID_ATTRIBUTE);
                            item.setErrorMessage("Software version higher than 2.7.93.1");
                        }
                    }
                }
                //
                String groupName = item.getGroupName();
                Long idOfClientGroup = null;
                if (item.getResCode().equals(SpecialDatesItem.ERROR_CODE_ALL_OK)) {
                    if (!StringUtils.isEmpty(groupName)) {
                        ClientGroup clientGroup = DAOUtils
                                .findClientGroupByGroupNameAndIdOfOrg(session, item.getIdOfOrg(), groupName);
                        if (clientGroup != null)
                            idOfClientGroup = clientGroup.getCompositeIdOfClientGroup().getIdOfClientGroup();
                        else {
                            item.setResCode(SpecialDatesItem.ERROR_CODE_NOT_FOUND_GROUPNAME);
                            item.setErrorMessage("Not found groupName");
                        }
                    }
                }
                if (item.getResCode().equals(SpecialDatesItem.ERROR_CODE_ALL_OK)) {
                    CompositeIdOfSpecialDate compositeId = new CompositeIdOfSpecialDate(item.getIdOfOrg(), item.getDate());
                    SpecialDate specialDate = null;

                    if (StringUtils.isEmpty(groupName)) {
                        specialDate = DAOReadonlyService.getInstance().findSpecialDate(compositeId);
                    } else {
                        specialDate = DAOReadonlyService.getInstance().findSpecialDateWithGroup(compositeId, idOfClientGroup);
                    }
                    Boolean isWeekend = item.getIsWeekend();
                    Boolean deleted = item.getDelete();
                    Org orgOwner = (Org)session.load(Org.class, item.getIdOfOrgOwner());
                    String comment = item.getComment();
                    if (comment == null) {
                        comment = "";
                    }

                    boolean createHistory = false;
                    if(specialDate == null){
                        specialDate = new SpecialDate(compositeId, isWeekend, comment);
                        specialDate.setIsWeekend(isWeekend);
                        specialDate.setDeleted(deleted);
                        specialDate.setComment(comment);
                        specialDate.setIdOfClientGroup(idOfClientGroup);
                        specialDate.setOrgOwner(orgOwner);
                        specialDate.setVersion(nextVersion);
                        specialDate.setStaffGuid(item.getStaffGuid());
                        specialDate.setArmLastUpdate(item.getArmLastUpdate());
                        session.save(specialDate);
                        createHistory = true;
                    } else {
                        boolean wasModified = false;
                        if (!specialDate.getIsWeekend().equals(isWeekend)) {
                            specialDate.setIsWeekend(isWeekend);
                            wasModified = true;
                        }
                        if (!specialDate.getDeleted().equals(deleted)) {
                            specialDate.setDeleted(deleted);
                            wasModified = true;
                        }
                        if (!specialDate.getComment().equals(comment)) {
                            specialDate.setComment(comment);
                            wasModified = true;
                        }
                        if (!specialDate.getOrgOwner().equals(orgOwner)) {
                            specialDate.setOrgOwner(orgOwner);
                            wasModified = true;
                        }
                        if (specialDate.getIdOfClientGroup() == null) {
                            if (idOfClientGroup != null) {
                                specialDate.setIdOfClientGroup(idOfClientGroup);
                                wasModified = true;
                            }
                        } else {
                            if (idOfClientGroup == null) {
                                specialDate.setIdOfClientGroup(idOfClientGroup);
                                wasModified = true;
                            } else if (!specialDate.getIdOfClientGroup().equals(idOfClientGroup)) {
                                specialDate.setIdOfClientGroup(idOfClientGroup);
                                wasModified = true;
                            }
                        }
                        if (wasModified) {
                            specialDate.setVersion(nextVersion);
                            specialDate.setLastUpdate(new Date());
                            specialDate.setStaffGuid(item.getStaffGuid());
                            specialDate.setArmLastUpdate(item.getArmLastUpdate());
                            session.merge(specialDate);
                            createHistory = true;
                        }
                    }
                    if (createHistory) {
                        createSpecialDateHistory(item, nextVersion, idOfClientGroup);
                    }

                    resItem = new ResSpecialDatesItem(session, specialDate);
                    resItem.setResCode(item.getResCode());
                } else {
                    resItem = new ResSpecialDatesItem();
                    resItem.setIdOfOrg(item.getIdOfOrg());
                    resItem.setDate(item.getDate());
                    resItem.setIsWeekend(item.getIsWeekend());
                    resItem.setComment(item.getComment());
                    resItem.setGroupName(item.getGroupName());
                    resItem.setDeleted(item.getDelete());
                    resItem.setResCode(item.getResCode());
                    resItem.setErrorMessage(item.getErrorMessage());
                }
                resItem.setIsWeekend(null);
                items.add(resItem);
            }
            session.flush();
            session.clear();
        }
        catch (Exception e) {
            logger.error("Error saving SpecialDates", e);
            return null;
        }
        result.setItems(items);
        return result;
    }

    private void createSpecialDateHistory(SpecialDatesItem item, Long version, Long idOfClientGroup) {
        SpecialDateHistory history = new SpecialDateHistory(item.getIdOfOrg(), item.getDate(), item.getIsWeekend(), item.getDelete(), item.getIdOfOrgOwner(),
                version, item.getComment(), idOfClientGroup, item.getStaffGuid(), item.getArmLastUpdate());
        session.save(history);
    }

    public SpecialDatesData processData() throws Exception {
        SpecialDatesData result = new SpecialDatesData();
        List<ResSpecialDatesItem> items = new ArrayList<ResSpecialDatesItem>();
        ResSpecialDatesItem resItem;
        List<SpecialDate> list = DAOUtils.getSpecialDatesForFriendlyOrgsSinceVersion(session,
                specialDates.getIdOfOrgOwner(), specialDates.getMaxVersion());
        for(SpecialDate sd : list){
            resItem = new ResSpecialDatesItem(session, sd);
            resItem.setComment(sd.getComment());
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }


    public List<ResSpecialDatesItem> getResSpecialDatesItems() {
        return resSpecialDatesItems;
    }
}
