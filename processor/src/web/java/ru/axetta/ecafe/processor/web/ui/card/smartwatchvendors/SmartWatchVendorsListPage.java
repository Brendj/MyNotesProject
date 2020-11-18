/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.card.smartwatchvendors;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.SmartWatchVendor;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.smartwatch.SmartWatchVendorManager;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.util.CollectionUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Scope("session")
public class SmartWatchVendorsListPage extends BasicWorkspacePage {
    private static final Logger log = LoggerFactory.getLogger(SmartWatchVendorsListPage.class);
    private String nameFilter;
    private String nameForNewVendor;
    private List<SmartWatchVendor> smartWatchVendorList;
    private SmartWatchVendorManager manager;
    private SmartWatchVendor selectedItem = null;
    private final Pattern urlPattern = Pattern.compile("^(https|http):\\/\\/[a-zA-Z0-9@:%._\\+~#=\\/&?]+");

    @Autowired
    public void setService(SmartWatchVendorManager manager) {
        this.manager = manager;
    }


    @Override
    public void onShow() throws Exception {
        nameFilter = "";
        smartWatchVendorList = manager.getAllVendors();
    }

    public void dropAndReloadCatalogList() {
        nameFilter = "";
        selectedItem = null;
        smartWatchVendorList = manager.getAllVendors();
    }

    public void updateVendorList() {
        try {
            smartWatchVendorList = manager.findVendorByName(nameFilter);
        } catch (Exception e) {
            printError("Не удалось найти элементы: " + e.getMessage());
            log.error("Can't find vendor", e);
        }
    }

    public void deleteVendor() {
        if(selectedItem == null){
            return;
        }
        try {
            if(selectedItem.getIdOfVendor() != null) {
                manager.deleteVendor(selectedItem);
            }
            smartWatchVendorList.remove(selectedItem);
        } catch (Exception e){
            printError("Не удалось удалить поставщика: " + e.getMessage());
            log.error("Can't delete vendor", e);
        }
    }

    public void refreshItems() {
        if(CollectionUtils.isEmpty(smartWatchVendorList)){
            return;
        }
        Session session = null;
        try{
            session = RuntimeContext.getInstance().createReportPersistenceSession();

            for (SmartWatchVendor vendor : smartWatchVendorList){
                if(vendor.getIdOfVendor() == null){
                    smartWatchVendorList.remove(vendor);
                } else {
                    session.refresh(vendor);
                }
            }
            session.close();
        } catch (Exception e){
            printError("Не удалось восстановить элементы: " + e.getMessage());
            log.error("Can't restore elements", e);
        } finally {
            HibernateUtils.close(session, log);
        }
    }

    public void applyChanges() {
        if(CollectionUtils.isEmpty(smartWatchVendorList)){
            return;
        }

        Session session = null;
        Transaction transaction = null;
        try{
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            for (SmartWatchVendor vendor : smartWatchVendorList){
                if(isInvalidData(vendor)){
                    continue;
                }
                session.saveOrUpdate(vendor);
            }

            transaction.commit();
            transaction = null;
            session.close();
        } catch (Exception e){
            printError("Не удалось восстановить элементы: " + e.getMessage());
            log.error("Can't restore elements", e);
        } finally {
            HibernateUtils.rollback(transaction, log);
            HibernateUtils.close(session, log);
        }
    }

    private boolean isInvalidData(SmartWatchVendor vendor) {
        if(StringUtils.isNotEmpty(vendor.getApiKey()) && !manager.isUUID(vendor.getApiKey())){
            printError("У поставщика " + vendor.getName() + " указан API-Key в неверном формате. Обработка пропущена");
            return true;
        }
        Matcher matcher = null;
        if(StringUtils.isNotEmpty(vendor.getEnterEventsEndPoint())){
            matcher = urlPattern.matcher(vendor.getEnterEventsEndPoint());
            if(!matcher.matches()){
                printError("У поставщика " + vendor.getName() + " указан неверный адрес для отправки событий проходов. Обработка пропущена");
                return true;
            }
        }

        if(StringUtils.isNotEmpty(vendor.getPurchasesEndPoint())){
            matcher = urlPattern.matcher(vendor.getPurchasesEndPoint());
            if(!matcher.matches()){
                printError("У поставщика " + vendor.getName() + " указан неверный адрес для отправки событий покупок. Обработка пропущена");
                return true;
            }
        }

        if(StringUtils.isNotEmpty(vendor.getPaymentEndPoint())){
            matcher = urlPattern.matcher(vendor.getPaymentEndPoint());
            if(!matcher.matches()){
                printError("У поставщика " + vendor.getName() + " указан неверный адрес для отправки событий поплнения счёта. Обработка пропущена");
                return true;
            }
        }

        return false;
    }

    public void createNewVendor() {
        if (StringUtils.isBlank(nameForNewVendor)) {
            printError("Введите название поставщика");
            return;
        }
        try {
            SmartWatchVendor vendor = new SmartWatchVendor();
            vendor.setName(nameForNewVendor);
            smartWatchVendorList.add(vendor);

        } catch (Exception e) {
            log.error("Can't create new vendor: ", e);
            printError("Ошибка при попытке создать поставщика: " + e.getMessage());
        } finally {
            nameForNewVendor = "";
        }
    }

    @Override
    public String getPageFilename(){
        return "card/vendors/vendors_list";
    }

    public String getNameFilter() {
        return nameFilter;
    }

    public void setNameFilter(String nameFilter) {
        this.nameFilter = nameFilter;
    }

    public List<SmartWatchVendor> getSmartWatchVendorList() {
        return smartWatchVendorList;
    }

    public void setSmartWatchVendorList(List<SmartWatchVendor> smartWatchVendorList) {
        this.smartWatchVendorList = smartWatchVendorList;
    }

    public SmartWatchVendor getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(SmartWatchVendor selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getNameForNewVendor() {
        return nameForNewVendor;
    }

    public void setNameForNewVendor(String nameForNewVendor) {
        this.nameForNewVendor = nameForNewVendor;
    }
}
