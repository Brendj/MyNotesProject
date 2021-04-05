/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class CreateOrUpdateGuardianCommand {

    private Logger logger = LoggerFactory.getLogger(CreateOrUpdateGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public CreateOrUpdateGuardianCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public CreateOrUpdateGuardianResponse createGuardian(CreateOrUpdateGuardianRequest request, User user)
    {
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Long newGuardianVersion = ClientManager.generateNewClientGuardianVersion(session);

            ClientGuardian guardian = FindExistingGuardianLink(request.getChildClientId(), request.getGuardianClientId(), session);
            if (guardian == null) guardian = new ClientGuardian(request.getChildClientId(), request.getGuardianClientId(), ClientCreatedFromType.ARM);
            guardian.setVersion(newGuardianVersion);
            guardian.setLastUpdate(new Date());
            guardian.setDeletedState(false);
            guardian.setDeleteDate(null);
            guardian.setDisabled(!request.getIsEnabledInformationSupport());
            guardian.setRepresentType(ClientGuardianRepresentType.fromInteger(request.getRights()));
            guardian.setRelation(ClientGuardianRelationType.fromInteger(request.getRelationType()));
            session.saveOrUpdate(guardian);
            ClientGuardian reloadedGuardian = FindExistingGuardianLink(request.getChildClientId(), request.getGuardianClientId(), session);
            session.flush();
            transaction.commit();
            transaction = null;
            if (reloadedGuardian == null) return CreateOrUpdateGuardianResponse.error(request, "Internal server error");
            return CreateOrUpdateGuardianResponse.success(reloadedGuardian.getIdOfClientGuardian(), request);
        }
        catch (WebApplicationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error in create or update guardian, ", e);
            throw new WebApplicationException("Error in create or update guardian, ", e);
        }
        finally
        {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


    private ClientGuardian FindExistingGuardianLink(long childId, long guardianId, Session session)
    {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", childId));
        criteria.add(Restrictions.eq("idOfGuardian", guardianId));
        return (ClientGuardian) criteria.uniqueResult();
    }
}
