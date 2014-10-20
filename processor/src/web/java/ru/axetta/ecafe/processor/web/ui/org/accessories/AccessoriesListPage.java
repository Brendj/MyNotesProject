/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.accessories;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Accessory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 13.10.14
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class AccessoriesListPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(AccessoriesListPage.class);

    protected List<SelectItem> accessoryTypes;
    protected List<Accessory> accessories;
    @PersistenceContext(unitName = "processorPU")
    private EntityManager entityManager;


    public List<Accessory> getAccessories() {
        return accessories;
    }

    public List<SelectItem> getAccessoryTypes() {
        if(accessoryTypes == null) {
            accessoryTypes = new ArrayList<SelectItem>();
        }
        if(accessoryTypes.size() < 1) {
            accessoryTypes.add(new SelectItem(0, ""));
            for(Object [] type : Accessory.TYPES) {
                accessoryTypes.add(new SelectItem((Integer) type[0], (String) type[1]));
            }
        }
        return accessoryTypes;
    }

    @Override
    public void onShow() {
        RuntimeContext.getAppContext().getBean(AccessoriesListPage.class).updateAccessories();
    }

    public void save() {
        RuntimeContext.getAppContext().getBean(AccessoriesListPage.class).refreshAccessories();
        RuntimeContext.getAppContext().getBean(AccessoriesListPage.class).updateAccessories();
    }

    @Transactional
    public void refreshAccessories() {
        Long selectedIdOfOrg = MainPage.getSessionInstance().getSelectedIdOfOrg();
        if(selectedIdOfOrg == null) {
            return;
        }
        if(accessories != null) {
            boolean found = false;
            for(Accessory a : accessories) {
                if(a.getIdOfTargetOrg() == null || a.getAccessoryType() == null || a.getAccessoryNumber() == null) {
                    continue;
                }
                found = true;
            }
            if(!found) {
                return;
            }
        }

        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            List<Accessory> currentAccessories = DAOUtils.getAccessories(session, selectedIdOfOrg);
            saveAccessories(session, currentAccessories);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    public void saveAccessories(Session session, List<Accessory> currentAccessories) {
        //  удаление оборудования
        if(currentAccessories != null && currentAccessories.size() > 0) {
            for(Accessory ca : currentAccessories) {
                boolean found = false;
                for(Accessory a : accessories) {
                    if(ca.getIdOfAccessory().equals(a.getIdOfAccessory()) &&
                       (a.getAccessoryType() == Accessory.BANK_ACCESSORY_TYPE || a.getAccessoryType() == Accessory.GATE_ACCESSORY_TYPE)) {
                        found = true;
                        break;
                    }
                }

                if(!found) {
                    session.delete(ca);
                    session.flush();
                    session.evict(ca);
                }
            }
        }

        //  сохранение оборудования
        for(Accessory a : accessories) {
            if(a.getIdOfTargetOrg() == null || a.getAccessoryType() == null || a.getAccessoryNumber() == null ||
               (a.getAccessoryType() != Accessory.BANK_ACCESSORY_TYPE && a.getAccessoryType() != Accessory.GATE_ACCESSORY_TYPE)) {
                continue;
            }
            a = (Accessory) session.merge(a);
            session.flush();
            session.evict(a);
            /*if(a.getIdOfAccessory() == null) {
                a = (Accessory) session.merge(a);
            } else {
                session.save(a);
            }*/
        }
        }

    public void update() {
        RuntimeContext.getAppContext().getBean(AccessoriesListPage.class).updateAccessories();
    }

    @Transactional
    public void updateAccessories() {
        Long selectedIdOfOrg = MainPage.getSessionInstance().getSelectedIdOfOrg();
        if(selectedIdOfOrg == null) {
            return;
        }

        Session session = null;
        try {
            session = (Session) entityManager.getDelegate();
            accessories = loadAccessories(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    protected List<Accessory> loadAccessories(Session session) throws Exception {
        Long selectedIdOfOrg = MainPage.getSessionInstance().getSelectedIdOfOrg();
        List<Accessory> res = DAOUtils.getAccessories(session, selectedIdOfOrg);
        if(res.size() < 10) {

        }
        int size = (res.size() / 10 + 1) * 10;
        for(int i=res.size(); i<size; i++) {
            Accessory a = new Accessory();
            a.setIdOfSourceOrg(selectedIdOfOrg);
            res.add(a);
        }
        return res;
    }

    public String getPageTitle() {
        return super.getPageTitle();
    }

    public String getPageFilename() {
        return "org/accessories/list";
    }
}