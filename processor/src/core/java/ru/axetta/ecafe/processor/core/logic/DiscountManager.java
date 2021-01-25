/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DiscountManager {

    private static final Logger logger = LoggerFactory.getLogger(DiscountManager.class);
    public static final String DELETE_COMMENT = "Удаление категории льгот";
    public static final String DELETE_INOE_COMMENT = "Удалено по причине окончания периода";
    public static final String RESERV_DISCOUNT = "Резерв";
    private static final String PROPERTY_DOU_DISCOUNT_DELETE = "ecafe.processor.clientMigrationHistory.dou.discountDelete";

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

        if(!oldDiscounts.equals(newDiscounts)) {
            List<CategoryDiscount> disjunctions = (List<CategoryDiscount>) CollectionUtils
                    .disjunction(newDiscounts, oldDiscounts);
            for (CategoryDiscount uncommon : disjunctions) {
                ClientDiscountHistory history = new ClientDiscountHistory();
                history.setClient(client);
                history.setComment(comment);
                history.setRegistryDate(new Date());
                history.setCategoryDiscount(uncommon);

                ClientDiscountHistoryOperationTypeEnum type = ClientDiscountHistoryOperationTypeEnum
                        .getType(oldDiscounts, newDiscounts, uncommon);
                history.setOperationType(type);
                session.save(history);
            }
        } else {
            for(CategoryDiscount discount : newDiscounts){
                // todo add one-to-many field in Client
            }
        }
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

    private static CategoryDiscount getCategoryDiscountByCode(Session session, Long idOfCategoryDiscount) {
        Criteria criteria = session.createCriteria(CategoryDiscount.class);
        criteria.add(Restrictions.eq("idOfCategoryDiscount", idOfCategoryDiscount));
        return (CategoryDiscount)criteria.uniqueResult();
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
        if(!discountsAfterRemove.isEmpty()) {
            client.setCategories(discountsAfterRemove);
        }

        String newDiscounts = "";
        for(CategoryDiscount discount : client.getCategories()){
            if(!newDiscounts.isEmpty()){
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
        if (item.getDtisznCode() == null) {
            ///для льготы Иное
            categoryDiscountDSZN = DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, 0L);
        } else {
            categoryDiscountDSZN = DAOUtils.getCategoryDiscountDSZNByDSZNCode(session, item.getDtisznCode());
        }
        return categoryDiscountDSZN.getCategoryDiscount().getEligibleToDelete();
    }

    public static boolean atLeastOneDiscountEligibleToDelete (Client client) {
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

    public static String getClientDiscountsAsString(Client client) {
        String result = "";
        for (CategoryDiscount cd : client.getCategories()) {
            result += cd.getIdOfCategoryDiscount() + ",";
        }
        if (result.length() > 0) {
            result = result.substring(0, result.length()-1);
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
            result = result.substring(0, result.length()-1);
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
