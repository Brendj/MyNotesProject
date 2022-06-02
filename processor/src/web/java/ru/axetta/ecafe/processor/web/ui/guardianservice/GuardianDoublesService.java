package ru.axetta.ecafe.processor.web.ui.guardianservice;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.card.CardManager;
import ru.axetta.ecafe.processor.core.client.items.NotificationSettingItem;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.service.card.CardService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import javax.faces.context.FacesContext;
import javax.persistence.Query;
import java.util.*;

@Component
public class GuardianDoublesService {
    Logger logger = LoggerFactory.getLogger(GuardianDoublesService.class);
    private Set<Long> processedCG = new HashSet<>();

    public static final String HISTORY_LABEL = "Удаление дубликата представителя";

    public void processDeleteDoubleGuardiansForOrg(long idOfOrg) {
        logger.info("Start process one org. Id=" + idOfOrg);
        processedCG.clear();
        List<CGItem> items = getCGItems(idOfOrg);
        Map<String, List<CGItem>> map = getCGItemsMap(items);
        for (Map.Entry me : map.entrySet()) {
            String fio = (String) me.getKey();
            List<CGItem> children = (List)me.getValue();
            processOneClient(fio, children);


            //logger.info(me.getKey().toString() + ": " + ((List)me.getValue()).size());
        }
        logger.info("End process one org. Id=" + idOfOrg);
    }

