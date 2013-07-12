/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group.GoodGroupSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
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
    @Autowired
    private DAOService daoService;
    @Autowired
    private GoodGroupItemsPanel goodGroupItemsPanel;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onShow() { }

    public Object onSearch(){
        try {
            reload();
        } catch (Exception e) {
            printError(String.format("Ошибка при загрузке данных: %s", e.getMessage()));
            logger.error("Good onSearch error: ", e);
        }
        return null;
    }

    public Object onClear() throws Exception{
        selectedGoodGroup = null;
        goodList.clear();
        return null;
    }

    public void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(selectedGoodGroup==null){
            if(user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                goodList = daoService.findGoods(deletedStatusSelected);
            } else {
                goodList = daoService.findGoods(orgOwners, deletedStatusSelected);
            }
        } else {
            if(user.getIdOfRole().equals(User.DefaultRole.SUPPLIER.getIdentification()) && (orgOwners==null || orgOwners.isEmpty())){
                goodList = daoService.findGoodsByGoodGroup(selectedGoodGroup,deletedStatusSelected);
            } else {
                goodList = daoService.findGoodsByGoodGroup(selectedGoodGroup,orgOwners, deletedStatusSelected);
            }
        }
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
        return "commodity_accounting/configuration_provider/good/list";
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
