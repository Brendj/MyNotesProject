/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.utils;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.PreorderMobileGroupOnCreateType;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nuc on 30.03.2020.
 */
public class PreorderUtils {
    public static final Integer SOAP_RC_CLIENT_NOT_FOUND = 110;
    public static final Integer SOAP_RC_SEVERAL_CLIENTS_WERE_FOUND = 120;
    public static final Integer SOAP_RC_PREORDERS_NOT_UNIQUE_CLIENT = 644;
    public static final Integer SOAP_RC_WRONG_GROUP = 710;
    public static final Integer SOAP_RC_MOBILE_DIFFERENT_GROUPS = 711;

    public static Integer getClientGroupResult(Session session, List<Client> clients) {
        Map<Integer, Integer> map = new HashMap<>();
        boolean isStudent = false;
        boolean isParent = false;
        boolean isTrueParent = false;
        boolean isEmployee = false;
        boolean isEmployeeParent = false;
        for (Client client : clients) {
            PreorderMobileGroupOnCreateType type = null;
            if (client.isStudent()) {
                type = PreorderMobileGroupOnCreateType.STUDENT;
                isStudent = true;
            }
            if (client.isParentMsk()) {
                type = PreorderMobileGroupOnCreateType.PARENT;
                isParent = true;
                isTrueParent = ClientManager.clientHasChildren(session, client.getIdOfClient());
            }
            if (client.isSotrudnikMsk()) {
                type = PreorderMobileGroupOnCreateType.EMPLOYEE;
                isEmployee = true;
                isEmployeeParent = ClientManager.clientHasChildren(session, client.getIdOfClient());
            }
            if (type == null) continue;
            Integer count = map.get(type);
            if (count == null) count = 0;
            map.put(type.ordinal(), count + 1);
        }
        if (map.size() == 0) return SOAP_RC_CLIENT_NOT_FOUND;

        for (Integer value : map.keySet()) {
            if (map.get(value) > 1) return SOAP_RC_SEVERAL_CLIENTS_WERE_FOUND;
        }

        if ((isStudent && isParent && isEmployee) || (isEmployee && isStudent) || (isParent && isStudent)) {
            return SOAP_RC_PREORDERS_NOT_UNIQUE_CLIENT;
        }
        PreorderMobileGroupOnCreateType value;
        if (isEmployeeParent && !isStudent && !isParent)
            value = PreorderMobileGroupOnCreateType.PARENT_EMPLOYEE;
        else if (isTrueParent && !isStudent && !isEmployee)
            value = PreorderMobileGroupOnCreateType.PARENT;
        else if (isEmployee && !isStudent && !isParent)
            value = PreorderMobileGroupOnCreateType.EMPLOYEE;
        else if (isStudent && !isEmployee && !isParent)
            value = PreorderMobileGroupOnCreateType.STUDENT;
        else value = null;
        if (value == null) {
            if (map.size() == 1) {
                return SOAP_RC_WRONG_GROUP;
            } else {
                return SOAP_RC_MOBILE_DIFFERENT_GROUPS;
            }
        }

        return value.ordinal();
    }

    public static List<Client> getClientsByMobile(Session session, Long idOfClient, String mobile) {
        Query query = session.createQuery("select c from Client c where (c.mobile = :mobile and c.idOfClient = :idOfClient) "
                + "or exists (select c2 from Client g, ClientGuardian cg where g.mobile = :mobile and g.idOfClient = cg.idOfGuardian and cg.idOfChildren = :idOfClient)");
        query.setParameter("mobile", mobile);
        query.setParameter("idOfClient", idOfClient);
        return query.list();
    }
}
