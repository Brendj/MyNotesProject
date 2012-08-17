/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.banks;

import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 09.08.12
 * Time: 10:05
 * To change this template use File | Settings | File Templates.
 */
@Component
public class BankListPage extends BasicWorkspacePage{

    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;

    protected List<Bank> entityList;

    public void onShow() throws Exception {
        fill();
    }

    private void fill(){
        entityList = entityManager.createQuery("from Bank order by id",Bank.class).getResultList();
    }

    public List<Bank> getEntityList() {
        return entityList;
    }

    public void setEntityList(List<Bank> entityList) {
        this.entityList = entityList;
    }

    @Transactional
    public void save(){
        for (int i=0; i<entityList.size(); i++){
            Bank b = entityList.get(i);
            Bank eb = entityManager.find(Bank.class, b.getIdOfBank());
            eb.setEnrollmentType(b.getEnrollmentType());
            eb.setLogoUrl(b.getLogoUrl());
            eb.setMinRate(b.getMinRate());
            eb.setName(b.getName());
            eb.setRate(b.getRate());
            eb.setTerminalsUrl(b.getTerminalsUrl());
            b = daoService.saveEntity(eb);
            entityList.set(i, b);
        }
    }

    @Transactional
    public Object addBank() {
        try{
            Bank bank = new Bank();
            daoService.persistEntity(bank);
            entityList = entityManager.createQuery("from Bank",Bank.class).getResultList();
        } catch (Exception e){
            printError("");  // page print error
            logAndPrintMessage("",e); //print log message
        }
        return null;
    }
}
