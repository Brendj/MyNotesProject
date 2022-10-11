/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.partner.etpmv.ETPMVDaoService;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

import static ru.axetta.ecafe.processor.core.service.nsi.DTSZNDiscountsReviseService.DATA_SOURCE_TYPE_MARKER_OU;

public class DiscountManager {

    private static final Logger logger = LoggerFactory.getLogger(DiscountManager.class);
    public static final String DELETE_COMMENT = "Удаление категории льгот";
    public static final String DELETE_INOE_COMMENT = "Удалено по причине окончания периода";
    public static final String RESERV_DISCOUNT = "Резерв";
    private static final String PROPERTY_DOU_DISCOUNT_DELETE = "ecafe.processor.clientMigrationHistory.dou.discountDelete";
    private static Map<Integer, Integer> discountPriorityMap = null;

    public static void saveDiscountHistory(Session session, Client client, Org org,
                                           Set<CategoryDiscount> oldDiscounts, Set<CategoryDiscount> newDiscounts,
                                           Integer oldDiscountMode, Integer newDiscountMode, String comment) {
        DiscountChangeHistory discountChangeHistory = new DiscountChangeHistory(client, org, newDiscountMode, oldDiscountMode,
                extractCategoryIdsFromDiscountSet(newDiscounts), extractCategoryIdsFromDiscountSet(oldDiscounts));
        discountChangeHistory.setComment(comment);

        saveNewClientDiscountHistoryRecord(session, client, oldDiscounts, newDiscounts, comment);
        session.save(discountChangeHistory);
    }

    private static void saveNewClientDiscountHistoryRecord(Session session, Client client,
                                                           Set<CategoryDiscount> oldDiscounts, Set<CategoryDiscount> newDiscounts, String comment) {
        ClientDiscountHistoryService service = RuntimeContext.getAppContext().getBean(ClientDiscountHistoryService.class);
        service.saveClientDiscountHistoryByOldScheme(session, client, oldDiscounts, newDiscounts, comment);
    }

    private static String extractCategoryIdsFromDiscountSet(Set<CategoryDiscount> categoryDiscounts) {
        StringBuilder sb = new StringBuilder();
        for (CategoryDiscount cd : categoryDiscounts) {
            if (sb.length() != 0) sb.append(",");
            sb.append(cd.getIdOfCategoryDiscount());
        }
        return sb.toString();
    }

    public static void deleteOtherDiscountForClientWithNoUpdateClient(Session session, Client client) throws Exception {
        Long otherDiscountCode = DAOUtils.getOtherDiscountCode(session);
        CategoryDiscount cdOther = getCategoryDiscountByCode(session, otherDiscountCode);
        if (client.getCategories() == null || !client.getCategories().contains(cdOther)) {
            return;
        }

        Integer oldDiscountMode = client.getDiscountMode();
        Integer newDiscountMode = client.getDiscountMode();
        Set<CategoryDiscount> oldCategories = new HashSet<>(client.getCategories());
        Set<CategoryDiscount> newCategories = client.getCategories();

        newCategories.remove(cdOther);
        if (client.getCategories().size() == 0) {
            newDiscountMode = Client.DISCOUNT_MODE_NONE;
        }
        saveDiscountHistory(session, client, client.getOrg(), oldCategories, newCategories,
                oldDiscountMode, newDiscountMode, DELETE_INOE_COMMENT);
        client.setCategories(newCategories);
        client.setLastDiscountsUpdate(new Date());
        //long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
        //client.setClientRegistryVersion(clientRegistryVersion);
        //session.update(client);
    }

    public static CategoryDiscount getCategoryDiscountByDtisznCode(Session session, Integer dtisznCode) {
        Criteria criteria = session.createCriteria(CategoryDiscountDSZN.class);
        criteria.add(Restrictions.eq("code", dtisznCode));
        CategoryDiscountDSZN categoryDiscountDSZN = (CategoryDiscountDSZN) criteria.uniqueResult();
        return categoryDiscountDSZN.getCategoryDiscount();
    }

