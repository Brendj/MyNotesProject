/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 29.07.13
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class DefaultWorkspacePage extends BasicWorkspacePage {

    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    @Transactional
    public void fill() {
    }

    public void fill(Session session) throws Exception {
    }





    /**
     * ****************************************************************************************************************
     * GUI
     * ****************************************************************************************************************
     */
    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(DefaultWorkspacePage.class).fill();
    }

    public String getPageTitle() {
        return "Добро пожаловать!";
    }
}