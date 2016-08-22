/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request.registry.accounts;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.LinkedList;
import java.util.List;

/**
 * User: shamil
 * Date: 21.05.15
 * Time: 9:45
 */
public class AccountsRegistryRequest implements SectionRequest{
    public static final String SYNC_NAME = "AccountsRegistryRequest",
    ATTRIBUT_CONTENT_TYPE="ContentType";

    public enum ContentType {
        ForAll(0),
        ForCardsAndClients(1),
        ForMigrants(2);

        private int code;
        ContentType(int code){
            this.code = code;
        }

        public static ContentType from(Integer contentType){
            if (contentType == null) return ForAll;
            for (ContentType type : ContentType.values()) {
                if (type.code == contentType)
                    return type;
            }
            return ForAll;
        }
    }

    private ContentType contentType;
    private List<AccountsRegistryRequestItem> items = new LinkedList<AccountsRegistryRequestItem>();

    public AccountsRegistryRequest(List<AccountsRegistryRequestItem> items,ContentType contentType) {
        this.items = items;
        this.contentType = contentType;
    }

    static public AccountsRegistryRequest build(Node node) throws Exception {
        ContentType contentType = ContentType.from(XMLUtils.getIntegerAttributeValue(node, ATTRIBUT_CONTENT_TYPE));
        Node itemNode = node.getFirstChild();
        List<AccountsRegistryRequestItem> items = new LinkedList<AccountsRegistryRequestItem>();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals(
                    AccountsRegistryRequestItem.SYNC_NAME)) {
                items.add(AccountsRegistryRequestItem.build(itemNode));
            }
            itemNode = itemNode.getNextSibling();
        }
        return new AccountsRegistryRequest(items,contentType);
    }

    public List<AccountsRegistryRequestItem> getItems() {
        return items;
    }

    public ContentType getContentType() {
        return contentType;
    }

    @Override
    public String getRequestSectionName() {
        return SYNC_NAME;
    }
}
