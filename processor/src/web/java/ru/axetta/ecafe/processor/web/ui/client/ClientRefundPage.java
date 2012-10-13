/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Scope("session")
public class ClientRefundPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {
    @PersistenceContext
    EntityManager em;


    @Override
    public String getPageFilename() {
        return "client/refund";
    }

    public Client client;
    public String clientName;
    public Long clientBalance;
    public Long clientContractId;
    public Long sum;
    public String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client сlient) {
        this.client = client;
    }

    public Long getClientContractId() {
        return clientContractId;
    }

    public Long getClientBalance() {
        return clientBalance;
    }

    public Object getClientName() {
        return clientName;
    }

    public Long getSum() {
        return sum;
    }

    public void setSum(Long sum) {
        this.sum = sum;
    }

    @Override
    public void completeClientSelection(Session session, Long idOfClient) throws Exception {
        Client cl = null;
        if (idOfClient != null) {
            cl = (Client) session.get(Client.class, idOfClient);
        }
        client = cl;
        updateClientInfo(session);
    }

    @Transactional
    public void updateClientInfo() {
        updateClientInfo((Session)em.getDelegate());
    }
    
    private void updateClientInfo(Session session) {
        clientName=null;
        clientBalance=null;
        if (client!=null) {
            client = (Client)session.get(Client.class, client.getIdOfClient());
            clientName = client.getPerson().getFullName();
            clientContractId = client.getContractId();
            clientBalance = client.getBalance();
        }
    }

    public Object registerRefund() throws Exception {
        if (client == null) {
            printError("Не указан клиент");
        } else if (sum == null) {
            printError("Не указана сумма");
        } else if (reason == null || reason.length() == 0) {
            printError("Не указана причина");
        } else if (sum <= 0) {
            printError("Сумма должна быть больше нуля");
        } else if (clientBalance<sum) {
            printError("Недостаточно средств на лицевом счете плательщика");
        } else {
            try {
                RuntimeContext.getFinancialOpsManager().createAccountRefund(client, sum, reason,
                        MainPage.getSessionInstance().getCurrentUser());
                printMessage("Возврат средств успешно проведен");
                RuntimeContext.getAppContext().getBean(ClientRefundPage.class).updateClientInfo();
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выполнении возврата средств", e);
            }
        }

        return null;
    }

}
