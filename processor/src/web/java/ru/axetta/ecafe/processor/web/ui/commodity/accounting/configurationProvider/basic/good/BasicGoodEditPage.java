/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.basic.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.UnitScale;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractEditPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItem;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderListItemsPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.07.13
 * Time: 18:10
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class BasicGoodEditPage extends AbstractEditPage<BasicGoodItem> implements ConfigurationProviderListItemsPanel.CompleteHandler {

    private List<SelectItem> unitsScaleSelectItemList;
    
    protected List<ConfigurationProviderItem> selectedProviders = new ArrayList<ConfigurationProviderItem>();

    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;

    @Autowired
    protected ConfigurationProviderListItemsPanel configurationProviderListItemsPanel;

    @Override
    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/basicGood/edit";
    }

    public BasicGoodEditPage getAp() {
        return (BasicGoodEditPage) MainPage.getSessionInstance().getCurrentWorkspacePage();
    }

    public List<SelectItem> getUnitsScaleSelectItemList() {
        if (unitsScaleSelectItemList == null) {
            unitsScaleSelectItemList = new ArrayList<SelectItem>();
            for (UnitScale unitScale: UnitScale.values()){
                this.unitsScaleSelectItemList.add(new SelectItem(unitScale,unitScale.toString()));
            }
        }
        return unitsScaleSelectItemList;
    }

    public void setUnitsScaleSelectItemList(List<SelectItem> unitsScaleSelectItemList) {
        this.unitsScaleSelectItemList = unitsScaleSelectItemList;
    }

    @Override
    protected boolean onCheckRequiredFields() {
        return true;
    }

   @Override
   public Object reload() {
       currentItem.refreshEntity(entityManager);
       selectedProviders.clear();
       javax.persistence.Query query = entityManager.createNativeQuery("select IdOfConfigurationProvider from cf_basicbasketgood_provider where idOfBasicGood = :basicgood");
       query.setParameter("basicgood", currentItem.getIdOfBasicGood());
       List list = query.getResultList();
       for (Object o : list) {
           ConfigurationProvider provider = entityManager.find(ConfigurationProvider.class, ((BigInteger)o).longValue());
           ConfigurationProviderItem item = new ConfigurationProviderItem(provider);
           selectedProviders.add(item);
       }
       return null;
   }

    @Override
    public Object save() {
        try {
            List<Long> list = new ArrayList<Long>();
            for (ConfigurationProviderItem item : selectedProviders) {
                list.add(item.getIdOfConfigurationProvider());
            }
            Boolean result = daoService.updateBasicGood(currentItem.getIdOfBasicGood(), currentItem.getNameOfGood(), currentItem.getUnitsScale(), currentItem.getNetWeight(), list);
            if(result){
                getSelectedEntityGroupPage().setCurrentEntityItem(currentItem);
                printMessage(currentItem + " успешно изменен.");
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при изменении: " + currentItem, e);
        }
        return null;
    }

    public String getSelectedProvidersString() {
        String result = "";
        for (ConfigurationProviderItem item : selectedProviders) {
            result += item.getName() + ", ";
        }
        if (result.length() > 0) result = result.substring(0, result.length()-2);
        return result;
    }

    public Object selectConfigurationProviderList() throws Exception{
        if (currentItem == null) currentItem = new BasicGoodItem();
        configurationProviderListItemsPanel.reload(selectedProviders);
        configurationProviderListItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void completeConfigurationProviderListSelection(List<ConfigurationProviderItem> items) {
        selectedProviders = items;
    }

}
