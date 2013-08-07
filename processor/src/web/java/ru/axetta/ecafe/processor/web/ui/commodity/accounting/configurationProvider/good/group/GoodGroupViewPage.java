/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.GoodListPage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
public class GoodGroupViewPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(GoodGroupViewPage.class);
    private GoodGroup currentGoodGroup;
    private Integer countGoods;
    private Org currentOrg;
    @Autowired
    private SelectedGoodGroupGroupPage selectedGoodGroupGroupPage;
    @Autowired
    private GoodListPage goodListPage;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupGroupPage.onShow();
        currentGoodGroup = selectedGoodGroupGroupPage.getCurrentGoodGroup();
        List<Good> goods = daoService.findGoodsByGoodGroup(currentGoodGroup, false);
        countGoods = goods.size();
        currentOrg = daoService.getOrg(currentGoodGroup.getOrgOwner());
    }

    public Object showGoods() throws Exception{
        goodListPage.setSelectedGoodGroup(currentGoodGroup);
        /* Не Показать удаленные */
        goodListPage.setDeletedStatusSelected(false);
        goodListPage.reload();
        goodListPage.show();
        return null;
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/good/group/view";
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public void setCurrentGoodGroup(GoodGroup currentGoodGroup) {
        this.currentGoodGroup = currentGoodGroup;
    }

    public Integer getCountGoods() {
        return countGoods;
    }

    public Org getCurrentOrg() {
        return currentOrg;
    }

}
