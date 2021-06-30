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
import ru.axetta.ecafe.processor.web.partner.schoolapi.calendar.dto.DeleteOrgCalendarDateResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service.DeleteGuardianCommand;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteOrgCalendarDateCommand {
    private final Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public DeleteOrgCalendarDateCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public DeleteOrgCalendarDateResponse deleteOrgCalendarDate(long recordId, long idOfOrgRequester, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            SpecialDate specialDate = (SpecialDate) session.get(SpecialDate.class, recordId);
            if (specialDate == null) return DeleteOrgCalendarDateResponse.error(recordId, 404, "orgCalendarDate with recordId = " + recordId + " was not found");
            specialDate.setDeleted(true);
            Org orgOwner = (Org)session.load(Org.class, idOfOrgRequester);
            if (orgOwner == null) return DeleteOrgCalendarDateResponse.error(recordId, 404, "org owner with recordId = " + idOfOrgRequester + " was not found");
            specialDate.setOrgOwner(orgOwner);
            specialDate.setVersion(DAOUtils.nextVersionBySpecialDate(session));
            session.update(specialDate);
            session.flush();
            transaction.commit();
            transaction = null;
            return DeleteOrgCalendarDateResponse.success(specialDate.getIdOfSpecialDate());
        } catch (Exception e) {
            logger.error("Error update org calendar date with record id " + recordId + ": ", e);
            return DeleteOrgCalendarDateResponse.error(recordId, 500, "Error update org calendar date with record id " + recordId + ": " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
