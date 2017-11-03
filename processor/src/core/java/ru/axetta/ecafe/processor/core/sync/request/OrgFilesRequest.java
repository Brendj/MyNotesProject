/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.response.OrgFilesItem;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

public class OrgFilesRequest implements SectionRequest {
    public static final String SECTION_NAME="OrgFileRequest";

    private final Operation operation;
    private final List<OrgFilesItem> items;

    private OrgFilesRequest(Operation operation, List<OrgFilesItem> items) {
        this.operation = operation;
        this.items = items;
    }

    static OrgFilesRequest build(Node orgFileRequestNode, Long idOfOrg) throws Exception {
        String operationDsc = XMLUtils.getStringAttributeValue(orgFileRequestNode, "operation", 10);
        Operation operation = Operation.LIST;

        if (operationDsc.equals(Operation.LIST.getDescription())) {
            operation = Operation.LIST;
        } else if (operationDsc.equals(Operation.ADD.getDescription())) {
            operation = Operation.ADD;
        } else if (operationDsc.equals(Operation.DOWNLOAD.getDescription())) {
            operation = Operation.DOWNLOAD;
        } else if (operationDsc.equals(Operation.DELETE.getDescription())) {
            operation = Operation.DELETE;
        }

        List<OrgFilesItem> items = new ArrayList<OrgFilesItem>();
        Node nodeItem = orgFileRequestNode.getFirstChild();
        while (null != nodeItem) {
            if (Node.ELEMENT_NODE == nodeItem.getNodeType() && nodeItem.getNodeName().equals("OF")) {
                items.add(OrgFilesItem.build(nodeItem, idOfOrg, operation));
            }
            nodeItem = nodeItem.getNextSibling();
        }

        return new OrgFilesRequest(operation, items);
    }

    @Override
    public String getRequestSectionName() {
        return SECTION_NAME;
    }

    public Operation getOperation() {
        return operation;
    }

    public List<OrgFilesItem> getItems() {
        return items;
    }


    public enum Operation {
        LIST(0,"list"),         // получить список файлов
        ADD(1,"add"),           // добавить файл (клиент->процессинг)
        DOWNLOAD(2,"download"), // загрузить файл (процессинг->клиент)
        DELETE(3,"delete");     // удалить файл

        private final int value;
        private final String description;

        Operation(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }
}
