/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteGuardianCommand {

    private Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;
    private static final int NOT_FOUND = 404, BAD_PARAMS = 400;

    @Autowired
    public DeleteGuardianCommand(RuntimeContext runtimeContext)
    {
        this.runtimeContext = runtimeContext;
    }

    public DeleteGuardianResponse deleteGuardian(long idOfRecord, User user)
    {
        Session session = null;
        Transaction transaction = null;

        try
        {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            ClientGuardian guardian = (ClientGuardian) session.get(ClientGuardian.class, idOfRecord);
            if (guardian == null) throw new WebApplicationException(NOT_FOUND, "Guardian with record ID = '" + idOfRecord + "' was not found");
            Long newGuardianVersion = ClientManager.generateNewClientGuardianVersion(session);
            guardian.delete(newGuardianVersion);
            session.update(guardian);
            session.flush();
            transaction.commit();
            return DeleteGuardianResponse.success(guardian.getIdOfClientGuardian());
        }
        catch (WebApplicationException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            logger.error("Error in update guardian, ", e);
            throw new WebApplicationException("Error in update guardian, ", e);
        }
        finally
        {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
