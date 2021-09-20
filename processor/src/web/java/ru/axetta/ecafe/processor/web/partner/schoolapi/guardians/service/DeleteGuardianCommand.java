/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.ClientCreatedFromType;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardian;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class DeleteGuardianCommand {

    private final Logger logger = LoggerFactory.getLogger(DeleteGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public DeleteGuardianCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public DeleteGuardianResponse deleteGuardian(long recordId, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            ClientGuardian guardian = (ClientGuardian) session.get(ClientGuardian.class, recordId);
            if (guardian == null) {
                return DeleteGuardianResponse
                        .error(recordId, 404, "guardian with recordId = " + recordId + " was not found");
            }
            //
            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.ARM);
            clientGuardianHistory.setReason("Веб метод deleteGuardian (АРМ администратора)");
            clientGuardianHistory.setUser(user);
            clientGuardianHistory.setChangeDate(new Date());
            guardian.initializateClientGuardianHistory(clientGuardianHistory);
            //
            Long newGuardianVersion = ClientManager.generateNewClientGuardianVersion(session);
            guardian.delete(newGuardianVersion);
            session.update(guardian);
            session.flush();
            transaction.commit();
            transaction = null;
            return DeleteGuardianResponse.success(guardian.getIdOfClientGuardian());
        } catch (Exception e) {
            logger.error("Error update guardian with record id " + recordId + ": ", e);
            return DeleteGuardianResponse
                    .error(recordId, 500, "Error update guardian with record id " + recordId + ": " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }
}
