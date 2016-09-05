/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts;

import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncReadOnlyRepository;
import ru.axetta.ecafe.processor.core.sync.SyncRequest;
import ru.axetta.ecafe.processor.core.sync.request.registry.accounts.AccountsRegistryRequestItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.CardsItem;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 18.05.15
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")
public class AccountsRegistryHandler {

    private static final Logger logger = LoggerFactory.getLogger(AccountsRegistryHandler.class);

    @Transactional
    public AccountsRegistry handlerFull(SyncRequest request,long idOfOrg) {
        if (!SyncRequest.versionIsAfter(request.getClientVersion(), "2.7")){
            return null;
        }
        AccountsRegistry accountsRegistry = new AccountsRegistry();

        List<Long> idOfOrgs = OrgReadOnlyRepository.getInstance().findFriendlyOrgIds(idOfOrg);

        ClientReadOnlyRepository clientDao = ClientReadOnlyRepository.getInstance();
        List<Client> clientList = clientDao.findAllActiveByOrg(idOfOrgs);

        // Добавляем карты временных посетителей (мигрантов)
        clientList.addAll(Processor.getMigrants(idOfOrg));

        // Добавляем карты перемещенных клиентов
        clientList.addAll(clientDao.findAllAllocatedClients(idOfOrg));

        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }

        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
        /*List<Visitor> visitorsWithCardsByOrg = cardReadOnlyRepository.findVisitorsWithCardsByOrg(idOfOrgs);
        for (Visitor visitor : visitorsWithCardsByOrg) {
            accountsRegistry.getVisitorItems().add(new VisitorItem(visitor));
        }*/

        List<Card> allFreeByOrg = cardReadOnlyRepository.findAllFreeByOrg(idOfOrg);
        for (Card card : allFreeByOrg) {
            accountsRegistry.getFreeCardsItems().add(new CardsItem(card));
        }
        return accountsRegistry;
    }

    @Transactional
    public AccountsRegistry handlerMigrants(long idOfOrg) {
        AccountsRegistry accountsRegistry = new AccountsRegistry();

        List<Client> clientList = Processor.getMigrants(idOfOrg);
        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }

        return accountsRegistry;
    }

    @Transactional
    public AccountsRegistry accRegistryHandler(SyncRequest request,long idOfOrg) {
        if (!SyncRequest.versionIsAfter(request.getClientVersion(), "2.7")){
            return null;
        }
        OrgSyncReadOnlyRepository orgSyncReadOnlyRepository = OrgSyncReadOnlyRepository.getInstance();

        Date lastAccRegistrySyncDate = orgSyncReadOnlyRepository.findLastAccRegistrySyncDate(idOfOrg);
        if(lastAccRegistrySyncDate == null){
            return null;
        }

        lastAccRegistrySyncDate = CalendarUtils.addMinute(lastAccRegistrySyncDate, -60);

        List<Long> idOfOrgs = OrgReadOnlyRepository.getInstance().findFriendlyOrgIds(idOfOrg);

        AccountsRegistry accountsRegistry = new AccountsRegistry();

        ClientReadOnlyRepository clientDao = ClientReadOnlyRepository.getInstance();
        List<Client> clientList = clientDao.findAllActiveByOrgAndUpdateDate(idOfOrgs,lastAccRegistrySyncDate);

        // Добавляем карты временных посетителей (мигрантов)
        clientList.addAll(Processor.getMigrants(idOfOrg));

        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }

        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
        /*List<Visitor> visitorsWithCardsByOrg = cardReadOnlyRepository.findVisitorsWithCardsByOrgAndDate(idOfOrgs,lastAccRegistrySyncDate);
        for (Visitor visitor : visitorsWithCardsByOrg) {
            accountsRegistry.getVisitorItems().add(new VisitorItem(visitor));
        }*/

        List<Card> freeCards = cardReadOnlyRepository.findAllFreeByOrgAndUpdateDate(idOfOrgs,lastAccRegistrySyncDate);
        for (Card card : freeCards) {
            accountsRegistry.getFreeCardsItems().add(new CardsItem(card));
        }
        return accountsRegistry;
    }


    public AccountsRegistry accRegistryUpdateHandler(SyncRequest request) {
        if (!SyncRequest.versionIsAfter(request.getClientVersion(), "2.7")){
            return null;
        }
        if ( request.getAccountsRegistryRequest() == null ||  request.getAccountsRegistryRequest().getItems().size()== 0){
            return null;
        }
        AccountsRegistry accountsRegistry = new AccountsRegistry();

        List<Long> idOfClients = new LinkedList<Long>();

        List<Long> idOfCards = new LinkedList<Long>();
        for (AccountsRegistryRequestItem item : request.getAccountsRegistryRequest().getItems()) {
            if(item.getIdOfClient()!= null) {
                idOfClients.add(item.getIdOfClient());
            }
            if (item.getIdOfCard() != null ){
                idOfCards.add(item.getIdOfCard());
            }
        }
        if (idOfClients.size()> 0){
            ClientReadOnlyRepository clientDao = ClientReadOnlyRepository.getInstance();
            List<Client> clientList = clientDao.findById(idOfClients );
            for (Client client : clientList) {
                accountsRegistry.getAccountItems().add(new AccountItem(client));
            }
        }

        if (idOfCards.size()> 0){
            CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
            List<Card> freeCards = cardReadOnlyRepository.findByIdAndState(idOfCards, CardState.FREE.getValue());
            for (Card card : freeCards) {
                accountsRegistry.getFreeCardsItems().add(new CardsItem(card));
            }
        }

        return accountsRegistry;
    }
}
