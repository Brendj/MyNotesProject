/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Scope("session")
public class ClientBalanceTransferPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;


    @Override
    public String getPageFilename() {
        return "client/balance_transfer";
    }

    public Client fromClient, toClient;
    public String fromClientName, toClientName;
    public Long fromClientBalance, toClientBalance;
    public Long fromClientContractId, toClientContractId;
    public String clientSelectType;
    public Long sum;
    public String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getClientSelectType() {
        return clientSelectType;
    }

    public void setClientSelectType(String clientSelectType) {
        this.clientSelectType = clientSelectType;
    }

    public Client getFromClient() {
        return fromClient;
    }

    public void setFromClient(Client fromClient) {
        this.fromClient = fromClient;
    }

    public Long getFromClientContractId() {
        return fromClientContractId;
    }

    public Long getToClientContractId() {
        return toClientContractId;
    }

    public Client getToClient() {
        return toClient;
    }

    public void setToClient(Client toClient) {
        this.toClient = toClient;
    }

    public String getFromClientName() {
        return fromClientName;
    }

    public String getToClientName() {
        return toClientName;
    }

    public Long getFromClientBalance() {
        return fromClientBalance;
    }

    public Long getToClientBalance() {
        return toClientBalance;
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
        if (clientSelectType.equals("from")) {
            fromClient = cl;
        } else if (clientSelectType.equals("to")) {
            toClient = cl;
        }
        updateClientInfo(session);
    }

    @Transactional
    public void updateClientInfo() {
        updateClientInfo((Session)em.getDelegate());
    }
    
    public void updateClientInfo(Session session) {
        fromClientName=null;
        fromClientBalance=null;
        toClientName=null;
        toClientBalance=null;
        if (fromClient!=null) {
            fromClient = (Client)session.get(Client.class, fromClient.getIdOfClient());
            fromClientName = fromClient.getPerson().getFullName();
            fromClientContractId = fromClient.getContractId();
            fromClientBalance = fromClient.getBalance();
        }
        if (toClient!=null) {
            toClient = (Client)session.get(Client.class, toClient.getIdOfClient());
            toClientName = toClient.getPerson().getFullName();
            toClientContractId = toClient.getContractId();
            toClientBalance = toClient.getBalance();
        }
    }

    public Object registerTransfer() throws Exception {
        if (fromClient == null) {
            printError("Не указан плательщик");
        } else if (toClient == null) {
            printError("Не указан получатель");
        } else if (!RuntimeContext.getFinancialOpsManager().defaultSupplierEqual(fromClient, toClient)) {
            printError("Операция не может быть проведена, так как организацию получателя обслуживает другой поставщик");
        } else if (sum == null) {
            printError("Не указана сумма");
        } else if (reason == null || reason.length() == 0) {
            printError("Не указана причина");
        } else if (sum <= 0) {
            printError("Сумма должна быть больше нуля");
        } else if (fromClientBalance < sum) {
            printError("Недостаточно средств на лицевом счете плательщика");
        } else {
            Session session = null;
            Transaction transaction = null;
            try {
                session = RuntimeContext.getInstance().createPersistenceSession();
                transaction = session.beginTransaction();
                RuntimeContext.getFinancialOpsManager().createAccountTransfer(session, fromClient, toClient, sum, reason,
                        MainPage.getSessionInstance().getCurrentUser());
                printMessage("Перевод успешно проведен");
                RuntimeContext.getAppContext().getBean(ClientBalanceTransferPage.class).updateClientInfo(session);
                transaction.commit();
                transaction = null;
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выполнении перевода", e);
            } finally {
                HibernateUtils.rollback(transaction, getLogger());
                HibernateUtils.close(session, getLogger());
            }
        }

        return null;
    }
}
