/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.nsi.MskNSIService;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.rule.ReportRuleEditPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
@Scope("session")
public class PupilCatalogFindPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    Logger logger = LoggerFactory.getLogger(PupilCatalogFindPage.class);
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
        for (Iterator i = pupilInfos.iterator(); i.hasNext(); ) {
            Item item = (Item) i.next();
            if (item.idOfClient != null) {
                i.remove();
            }
        }
        return null;
    }

    public Object removeFoundClientsByFullName() {
        for (Iterator i = pupilInfos.iterator(); i.hasNext(); ) {
            Item item = (Item) i.next();
            if (item.idOfClientForBind != null) {
                i.remove();
            }
        }
        return null;
    }

    public Integer getClientTotalCount() {
        return pupilInfos == null ? 0 : pupilInfos.size();
    }

    public void markAllForRegistration() {
        if (pupilInfos == null) {
            return;
        }
        for (Item i : pupilInfos) {
            i.toAdd = true;
        }
    }

    public void unmarkAllForRegistration() {
        if (pupilInfos == null) {
            return;
        }
        for (Item i : pupilInfos) {
            i.toAdd = false;
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
            return idOfClient == null;
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
            if (org == null) {
                printError("Необходимо выбрать организацию");
                return;
            }
            if (org.getGuid() == null) {
                printError("У выбранной организации не указан GUID");
                return;
            }
            pupilInfos = new LinkedList<Item>();
            int nItemsNotFound = 0;
            List<ImportRegisterClientsService.PupilInfo> pis = nsiService
                    .getPupilsByOrgGUID(org.getGuid(), familyName, null);
            for (ImportRegisterClientsService.PupilInfo pi : pis) {
                Item i = new Item(pi);
                i.idOfClient = DAOUtils.getClientIdByGuid(em, i.guid);
                if (i.idOfClient == null) {
                    nItemsNotFound++;
                }
                i.toAdd = i.idOfClient == null;
                pupilInfos.add(i);
            }
            printMessage("Получено записей: " + pupilInfos.size() + ", не найдено по GUID: " + nItemsNotFound);
        } catch (Exception e) {
            super.logAndPrintMessage("Ошибка получения данных", e);
        }
    }

    @Transactional
    public Object checkFullNameDuplicates() {
        try {
            for (Item i : pupilInfos) {
                i.toBind = false;
                if (i.idOfClient != null) {
                    continue;
                }
                Long res = DAOUtils
                        .findClientByFullName(em, org, emptyIfNull(i.getFamilyName()), emptyIfNull(i.getFirstName()),
                                emptyIfNull(i.getSecondName()), true);
                if (res == null) {
                    continue;
                }
                if (res == -1L) {
                    i.findByFIOResult = ">1 записи";
                } else {
                    i.findByFIOResult = res + "";
                    i.idOfClientForBind = res;
                    i.toBind = i.idOfClientForBind != null;
                }
            }
        } catch (Exception e) {
            logAndPrintMessage("Ошибка", e);
        }

        return null;
    }

    public Object registerClients() {
        int nItems = 0;
        try {
            for (Item i : pupilInfos) {
                if (!i.toAdd) {
                    continue;
                }
                ClientManager.ClientFieldConfig fieldConfig = new ClientManager.ClientFieldConfig();
                fieldConfig.setValue(ClientManager.FieldId.CLIENT_GUID, i.getGuid());
                fieldConfig.setValue(ClientManager.FieldId.SURNAME, emptyIfNull(i.getFamilyName()));
                fieldConfig.setValue(ClientManager.FieldId.NAME, emptyIfNull(i.getFirstName()));
                fieldConfig.setValue(ClientManager.FieldId.SECONDNAME, emptyIfNull(i.getSecondName()));
                fieldConfig.setValue(ClientManager.FieldId.COMMENTS, MskNSIService.COMMENT_MANUAL_IMPORT);
                if (i.getGroup() != null) {
                    fieldConfig.setValue(ClientManager.FieldId.GROUP, i.getGroup());
                }
                i.idOfClient = ClientManager.registerClient(org.getIdOfOrg(), fieldConfig, true);
                ++nItems;
            }
            printMessage("Успешно зарегистрировано клиентов: " + nItems);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при регистрации", e);
        }

        return null;
    }

    @Transactional
    public Object bindClients() {
        int nItems = 0;
        try {
            for (Item i : pupilInfos) {
                if (!i.isToBind() || i.idOfClientForBind == null) {
                    continue;
                }
                Client client = em.find(Client.class, i.idOfClientForBind);
                client.setClientGUID(i.getGuid());
                em.persist(client);
                i.toBind = false;
                i.toAdd = false;
                i.idOfClient = i.idOfClientForBind;
                nItems++;
            }
            printMessage("Успешно связано клиентов: " + nItems);
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при связывании", e);
        }

        return null;
    }

    private String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    public String getPageFilename() {
        return "service/msk/pupil_catalog_find_page";
    }

    public List<Item> getPupilInfos() {
        return pupilInfos;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg == null) {
            this.orgName = null;
        } else {
            this.org = (Org) session.load(Org.class, idOfOrg);
            this.orgName = org.getShortName();
        }
    }


    public void buildComparisonCSVFile() {
        /*
                                                             ДЛЯ ТЕСТА!!!
        pupilInfos = new ArrayList<Item>();
        ImportRegisterClientsService.PupilInfo test = new ImportRegisterClientsService.PupilInfo();
        test.familyName = "Кожина";
        test.firstName = "Ольга";
        test.secondName = "Александровна";
        test.group = "11А";
        test.guid = "111";
        pupilInfos.add(new Item(test));
        test = new ImportRegisterClientsService.PupilInfo();
        test.familyName = "Копейкин";
        test.firstName = "Александр";
        test.secondName = "Юрьевич";
        test.group = "2Б";
        test.guid = "222";
        pupilInfos.add(new Item(test));
        test = new ImportRegisterClientsService.PupilInfo();
        test.familyName = "Тест";
        test.firstName = "Тест";
        test.secondName = "Тест";
        test.group = "1А";
        test.guid = "444";
        pupilInfos.add(new Item(test));
        */
        if (org == null) {
            printError("Необходимо выбрать организацию");
            return;
        }
        if (org.getGuid() == null) {
            printError("У выбранной организации не указан GUID");
            return;
        }
        if (pupilInfos == null) {
            printError("Перед сосотавлением файла CSV, необходимо выполнить запрос к Реестрам");
            return;
        }
        List<Item> missedISPPClients = new ArrayList<Item>();
        List<Client> missedRegisterClients = new ArrayList<Client>();
        List<Client> orgClients = DAOService.getInstance().getClientsByOrgId(org.getIdOfOrg());
        //  Проверяем данные из Реестров и ищем клиентов в ИС ПП
        for (Item i : pupilInfos) {
            String lookupGuid = i.getGuid();
            if (lookupGuid == null || lookupGuid.length() < 1) {
                continue;
            }
            Client cl = DAOService.getInstance().getClientByGuid(lookupGuid);
            if (cl == null) {
                missedISPPClients.add(i);
            }
        }
        //  Проверяем всех клиентов из ИС ПП на их присутствие в Реестах
        for (Client cl : orgClients) {
            if (cl.getClientGUID() == null || cl.getClientGUID().length() < 1) {
                continue;
            }
            boolean found = false;
            for (Item i : pupilInfos) {
                if (cl.getClientGUID().equals(i.getGuid())) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                missedRegisterClients.add(cl);
            }
        }

        //  Проверки наличия клиентов выполнены, можно составлять файл
        exportMissedClientsFile(missedISPPClients, missedRegisterClients);
    }

    public void exportMissedClientsFile(List<Item> missedISPPClients, List<Client> missedRegisterClients) {
        try {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            ExternalContext externalContext = facesContext.getExternalContext();
            HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

            response.setHeader("Content-disposition", "attachment; filename=Synch.csv");
            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/csv");
            final ServletOutputStream responseOutputStream = response.getOutputStream();
            try {
                String str = "л\\с ИС ПП;Фамилия;Имя;Отчество;Класс;Есть в ИС ПП;Есть в Реестре;Результат;\n";
                responseOutputStream.write(str.getBytes());
                for (Item i : missedISPPClients) {
                    str = ";" +
                            i.getFamilyName() + ";" +
                            i.getFirstName() + ";" +
                            i.getSecondName() + ";" +
                            i.getGroup() + ";" +
                            "Нет;" +
                            "Да;;\n";
                    responseOutputStream.write(str.getBytes());
                }
                for (Client cl : missedRegisterClients) {
                    str = cl.getContractId() + ";" +
                            cl.getPerson().getSurname() + ";" +
                            cl.getPerson().getFirstName() + ";" +
                            cl.getPerson().getSecondName() + ";" +
                            cl.getClientGroup().getGroupName() + ";" +
                            "Да;" +
                            "Нет;;\n";
                    responseOutputStream.write(str.getBytes());
                }
                responseOutputStream.flush();
            } catch (Exception e1) {
                throw e1;
            } finally {
                responseOutputStream.close();
            }
            facesContext.responseComplete();
        } catch (Exception e) {
            logger.error("Failed to send comparison file", e);
        }
    }
}