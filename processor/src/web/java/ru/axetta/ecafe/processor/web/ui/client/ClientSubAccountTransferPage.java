/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.client;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.converter.FromTypeConverter;

import org.hibernate.Session;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.model.SelectItem;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

@Component
@Scope("session")
public class ClientSubAccountTransferPage extends BasicWorkspacePage implements ClientSelectPage.CompleteHandler {
    @PersistenceContext(unitName = "processorPU")
    private EntityManager em;


    @Override
    public String getPageFilename() {
        return "client/sub_account_transfer";
    }

    public Client client;
    public String clientName;
    public Long clientBalance;
    public Long clientSubBalance;
    public Long clientContractId;
    public Long clientSubContractId;
    public Long sum;
    public String reason;
    private FromTypeConverter fromTypeMenu = new FromTypeConverter();

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
            clientContractId = client.getContractId()*100;
            clientSubContractId = client.getContractId()*100+1;
            clientBalance = client.getBalance();
            clientSubBalance = client.getSubBalance(1);
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
                if(fromTypeMenu.getFromType().equals(FromTypeConverter.FromTypeEnum.FROM_TO_SUB_BALANCE)){
                    RuntimeContext.getFinancialOpsManager().createSubAccountTransfer(client, 0, 1, sum);
                }
                else {
                    RuntimeContext.getFinancialOpsManager().createSubAccountTransfer(client, 1, 0, sum);
                }
                printMessage("Возврат средств успешно проведен");
                RuntimeContext.getAppContext().getBean(ClientSubAccountTransferPage.class).updateClientInfo();
            } catch (Exception e) {
                logAndPrintMessage("Ошибка при выполнении возврата средств", e);
            }
        }

        return null;
    }

    public FromTypeConverter getFromTypeMenu() {
        return fromTypeMenu;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Client getClient() {
        return client;
    }

    public Long getClientContractId() {
        return clientContractId;
    }

    public Long getClientSubContractId() {
        return clientSubContractId;
    }

    public Long getClientBalance() {
        return clientBalance;
    }

    public Long getClientSubBalance() {
        return clientSubBalance;
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

}
