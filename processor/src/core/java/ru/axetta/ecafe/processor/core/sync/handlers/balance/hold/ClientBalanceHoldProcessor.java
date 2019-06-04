/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.FinancialOpsManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ClientBalanceHoldService;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ClientBalanceHoldProcessor extends AbstractProcessor<ClientBalanceHoldFeeding> {

    private final ClientBalanceHoldRequest clientBalanceHoldRequest;
    private final ClientBalanceHoldData clientBalanceHoldData;
    private final static Logger logger = LoggerFactory.getLogger(ClientBalanceHoldProcessor.class);
    private static final String WRONG_STATUS = "Заявление не может быть аннулировано";

    public ClientBalanceHoldProcessor(Session persistenceSession, ClientBalanceHoldRequest clientBalanceHoldRequest, ClientBalanceHoldData clientBalanceHoldData) {
        super(persistenceSession);
        this.clientBalanceHoldRequest = clientBalanceHoldRequest;
        this.clientBalanceHoldData = clientBalanceHoldData;
    }

    @Override
    public ClientBalanceHoldFeeding process() throws Exception {
        ClientBalanceHoldFeeding result = new ClientBalanceHoldFeeding();
        List<ClientBalanceHoldItem> items = new ArrayList<ClientBalanceHoldItem>();

        List<ClientBalanceHold> list = RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).getClientBalanceHoldForOrgSinceVersion(session,
                clientBalanceHoldRequest.getOrgOwner(), clientBalanceHoldRequest.getMaxVersion());
        for (ClientBalanceHold clientBalanceHold : list) {
            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(clientBalanceHold);
            items.add(resItem);
        }
        result.setItems(items);
        return result;
    }

    public ResClientBalanceHoldData processData() {
        ResClientBalanceHoldData result = new ResClientBalanceHoldData();
        List<ClientBalanceHoldItem> items = new ArrayList<ClientBalanceHoldItem>();
        Long nextVersion = DAOUtils.nextVersionByClientBalanceHold(session);
        for (ClientBalanceHoldItem item : clientBalanceHoldData.getItems()) {
            if (!StringUtils.isEmpty(item.getErrorMessage())) {
                ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 100, item.getErrorMessage(), null);
                items.add(resItem);
                continue;
            }
            Criteria criteria = session.createCriteria(ClientBalanceHold.class);
            criteria.add(Restrictions.eq("guid", item.getGuid()));
            ClientBalanceHold clientBalanceHold = (ClientBalanceHold) criteria.uniqueResult();

            try {
                Client declarer = (item.getIdOfDeclarer() == null) ? null : DAOReadonlyService.getInstance().findClientById(item.getIdOfDeclarer());
                if (clientBalanceHold == null) {
                    //Для нового объекта присваиваем все поля
                    Client client = DAOReadonlyService.getInstance().findClientById(item.getIdOfClient());
                    Org oldOrg = DAOReadonlyService.getInstance().findOrg(item.getIdOfOldOrg());
                    Org newOrg = (item.getIdOfNewOrg() == null) ? null : DAOReadonlyService.getInstance().findOrg(item.getIdOfNewOrg());
                    Contragent oldContragent = oldOrg.getDefaultSupplier();
                    Contragent newContragent = (newOrg == null) ? null : newOrg.getDefaultSupplier();
                    ClientBalanceHoldCreateStatus createStatus = ClientBalanceHoldCreateStatus.fromInteger(item.getCreateStatus());
                    ClientBalanceHoldRequestStatus requestStatus = ClientBalanceHoldRequestStatus.fromInteger(item.getRequestStatus());
                    RuntimeContext.getAppContext().getBean(ClientBalanceHoldService.class).holdClientBalance(item.getGuid(), client, item.getHoldSum(), declarer, oldOrg,
                            newOrg, oldContragent, newContragent, createStatus, requestStatus, item.getPhoneOfDeclarer(),
                            item.getDeclarerInn(), item.getDeclarerAccount(), item.getDeclarerBank(), item.getDeclarerBik(), item.getDeclarerCorrAccount(), nextVersion);
                } else {
                    //Проверяем пришедший статус. если приходит аннулирование, то заявление на тек. момент может быть только в статусе создания. Иной статус - ошибка
                    if (item.getRequestStatus().equals(ClientBalanceHoldRequestStatus.ANNULLED.ordinal())) {
                        if (!clientBalanceHold.getRequestStatus().equals(ClientBalanceHoldRequestStatus.ANNULLED)
                                && !clientBalanceHold.getRequestStatus().equals(ClientBalanceHoldRequestStatus.CREATED)
                                && !clientBalanceHold.getRequestStatus().equals(ClientBalanceHoldRequestStatus.SUBSCRIBED)) {
                            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 102, WRONG_STATUS, null);
                            items.add(resItem);
                            continue;
                        }
                        //если прежний статус - Создано, а новый Аннулировано, восстанавливаем баланс
                        if (clientBalanceHold.getRequestStatus().equals(ClientBalanceHoldRequestStatus.CREATED)) {
                            RuntimeContext.getAppContext().getBean(FinancialOpsManager.class)
                                    .declineClientBalanceNonTransactional(session, clientBalanceHold);
                        }
                    }
                    //Если объект найден в БД, то меняем только статусы, данные о заявителе и версию
                    clientBalanceHold.setVersion(nextVersion);
                    clientBalanceHold.setCreateStatus(ClientBalanceHoldCreateStatus.fromInteger(item.getCreateStatus()));
                    clientBalanceHold.setRequestStatus(ClientBalanceHoldRequestStatus.fromInteger(item.getRequestStatus()));
                    if (clientBalanceHold.getDeclarer() == null) {
                        clientBalanceHold.setDeclarer(declarer);
                    }
                    clientBalanceHold.setPhoneOfDeclarer(item.getPhoneOfDeclarer());
                    clientBalanceHold.setDeclarerAccount(item.getDeclarerAccount());
                    clientBalanceHold.setDeclarerBank(item.getDeclarerBank());
                    clientBalanceHold.setDeclarerBik(item.getDeclarerBik());
                    clientBalanceHold.setDeclarerInn(item.getDeclarerInn());
                    clientBalanceHold.setDeclarerCorrAccount(item.getDeclarerCorrAccount());
                    clientBalanceHold.setLastUpdate(new Date());
                    session.update(clientBalanceHold);
                }
            } catch (Exception e) {
                logger.error("Error in processing ClientBalanceHold entity: ", e);
                ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 101, "Error in processing entity: " + e.getMessage(), null);
                items.add(resItem);
                continue;
            }
            ClientBalanceHoldItem resItem = new ClientBalanceHoldItem(item.getGuid(), 0, null, nextVersion);
            items.add(resItem);
        }

        result.setItems(items);
        return result;
    }
}
