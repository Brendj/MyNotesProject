/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.technologicalMapProduct.ProductListItemsPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
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
public class TechnologicalMapViewPage extends BasicWorkspacePage{

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapViewPage.class);

    private TechnologicalMap currentTechnologicalMap;
    private TechnologicalMapProduct currentTechnologicalMapProduct;
    private List<Product> pr = new LinkedList<Product>();
    private List<TechnologicalMapProduct> technologicalMapProducts = new LinkedList<TechnologicalMapProduct>();

    private ConfigurationProvider currentConfigurationProvider;
    private TechnologicalMapGroup currentTechnologicalMapGroup;

    //@PersistenceContext(unitName = "processorPU")
    //private EntityManager em;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ProductListItemsPanel productItemsPanel;
    @Autowired
    private ConfigurationProviderItemsPanel configurationProviderItemsPanel;
    @Autowired
    private TechnologicalMapGroupItemsPanel technologicalMapGroupItemsPanel;
    @Autowired
    private SelectedTechnologicalMapGroupPage selectedTechnologicalMapGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedTechnologicalMapGroupPage.onShow();
        currentTechnologicalMap = selectedTechnologicalMapGroupPage.getCurrentTechnologicalMap();
        currentTechnologicalMap = daoService.saveEntity(currentTechnologicalMap);
        if(currentTechnologicalMap.getIdOfConfigurationProvider() !=null){
            currentConfigurationProvider = DAOReadonlyService.getInstance().getConfigurationProvider(currentTechnologicalMap.getIdOfConfigurationProvider());
        }
        currentTechnologicalMapGroup = daoService.findTechnologicalMapGroupByTechnologicalMap(currentTechnologicalMap);
        reload();
    }

    public void reload() throws Exception {
        List<TechnologicalMapProduct> technologicalMapProducts =
                DAOReadonlyService.getInstance().findTechnologicalMapProductByTechnologicalMap(currentTechnologicalMap.getGlobalId());
        for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
            pr.add(technologicalMapProduct.getProduct());
        }
    }

    public String getPageFilename() {
        return "commodity_accounting/configuration_provider/technologicalMap/view";
    }

    public ConfigurationProvider getCurrentConfigurationProvider() {
        return currentConfigurationProvider;
    }


    public TechnologicalMapGroup getCurrentTechnologicalMapGroup() {
        return currentTechnologicalMapGroup;
    }

    public TechnologicalMap getCurrentTechnologicalMap() {
        return currentTechnologicalMap;
    }

    public TechnologicalMapProduct getCurrentTechnologicalMapProduct() {
        return currentTechnologicalMapProduct;
    }

    public List<TechnologicalMapProduct> getTechnologicalMapProducts() {
        return technologicalMapProducts;
    }

}
