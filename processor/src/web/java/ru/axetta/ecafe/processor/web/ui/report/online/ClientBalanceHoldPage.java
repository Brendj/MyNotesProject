/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;
import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHoldRequestStatus;
import ru.axetta.ecafe.processor.core.report.ClientBalanceHoldPageItem;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.client.ClientSelectListPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope(value = "session")
public class ClientBalanceHoldPage extends OnlineReportPage {

    private final static Logger logger = LoggerFactory.getLogger(ClientBalanceHoldPage.class);
    private List<ClientBalanceHoldPageItem> items = new ArrayList<ClientBalanceHoldPageItem>();
    private ClientBalanceHoldPageItem currentItem;
    private static final Integer NO_CONDITION = 100;
    private Integer requestStatus = NO_CONDITION;
    private SelectItem[] requestStatusItems = readAllItems();

    private static SelectItem[] readAllItems() {
        SelectItem[] items = new SelectItem[ClientBalanceHoldRequestStatus.values().length + 1];
        items[0] = new SelectItem(NO_CONDITION, "");
        int i = 1;
        for (ClientBalanceHoldRequestStatus value : ClientBalanceHoldRequestStatus.values()) {
            items[i] = new SelectItem(i-1, value.toString());
            i++;
        }
        return items;
    }

    public String getPageFilename ()
    {
        return "report/online/client_balance_hold";
    }

    public List<ClientBalanceHoldPageItem> getItems() {
        return items;
    }

    public void setItems(List<ClientBalanceHoldPageItem> items) {
        this.items = items;
    }

    public void reload() {
        if (idOfOrg == null && getClientList().isEmpty()) {
            printError("Выберите организацию или клиента!");
            return;
        }
        items.clear();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            List<Long> idOfClientList = new ArrayList<Long>();
            for (ClientSelectListPage.Item item : getClientList()) {
                idOfClientList.add(item.getIdOfClient());
            }
            List<ClientBalanceHold> list = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                    .getClientBalanceHolds(persistenceSession, idOfOrg, idOfClientList, requestStatus);
            for (ClientBalanceHold clientBalanceHold : list) {
                ClientBalanceHoldPageItem item = new ClientBalanceHoldPageItem(clientBalanceHold);
                items.add(item);
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to build client balance hold items", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void confirm() {
        //меняем статус
        if (currentItem == null) return;
        for (ClientBalanceHoldPageItem item : items) {
            if (item.equals(currentItem)) {
                RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).setStatusWithValue(item.getIdOfClientBalanceHold(),
                        ClientBalanceHoldRequestStatus.REFUNDED);
                currentItem.setRequestStatus(ClientBalanceHoldRequestStatus.REFUNDED.toString());
                break;
            }
        }
    }

    public void decline() {
        //меняем статус и возвращаем деньги на баланс
        if (currentItem == null) return;
        try {
            for (ClientBalanceHoldPageItem item : items) {
                if (item.equals(currentItem)) {
                    RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).declineClientBalance(item.getIdOfClientBalanceHold());
                    RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class)
                            .setStatusWithValue(item.getIdOfClientBalanceHold(), ClientBalanceHoldRequestStatus.DECLINED);
                    currentItem.setRequestStatus(ClientBalanceHoldRequestStatus.DECLINED.toString());
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Error in decline client balance hold: ", e);
            printError("Не удалось выполнить запрошенную операцию, ошибка " + e.getMessage());
        }
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        super.completeOrgSelection(session, idOfOrg);
        getClientList().clear();
        filterClient = getStringClientList();
    }

    @Override
    public void completeClientSelection(Session session, List<ClientSelectListPage.Item> items) throws Exception {
        super.completeClientSelection(session, items);
        idOfOrg = null;
        filter = "Не выбрано";
    }

    public ClientBalanceHoldPageItem getCurrentItem() {
        return currentItem;
    }

    public void setCurrentItem(ClientBalanceHoldPageItem currentItem) {
        this.currentItem = currentItem;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public SelectItem[] getRequestStatusItems() {
        return requestStatusItems;
    }
}
