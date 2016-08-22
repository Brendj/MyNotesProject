/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.clientphoto;

import ru.axetta.ecafe.processor.core.sync.request.SectionRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 13.04.16
 * Time: 10:27
 */
public class ClientsPhotos implements SectionRequest {
    public static final String SECTION_NAME = "ClientsPhotos";

    private final List<ClientPhotosItem> items;
    private final Long maxVersion;
    private final Long idOfOrgOwner;
    private final Integer syncPhotoCount;

    public ClientsPhotos(Node clientPhotoRequestNode, Long orgOwner) {
        maxVersion = XMLUtils.getLongAttributeValue(clientPhotoRequestNode, "V");
        syncPhotoCount = XMLUtils.getIntegerAttributeValue(clientPhotoRequestNode, "SyncPhotoCount");
        this.items = new ArrayList<ClientPhotosItem>();
        this.idOfOrgOwner = orgOwner;

        Node itemNode = clientPhotoRequestNode.getFirstChild();
        while (null != itemNode) {
            if (Node.ELEMENT_NODE == itemNode.getNodeType() && itemNode.getNodeName().equals("CP")) {
                ClientPhotosItem item = ClientPhotosItem.build(itemNode, orgOwner);
                getItems().add(item);
            }
            itemNode = itemNode.getNextSibling();
        }
    }

    public Long getIdOfOrgOwner() {
        return idOfOrgOwner;
    }

    public List<ClientPhotosItem> getItems() {
        return items;
    }

    public Long getMaxVersion() {
        return maxVersion;
    }

    public Integer getSyncPhotoCount() {
        return syncPhotoCount;
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }
}
