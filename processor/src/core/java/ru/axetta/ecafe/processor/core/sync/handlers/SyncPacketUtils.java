/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers;

import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;

/**
 * Created by nuc on 17.02.2020.
 */
public class SyncPacketUtils {

    public static Integer readIntegerValue(Node itemNode, String nameAttr, StringBuilder errorMessage) {
        String strValue = XMLUtils.getAttributeValue(itemNode, nameAttr);
        if (StringUtils.isNotEmpty(strValue)) {
            try {
                return Integer.parseInt(strValue);
            } catch (NumberFormatException e) {
                errorMessage.append(String.format("NumberFormatException incorrect format %s", nameAttr));
            }
        } else {
            errorMessage.append(String.format("Attribute %s not found", nameAttr));
        }
        return null;
    }

    public static Boolean getDeletedState(Node itemNode, StringBuilder errorMessage) {
        Boolean result = null;
        String strDeletedState = XMLUtils.getAttributeValue(itemNode, "D");
        if (StringUtils.isNotEmpty(strDeletedState)) {
            try {
                result = (Integer.parseInt(strDeletedState) == 1);
            } catch (NumberFormatException e) {
                errorMessage.append("NumberFormatException incorrect format DeletedState");
            }
        }
        return result;
    }

}