    private static CategoryDiscount getCategoryDiscountByCode(Session session, Long idOfCategoryDiscount) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.add(Restrictions.eq("idOfCategoryDiscount", idOfCategoryDiscount));
        return (CategoryDiscount) criteria.uniqueResult();
    }

    public static void addOtherDiscountForClient(Session session, Client client) throws Exception {
        Long otherDiscountCode = DAOUtils.getOtherDiscountCode(session);

        Integer oldDiscountMode = client.getDiscountMode();
        boolean change = false;
        if (Client.DISCOUNT_MODE_NONE == oldDiscountMode) {
            client.setDiscountMode(Client.DISCOUNT_MODE_BY_CATEGORY);
            change = true;
        }
        Integer newDiscountMode = client.getDiscountMode();

        CategoryDiscount cdOther = getCategoryDiscountByCode(session, otherDiscountCode);
        Set<CategoryDiscount> newCategories = new HashSet<>(client.getCategories());
        if (!newCategories.contains(cdOther)) {
            newCategories.add(cdOther);
            change = true;
        }
        if (change) {
            saveDiscountHistory(session, client, client.getOrg(), client.getCategories(), newCategories,
                    oldDiscountMode, newDiscountMode, DiscountChangeHistory.MODIFY_BY_US);
            client.setCategories(newCategories);
            client.setLastDiscountsUpdate(new Date());
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            client.setClientRegistryVersion(clientRegistryVersion);
            session.update(client);
        }
    }

    public static void deleteDiscount(Client client, Session session) throws Exception {
        Integer oldDiscountMode = client.getDiscountMode();
        Set<CategoryDiscount> discountsAfterRemove = new HashSet<>();
        Set<CategoryDiscount> discounts = client.getCategories();
        for (CategoryDiscount discount : discounts) {
            if (discount.getEligibleToDelete()) {
                archiveDtisznDiscount(client, session, discount.getIdOfCategoryDiscount());
                logger.info(String.format("Delete discount id=%s, client id=%s", discount.getIdOfCategoryDiscount(), client.getIdOfClient()));
            } else {
                discountsAfterRemove.add(discount);
            }
        }
        client.getCategories().clear();
        if (!discountsAfterRemove.isEmpty()) {
            client.setCategories(discountsAfterRemove);
        }

        String newDiscounts = "";
        for (CategoryDiscount discount : client.getCategories()) {
            if (!newDiscounts.isEmpty()) {
                newDiscounts += ",";
            }
            newDiscounts += discount.getIdOfCategoryDiscount();
        }
        Integer newDiscountMode = discountsAfterRemove.size() == 0 ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;
        if (!oldDiscountMode.equals(newDiscountMode) || !discounts.equals(discountsAfterRemove)) {
            client.setDiscountMode(newDiscountMode);
            saveDiscountHistory(session, client, client.getOrg(), discounts, discountsAfterRemove, oldDiscountMode, newDiscountMode,
                    DiscountChangeHistory.MODIFY_BY_TRANSITION);
            client.setClientRegistryVersion(DAOUtils.updateClientRegistryVersionWithPessimisticLock());
            client.setLastDiscountsUpdate(new Date());
            session.update(client);
        }
    }

    public static void archiveDtisznDiscount(Client client, Session session, Long idOfCategoryDiscount) {
        List<Integer> list = DAOUtils.getDsznCodeListByCategoryDiscountCode(session, idOfCategoryDiscount);
        Long clientDTISZNDiscountVersion = null;
        Long applicationForFoodVersion = null;

        for (Integer dsznCode : list) {
            ClientDtisznDiscountInfo info = DAOUtils
                    .getDTISZNDiscountInfoByClientAndCode(session, client, dsznCode.longValue());
            if (null != info) {
                if (!info.getArchived()) {
                    DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(info);
                    builder.withArchived(true);
                    if (null == clientDTISZNDiscountVersion) {
                        clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
                    }
                    builder.save(session, clientDTISZNDiscountVersion);
                }
            }
            ApplicationForFood food = DAOUtils
                    .getApplicationForFoodByClientAndCode(session, client, dsznCode.longValue());
            if (null != food) {
                archiveApplicationForFood(session, food, applicationForFoodVersion);
            }
        }
    }

    public static Integer getDiscountPriority(Long dsznCode) {
        if (discountPriorityMap == null) {
            discountPriorityMap = new HashMap<>();
            List<CategoryDiscountDSZN> categoryDiscountDSZNList = DAOReadonlyService.getInstance().getCategoryDiscountDSZNList();
            for (CategoryDiscountDSZN categoryDiscountDSZN : categoryDiscountDSZNList) {
                discountPriorityMap.put(categoryDiscountDSZN.getCode(), categoryDiscountDSZN.getPriority());
            }
        }
        return discountPriorityMap.get(dsznCode.intValue()) == null ? 0 : discountPriorityMap.get(dsznCode.intValue());
    }

    public static void rebuildAppointedMSPByClient(Session session, Client client) {
        List<ClientDtisznDiscountInfo> list = DAOUtils.getDTISZNDiscountsInfoByClient(session, client);
        if (list.size() == 0) return;
        ClientDtisznDiscountInfo oldAppointedMSP = list.stream()
                .filter(i -> i.getAppointedMSP() != null && i.getAppointedMSP()).findFirst().orElse(null);
        Collections.sort(list);
        ClientDtisznDiscountInfo newAppointedMSP = list.get(list.size() - 1);
        if (oldAppointedMSP == null) {
            newAppointedMSP.setAppointedMSP(true);
            session.update(newAppointedMSP);
        } else if (!oldAppointedMSP.equals(newAppointedMSP)) {
            oldAppointedMSP.setAppointedMSP(false);
            newAppointedMSP.setAppointedMSP(true);
            session.update(oldAppointedMSP);
            session.update(newAppointedMSP);
        }
    }

    //Удаляем льготу ДСЗН без привязке к ЗЛП
    public static void removeDtisznDiscount(Session session, Client client, Integer dtisznCode, boolean rebuild) throws Exception {
        ClientDtisznDiscountInfo discountInfo = DAOUtils.getDTISZNDiscountInfoByClientAndCode(session, client, dtisznCode.longValue());
        if (discountInfo != null && !discountInfo.getArchived()) {
            Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
            DiscountManager.ClientDtisznDiscountInfoBuilder builder = new DiscountManager.ClientDtisznDiscountInfoBuilder(discountInfo);
            builder.withArchived(true);
            builder.save(session, clientDTISZNDiscountVersion);
            CategoryDiscount categoryDiscount = getCategoryDiscountByDtisznCode(session, dtisznCode);
            if (categoryDiscount != null) {
                for (CategoryDiscount cd : client.getCategories()) {
                    if (cd.equals(categoryDiscount)) {
                        deleteOneDiscount(session, client, cd);
                        break;
                    }
                }
            }
            if (rebuild) {
                rebuildAppointedMSPByClient(session, client);
            }
        }
    }

    public static void addDtisznDiscount(Session session, Client client, Integer dtisznCode, Date startDate, Date endDate, boolean rebuild) throws Exception {
        ClientDtisznDiscountInfo discountInfo = DAOUtils.getDTISZNDiscountInfoByClientAndCode(session, client, dtisznCode.longValue());
        Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
        if (null == discountInfo) {
            CategoryDiscountDSZN categoryDiscountDSZN = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                    .getCategoryDiscountDSZNByCode(dtisznCode);
            String title = categoryDiscountDSZN == null ? "" : categoryDiscountDSZN.getDescription();
            discountInfo = new ClientDtisznDiscountInfo(client, dtisznCode.longValue(), title,
                    ClientDTISZNDiscountStatus.CONFIRMED, startDate, endDate,
                    new Date(), DATA_SOURCE_TYPE_MARKER_OU, clientDTISZNDiscountVersion, true);
            discountInfo.setArchived(false);
            session.save(discountInfo);
        } else {
            discountInfo.setDateStart(startDate);
            discountInfo.setDateEnd(endDate);
            discountInfo.setArchived(false);
            discountInfo.setStatus(ClientDTISZNDiscountStatus.CONFIRMED);
            discountInfo.setLastUpdate(new Date());
            discountInfo.setVersion(clientDTISZNDiscountVersion);
            discountInfo.setActive(true);
            RuntimeContext.getAppContext().getBean(ClientDiscountHistoryService.class).saveChangeHistoryByDiscountInfo(session, discountInfo,
                    DiscountChangeHistory.MODIFY_IN_SERVICE);
            session.update(discountInfo);
        }
        addDiscount(session, client, getCategoryDiscountByDtisznCode(session, dtisznCode), DiscountChangeHistory.MODIFY_IN_SERVICE);
        if (rebuild) {
            rebuildAppointedMSPByClient(session, client);
        }
    }

    public static ClientDtisznDiscountInfo getAppointedClientDtisznDiscount(Client client) {
        if (CollectionUtils.isEmpty(client.getCategoriesDSZN())) return null;
        ClientDtisznDiscountInfo info = client.getCategoriesDSZN().stream()
                .filter(i -> i.getAppointedMSP() != null && i.getAppointedMSP()).findFirst().orElse(null);
        if (info != null) return info;
        List<ClientDtisznDiscountInfo> infos = new ArrayList(client.getCategoriesDSZN());
        Collections.sort(infos);
        return infos.get(infos.size() - 1);
    }

    public static void archiveApplicationForFood(Session session, ApplicationForFood applicationForFood, Long newVersion) {
        if (!applicationForFood.getArchived() && isEligibleToDelete(session, applicationForFood)) {
            applicationForFood.setArchived(true);
            if (null == newVersion) {
                newVersion = DAOUtils.nextVersionByApplicationForFood(session);
            }
            applicationForFood.setVersion(newVersion);
            applicationForFood.setLastUpdate(new Date());
            session.update(applicationForFood);
        }
    }

    public static boolean isEligibleToDelete(Session session, ApplicationForFood item) {
        CategoryDiscountDSZN categoryDiscountDSZN;
        boolean result = true;
        for (ApplicationForFoodDiscount discount : item.getDtisznCodes()) {
            if (discount.getDtisznCode() == null) {
                ///для льготы Иное
                categoryDiscountDSZN = DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, 0L);
            } else {
                categoryDiscountDSZN = DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, discount.getDtisznCode().longValue());
            }
            if (!categoryDiscountDSZN.getCategoryDiscount().getEligibleToDelete()) result = false;
        }

        return result;
    }

    public static boolean atLeastOneDiscountEligibleToDelete(Client client) {
        Set<CategoryDiscount> discounts = client.getCategories();
        for (CategoryDiscount discount : discounts) {
            if (discount.getEligibleToDelete()) {
                return true;
            }
        }
        return false;
    }

    public static void renewDiscounts(Session session, Client client,
                                      Set<CategoryDiscount> newDiscounts, Set<CategoryDiscount> oldDiscounts,
                                      String historyComment, boolean upVersion) throws Exception {
        Integer oldDiscountMode = client.getDiscountMode();
        Integer newDiscountMode =
                newDiscounts.size() == 0 ? Client.DISCOUNT_MODE_NONE : Client.DISCOUNT_MODE_BY_CATEGORY;
        client.setDiscountMode(newDiscountMode);

        saveDiscountHistory(session, client, client.getOrg(), oldDiscounts, newDiscounts,
                oldDiscountMode, newDiscountMode, historyComment);
        client.setLastDiscountsUpdate(new Date());
        client.setCategories(newDiscounts);
        if (upVersion) {
            long clientRegistryVersion = DAOUtils.updateClientRegistryVersionWithPessimisticLock();
            client.setClientRegistryVersion(clientRegistryVersion);
            session.update(client);
        }
    }

    public static void renewDiscounts(Session session, Client client,
                                      Set<CategoryDiscount> newDiscounts, Set<CategoryDiscount> oldDiscounts,
                                      String historyComment) throws Exception {
        renewDiscounts(session, client, newDiscounts, oldDiscounts, historyComment, true);
    }

    /*Для вызова в коде после изменения или присваивания ageTypeGroup и изменения/присваивания списка категорий льгот*/
    public static void deleteDOUDiscountsIfNeedAfterSetAgeTypeGroup(Session session, Client client) throws Exception {
        return; //удаление льгот ДОУ перенесено в ClientMigrationHistoryService.deleteDOUDiscounts
        /*if (!RuntimeContext.getInstance().getPropertiesValue(PROPERTY_DOU_DISCOUNT_DELETE, "false").equals("true")) return;
        boolean douDiscountsExist = false;
        for (CategoryDiscount cd : client.getCategories()) {
            if (cd.getOrgType().equals(CategoryDiscount.KINDERGARTEN_ID)) {
                douDiscountsExist = true;
                break;
            }
        }
        if (!douDiscountsExist) return;
        if (client.notDOUClient()) {
            //клиент ДОУ и орга СОШ
            deleteDOUDiscounts(session, client);
            return;
        }
        if (!StringUtils.isEmpty(client.getAgeTypeGroup())) {
            //Если не дошкол и заполнена возрастная категория, выходим
            return;
        }
        if (client.getOrg().getType().equals(OrganizationType.SCHOOL)) {
            //не заполнена возрастная категория и тип ОО = СОШ
            deleteDOUDiscounts(session, client);
        }*/
    }

    public static void deleteDOUDiscounts(Session session, Client client) throws Exception {
        Set<CategoryDiscount> newDiscounts = new HashSet<>();
        for (CategoryDiscount cd : client.getCategories()) {
            if (!cd.getOrgType().equals(CategoryDiscount.KINDERGARTEN_ID)) {
                newDiscounts.add(cd);
            } else {
                archiveDtisznDiscount(client, session, cd.getIdOfCategoryDiscount());
            }
        }
        if (newDiscounts.equals(client.getCategories())) return;
        renewDiscounts(session, client, newDiscounts, client.getCategories(),
                DiscountChangeHistory.MODIFY_BY_TRANSITION, false);
    }

    public static void addDiscount(Session session, Client client, CategoryDiscount categoryDiscount, String historyComment) throws Exception {
        Set<CategoryDiscount> newDiscounts = new HashSet<>();
        for (CategoryDiscount cd : client.getCategories()) {
            newDiscounts.add(cd);
        }
        newDiscounts.add(categoryDiscount);
        if (newDiscounts.equals(client.getCategories())) return;

        renewDiscounts(session, client, newDiscounts, client.getCategories(), historyComment);
    }

    //0 элемент - льготы ИСПП, 1 - ДСЗН
    public static String[] getClientDiscountsAsArray(Client client, List<CategoryDiscountDSZN> categoryDiscountDSZNList) {
        String[] result = {"", ""};
        ClientDtisznDiscountInfo info = getAppointedClientDtisznDiscount(client);
        if (info != null) {
            result[0] = getISPPDiscounts(client, info, categoryDiscountDSZNList);
            result[1] = info.getDtisznCode().toString();
        } else {
            result[0] = getClientDiscountsAsString(client);
        }
        return result;
    }

    private static String getISPPDiscounts(Client client, ClientDtisznDiscountInfo info,
                                           List<CategoryDiscountDSZN> categoryDiscountDSZNList) {
        String result = "";
        for (CategoryDiscount cd : client.getCategories()) {
            if (categoryDiscountDSZNList.stream().anyMatch(cdDszn -> cdDszn.getCategoryDiscount().equals(cd)
                    && info.getDtisznCode().equals(cdDszn.getCode().longValue()))) {
                result += cd.getIdOfCategoryDiscount() + ",";
            }
            if (!categoryDiscountDSZNList.stream().anyMatch(cdDszn -> cdDszn.getCategoryDiscount().equals(cd))) {
                result += cd.getIdOfCategoryDiscount() + ",";
            }
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String getClientDiscountsAsString(Client client) {
        String result = "";
        for (CategoryDiscount cd : client.getCategories()) {
            result += cd.getIdOfCategoryDiscount() + ",";
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String getClientDiscountsDSZNAsString(Client client) {
        String result = "";
        for (ClientDtisznDiscountInfo info : client.getCategoriesDSZN()) {
            if (!info.getArchived()) {
                result += info.getDtisznCode().toString() + ",";
            }
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static void deleteCategoryDiscount(Long idOfCategoryDiscount) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            Query query = session.createSQLQuery("SELECT t.idofcategorydiscountdszn FROM cf_categorydiscounts_dszn t " + "WHERE t.idofcategorydiscount = :idOfCategoryDiscount");
            query.setParameter("idOfCategoryDiscount", idOfCategoryDiscount);
            query.setMaxResults(1);
            List list = query.list();
            if (list.size() > 0) {
                throw new Exception("Выбранная категория имеет привязку к категории ДТиСЗН и не может быть удалена");
            }

            query = session.createSQLQuery("SELECT t.idofdrcd FROM cf_discountrules_categorydiscounts t " + "WHERE t.idofcategorydiscount = :idOfCategoryDiscount");
            query.setParameter("idOfCategoryDiscount", idOfCategoryDiscount);
            query.setMaxResults(1);
            list = query.list();
            if (list.size() > 0) {
                throw new Exception("Выбранная категория имеет привязку к правилам скидок и не может быть удалена");
            }

            query = session.createSQLQuery("SELECT t.idofrule FROM cf_wt_discountrules_categorydiscount t " + "WHERE t.idofcategorydiscount = :idOfCategoryDiscount");
            query.setParameter("idOfCategoryDiscount", idOfCategoryDiscount);
            query.setMaxResults(1);
            list = query.list();
            if (list.size() > 0) {
                throw new Exception(
                        "Выбранная категория имеет привязку к правилам скидок веб технолога и не может быть удалена");
            }

            CategoryDiscount categoryDiscount = (CategoryDiscount) session
                    .load(CategoryDiscount.class, idOfCategoryDiscount);

            for (Client client : categoryDiscount.getClients()) {
                deleteOneDiscount(session, client, categoryDiscount);
            }

            categoryDiscount.setDeletedState(true);
            categoryDiscount.setDeleteDate(new Date());
            session.update(categoryDiscount);

            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static void deleteOneDiscount(Session session, Client client, CategoryDiscount categoryDiscount) throws Exception {
        Set<CategoryDiscount> oldDiscounts = client.getCategories();
        Set<CategoryDiscount> newDiscounts = new HashSet<>();
        for (CategoryDiscount cd : oldDiscounts) {
            if (cd.getIdOfCategoryDiscount() != categoryDiscount.getIdOfCategoryDiscount())
                newDiscounts.add(cd);
        }
        renewDiscounts(session, client, newDiscounts, oldDiscounts, DELETE_COMMENT);
    }

    public static void disableAllDiscounts(Client client) {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();
            List<ClientDtisznDiscountInfo> list = DAOUtils.getDTISZNDiscountsInfoByClient(session, client);
            if (list.size() == 0)
                return;
            for (ClientDtisznDiscountInfo clientDtisznDiscountInfo : list) {
                clientDtisznDiscountInfo.setActive(false);
                clientDtisznDiscountInfo.setLastUpdate(new Date());
                clientDtisznDiscountInfo.setVersion(clientDtisznDiscountInfo.getVersion() + 1);
                session.update(clientDtisznDiscountInfo);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in disableAllDiscounts", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static List<CategoryDiscount> getCategoryDiscounts(Session session) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.add(Restrictions.ne("deletedState", true));
        return criteria.list();
    }

    public static class ClientDtisznDiscountInfoBuilder {
        private final ClientDtisznDiscountInfo info;

        public ClientDtisznDiscountInfoBuilder(ClientDtisznDiscountInfo info) {
            this.info = info;
        }

        public static ClientDtisznDiscountInfoBuilder createBuilder(ClientDtisznDiscountInfo info) {
            return new ClientDtisznDiscountInfoBuilder(info);
        }

        public ClientDtisznDiscountInfoBuilder withArchived(Boolean archived) {
            info.setArchived(archived);
            return this;
        }

        public ClientDtisznDiscountInfoBuilder withDateStart(Date dateStart) {
            info.setDateStart(dateStart);
            return this;
        }

        public ClientDtisznDiscountInfoBuilder withDateEnd(Date dateEnd) {
            info.setDateEnd(dateEnd);
            return this;
        }

        public ClientDtisznDiscountInfoBuilder withStatus(ClientDTISZNDiscountStatus status) {
            info.setStatus(status);
            return this;
        }

        public void save(Session session, Long version) {
            info.setLastUpdate(new Date());
            info.setVersion(version);
            session.update(info);
        }

        public void save(Session session) {
            Long clientDTISZNDiscountVersion = DAOUtils.nextVersionByClientDTISZNDiscountInfo(session);
            save(session, clientDTISZNDiscountVersion);
        }
    }
}
