/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
public class PupilCatalogFindPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {
    @Resource
    MskNSIService nsiService;
    @PersistenceContext
    EntityManager em;

    Org org;
    String orgName;
    String familyName;

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgName() {
        return orgName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public Object removeFoundClientsByGUID() {
        for (Iterator i = pupilInfos.iterator();i.hasNext();) {
            Item item = (Item)i.next();
            if (item.idOfClient!=null) i.remove();
        }
        return null;
    }

    public Object removeFoundClientsByFullName() {
        for (Iterator i = pupilInfos.iterator();i.hasNext();) {
            Item item = (Item)i.next();
            if (item.idOfClientForBind!=null) i.remove();
        }
        return null;
    }

    public Integer getClientTotalCount() {
        return pupilInfos==null?0:pupilInfos.size();
    }

    public void markAllForRegistration() {
        if (pupilInfos==null) return;
        for (Item i : pupilInfos) {
            i.toAdd=true;
        }
    }
    public void unmarkAllForRegistration() {
        if (pupilInfos==null) return;
        for (Item i : pupilInfos) {
            i.toAdd=false;
        }
    }

    public static class Item extends ImportRegisterClientsService.PupilInfo {
        boolean toAdd, toBind;
        Long idOfClient, idOfClientForBind;
        String findByFIOResult;

        public Item(ImportRegisterClientsService.PupilInfo pi) {
            this.copyFrom(pi);
        }

        public boolean isToAdd() {
            return toAdd;
        }

        public void setToAdd(boolean toAdd) {
            this.toAdd = toAdd;
        }

        public Long getIdOfClient() {
            return idOfClient;
        }

        public void setIdOfClient(Long idOfClient) {
            this.idOfClient = idOfClient;
        }
        
        public boolean getCanBeAdded() {
            return idOfClient==null;
        }

        public Long getIdOfClientForBind() {
            return idOfClientForBind;
        }

        public void setIdOfClientForBind(Long idOfClientForBind) {
            this.idOfClientForBind = idOfClientForBind;
        }

        public boolean isToBind() {
            return toBind;
        }

        public void setToBind(boolean toBind) {
            this.toBind = toBind;
        }

        public String getFindByFIOResult() {
            return findByFIOResult;
        }
    }

    List<Item> pupilInfos = new LinkedList<Item>();

    @Transactional
    public void updateList() {
        try {
            if (org==null) {
                printError("Необходимо выбрать организацию");
                return;
            }
            if (org.getGuid()==null) {
                printError("У выбранной организации не указан GUID");
                return;
            }
            pupilInfos = new LinkedList<Item>();
            int nItemsNotFound=0;
            List<ImportRegisterClientsService.PupilInfo> pis = nsiService.getPupilsByOrgGUID(org.getGuid(), familyName, null);
            for (ImportRegisterClientsService.PupilInfo pi : pis) {
                Item i = new Item(pi);
                i.idOfClient = DAOUtils.getClientIdByGuid(em, i.guid);
                if (i.idOfClient==null) nItemsNotFound++;
                i.toAdd = i.idOfClient == null;
                pupilInfos.add(i);
            }
            printMessage("Получено записей: "+pupilInfos.size()+", не найдено по GUID: "+nItemsNotFound);
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
        }
    }
    
    @Transactional
    public Object checkFullNameDuplicates() {
        try {
            for (Item i : pupilInfos) {
                i.toBind = false;
                if (i.idOfClient!=null) continue;
                Long res = DAOUtils.findClientByFullName(em, org, emptyIfNull(i.getFamilyName()), emptyIfNull(i.getFirstName()), emptyIfNull(i.getSecondName()));
                if (res==null) continue;
                if (res==-1L) {
                    i.findByFIOResult=">1 записи";
                }
                else {
                    i.findByFIOResult = res+"";
                    i.idOfClientForBind = res;
                    i.toBind = i.idOfClientForBind!=null;
                }
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка", e);
        }

        return null;
    }

    public Object registerClients() {
        int nItems=0;
        try {
            for (Item i : pupilInfos) {
                if (!i.toAdd) continue;
                ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
                fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, i.getGuid());
                fieldConfig.setValue(ClientManager.FieldId.SURNAME, emptyIfNull(i.getFamilyName()));
                fieldConfig.setValue(ClientManager.FieldId.NAME, emptyIfNull(i.getFirstName()));
                fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, emptyIfNull(i.getSecondName()));
                fieldConfig.setValue(ClientManager.FieldId.COMMENTS, MskNSIService.COMMENT_MANUAL_IMPORT);
                if (i.getGroup()!=null) fieldConfig.setValue(ClientManager.FieldId.GROUP, i.getGroup());
                i.idOfClient = ClientManager.registerClient(org.getIdOfOrg(), fieldConfig, true);
                ++nItems;
            }
            printMessage("Успешно зарегистрировано клиентов: "+nItems);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при регистрации", e);
        }

        return null;
    }

    @Transactional
    public Object bindClients() {
        int nItems=0;
        try {
            for (Item i : pupilInfos) {
                if (!i.isToBind() || i.idOfClientForBind==null) continue;
                Client client = em.find(Client.class, i.idOfClientForBind);
                client.setClientGUID(i.getGuid());
                em.persist(client);
                i.toBind =  false;
                i.toAdd = false;
                i.idOfClient = i.idOfClientForBind;
                nItems++;
            }
            printMessage("Успешно связано клиентов: "+nItems);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при связывании", e);
        }

        return null;
    }

    private String emptyIfNull(String str) {
        return str==null?"":str;
    }

    public String getPageFilename() {
        return "service/msk/pupil_catalog_find_page";
    }

    public List<Item> getPupilInfos() {
        return pupilInfos;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg==null) this.orgName=null;
        else {
            this.org = (Org) session.load(Org.class, idOfOrg);
            this.orgName = org.getShortName();
        }
    }

}

