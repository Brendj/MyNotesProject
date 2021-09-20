/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;

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
public class CreateOrUpdateGuardianCommand {
    private final Logger logger = LoggerFactory.getLogger(CreateOrUpdateGuardianCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public CreateOrUpdateGuardianCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }

    public CreateOrUpdateGuardianResponse createOrUpdateGuardian(CreateOrUpdateGuardianRequest request, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Long newGuardianVersion = DAOUtils.nextVersionBySpecialDate(session);

            ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
            clientGuardianHistory.setCreatedFrom(ClientCreatedFromType.ARM);
            clientGuardianHistory.setReason("Rest метод createOrUpdateGuardian (АРМ администратора)");
            clientGuardianHistory.setUser(user);
            clientGuardianHistory.setChangeDate(new Date());

            ClientGuardian guardian = FindExistingGuardianLink(request.getChildClientId(),
                    request.getGuardianClientId(), session);
            if (guardian == null) {
                guardian = new ClientGuardian(request.getChildClientId(), request.getGuardianClientId(),
                        ClientCreatedFromType.ARM);
                clientGuardianHistory.setAction("Создание новой связки");

            }
            else {
                clientGuardianHistory.setAction("Обновление данных связки");
            }
            try{
                clientGuardianHistory.setGuardian(DAOReadonlyService.getInstance().findClientById(request.getGuardianClientId()).getContractId().toString());
            } catch (Exception e)
            {
                clientGuardianHistory.setGuardian("Не удалось получить л/с представителя");
            }
            //
            ClientGuardianHistory clientGuardianHistoryChanged =
                    clientGuardianHistory.getCopyClientGuardionHistory(clientGuardianHistory);
            session.persist(clientGuardianHistoryChanged);
            //
            guardian.initializateClientGuardianHistory(clientGuardianHistory);
            guardian.setVersion(newGuardianVersion);
            guardian.setLastUpdate(new Date());
            guardian.setDeletedState(false);
            guardian.setDeleteDate(null);
            guardian.setDisabled(!request.getIsEnabledInformationSupport());
            guardian.setRepresentType(ClientGuardianRepresentType.fromInteger(request.getRights()));
            guardian.setRelation(ClientGuardianRelationType.fromInteger(request.getRelationType()));
            session.saveOrUpdate(guardian);
            ClientGuardian reloadedGuardian = FindExistingGuardianLink(request.getChildClientId(),
                    request.getGuardianClientId(), session);
            session.flush();
            transaction.commit();
            transaction = null;
            if (reloadedGuardian == null) {
                return CreateOrUpdateGuardianResponse.error(request, 500, "reloaded guardian is null");
            }
            return CreateOrUpdateGuardianResponse.success(request, reloadedGuardian.getIdOfClientGuardian());
        } catch (Exception e) {
            logger.error("Error create or update guardian, ", e);
            return CreateOrUpdateGuardianResponse
                    .error(request, 500, "Error create or update guardian: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }


    private ClientGuardian FindExistingGuardianLink(long childId, long guardianId, Session session) {
        Criteria criteria = session.createCriteria(ClientGuardian.class);
        criteria.add(Restrictions.eq("idOfChildren", childId));
        criteria.add(Restrictions.eq("idOfGuardian", guardianId));
        return (ClientGuardian) criteria.uniqueResult();
    }
}
