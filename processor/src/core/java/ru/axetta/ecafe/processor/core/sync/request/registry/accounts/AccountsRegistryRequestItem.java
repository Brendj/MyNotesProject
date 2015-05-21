/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request.registry.accounts;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 9:49
 */
public class AccountsRegistryRequestItem {
    public static final String SYNC_NAME = "RI";

    private Long idOfClient;
    private Long idOfCard;

    public static AccountsRegistryRequestItem build(Node itemNode) throws Exception {
        Long idOfClient = XMLUtils.getLongValueNullSafe(itemNode.getAttributes(), "ClientId");
        Long idOfCard = XMLUtils.getLongValueNullSafe(itemNode.getAttributes(), "CardId");

        return new AccountsRegistryRequestItem(idOfClient, idOfCard);
    }

    public AccountsRegistryRequestItem(Long idOfClient, Long idOfCard) {
        this.idOfClient = idOfClient;
        this.idOfCard = idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }
}
