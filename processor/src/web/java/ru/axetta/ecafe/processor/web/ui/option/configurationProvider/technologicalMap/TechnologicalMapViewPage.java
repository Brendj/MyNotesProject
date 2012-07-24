/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group.TechnologicalMapGroupSelect;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItem;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

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

    @PersistenceContext
    EntityManager em;
    @Autowired
    private DAOService daoService;
    @Autowired
    private ProductItemsPanel productItemsPanel;
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
        if(currentTechnologicalMap.getIdOfConfigurationProvider() !=null){
            currentConfigurationProvider = em.find(ConfigurationProvider.class, currentTechnologicalMap.getIdOfConfigurationProvider());
        }
        if(currentTechnologicalMap.getTechnologicalMapGroup()!=null){
            currentTechnologicalMapGroup = currentTechnologicalMap.getTechnologicalMapGroup();
        }
        reload();
    }

    public void reload() throws Exception {
        currentTechnologicalMap = em.merge(currentTechnologicalMap);
        TypedQuery<TechnologicalMapProduct> query = em.createQuery("from TechnologicalMapProduct where technologicalMap=:technologicalMap",TechnologicalMapProduct.class);
        query.setParameter("technologicalMap", currentTechnologicalMap);
        technologicalMapProducts = query.getResultList();
         for (TechnologicalMapProduct technologicalMapProduct: technologicalMapProducts){
            pr.add(technologicalMapProduct.getProduct());
         }
    }

    public String getPageFilename() {
        return "option/configuration_provider/technologicalMap/view";
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
