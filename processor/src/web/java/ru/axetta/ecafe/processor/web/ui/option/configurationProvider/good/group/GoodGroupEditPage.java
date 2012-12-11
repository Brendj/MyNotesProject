/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
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
public class GoodGroupEditPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(GoodGroupEditPage.class);
    private GoodGroup currentGoodGroup;
    private Org org;
    @PersistenceContext
    private EntityManager entityManager;
    @Autowired
    private DAOService daoService;
    @Autowired
    private SelectedGoodGroupGroupPage selectedGoodGroupGroupPage;

    @Override
    public void onShow() throws Exception {
        selectedGoodGroupGroupPage.onShow();
        currentGoodGroup = selectedGoodGroupGroupPage.getCurrentGoodGroup();
        currentGoodGroup = entityManager.merge(currentGoodGroup);
        org = daoService.findOrById(currentGoodGroup.getOrgOwner());
    }

    public Object onSave(){
        try {
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(currentGoodGroup.getNameOfGoodsGroup() == null || currentGoodGroup.getNameOfGoodsGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            currentGoodGroup.setOrgOwner(org.getIdOfOrg());
            currentGoodGroup.setGlobalVersion(
                    daoService.updateVersionByDistributedObjects(GoodGroup.class.getSimpleName()));
            currentGoodGroup = (GoodGroup) daoService.mergeDistributedObject(currentGoodGroup,currentGoodGroup.getGlobalVersion()+1);
            selectedGoodGroupGroupPage.setCurrentGoodGroup(currentGoodGroup);
            printMessage("Группа товаров сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при сохранении группы для продуктов.");
            logger.error("Error saved Good Group",e);
        }
        return null;
    }

    public Object remove(){
        removeGroup();
        return null;
    }


    @Transactional
    protected void removeGroup(){
        if(!currentGoodGroup.getDeletedState()) {
            printError("Группа не может быть удалена.");
            return;
        }
        TypedQuery<Good> query = entityManager.createQuery("from Good where goodGroup=:goodGroup",Good.class);
        query.setParameter("goodGroup",currentGoodGroup);
        List<Good> goodList = query.getResultList();
        if(!(goodList==null || goodList.isEmpty())){
            printError("В группе имеются зарегистрированные товары.");
            return;
        }
        try{
            //GoodGroup pg = entityManager.getReference(GoodGroup.class, currentGoodGroup.getGlobalId());
            //entityManager.remove(pg);
            GoodGroup gg = entityManager.merge(currentGoodGroup);
            gg.setDeletedState(true);
            currentGoodGroup = entityManager.merge(gg);
            printMessage("Группа удалена успешно.");
        }  catch (Exception e){
            printError("Ошибка при удалении группа.");
            logger.error("Error by delete Good Group.", e);
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = daoService.findOrById(idOfOrg);
        }
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public String getPageFilename() {
        return "option/configuration_provider/good/group/edit";
    }

    public GoodGroup getCurrentGoodGroup() {
        return currentGoodGroup;
    }

    public void setCurrentGoodGroup(GoodGroup currentGoodGroup) {
        this.currentGoodGroup = currentGoodGroup;
    }
}
