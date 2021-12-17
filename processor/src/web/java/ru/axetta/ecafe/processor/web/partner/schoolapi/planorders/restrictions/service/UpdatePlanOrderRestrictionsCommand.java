/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.planorders.restrictions.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtComplex;
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
            // обновить ограничения
            return updateClientPlanOrderRestrictions(idOfClient, updateItems);
        } else {
            // удаление ограничений от старой конф. поставщика
            return notifiedOldClientPlanOrderRestrictions(idOfClient, updateItems);
        }
    }

    private List<PlanOrdersRestriction> updateClientPlanOrderRestrictions(long idOfClient,
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
            List<PlanOrdersRestriction> clientRestrictions = DAOUtils.getPlanOrdersRestrictionByClient(session, idOfClient);
            List<PlanOrderRestrictionDTO> currentRestrictions = filterCurrentRestrictions(client, updateItems);
            long nextVersion = getNextVersion(session);
            List<PlanOrdersRestriction> updatedRestrictions = updateRestrictions(currentRestrictions, session, nextVersion, clientRestrictions);
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

    private List<PlanOrdersRestriction> notifiedOldClientPlanOrderRestrictions(long idOfClient, List<PlanOrderRestrictionDTO> updateItems) {
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
                List<PlanOrdersRestriction> clientRestrictions = DAOUtils.getPlanOrdersRestrictionByClient(session, idOfClient);
                for (PlanOrderRestrictionDTO item : olderRestrictions) {
                    PlanOrdersRestriction foundItem = findRestrictionsByIdIn(clientRestrictions, item.getId());
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

    private List<PlanOrderRestrictionDTO> filterCurrentRestrictions(Client client, List<PlanOrderRestrictionDTO> restrictins) {
        List<PlanOrderRestrictionDTO> oldRestrictions = filterOlderRestrictions(client, restrictins);
        List<PlanOrderRestrictionDTO> result = new ArrayList<>(restrictins);
        if (!oldRestrictions.isEmpty()) {
            result.removeAll(oldRestrictions);
        }
        return result;
    }

    private List<PlanOrderRestrictionDTO> filterOlderRestrictions(Client client, List<PlanOrderRestrictionDTO> restrictions) {
        List<PlanOrderRestrictionDTO> result = new ArrayList<>();
        for (PlanOrderRestrictionDTO item : restrictions) {
            if (!client.getOrg().getIdOfOrg().equals(item.getIdOfOrg())) {
                result.add(item);
            }
        }
        return result;
    }

    private PlanOrdersRestriction findRestrictionsByIdIn(List<PlanOrdersRestriction> restrictions, Long id) {
        for (PlanOrdersRestriction restriction : restrictions) {
            if (Objects.equals(restriction.getIdOfPlanOrdersRestriction(), id)) {
                return restriction;
            }
        }
        return null;
    }

    private List<PlanOrdersRestriction> updateRestrictions(List<PlanOrderRestrictionDTO> updateItems, Session session,
            long nextVersion, List<PlanOrdersRestriction> currentRestrictions) {
        List<PlanOrdersRestriction> result = new ArrayList<>();
        for (PlanOrderRestrictionDTO item : updateItems) {
            if (item.getId() == null) {
                // добавить новые ограничения
                PlanOrdersRestriction foundSimilar = DAOUtils.findPlanOrdersRestriction(session, item.getIdOfClient(),
                        item.getIdOfOrg(), item.getComplexId().intValue());
                if (foundSimilar == null) {
                    result.add(createNewRestriction(session, nextVersion, item));
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
            PlanOrdersRestriction foundUpdatedRestriction = findRestrictionsByIdIn(updatedRestrictions,
                    restriction.getIdOfPlanOrdersRestriction());
            if (foundUpdatedRestriction == null && !restriction.getDeletedState()
                    && restriction.getResol() == PlanOrdersRestriction.RESOLUTION_EXCLUDE) {
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
            if (Objects.equals(currentItem.getDeletedState(), false)
                    && Objects.equals(currentItem.getResol(), updateItem.getResolution())
                    && currentItem.getArmComplexId().longValue() == updateItem.getComplexId()) {
                return currentItem;
            }
            if (currentItem.getIdOfConfigurationProoviderOnCreate() == null) {
                // если нет конфг, проставляем
                currentItem.setIdOfConfigurationProoviderOnCreate(getIdOfConfigurationProvider(session, updateItem.getIdOfOrg()));
            }
            if (currentItem.getArmComplexId().longValue() != updateItem.getComplexId()) {
                currentItem.setArmComplexId(updateItem.getComplexId().intValue());
                WtComplex complex = (WtComplex) session.load(WtComplex.class, updateItem.getComplexId());
                if (complex == null) {
                    throw WebApplicationException.notFound(404, String.format("Комплекс с ид: '%d' не найден", updateItem.getComplexId()));
                }
                currentItem.setComplexName(complex.getName());
            }
            currentItem.setDeletedState(false);
            currentItem.setResol(updateItem.getResolution());
            currentItem.setVersion(nextVersion);
            currentItem.setLastUpdate(new Date());
            session.saveOrUpdate(currentItem);
            return currentItem;
        } catch (Exception e) {
            logger.error("Error saving planOrdersRestriction item = " + updateItem.toString(), e);
            throw new WebApplicationException("Error saving planOrdersRestriction item = " + updateItem);
        }
    }

    private PlanOrdersRestriction createNewRestriction(Session session, long nextVersion,
            PlanOrderRestrictionDTO updateItem) {
        try {
            WtComplex complex = (WtComplex) session.load(WtComplex.class, updateItem.getComplexId());
            if (complex == null) {
                throw WebApplicationException.notFound(404,
                        String.format("Комплекс с ид: '%d' не найден", updateItem.getComplexId()));
            }
            PlanOrdersRestrictionType planOrdersRestrictionType = PlanOrdersRestrictionType.fromInteger(
                    updateItem.getPlanType());
            if (planOrdersRestrictionType == null) {
                planOrdersRestrictionType = PlanOrdersRestrictionType.LP;
            }
            PlanOrdersRestriction planOrdersRestriction = new PlanOrdersRestriction();
            planOrdersRestriction.setIdOfClient(updateItem.getIdOfClient());
            planOrdersRestriction.setIdOfOrgOnCreate(updateItem.getIdOfOrg());
            planOrdersRestriction.setArmComplexId(updateItem.getComplexId().intValue());
            planOrdersRestriction.setComplexName(complex.getName());
            planOrdersRestriction.setIdOfConfigurationProoviderOnCreate(getIdOfConfigurationProvider(session, updateItem.getIdOfOrg()));
            planOrdersRestriction.setPlanOrdersRestrictionType(planOrdersRestrictionType);
            planOrdersRestriction.setDeletedState(false);
            planOrdersRestriction.setResol(updateItem.getResolution());
            planOrdersRestriction.setVersion(nextVersion);
            planOrdersRestriction.setLastUpdate(new Date());
            session.saveOrUpdate(planOrdersRestriction);
            return planOrdersRestriction;
        } catch (Exception e) {
            logger.error("Error create new planOrdersRestriction item = " + updateItem.toString(), e);
            throw new WebApplicationException("Error saving planOrdersRestriction item = " + updateItem);
        }
    }

    /*
    * Получить ид. конфигурации,  в порядке убывания приоритета
    * 1. тек. организация
    * 2. любая конф друж. организации того же корпуса того же типа орг
    * 3. любая конф друж. организации того же корпуса
    * 4. любая конф организации того же округа
    * 5. null
    * */
    private Long getIdOfConfigurationProvider(Session session, Long idOfOrg) throws Exception {
        Long orgConfigurationProvider = DAOUtils.getOrgConfigurationProvider(session, idOfOrg);
        // конфигурация тек. организации
        if (orgConfigurationProvider > 0) {
            return orgConfigurationProvider;
        }
        List<Org> friendlyOrgs = DAOUtils.findAllFriendlyOrgs(session, idOfOrg);
        OrganizationType orgType = null;
        String district = "";
        // сохраняю тип текущей организации
        for (Org org : friendlyOrgs) {
            if (org.getIdOfOrg().equals(idOfOrg)) {
                orgType = org.getType();
                district = org.getDistrict();
                break;
            }
        }
        // беру первую конфигурацию организации того же типа, что и исходная
        for (Org org : friendlyOrgs) {
            if (org.getType().equals(orgType) && org.getConfigurationProvider() != null) {
                return org.getConfigurationProvider().getIdOfConfigurationProvider();
            }
        }
        // если ничего нет, беру первую не пустую конфигурацию
        for (Org org : friendlyOrgs) {
            if (org.getConfigurationProvider() != null) {
                return org.getConfigurationProvider().getIdOfConfigurationProvider();
            }
        }
        // последний случай, либо конф орг из того же округа либо ничего
        return findFirstConfProviderOrgFromDistrict(session, district);
    }

    private static Long findFirstConfProviderOrgFromDistrict(Session session, String district) {
        List result = session.createQuery(
                        "select o.configurationProvider.idOfConfigurationProvider from Org o where o.district=:district and o.configurationProvider is not null")
                .setParameter("district", district).setFirstResult(1).list();
        return result != null && !result.isEmpty() ? (Long) result.get(0) : null;
    }

}
