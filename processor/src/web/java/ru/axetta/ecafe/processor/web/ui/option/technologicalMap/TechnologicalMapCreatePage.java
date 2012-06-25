/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Products;
import ru.axetta.ecafe.processor.core.persistence.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
public class TechnologicalMapCreatePage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);
    private TechnologicalMap technologicalMap = new TechnologicalMap();
    private List<Products> productsList;

    @Override
    public void onShow() throws Exception {
        technologicalMap.setProducts(new ArrayList<Products>());
    }

    public TechnologicalMapCreatePage() {
        technologicalMap.setProducts(new ArrayList<Products>());
    }

    public String getPageFilename() {
        return "option/technologicalMap/create";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    @Transactional
    public void createTechnologicalMap() {
        try{
            DAOService.getInstance().persistEntity(technologicalMap);
            printMessage("Новая технологическая карта создана успешно.");
        } catch (Exception e){
            printError("Ошибка при создании новой технологической карты.");
            logger.error("Error by create Technological Map.", e);
        }
    }

    public Object addProduct() {
        //TODO continue
        return null;
    }
}
