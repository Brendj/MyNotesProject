/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.org;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.client.items.ClientGroupsByRegExAndOrgItem;
import ru.axetta.ecafe.processor.core.daoservices.org.ClientAllocationRuleDao;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientAllocationRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 03.09.13
 * Time: 12:06
 */

@Component
@Scope(value = "session")
public class ClientAllocationRulesPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final String ORG_TYPE_SOURCE = "source";
    private static final String ORG_TYPE_DEST = "destination";

    private List<ClientAllocationRuleItem> rules = new ArrayList<ClientAllocationRuleItem>();
    private String orgTypeSelected;
    private ClientAllocationRuleItem currentItem;

    private static final int MAX_FILTER_GROUP_LENGTH = 1024;

    @Autowired
    private ClientAllocationRuleDao dao;

    public List<ClientAllocationRuleItem> getRules() {
        return rules;
    }

    public void setRules(List<ClientAllocationRuleItem> rules) {
        this.rules = rules;
    }

    public Object processSourceOrg(int row) {
        currentItem = rules.get(row);
        this.orgTypeSelected = ORG_TYPE_SOURCE;
        return MainPage.getSessionInstance().showOrgSelectPage();
    }

    public Object processDestinationOrg(int row) {
        currentItem = rules.get(row);
        this.orgTypeSelected = ORG_TYPE_DEST;
        return MainPage.getSessionInstance().showOrgSelectPage();
    }

    @Override
    public void onShow() throws Exception {
        List<ClientAllocationRule> rules = dao.findAll();
        this.rules.clear();
        for (ClientAllocationRule rule : rules) {
            this.rules.add(new ClientAllocationRuleItem(rule));
        }
    }

    public Object save() throws Exception {
        Session session = null;
        for (int i = 0; i < rules.size(); i++) {
            ClientAllocationRuleItem item = rules.get(i);
            if (item.isEditable()) {
                if (item.getGroupFilter() != null && item.getGroupFilter().length() > MAX_FILTER_GROUP_LENGTH) {
                    this.printError(String.format(
                            "Строка №%s, поле 'Фильтр Групп' - слишком длинное значение. Допустимый максимум - %s символа", i + 1, MAX_FILTER_GROUP_LENGTH));
                    return null;
                } else if (validateItem(item)) {
                    if (validateGroupFilter(item)) {
                        try {
                            session = RuntimeContext.getInstance().createPersistenceSession();
                            boolean isNewRule = item.getId() == null;
                            ClientAllocationRule rule = isNewRule ? new ClientAllocationRule() : dao.find(item.getId());
                            if (!isNewRule) {
                                if (!checkIsReallyEdited(item, rule)) {
                                    continue;
                                }
                            }
                            rule.setSourceOrg(DAOUtils.getOrgReference(session, item.getIdOfSourceOrg()));
                            rule.setDestinationOrg(DAOUtils.getOrgReference(session, item.getIdOfDestOrg()));
                            rule.setGroupFilter(item.getGroupFilter());
                            rule.setTempClient(item.isTempClient());
                            rule = dao.saveOrUpdate(rule);
                            updateClientsVersion(rule, session);
                        } catch (Exception ex) {
                            if (ex.getCause() instanceof ConstraintViolationException) {
                                this.printError(String.format(
                                        "Строка №%s является дубликатом! Необходимо либо удалить ее, либо изменить ее параметры для дальнейшего сохранения.",
                                        i + 1));
                            } else {
                                this.printError(ex.getMessage());
                            }
                            getLogger().error(ex.getMessage());
                            return null;
                        } finally {
                            item.setEditable(false);
                            HibernateUtils.close(session, getLogger());
                        }
                    } else {
                        this.printError(
                                String.format("У строки №%s в поле Фильтр Групп перечисление нужно задавать через запятую", i + 1));
                        return null;
                    }
                } else {
                    this.printError(String.format("У строки №%s не все обязательные поля заполнены!", i + 1));
                    return null;
                }
            }
        }
        this.printMessage("Правила успешно сохранены.");
        return null;
    }

    // Проверка того, действительно ли правило было изменено.
    // Т.о., снижаем нагрузку, чтобы лишний раз не обновлять версии клиентов.
    private boolean checkIsReallyEdited(ClientAllocationRuleItem item, ClientAllocationRule rule) {
        return !(item.getIdOfDestOrg().equals(rule.getDestinationOrg().getIdOfOrg()) &&
                item.getIdOfSourceOrg().equals(rule.getSourceOrg().getIdOfOrg()) &&
                item.getGroupFilter().equals(rule.getGroupFilter()) &&
                item.isTempClient() == rule.isTempClient());
    }

    // Обновляет версии клиентов. Нужно для синхронизации.
    // Вызывается при создании правила или редактировании его.
    private void updateClientsVersion(ClientAllocationRule rule, Session session) throws Exception {
        Org org = DAOUtils.findOrg(session, rule.getDestinationOrg().getIdOfOrg());
        List<Client> clients = new ArrayList<Client>();
        if (org.getFriendlyOrg().isEmpty()) {
            clients.addAll(org.getClients());
        } else {
            for (Org frOrg : org.getFriendlyOrg()) {
                clients.addAll(frOrg.getClients());
            }
        }
        org = DAOUtils.findOrg(session, rule.getSourceOrg().getIdOfOrg());
        final Set<Org> friendlyOrg = org.getFriendlyOrg();
        List<Long> idOfOrgList = new ArrayList<Long>(friendlyOrg.size());
        for (Org o : friendlyOrg) {
            idOfOrgList.add(o.getIdOfOrg());
        }
        List<ClientGroupsByRegExAndOrgItem> idOfClientGroupsList = ClientManager.findMatchedClientGroupsByRegExAndOrg(session, idOfOrgList, rule.getGroupFilter());
        List<Client> friendlyClients = ClientManager.findClientsByInOrgAndInGroups(session, idOfClientGroupsList);
        clients.addAll(friendlyClients);

        Collection<Client> temp = new ArrayList<>();
        for (Client client: clients) {
            temp.add(client);
            if (temp.size() == 1000) {
                ClientManager.updateClientVersionTransactional(session, temp);
                temp.clear();
            }
        }
        if (temp.size() > 0)
            ClientManager.updateClientVersionTransactional(session, temp);
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        Org org = DAOUtils.findOrg(session, idOfOrg);
        if (ORG_TYPE_DEST.equals(orgTypeSelected)) {
            currentItem.setIdOfDestOrg(org.getIdOfOrg());
            currentItem.setDestOrgName(org.getShortName());
        }
        if (ORG_TYPE_SOURCE.equals(orgTypeSelected)) {
            currentItem.setIdOfSourceOrg(org.getIdOfOrg());
            currentItem.setSourceOrgName(org.getShortName());
        }
    }

    public Object addRule() {
        ClientAllocationRuleItem newItem = new ClientAllocationRuleItem();
        newItem.setEditable(true);
        rules.add(newItem);
        return null;
    }

    public void editRule(int row) {
        ClientAllocationRuleItem item = rules.get(row);
        item.setEditable(true);
    }

    public void deleteRule(int row) {
        ClientAllocationRuleItem item = rules.remove(row);
        if (item.getId() != null) {
            dao.delete(item.getId());
        }
        this.printMessage("Правило удалено.");
    }

    public Object cancel() throws Exception {
        onShow();
        return null;
    }

    private boolean validateItem(ClientAllocationRuleItem item) {
        return item.getIdOfSourceOrg() != null && item.getIdOfDestOrg() != null && StringUtils
                .isNotEmpty(item.getGroupFilter());
    }

    private boolean validateGroupFilter(ClientAllocationRuleItem item) {
        if (item.getGroupFilter().contains(",")) {
            if (item.getGroupFilter().contains(";")) {
                return false;
            } else {
                return true;
            }
        } else {
            if (item.getGroupFilter().contains(";")) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public String getPageFilename() {
        return "org/client_allocation_rules";
    }
}
