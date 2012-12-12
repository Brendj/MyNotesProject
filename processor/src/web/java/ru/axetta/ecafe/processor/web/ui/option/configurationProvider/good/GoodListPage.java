/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group.GoodGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class GoodListPage extends BasicWorkspacePage implements GoodGroupSelect{

    private static final Logger logger = LoggerFactory.getLogger(GoodListPage.class);
    private List<Good> goodList;
    private Boolean deletedStatusSelected = Boolean.FALSE;
    private GoodGroup selectedGoodGroup;

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private GoodGroupItemsPanel goodGroupItemsPanel;

    @Override
    public void onShow() { }

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception{
        selectedGoodGroup = null;
        goodList.clear();
        return null;
    }

    @Transactional
    public void reload() throws Exception{
        String where = "";
        if(selectedGoodGroup!=null){
            where = " goodGroup=:goodGroup ";
        }
        where = (where.equals("")?"":" where ") + where;
        TypedQuery<Good> query = entityManager.createQuery("from Good " + where, Good.class);
        if(selectedGoodGroup!=null){
            query.setParameter("goodGroup", selectedGoodGroup);
        }
        goodList = query.getResultList();
    }

    public Object selectGoodGroup() throws Exception{
        goodGroupItemsPanel.reload();
        goodGroupItemsPanel.setSelectGoodGroup(selectedGoodGroup);
        goodGroupItemsPanel.pushCompleteHandler(this);
        return null;
    }

    @Override
    public void select(GoodGroup goodGroup) {
        selectedGoodGroup = goodGroup;
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (goodList==null?0:goodList.size()));
    }

    public String getPageFilename() {
        return "option/configuration_provider/good/list";
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }

    public List<Good> getGoodList() {
        return goodList;
    }

    public void setGoodList(List<Good> goodList) {
        this.goodList = goodList;
    }

    public Boolean getEmptyGoodList(){
        return  this.goodList == null || this.goodList.isEmpty();
    }

    public GoodGroup getSelectedGoodGroup() {
        return selectedGoodGroup;
    }

    public void setSelectedGoodGroup(GoodGroup selectedGoodGroup) {
        this.selectedGoodGroup = selectedGoodGroup;
    }
}
