/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestriction;
import ru.axetta.ecafe.processor.core.persistence.PlanOrdersRestrictionType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.ResponseCodes;
import ru.axetta.ecafe.processor.web.partner.schoolapi.error.WebApplicationException;
import ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.dto.PlanOrderRestrictionDTO;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
class UpdatePlanOrderRestrictionsCommand {

    private final Logger logger = LoggerFactory.getLogger(UpdatePlanOrderRestrictionsCommand.class);
    private final RuntimeContext runtimeContext;

    @Autowired
    public UpdatePlanOrderRestrictionsCommand(RuntimeContext runtimeContext) {
        this.runtimeContext = runtimeContext;
    }


    public List<PlanOrdersRestriction> updateClientPlanOrderRestrictions(long idOfClient,
            List<PlanOrderRestrictionDTO> updateItems, boolean notified) {

        if (!notified) {
            // перезапись ограничений
            return setClientPlanOrderRestrictions(idOfClient, updateItems);
        } else {
            // удаление ограничений от старой конф. поставщика
            return notifiedOldClientPlanOrderRestrictions(idOfClient, updateItems);
        }
    }

    private List<PlanOrdersRestriction> setClientPlanOrderRestrictions(long idOfClient,
            List<PlanOrderRestrictionDTO> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            return new ArrayList<>();
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = checkClientOrRaiseError(idOfClient, session);
            List<PlanOrdersRestriction> currentRestrictions = DAOUtils
                    .getPlanOrdersRestrictionByClient(session, idOfClient);
            List<PlanOrderRestrictionDTO> currentConfProviderRestrictions = filterCurrentConfProviderRestrictions(
                    client, updateItems);

            long nextVersion = getNextVersion(session);
            List<PlanOrdersRestriction> updatedRestrictions = updateRestrictions(currentConfProviderRestrictions,
                    session, client, nextVersion, currentRestrictions);
            deleteOldRestrictions(session, nextVersion, currentRestrictions, updatedRestrictions);
            session.flush();
            transaction.commit();
            transaction = null;
            return updatedRestrictions;
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in update plan orders restrictions, ", e);
            throw new WebApplicationException("Error in update plan orders restrictions, ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private long getNextVersion(Session session) {
        return DAOUtils.nextVersionByTableWithoutLock(session, "cf_plan_orders_restrictions");
    }

    private List<PlanOrderRestrictionDTO> filterCurrentConfProviderRestrictions(Client client,
            List<PlanOrderRestrictionDTO> updateItems) {
        List<PlanOrderRestrictionDTO> oldRestrictions = filterOlderRestrictions(client, updateItems);
        List<PlanOrderRestrictionDTO> result = new ArrayList<>(updateItems);
        if (!oldRestrictions.isEmpty()) {
            result.removeAll(oldRestrictions);
        }
        return result;
    }

    private List<PlanOrdersRestriction> notifiedOldClientPlanOrderRestrictions(long idOfClient,
            List<PlanOrderRestrictionDTO> updateItems) {
        if (updateItems == null || updateItems.isEmpty()) {
            return new ArrayList<>();
        }
        Session session = null;
        Transaction transaction = null;
        try {
            session = this.runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Client client = checkClientOrRaiseError(idOfClient, session);
            List<PlanOrderRestrictionDTO> olderRestrictions = filterOlderRestrictions(client, updateItems);
            List<PlanOrdersRestriction> result = new ArrayList<>();
            if (olderRestrictions.size() > 0) {
                long nextVersion = getNextVersion(session);
                List<PlanOrdersRestriction> currentRestrictions = DAOUtils
                        .getPlanOrdersRestrictionByClient(session, idOfClient);

                for (PlanOrderRestrictionDTO item : olderRestrictions) {
                    PlanOrdersRestriction foundItem = findRestrictionsByIdIn(currentRestrictions, item.getId());
                    if (foundItem != null && !foundItem.getDeletedState()) {
                        result.add(deleteRestriction(session, nextVersion, foundItem));
                    }
                }
            }
            session.flush();
            transaction.commit();
            transaction = null;
            return result;
        } catch (WebApplicationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Error in delete plan orders restrictions, ", e);
            throw new WebApplicationException("Error in update plan orders restrictions, ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private List<PlanOrderRestrictionDTO> filterOlderRestrictions(Client client,
            List<PlanOrderRestrictionDTO> updateItems) {
        Long clientOrgConfProvider = client.getOrg().getConfigurationProvider().getIdOfConfigurationProvider();
        List<PlanOrderRestrictionDTO> result = new ArrayList<>();
        for (PlanOrderRestrictionDTO item : updateItems) {
            if (item.getIdOfConfigurationProvider() != null && !Objects
                    .equals(item.getIdOfConfigurationProvider(), clientOrgConfProvider)) {
                result.add(item);
            }
        }
        return result;
    }


    private List<PlanOrdersRestriction> updateRestrictions(List<PlanOrderRestrictionDTO> updateItems, Session session,
            Client client, long nextVersion, List<PlanOrdersRestriction> currentRestrictions) {
        List<PlanOrdersRestriction> result = new ArrayList<>();
        for (PlanOrderRestrictionDTO item : updateItems) {
            if (item.getId() == null) {
                // добавить новые ограничения
                PlanOrdersRestriction foundSimilar = DAOUtils
                        .findPlanOrdersRestriction(session, item.getIdOfClient(), item.getIdOfOrg(),
                                item.getComplexId().intValue());
                if (foundSimilar == null) {
                    result.add(createNewRestriction(session, nextVersion, item, client));
                } else {
                    result.add(updateRestriction(session, nextVersion, foundSimilar, item));
                }
            } else {
                // обновить существующие
                PlanOrdersRestriction foundRestriction = findRestrictionsByIdIn(currentRestrictions, item.getId());
                if (foundRestriction != null) {
                    result.add(updateRestriction(session, nextVersion, foundRestriction, item));
                }
            }
        }
        return result;
    }

    private void deleteOldRestrictions(Session session, long nextVersion,
            List<PlanOrdersRestriction> currentRestrictions, List<PlanOrdersRestriction> updatedRestrictions) {
        for (PlanOrdersRestriction restriction : currentRestrictions) {
            PlanOrdersRestriction foundUpdatedRestriction = findRestrictionsByIdIn(updatedRestrictions, restriction.getIdOfPlanOrdersRestriction());
            if (foundUpdatedRestriction == null && !restriction.getDeletedState() && restriction.getResol() == 0 /*Exclude*/) {
                // если не прислали ограничение, значит оно устарело
                deleteRestriction(session, nextVersion, restriction);
            }
        }
    }


    private Client checkClientOrRaiseError(long idOfClient, Session session) {
        Client client = (Client) session.load(Client.class, idOfClient);
        if (client == null) {
            throw WebApplicationException.notFound(ResponseCodes.CLIENT_NOT_FOUND.getCode(),
                    String.format("Client with id='%d' not found", idOfClient));
        }
        return client;
    }

    private PlanOrdersRestriction findRestrictionsByIdIn(List<PlanOrdersRestriction> restrictions, Long id) {
        for (PlanOrdersRestriction restriction : restrictions) {
            if (Objects.equals(restriction.getIdOfPlanOrdersRestriction(), id)) {
                return restriction;
            }
        }
        return null;
    }

    private PlanOrdersRestriction deleteRestriction(Session session, long nextVersion,
            PlanOrdersRestriction foundItem) {
        foundItem.setDeletedState(true);
        foundItem.setVersion(nextVersion);
        foundItem.setLastUpdate(new Date());
        session.update(foundItem);
        return foundItem;
    }


    private PlanOrdersRestriction updateRestriction(Session session, long nextVersion,
            PlanOrdersRestriction currentItem, PlanOrderRestrictionDTO updateItem) {
        try {
            if (Boolean.FALSE.equals(currentItem.getDeletedState()) && Objects
                    .equals(currentItem.getResol(), updateItem.getResolution())) {
                return currentItem;
            }
            currentItem.setDeletedState(false);
            currentItem.setResol(updateItem.getResolution());
            currentItem.setVersion(nextVersion);
            currentItem.setLastUpdate(new Date());
            session.saveOrUpdate(currentItem);
            return currentItem;
        } catch (Exception e) {
            logger.error("Error saving planOrdersRestriction item = " + updateItem.toString(), e);
            throw new WebApplicationException("Error saving planOrdersRestriction item = " + updateItem.toString());
        }
    }

    private PlanOrdersRestriction createNewRestriction(Session session, long nextVersion,
            PlanOrderRestrictionDTO updateItem, Client client) {
        try {
            PlanOrdersRestrictionType planOrdersRestrictionType = PlanOrdersRestrictionType
                    .fromInteger(updateItem.getPlanType());
            if (planOrdersRestrictionType == null) {
                planOrdersRestrictionType = PlanOrdersRestrictionType.LP;
            }
            Long idOfConfigarationProvider = updateItem.getIdOfConfigurationProvider();
            if (idOfConfigarationProvider == null) {
                idOfConfigarationProvider = client.getOrg().getConfigurationProvider().getIdOfConfigurationProvider();
            }
            PlanOrdersRestriction planOrdersRestriction = new PlanOrdersRestriction();
            planOrdersRestriction.setIdOfClient(updateItem.getIdOfClient());
            planOrdersRestriction.setIdOfOrgOnCreate(updateItem.getIdOfOrg());
            planOrdersRestriction.setArmComplexId(updateItem.getComplexId().intValue());
            planOrdersRestriction.setComplexName(updateItem.getComplexName());
            planOrdersRestriction.setIdOfConfigurationProoviderOnCreate(idOfConfigarationProvider);
            planOrdersRestriction.setPlanOrdersRestrictionType(planOrdersRestrictionType);
            planOrdersRestriction.setDeletedState(false);
            planOrdersRestriction.setResol(updateItem.getResolution());
            planOrdersRestriction.setVersion(nextVersion);
            planOrdersRestriction.setLastUpdate(new Date());
            session.saveOrUpdate(planOrdersRestriction);
            return planOrdersRestriction;
        } catch (Exception e) {
            logger.error("Error create new planOrdersRestriction item = " + updateItem.toString(), e);
            throw new WebApplicationException("Error saving planOrdersRestriction item = " + updateItem.toString());
        }
    }


}
