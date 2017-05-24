/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org.accessories;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Accessory;
import ru.axetta.ecafe.processor.core.persistence.OrgInventory;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.report.online.OnlineReportPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
    protected OrgInventoryItem orgInventoryItem;
    private OrgInventory orgInventory;


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
        }

        //Сохранение количественных показателей
        if (orgInventoryItem.isEmpty() && orgInventory == null) return;
        OrgInventory inventory;
        if (orgInventory == null) {
            inventory = new OrgInventory();
            inventory.setIdOfOrg(MainPage.getSessionInstance().getSelectedIdOfOrg());
            fillOrgInventory(inventory,
                    orgInventoryItem.getAmount_armadmin(), orgInventoryItem.getAmount_armcontroller(), orgInventoryItem.getAmount_armoperator(),
                    orgInventoryItem.getAmount_armlibrary(), orgInventoryItem.getAmount_turnstiles(), orgInventoryItem.getAmount_elocks(),
                    orgInventoryItem.getAmount_ereaders(), orgInventoryItem.getAmount_infopanels(), orgInventoryItem.getAmount_infokiosks());
            session.save(inventory);
        } else {
            fillOrgInventory(orgInventory,
                    orgInventoryItem.getAmount_armadmin(), orgInventoryItem.getAmount_armcontroller(), orgInventoryItem.getAmount_armoperator(),
                    orgInventoryItem.getAmount_armlibrary(), orgInventoryItem.getAmount_turnstiles(), orgInventoryItem.getAmount_elocks(),
                    orgInventoryItem.getAmount_ereaders(), orgInventoryItem.getAmount_infopanels(), orgInventoryItem.getAmount_infokiosks());
            session.update(orgInventory);
        }
    }

    private void fillOrgInventory(OrgInventory inventory, String amount_armadmin, String amount_armcontroller, String amount_armoperator,
            String amount_armlibrary, String amount_turnstiles, String amount_elocks,
            String amount_ereaders, String amount_infopanels, String amount_infokiosks) {
        inventory.setAmount_armadmin(StringUtils.isEmpty(amount_armadmin) ? null : new Integer(amount_armadmin));
        inventory.setAmount_armcontroller(StringUtils.isEmpty(amount_armcontroller) ? null : new Integer(amount_armcontroller));
        inventory.setAmount_armoperator(StringUtils.isEmpty(amount_armoperator) ? null : new Integer(amount_armoperator));
        inventory.setAmount_armlibrary(StringUtils.isEmpty(amount_armlibrary) ? null : new Integer(amount_armlibrary));
        inventory.setAmount_turnstiles(StringUtils.isEmpty(amount_turnstiles) ? null : new Integer(amount_turnstiles));
        inventory.setAmount_elocks(StringUtils.isEmpty(amount_elocks) ? null : new Integer(amount_elocks));
        inventory.setAmount_ereaders(StringUtils.isEmpty(amount_ereaders) ? null : new Integer(amount_ereaders));
        inventory.setAmount_infopanels(StringUtils.isEmpty(amount_infopanels) ? null : new Integer(amount_infopanels));
        inventory.setAmount_infokiosks(StringUtils.isEmpty(amount_infokiosks) ? null : new Integer(amount_infokiosks));
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
            orgInventoryItem = loadOrgInventoryItem(session);
        } catch (Exception e) {
            logger.error("Failed to load clients data", e);
        } finally {
            //HibernateUtils.close(session, logger);
        }
    }

    protected OrgInventoryItem loadOrgInventoryItem(Session session) {
        Long selectedIdOfOrg = MainPage.getSessionInstance().getSelectedIdOfOrg();
        orgInventory = DAOUtils.getOrgInventory(session, selectedIdOfOrg);
        return new OrgInventoryItem(orgInventory);
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

    public OrgInventoryItem getOrgInventoryItem() {
        return orgInventoryItem;
    }

    public void setOrgInventoryItem(OrgInventoryItem orgInventoryItem) {
        this.orgInventoryItem = orgInventoryItem;
    }

    public OrgInventory getOrgInventory() {
        return orgInventory;
    }

    public void setOrgInventory(OrgInventory orgInventory) {
        this.orgInventory = orgInventory;
    }
}