/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Service
class UpdateClientCommand {

    private Logger logger = LoggerFactory.getLogger(UpdateClientCommand.class);
    private final RuntimeContext runtimeContext;
    private final MoveClientsCommand moveClientsCommand;
    private static final int NOT_FOUND = 404, BAD_PARAMS = 400;
    @Autowired
    public UpdateClientCommand(RuntimeContext runtimeContext, MoveClientsCommand moveClientsCommand) {
        this.runtimeContext = runtimeContext;
        this.moveClientsCommand = moveClientsCommand;
    }

    public ClientUpdateResult updateClient(Long idOfClient, ClientUpdateItem request, User user) {
        Session session = null;
        Transaction transaction = null;

        try {
            long version = updateClientRegistryVersion(null);
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = (Client) session.get(Client.class, idOfClient);
            if (client == null) {
                throw new WebApplicationException(NOT_FOUND, String.format("Client with ID='%d' not found", idOfClient));
            }
            setMobilePhone(client, request.getMobile(), user);
            setBirthDate(request.getBirthDate(), client);
            setGender(request.getGender(), client);
            setConfirmVideo(request.getConfirmVisualRecognition(), client);
            setDisableFromPlan(request.getStartExcludeDate(), request.getEndExcludedDate(), client);
            setGroupAndMiddleGroup(request, client, session);
            client.setClientRegistryVersion(version);
            session.update(client);
            session.flush();
            transaction.commit();
            return ClientUpdateResult.success(client.getIdOfClient());
        }
        catch (WebApplicationException e){
            throw e;
        }
        catch (Exception e) {
            logger.error("Error in update client info, ", e);
            throw new WebApplicationException("Error in update client info, ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void setGroupAndMiddleGroup(ClientUpdateItem request, Client client, Session session) {
        if (request.getIdOfClientGroup() == null) {
            return;
        }
        if (!client.getIdOfClientGroup().equals(request.getIdOfClientGroup())) {
            // обновляем группу
            String result = moveClientsCommand
                    .updateClientGroupOrGetError(request.getIdOfClientGroup(), request.getIdOfOrg(), session, client);
            if (result != null) {
                throw new WebApplicationException(result);
            }
            // обновляем подгруппу если нужно
            result = moveClientsCommand.updateMiddleGroupOrGetError(request.getIdOfClientGroup(), request.getIdOfMiddleGroup(), session, client);
            if (result != null) {
                throw new WebApplicationException(result);
            }
        }
    }

    private void setDisableFromPlan(Date startExcludeDate, Date endExcludedDate, Client client) {
        if (startExcludeDate != null) {
            client.setDisablePlanCreationDate(startExcludeDate);
        }
        if (endExcludedDate != null) {
            client.setDisablePlanEndDate(endExcludedDate);
        }
    }

    private void setConfirmVideo(Boolean confirmVisualRecognition, Client client) {
        if (confirmVisualRecognition != null) {
            client.setConfirmVisualRecognition(confirmVisualRecognition);
        }
    }

    private void setBirthDate(Date birthDate, Client client) {
        if (birthDate != null) {
            client.setBirthDate(birthDate);
        }
    }

    private void setGender(Integer gender, Client client) {
        if (gender != null) {
            if (gender == 0 || gender == 1) {
                client.setGender(gender);
            } else {
                throw new WebApplicationException(BAD_PARAMS, "Не верное значение для gender, " + gender);
            }
        }
    }

    private void setMobilePhone(Client client, String mobilePhone, User user) {
        if (mobilePhone == null || mobilePhone.length() == 0) {
            return;
        }
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone == null) {
            throw new WebApplicationException(BAD_PARAMS, "Неправильный формат мобильного телефона");
        }
        ClientsMobileHistory clientsMobileHistory = new ClientsMobileHistory("REST API, updateClient метод");
        clientsMobileHistory.setUser(user);
        clientsMobileHistory.setOrg(user.getOrg());
        clientsMobileHistory.setShowing(
                String.format("WebАРМ, пользователь с ид: %d, name: %s", user.getIdOfUser(), user.getUserName()));
        client.initClientMobileHistory(clientsMobileHistory);
        client.setMobile(mobilePhone);
    }


}
