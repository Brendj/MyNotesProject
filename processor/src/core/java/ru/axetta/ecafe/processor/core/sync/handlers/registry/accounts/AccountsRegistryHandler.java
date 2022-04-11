/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.Processor;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardWritableRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
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

import java.util.*;

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
    private static Boolean newWayAccountsRegistry = null;

    private boolean isNewWayAccountsRegistry() {
        if (newWayAccountsRegistry == null) {
            newWayAccountsRegistry = RuntimeContext.getInstance().groupActionIsOnByNode("ecafe.processor.sync.newaccountsregistry.nodes");
        }
        return newWayAccountsRegistry;
    }

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

        Set<Client> clientSet = new HashSet<Client>(clientList);
        if (isNewWayAccountsRegistry()) {
            addCardsNewWay(accountsRegistry, clientSet);
        } else {
            addCards(accountsRegistry, clientSet);
        }

        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();

        List<Card> allFreeByOrg = cardReadOnlyRepository.findAllFreeByOrg(idOfOrgs);
        for (Card card : allFreeByOrg) {
            accountsRegistry.getFreeCardsItems().add(new CardsItem(card));
        }
        return accountsRegistry;
    }

    private void addCards(AccountsRegistry accountsRegistry, Set<Client> clientList) {
        List<Client> temp = new ArrayList<Client>();
        int counter = 0;
        for (Client client : clientList) {
            temp.add(client);
            counter++;
            if (counter % 10 == 0) {
                accountsRegistry.getAccountItems().addAll(getAccountItems(temp));
                temp.clear();
            }
        }
        if (temp.size() > 0) {
            accountsRegistry.getAccountItems().addAll(getAccountItems(temp));
        }
    }

    private void addCardsNewWay(AccountsRegistry accountsRegistry, Set<Client> clientList) {
        List<Client> temp = new ArrayList<Client>();
        for (Client client : clientList) {
            temp.add(client);
        }
        accountsRegistry.getAccountItems().addAll(getAccountItems(temp));
    }

    private List<AccountItem> getAccountItems(List<Client> clients) {
        List<Card> cards;
        //if (isNewWayAccountsRegistry()) {
        //    cards = CardWritableRepository.getInstance().findAllByClientList(clients);
        //} else {
            cards = CardReadOnlyRepository.getInstance().findAllByClientList(clients);
        //}

        List<AccountItem> result = new ArrayList<AccountItem>();
        for (Client client : clients) {
            result.add(new AccountItem(client, cards));
        }
        return result;
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
    public AccountsRegistry handlerCardsUpdate(long idOfOrg, Date lastUpdate) {
        AccountsRegistry accountsRegistry = new AccountsRegistry();

        List<Card> cardList = DAOReadonlyService.getInstance().getClientsForCardsUpdate(idOfOrg, lastUpdate);
        for (Card card : cardList) {
            if (card.getClient() == null) {
                accountsRegistry.getFreeCardsItems().add(new CardsItem(card));
            } else {
                accountsRegistry.getAccountItems().add(new AccountItem(card.getClient()));
            }
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

        /*List<Client> clientList = new ArrayList<>();
        clientList.addAll(Processor.getMigrants(idOfOrg));

        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }*/

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
        if (request.getAccountsRegistryRequest() == null){
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

        //Обработка карт
        List<Card> externalChangedCards = CardReadOnlyRepository.getInstance().findByOrgandStateChange(1L, request.getIdOfOrg());
        for (Card card: externalChangedCards)
        {
            CardWritableRepository.getInstance().updateCardSync(request.getIdOfOrg(), card , 0L);
            accountsRegistry.getChangedCardItems().add(new CardsItem(card));
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