    private List<CGItem> getCGItems(long idOfOrg) {
        Session session = null;
        Transaction transaction = null;
        List<CGItem> result = new LinkedList<>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String query_str = "select c.idofclient, cg.idofguardian, pg.surname, pg.firstname, " +
                    "pg.secondname, g.mobile, ca.cardno, ca.state, c.idoforg as clientOrg, g.idoforg as guardianOrg, " +
                    "cg.idofclientguardian, ca.lastupdate, g.balance, g.idofclientgroup, g.lastupdate as guardianLU, " +
                    "cg.deletedstate, cg.disabled " +
                    "from cf_clients c join cf_client_guardian cg on c.idofclient = cg.idofchildren " +
                    "join cf_clients g on g.idofclient = cg.idofguardian " +
                    "join cf_persons pg on pg.idofperson = g.idofperson " +
                    "left join cf_cards ca on ca.idofclient = g.idofclient and ca.state in (0, 4) and ca.lifestate = 1 and ca.validdate > :card_date " +
                    "where g.idoforg in (select friendlyorg from cf_friendly_organization where currentorg = :idOfOrg) " +
                    "and g.idofclientgroup >= :group_employees and g.mobile is not null and g.mobile <> '' " +
                    "and g.idofclientgroup not in (:group_leaving, :group_deleted) " +
                    "order by c.idofclient";
            Query query = session.createNativeQuery(query_str);
            query.setParameter("idOfOrg", idOfOrg);
            query.setParameter("group_employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("group_leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            query.setParameter("group_deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
            query.setParameter("card_date", (new Date()).getTime());
            List list = query.getResultList();
            for (Object o : list) {
                Object[] row = (Object[]) o;
                CGItem item = new CGItem(HibernateUtils.getDbLong(row[0]),
                        HibernateUtils.getDbLong(row[1]),
                        HibernateUtils.getDbString(row[2]).trim()
                                .concat(HibernateUtils.getDbString(row[3]).trim())
                                .concat(HibernateUtils.getDbString(row[4]).trim()),
                        HibernateUtils.getDbString(row[5]),
                        HibernateUtils.getDbLong(row[6]),
                        HibernateUtils.getDbInt(row[7]),
                        HibernateUtils.getDbLong(row[8]).equals(HibernateUtils.getDbLong(row[9])),
                        HibernateUtils.getDbLong(row[10]),
                        HibernateUtils.getDbLong(row[11]),
                        HibernateUtils.getDbLong(row[12]),
                        HibernateUtils.getDbLong(row[13]),
                        HibernateUtils.getDbLong(row[14]),
                        (Boolean)row[15],
                        HibernateUtils.getDbInt(row[16]));
                result.add(item);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error(String.format("Error in getCGItems. IdOrg = %s: ", idOfOrg), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<String, List<CGItem>> getCGItemsMap(List<CGItem> items) {
        /*Map<Long, List<CGItem>> result = new HashMap<>();
        for (CGItem item : items) {
            List<CGItem> list = result.get(item.getIdOfClient());
            if (list == null) {
                list = new LinkedList<>();
            }

            list.add(item);
            result.put(item.getIdOfClient(), list);
        }
        return result;*/
        Map<String, List<CGItem>> result = new HashMap<>();
        for (CGItem item : items) {
            List<CGItem> list = result.get(item.getFioPlusMobile());
            if (list == null) {
                list = new LinkedList<>();
            }

            list.add(item);
            result.put(item.getFioPlusMobile(), list);
        }
        return result;
    }

    private void processOneClient(String fio, List<CGItem> children) {
        for (CGItem item : children) {
            if (processedCG.contains(item.getIdOfClientGuardian())) continue;
            List<CGItem> processList = getNotProcessedDoubles(item, children);
            if (processList.size() == 1) continue;
            //Есть дубли, дальше обрабатываем
            processDoubles(processList);
        }
    }

    private void processDoubles(List<CGItem> processList) {
        Set<CGCardItem> cardItems = new TreeSet<>(); //все карты
        String log_message = "Processing doubles: ";
        for (CGItem item : processList) {
            log_message += String.format("\nFIO: %s, idOfClient: %s, idOfGuardian: %s, cardNo: %s, guardianClientGroup: %s, " +
                    "guardianBalance: %s, guardianLastUpdate: %s, cardLastUpdate: %s, clientGuardianDeletedState: %s, " +
                    "clientGuardianDisabled: %s",
                    item.getFioPlusMobile(), item.getIdOfClient(),
                    item.getIdOfGuardin(), item.getCardno(), item.getIdOfClientGroup(), item.getBalance(), item.getGuardianLastUpdate(),
                    item.getCardLastUpdate(), item.getDeletedState(), item.getDisabled());
            if (item.getCardno() != null) {
                cardItems.add(new CGCardItem(item.getCardno(), item.getIdOfGuardin(), item.getCardLastUpdate(),
                        item.getIdOfClientGroup(), item.getGuardianLastUpdate()));
            }
        }
        logger.info(log_message);
        CGCardItem priorityCard = null;
        if (cardItems.size() > 0) {
            priorityCard = cardItems.iterator().next();
        }
        Collections.sort(processList);
        logger.info(String.format("Priority client id: %s, Priority card: %s", processList.get(0).getIdOfGuardin(),
                priorityCard == null ? "null" : priorityCard.getIdOfCard()));
        deleteGuardians(processList.get(0), processList, priorityCard);
        for (CGItem item : processList) {
            processedCG.add(item.getIdOfClientGuardian());
        }
    }

    private void deleteGuardians(CGItem aliveGuardian, List<CGItem> deleteGuardianList, CGCardItem priorityCard) {
        if (allTheSameGuardian(deleteGuardianList)) return;
        boolean allCGDeleted = getAllCGDeleted(deleteGuardianList);
        Map<Long, Boolean> cgDisabled = getCGDisabledMap(deleteGuardianList);
        //boolean allCGDisabled = getAllCGDisabled(deleteGuardianList);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Long version = ClientManager.generateNewClientGuardianVersion(session);
            boolean aliveGuardianCardIsAlive = true;
            if (priorityCard != null && aliveGuardian.getCardno() != null
                    && !aliveGuardian.getCardno().equals(priorityCard.getIdOfCard())) {
                aliveGuardianCardIsAlive = false;
            }
            for (CGItem item : deleteGuardianList) {
                if (item.equals(aliveGuardian) && !allCGDeleted) continue;
                if (item.getCardno() != null && priorityCard != null && !item.getCardno().equals(priorityCard.getIdOfCard())) {
                    Client g = DAOUtils.findClient(session, item.getIdOfGuardin());
                    RuntimeContext.getAppContext().getBean(CardService.class).block(item.getCardno(), g.getOrg().getIdOfOrg(),
                    item.getIdOfGuardin(), true, HISTORY_LABEL, CardState.BLOCKED);
                    logger.info(String.format("Blocked card with cardno = %s", item.getCardno()));
                }
                //Set<ClientGuardianNotificationSetting> notificationSettings
                deleteGuardian(session, aliveGuardian, item, version, cgDisabled);

            }
            if (priorityCard != null && aliveGuardian.getCardno() != null && !aliveGuardianCardIsAlive) {
                Client g = DAOUtils.findClient(session, aliveGuardian.getIdOfGuardin());
                RuntimeContext.getAppContext().getBean(CardService.class).block(aliveGuardian.getCardno(), g.getOrg().getIdOfOrg(),
                        aliveGuardian.getIdOfGuardin(), true, HISTORY_LABEL, CardState.BLOCKED);
                logger.info(String.format("Blocked card with cardno = %s", aliveGuardian.getCardno()));
            }
            if (priorityCard != null && (aliveGuardian.getCardno() == null || !aliveGuardian.getCardno().equals(priorityCard.getIdOfCard()))) {
                Card card = DAOUtils.findCardByCardNo(session, priorityCard.getIdOfCard());
                RuntimeContext.getInstance().getCardManager().updateCardInSession(session, aliveGuardian.getIdOfGuardin(),
                        card.getIdOfCard(), card.getCardType(), CardState.ISSUED.getValue(), card.getValidTime(),
                        card.getLifeState(), HISTORY_LABEL, new Date(), card.getExternalId(), null, card.getOrg().getIdOfOrg(),
                        "", false);
                logger.info(String.format("Card with cardno = %s issue to client id = %s", priorityCard.getIdOfCard(), aliveGuardian.getIdOfGuardin()));
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.info("Cannot delete guardians for client id = " + aliveGuardian.getIdOfClient(), e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private boolean allTheSameGuardian(List<CGItem> list) {
        Long id = null;
        for (CGItem item : list) {
            if (id == null) {
                id = item.getIdOfGuardin();
                continue;
            }
            if (!item.getIdOfGuardin().equals(id)) return false;
        }
        return true;
    }

    private boolean getAllCGDeleted(List<CGItem> list) {
        for (CGItem item : list) {
            if (!item.getDeletedState()) {
                return false;
            }
        }
        return true;
    }

    private Map<Long, Boolean> getCGDisabledMap(List<CGItem> list) {
        Map<Long, Boolean> result = new HashMap<>();
        for (CGItem item : list) {
            Boolean v = result.get(item.getIdOfClient());
            if (v != null && !v) {
                continue;
            }
            result.put(item.getIdOfClient(), item.getDisabled());
        }
        return result;
    }

    /*private boolean getAllCGDisabled(List<CGItem> list) {
        for (CGItem item : list) {
            if (!item.getDisabled()) {
                return false;
            }
        }
        return true;
    }*/

    private void deleteGuardian(Session session, CGItem aliveGuardian, CGItem deletedGuardian, Long version,
                                Map<Long, Boolean> mapDisabled) throws Exception {
        ClientGuardian clientGuardian = getClientGuardianByCGItem(session, deletedGuardian); //связки у удаляемого представителя

        if (clientGuardian != null) {
            if (!clientGuardian.getDeletedState()) {
                ClientGuardian cg = getGuardianChildlink(session, aliveGuardian, clientGuardian);
                if (cg == null) {
                    ClientGuardianHistory clientGuardianHistory2 = new ClientGuardianHistory();
                    clientGuardianHistory2.setUser(MainPage.getSessionInstance().getCurrentUser());
                    clientGuardianHistory2.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
                    clientGuardianHistory2.setReason(HISTORY_LABEL);
                    ClientManager.addGuardianByClient(session, deletedGuardian.getIdOfClient(), aliveGuardian.getIdOfGuardin(),
                            version, deletedGuardian.getDisabled(), clientGuardian.getRelation(), ClientManager.getNotificationSettings(clientGuardian),
                            clientGuardian.getCreatedFrom(), clientGuardian.getRepresentType(), clientGuardianHistory2);
                    logger.info(String.format("Added guardian id=%d to client id=%d", aliveGuardian.getIdOfGuardin(), deletedGuardian.getIdOfClient()));
                } else {
                    addNotificationSettingsAndOptions(session, cg, clientGuardian, mapDisabled);
                    if (cg.getDeletedState()) {
                        cg.setDeletedState(false);
                        if (!mapDisabled.get(cg.getIdOfChildren())) {
                            cg.setDisabled(false);
                        }
                        logger.info(String.format("Set deleted state false guardian id=%d to client id=%d", cg.getIdOfGuardian(), cg.getIdOfChildren()));
                        session.update(cg);
                    }
                }
            }
        }

        ClientGuardianHistory clientGuardianHistory = new ClientGuardianHistory();
        clientGuardianHistory.setUser(MainPage.getSessionInstance().getCurrentUser());
        clientGuardianHistory.setWebAdress(MainPage.getSessionInstance().getSourceWebAddress());
        clientGuardianHistory.setReason(HISTORY_LABEL);

        Client client = DAOUtils.findClient(session, deletedGuardian.getIdOfGuardin());
        if (!client.isDeletedOrLeaving()) {
            ClientManager.createClientGroupMigrationHistory(session, client, client.getOrg(), ClientGroup.Predefined.CLIENT_LEAVING.getValue(),
                    ClientGroup.Predefined.CLIENT_LEAVING.getNameOfGroup(), HISTORY_LABEL + " (пользователь " + FacesContext.getCurrentInstance()
                            .getExternalContext().getRemoteUser() + ")", clientGuardianHistory);
            client.setIdOfClientGroup(ClientGroup.Predefined.CLIENT_LEAVING.getValue());
            session.update(client);
            logger.info(String.format("Deleted client id = %s", client.getIdOfClient()));
        }
        session.flush();
    }

    private void addNotificationSettingsAndOptions(Session session, ClientGuardian aliveCG,
                                                   ClientGuardian deletedCG, Map<Long, Boolean> mapDisabled) {
        boolean changed = false;
        for (ClientGuardianNotificationSetting settingDeleted : deletedCG.getNotificationSettings()) {
            boolean found = false;
            for (ClientGuardianNotificationSetting settingAlive : aliveCG.getNotificationSettings()) {
                if (settingDeleted.getNotifyType().equals(settingAlive.getNotifyType())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                changed = true;
                aliveCG.getNotificationSettings().add(new ClientGuardianNotificationSetting(aliveCG, settingDeleted.getNotifyType()));
                logger.info(String.format("Added notification flag = %s to guardian id = %s",
                        settingDeleted.getNotifyType(), aliveCG.getIdOfGuardian()));
            }
        }
        if (aliveCG.isDisabled() && !mapDisabled.get(aliveCG.getIdOfChildren())) {
            aliveCG.setDisabled(false);
            changed = true;
            logger.info(String.format("Client id = %s, Guardian id=%s set disabled false", aliveCG.getIdOfChildren(), aliveCG.getIdOfGuardian()));
        }
        boolean specialMenu = ClientManager.getInformedSpecialMenu(session, deletedCG.getIdOfChildren(), deletedCG.getIdOfGuardian());
        boolean allowedPreorder = ClientManager.getAllowedPreorderByClient(session, deletedCG.getIdOfChildren(), deletedCG.getIdOfGuardian());
        if (specialMenu || allowedPreorder) {
            PreorderFlag preorderFlagAliveCG = getPreorderFlag(session, aliveCG.getIdOfChildren(), aliveCG.getIdOfGuardian());
            if (preorderFlagAliveCG == null) {
                Client client = DAOUtils.findClient(session, aliveCG.getIdOfChildren());
                preorderFlagAliveCG = new PreorderFlag(client);
            }
            Client guardian = DAOUtils.findClient(session, aliveCG.getIdOfGuardian());
            if (specialMenu) {
                preorderFlagAliveCG.setInformedSpecialMenu(true);
                preorderFlagAliveCG.setGuardianInformedSpecialMenu(guardian);
                logger.info("Client id = %s, Guardian id=%s set ON special menu", aliveCG.getIdOfChildren(), aliveCG.getIdOfGuardian());
            }
            if (allowedPreorder) {
                preorderFlagAliveCG.setAllowedPreorder(true);
                preorderFlagAliveCG.setGuardianAllowedPreorder(guardian);
                logger.info("Client id = %s, Guardian id=%s set ON allowed preorder", aliveCG.getIdOfChildren(), aliveCG.getIdOfGuardian());
            }
            session.saveOrUpdate(preorderFlagAliveCG);
        }
        if (changed) {
            //aliveCG.setNotificationSettings(settings);
            session.save(aliveCG);
        }
        session.flush();
    }

    private PreorderFlag getPreorderFlag(Session session, Long idOfClient, Long idOfGuardian) {
        Criteria criteria = session.createCriteria(PreorderFlag.class);
        criteria.add(Restrictions.eq("client.idOfClient", idOfClient));
        criteria.add(Restrictions.eq("guardianInformedSpecialMenu.idOfClient", idOfGuardian));
        List<PreorderFlag> list = criteria.list();
        if (list.size() == 0) return null;
        return list.get(0);
    }

    private ClientGuardian getClientGuardianByCGItem(Session session, CGItem item) {
        Query query = session.createQuery("select cg from ClientGuardian cg where cg.idOfClientGuardian = :id");
        query.setParameter("id", item.getIdOfClientGuardian());
        List<ClientGuardian> list = query.getResultList();
        if (list.size() == 0) return null;
        else return list.get(0);
    }

    private ClientGuardian getGuardianChildlink(Session session, CGItem aliveGuardian, ClientGuardian deletedGuardian) {
        Query query = session.createQuery("select cg from ClientGuardian cg " +
                "where cg.idOfGuardian = :idOfGuardian and cg.idOfChildren = :idOfChildren");
        query.setParameter("idOfGuardian", aliveGuardian.getIdOfGuardin());
        query.setParameter("idOfChildren", deletedGuardian.getIdOfChildren());
        List<ClientGuardian> list = query.getResultList();
        if (list.size() == 0) return null;
        else return list.get(0);
    }

    private List<CGItem> getNotProcessedDoubles(CGItem item, List<CGItem> guardians) {
        List<CGItem> result = new LinkedList<>();
        result.add(item);
        for (CGItem guardian : guardians) {
            if (!processedCG.contains(guardian.getIdOfClientGuardian())
                    && !item.getIdOfClientGuardian().equals(guardian.getIdOfClientGuardian())
                    && item.getFioPlusMobile().equals(guardian.getFioPlusMobile())) {
                result.add(guardian);
            }
        }
        return result;
    }


}