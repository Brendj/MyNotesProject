/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.clients.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateItem;
import ru.axetta.ecafe.processor.web.partner.schoolapi.clients.dto.ClientUpdateResult;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.regex.Pattern;

import static ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils.updateClientRegistryVersion;

@Service
class UpdateClientCommand {

    private final Logger logger = LoggerFactory.getLogger(UpdateClientCommand.class);
    private final RuntimeContext runtimeContext;
    private final MoveClientsCommand moveClientsCommand;
    private static final int NOT_FOUND = 404, BAD_PARAMS = 400;
    private final Pattern namePattern = Pattern.compile("[a-zA-Zа-яА-Я\\s-]+");

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
                throw WebApplicationException.notFound(NOT_FOUND,
                        String.format("Client with ID='%d' not found", idOfClient));
            }
            setMobilePhone(client, request.getMobile(), user);
            setBirthDate(request.getBirthDate(), client);
            setGender(request.getGender(), client);
            setConfirmVideo(request.getConfirmVisualRecognition(), client);
            setDisableFromPlan(request.getStartExcludeDate(), request.getEndExcludedDate(),
                    request.getUseLastEEModeForPlan(), client);
            setGroupAndMiddleGroup(request, client, session, user);
            setPersonName(request, client);
            client.setUpdateTime(new Date());
            client.setClientRegistryVersion(version);
            session.update(client);
            session.flush();
            transaction.commit();
            transaction = null;
            return ClientUpdateResult.success(client.getIdOfClient());
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in update client info, ", e);
            throw new WebApplicationException("Error in update client info, ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void setPersonName(ClientUpdateItem request, Client client) {
        if (request.getPerson() == null) {
            return;
        }
        ClientGroup.Predefined predefinedGroup = ClientGroup.Predefined.parse(client.getIdOfClientGroup());
        if (predefinedGroup == null) {
            throw WebApplicationException.badRequest(400,
                    "Изменить ФИО можно только для клиентов предопределенных групп");
        }
        if (validateName(request.getPerson().getSurname(), 128)) {
            client.getPerson().setSurname(request.getPerson().getSurname().trim());
        }
        if (validateName(request.getPerson().getFirstName(), 64)) {
            client.getPerson().setFirstName(request.getPerson().getFirstName().trim());
        }
        if (validateName(request.getPerson().getSecondName(), 128)) {
            client.getPerson().setSecondName(request.getPerson().getSecondName().trim());
        }
    }

    private boolean validateName(String name, int maxLength) {
        if (StringUtils.isNotEmpty(name)) {
            if (name.trim().length() > maxLength) {
                throw new WebApplicationException(400,
                        String.format("Превышен максимально допустимый размер имени, '%s' (%d символов)", name,
                                maxLength));
            }
            return namePattern.matcher(name.trim()).matches();
        } else {
            return false;
        }
    }

    private void setGroupAndMiddleGroup(ClientUpdateItem request, Client client, Session session, User user) {
        if (request.getIdOfClientGroup() == null) {
            return;
        }
        if (!client.getIdOfClientGroup().equals(request.getIdOfClientGroup())) {

            long idOfOrg = request.getIdOfOrg() != null ? request.getIdOfOrg()
                    : client.getClientGroup().getCompositeIdOfClientGroup().getIdOfOrg();
            ClientGroup moveToGroup = (ClientGroup) session.get(ClientGroup.class,
                    new CompositeIdOfClientGroup(idOfOrg, request.getIdOfClientGroup()));
            if (moveToGroup == null) {
                throw WebApplicationException.notFound(NOT_FOUND,
                        String.format("Group of client with ID='%d' not found", request.getIdOfClientGroup()));
            }
            // обновляем группу
            String result = moveClientsCommand.updateClientGroupOrGetError(session, moveToGroup, client, user, false);
            if (StringUtils.isNotEmpty(result)) {
                throw new WebApplicationException(result);
            }
            // обновляем подгруппу если нужно
            result = moveClientsCommand.updateMiddleGroupOrGetError(session, request.getIdOfClientGroup(),
                    request.getIdOfMiddleGroup(), client);
            if (StringUtils.isNotEmpty(result)) {
                throw new WebApplicationException(result);
            }
        }
    }

    private void setDisableFromPlan(Date startExcludeDate, Date endExcludedDate, Boolean useLastEEModeForPlan,
            Client client) {
        if (useLastEEModeForPlan != null) {
            client.setUseLastEEModeForPlan(useLastEEModeForPlan);
        }
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
                throw WebApplicationException.badRequest(BAD_PARAMS, "Не верное значение для gender, " + gender);
            }
        }
    }

    private void setMobilePhone(Client client, String mobilePhone, User user) {
        if (mobilePhone == null || mobilePhone.length() == 0) {
            return;
        }
        mobilePhone = Client.checkAndConvertMobile(mobilePhone);
        if (mobilePhone == null) {
            throw WebApplicationException.badRequest(BAD_PARAMS, "Неправильный формат мобильного телефона");
        }
        ClientsMobileHistory clientsMobileHistory = new ClientsMobileHistory("REST API, updateClient метод");
        clientsMobileHistory.setUser(user);
        clientsMobileHistory.setOrg(user.getOrg());
        clientsMobileHistory.setShowing(
                String.format("Веб-АРМ, пользователь с ид: %d, name: %s", user.getIdOfUser(), user.getUserName()));
        client.initClientMobileHistory(clientsMobileHistory);
        client.setMobile(mobilePhone);
    }


}
