/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.SpecialDate;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.CreateOrUpdateOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.CreateOrUpdateGuardianCommand;
import ru.axetta.ecafe.processor.web.partner.schoolapi.util.DateTimeExtensions;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class CreateOrUpdateOrgCalendarDateCommand {
    private final Logger logger = LoggerFactory.getLogger(CreateOrUpdateGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public CreateOrUpdateOrgCalendarDateCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public CreateOrUpdateOrgCalendarDateResponse createorUpdateOrgCalendarDate(CreateOrUpdateOrgCalendarDateRequest request, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Long newVersion = DAOUtils.nextVersionBySpecialDate(session);
            SpecialDate specialDate = FindExistingSpecialDate(request.getIdOfOrg(), request.getIdOfGroup(), request.getDate(), session);

            if (specialDate == null) {
                specialDate = new SpecialDate();
                specialDate.setIdOfOrg(request.getIdOfOrg());
                specialDate.setIdOfClientGroup(request.getIdOfGroup());
                specialDate.setDate(DateTimeExtensions.sameDateWithOtherTime(request.getDate(), 0,0,0));
            }
            specialDate.setIsWeekend(request.getIsWeekend());
            specialDate.setComment(request.getComment());
            specialDate.setDeleted(false);
            specialDate.setVersion(newVersion);
            Org orgOwner = (Org)session.load(Org.class, request.getIdOfOrgRequester());
            if (orgOwner == null) return CreateOrUpdateOrgCalendarDateResponse.error(request, 404, "org owner with recordId = " + request.getIdOfOrgRequester() + " was not found");
            specialDate.setOrgOwner(orgOwner);
            session.saveOrUpdate(specialDate);
            SpecialDate reloadedSpecialDate = FindExistingSpecialDate(request.getIdOfOrg(), request.getIdOfGroup(), request.getDate(), session);
            session.flush();
            transaction.commit();
            transaction = null;
            if (reloadedSpecialDate == null) {
                return CreateOrUpdateOrgCalendarDateResponse.error(request, 500, "reloaded special date is null");
            }
            return CreateOrUpdateOrgCalendarDateResponse.success(request, reloadedSpecialDate.getIdOfSpecialDate());
        } catch (Exception e) {
            logger.error("Error create or update special date, ", e);
            return CreateOrUpdateOrgCalendarDateResponse
                    .error(request, 500, "Error create or update special date: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


    private SpecialDate FindExistingSpecialDate(long idOfOrg, long idOfGroup, Date date, Session session) {
        Date fromDate = DateTimeExtensions.sameDateWithOtherTime(date, 0, 0, 0);
        Date toDate = DateTimeExtensions.sameDateWithOtherTime(date, 23, 59, 59);
        Criteria criteria = session.createCriteria(SpecialDate.class);
        criteria.add(Restrictions.eq("idOfOrg", idOfOrg));
        criteria.add(Restrictions.eq("idOfClientGroup", idOfGroup));
        criteria.add(Restrictions.between("date", fromDate, toDate));
        return (SpecialDate) criteria.uniqueResult();
    }
}
