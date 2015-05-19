/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.registry.accounts;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.dao.card.CardReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.dao.org.OrgSyncReadOnlyRepository;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountItem;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.AccountsRegistry;
import ru.axetta.ecafe.processor.core.sync.response.registry.accounts.CardsItem;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: regal
 * Date: 18.05.15
 * Time: 12:48
 * To change this template use File | Settings | File Templates.
 */
public class AccountsRegistryHandler {

    @Transactional
    public AccountsRegistry handlerFull(long idOfOrg) {
        AccountsRegistry accountsRegistry = new AccountsRegistry();


        ClientReadOnlyRepository clientDao = ClientReadOnlyRepository.getInstance();
        List<Client> clientList = clientDao.findAllActiveByOrg(idOfOrg);
        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }

        //todo visitor

        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
        List<Card> allFreeByOrg = cardReadOnlyRepository.findAllFreeByOrg(idOfOrg);
        for (Card card : allFreeByOrg) {
            accountsRegistry.getFreeCardsItems().add(new CardsItem(card, null));
        }

        return accountsRegistry;
    }


    public AccountsRegistry handleAccRegistry(long idOfOrg) {
        OrgSyncReadOnlyRepository orgSyncReadOnlyRepository = OrgSyncReadOnlyRepository.getInstance();

        Long lastAccRegistrySyncDate = orgSyncReadOnlyRepository.findLastAccRegistrySyncDate(idOfOrg);
        if(lastAccRegistrySyncDate == null){
            return null;
        }

        AccountsRegistry accountsRegistry = new AccountsRegistry();

        ClientReadOnlyRepository clientDao = ClientReadOnlyRepository.getInstance();
        List<Client> clientList = clientDao.findAllActiveByOrgAndUpdateDate(idOfOrg, lastAccRegistrySyncDate);
        for (Client client : clientList) {
            accountsRegistry.getAccountItems().add(new AccountItem(client));
        }

        //todo visitor

        CardReadOnlyRepository cardReadOnlyRepository = CardReadOnlyRepository.getInstance();
        List<Card> allFreeByOrg = cardReadOnlyRepository.findAllFreeByOrgAndUpdateDate(idOfOrg, lastAccRegistrySyncDate);
        for (Card card : allFreeByOrg) {
            accountsRegistry.getFreeCardsItems().add(new CardsItem(card, null));
        }

        return accountsRegistry;
    }


}
