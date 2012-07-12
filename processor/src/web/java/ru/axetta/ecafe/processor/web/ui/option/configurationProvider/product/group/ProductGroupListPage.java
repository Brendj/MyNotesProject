/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class ProductGroupListPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ProductGroupListPage.class);
    private List<ProductGroup> productGroupList;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() {
        try {
            RuntimeContext.getAppContext().getBean(getClass()).reload();
        } catch (Exception e) {
            printError("Ошибка при загрузке списка групп.");
        }
    }

    @Transactional
    private void reload() throws Exception{
        productGroupList = entityManager.createQuery("FROM ProductGroup ORDER BY globalId",ProductGroup.class).getResultList();
    }

    public String getPageFilename() {
        return "option/product/group/list";
    }

    public List<ProductGroup> getProductGroupList() {
        return productGroupList;
    }

    public void setProductGroupList(List<ProductGroup> productGroupList) {
        this.productGroupList = productGroupList;
    }
    @Transactional
    private boolean getNullOrEmpty(){
        List<Long> pGl = entityManager.createQuery("SELECT id FROM ProductGroup",Long.class).getResultList();
        return pGl==null || pGl.isEmpty();
    }

    public boolean getNullOrEmptyGroup(){
        return getNullOrEmpty();
    }
}
