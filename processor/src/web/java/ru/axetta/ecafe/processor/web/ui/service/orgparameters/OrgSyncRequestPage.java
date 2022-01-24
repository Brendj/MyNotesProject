/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.orgparameters;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.*;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class OrgSyncRequestPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList, ContragentSelectPage.CompleteHandler {
    private final Logger logger = LoggerFactory.getLogger(OrgSyncRequestPage.class);

    private LinkedList<Long> idOfOrgList;
    private List<SelectItem> listOfSyncType;
    private String filter;
    private Integer selectedSyncType = SyncType.FULL_SYNC.ordinal();
    private Contragent defaultSupplier;
    private Boolean selectReceiver;

    @Override
    public void onShow() throws Exception {
        Session session = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            listOfSyncType = buildListOfSyncType();
        } catch (Exception e){
            logger.error("Exception when prepared the OrgSyncRequestPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    private List<SelectItem> buildListOfSyncType() {
        List<SelectItem> selectItemList = new LinkedList<>();
        try{
            for(SyncType type: SyncType.values()){
                selectItemList.add(new SelectItem(type.ordinal(), type.description));
            }
        } catch (Exception e){
            logger.error("Cant build SyncType items", e);
        }
        return selectItemList;
    }

    public void applySyncOperation(){
        Session session = null;
        Transaction transaction = null;
        List<Long> ids = new LinkedList<>();
        try{
            if(defaultSupplier == null && CollectionUtils.isEmpty(idOfOrgList)){
                throw new Exception("Не выбран не один из необходимых параметров");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Criteria criteria = session.createCriteria(Org.class);
            if(CollectionUtils.isNotEmpty(idOfOrgList)) {
                criteria.add(Restrictions.in("idOfOrg", idOfOrgList));
            }
            if(defaultSupplier != null){
                criteria.add(Restrictions.eq("defaultSupplier", defaultSupplier));
            }

            List<Org> listOfOrg = criteria.list();

            SyncType currentType = SyncType.valueOf(selectedSyncType);

            for(Org o : listOfOrg){
                switch (currentType){
                    case FULL_SYNC:
                        o.setFullSyncParam(true);
                        break;
                    case MENU_SYNC:
                        o.setMenusSyncParam(true);
                        break;
                    case CLIENT_SYNC:
                        o.setClientsSyncParam(true);
                        break;
                    case ORG_SETTING_SYNC:
                        o.setOrgSettingsSyncParam(true);
                        break;
                    case CARD_SYNC:
                        o.setCardSyncParam(true);
                        break;
                    case PREORDERS:
                        o.setPreorderSyncParam(true);
                        break;
                    case PHOTOS:
                        o.setPhotoSyncParam(true);
                        break;
                    case ZERO_TRANSACTIONS:
                        o.setZeroTransactionsSyncParam(true);
                        break;
                    case DISCOUNT_PREORDERS:
                        o.setDiscountPreordersSyncParam(true);
                        break;
                    case FOOD_APPLICATIONS:
                        o.setFoodApplicationSyncParam(true);
                        break;
                    default:
                        throw new Exception("Unknown SyncType, ordinal: " + selectedSyncType);
                }
                session.save(o);
                ids.add(o.getIdOfOrg());
            }
            transaction.commit();
            transaction = null;

            session.close();

            printMessage("Запрос отправлен для ID OO: " + StringUtils.join(ids, ", "));
        } catch (Exception e){
            logger.error("Can't apply SyncParam ", e);
            printError("Ошибка при обработке: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    @Override
    public String getPageFilename() {
        return "service/org_sync_request";
    }

    @Override
    public Logger getLogger(){
        return this.logger;
    }

    public LinkedList<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public void setIdOfOrgList(LinkedList<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public Contragent getDefaultSupplier() {
        return defaultSupplier;
    }

    public void setDefaultSupplier(Contragent defaultSupplier) {
        this.defaultSupplier = defaultSupplier;
    }

    public Boolean getSelectReceiver() {
        return selectReceiver;
    }

    public void setSelectReceiver(Boolean selectReceiver) {
        this.selectReceiver = selectReceiver;
    }

    public List<SelectItem> getListOfSyncType() {
        return listOfSyncType;
    }

    public void setListOfSyncType(List<SelectItem> listOfSyncType) {
        this.listOfSyncType = listOfSyncType;
    }

    public Integer getSelectedSyncType() {
        return selectedSyncType;
    }

    public void setSelectedSyncType(Integer selectedSyncType) {
        this.selectedSyncType = selectedSyncType;
    }

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (idOfContragent != null) {
            this.defaultSupplier = (Contragent) session.get(Contragent.class, idOfContragent);
        } else {
            defaultSupplier = null;
        }
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new LinkedList<>();
            if (orgMap.isEmpty())  {
                filter = "Не выбрано";
            } else {
                filter = "";
                StringBuilder stringBuilder = new StringBuilder();
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                filter = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }
    }

    public Object showOrgListSelectPage() {
        if (defaultSupplier != null) {
            MainPage.getSessionInstance().setIdOfContragentList(
                    Collections.singletonList(defaultSupplier.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }


    public enum SyncType {
        FULL_SYNC("Полная"),
        CLIENT_SYNC("Данные по клиентам"),
        MENU_SYNC("Меню"),
        ORG_SETTING_SYNC("Настройки ОО"),
        CARD_SYNC("Данные по картам"),
        PREORDERS("Предзаказы"),
        PHOTOS("Фотографии"),
        ZERO_TRANSACTIONS("Нулевые транзакции"),
        DISCOUNT_PREORDERS("Заявления на ЛП"),
        FOOD_APPLICATIONS("Заявки на питание");

        SyncType(String description){
            this.description = description;
        }

        static private final Map<Integer, SyncType> map;

        static {
            map = new HashMap<>();
            for(SyncType type : SyncType.values()){
                map.put(type.ordinal(), type);
            }
        }

        public static SyncType valueOf(Integer i){
            return map.get(i);
        }

        private final String description;
    }
}
