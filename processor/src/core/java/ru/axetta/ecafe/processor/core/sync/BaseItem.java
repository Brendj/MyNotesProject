/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by baloun on 15.06.2018.
 */
public class BaseItem {
    protected static Date getDateValue(Node itemNode, String attrName, StringBuilder errorMessage) {
        Date result = null;
        String str = XMLUtils.getAttributeValue(itemNode, attrName);
        if (StringUtils.isNotEmpty(str)) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy");
                result = simpleDateFormat.parse(str);
            } catch (Exception e){
                errorMessage.append(String.format("Attribute %s not found or incorrect", attrName));
            }
        } else {
            errorMessage.append(String.format("Attribute %s not found", attrName));
        }
        return result;
    }

    protected static Long getOrgId(Node itemNode, StringBuilder errorMessage) {
        Long orgId = null;
        String strOrgId = XMLUtils.getAttributeValue(itemNode, "OrgId");
        if(StringUtils.isNotEmpty(strOrgId)){
            try {
                orgId =  Long.parseLong(strOrgId);
                Org o = DAOReadonlyService.getInstance().findOrg(orgId);
                if (o == null) {
                    errorMessage.append(String.format("Org with id=%s not found", orgId));
                }
            } catch (NumberFormatException e){
                errorMessage.append("NumberFormatException OrgId not found");
            }
        } else {
            errorMessage.append("Attribute OrgId not found");
        }
        return orgId;
    }

    protected static Long getLongValue(Node itemNode, String attrName, StringBuilder errorMessage, boolean errorIfEmpty) {
        Long result = null;
        String str = XMLUtils.getAttributeValue(itemNode, attrName);
        if (StringUtils.isNotEmpty(str)) {
            try {
                result = Long.parseLong(str);
            } catch (Exception e){
                errorMessage.append(String.format("Attribute %s not found or incorrect", attrName));
            }
        } else {
            if (errorIfEmpty) errorMessage.append(String.format("Attribute %s not found", attrName));
        }
        return result;
    }
}
