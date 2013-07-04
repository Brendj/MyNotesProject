/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;

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
public class GoodGroupListPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(GoodGroupListPage.class);
    private List<GoodGroup> goodGroupList;
    private Boolean deletedStatusSelected = false;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

    @Override
    public void onShow() {}

    public Object onSearch() throws Exception{
        reload();
        return null;
    }

    public Object onClear() throws Exception{
        return null;
    }

    public void reload() throws Exception{
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(orgOwners==null || orgOwners.isEmpty()){
            goodGroupList = daoService.findGoodGroupBySuplifier();
        } else {
            goodGroupList = daoService.findGoodGroupBySuplifier(orgOwners);
        }
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/group/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", (goodGroupList==null?0:goodGroupList.size()));
    }

    public List<GoodGroup> getGoodGroupList() {
        return goodGroupList;
    }

    public void setGoodGroupList(List<GoodGroup> goodGroupList) {
        this.goodGroupList = goodGroupList;
    }

    public Boolean getEmptyGoodGroupList(){
        return  this.goodGroupList == null || this.goodGroupList.isEmpty();
    }

    public Boolean getDeletedStatusSelected() {
        return deletedStatusSelected;
    }

    public void setDeletedStatusSelected(Boolean deletedStatusSelected) {
        this.deletedStatusSelected = deletedStatusSelected;
    }
}
