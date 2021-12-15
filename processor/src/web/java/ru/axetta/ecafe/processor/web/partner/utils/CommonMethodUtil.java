/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.utils;

import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientWithAddInfo;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientsWithResultCode;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CommonMethodUtil {
    private Logger logger = LoggerFactory.getLogger(CommonMethodUtil.class);

    public ClientsWithResultCode getClientsByGuardMobile(String mobile, Session session, Integer RC_OK, Integer RC_CLIENT_NOT_FOUND,
            Integer RC_INTERNAL_ERROR) {

        ClientsWithResultCode data = new ClientsWithResultCode();
        try {
            Map<Client, ClientWithAddInfo> clients = extractClientsFromGuardByGuardMobile(
                    Client.checkAndConvertMobile(mobile), session);
            if (clients.isEmpty()) {
                data.resultCode = RC_CLIENT_NOT_FOUND.longValue();
                data.description = "Клиент не найден";
            } else {
                boolean onlyNotActiveCG = true;
                for (Map.Entry<Client, ClientWithAddInfo> entry : clients.entrySet()) {
                    if (entry.getValue() != null) {
                        onlyNotActiveCG = false;
                        break;
                    }
                }
                if (onlyNotActiveCG) {
                    data.resultCode = RC_CLIENT_NOT_FOUND.longValue();
                    data.description = "Связка не активна";
                } else {
                    data.setClients(clients);
                    data.resultCode = RC_OK.longValue();
                    data.description = "OK";
                }
            }
        } catch (Exception e) {
            logger.error("Failed to process client room controller request", e);
            data.resultCode = RC_INTERNAL_ERROR.longValue();
            data.description = e.toString();
        }
        return data;
    }

    public Map<Client, ClientWithAddInfo> extractClientsFromGuardByGuardMobile(String guardMobile, Session session)
            throws Exception {
        Map<Client, ClientWithAddInfo> result = new HashMap<Client, ClientWithAddInfo>();
        String query =
                "select client.idOfClient from cf_clients client where (client.phone=:guardMobile or client.mobile=:guardMobile) "
                        + "and client.IdOfClientGroup not in (:leaving, :deleted)"; //все клиенты с номером телефона
        Query q = session.createSQLQuery(query);
        q.setParameter("guardMobile", guardMobile);
        q.setParameter("leaving", ClientGroup.Predefined.CLIENT_LEAVING.getValue());
        q.setParameter("deleted", ClientGroup.Predefined.CLIENT_DELETED.getValue());
        List<BigInteger> clients = q.list();

        if (clients != null && !clients.isEmpty()) {
            for (BigInteger id : clients) {
                Long londId = id.longValue();
                Query q2 = session.createQuery("select c, cg from ClientGuardian cg, Client c "
                        + "where cg.idOfChildren = c.idOfClient and cg.idOfGuardian = :idOfGuardian "
                        + "and cg.deletedState = false");  //все дети текущего клиента
                q2.setParameter("idOfGuardian", londId);
                List list = q2.list();
                if (list != null && list.size() > 0) {
                    for (Object o : list) {
                        Object[] row = (Object[]) o;
                        if (result.get(row[0]) == null || (result.get(row[0]) != null && result.get(row[0])
                                .isDisabled())) {
                            //если по клиенту инфы еще нет то добавляем, или инфа уже есть, но связка выключена, то обновляем инфу
                            Client child = (Client) row[0];
                            ClientGuardian cg = (ClientGuardian) row[1];
                            ClientWithAddInfo addInfo = new ClientWithAddInfo();
                            addInfo.setInformedSpecialMenu(ClientManager
                                    .getInformedSpecialMenu(session, child.getIdOfClient(), cg.getIdOfGuardian()) ? 1
                                    : null);
                            addInfo.setPreorderAllowed(ClientManager
                                    .getAllowedPreorderByClient(session, child.getIdOfClient(), cg.getIdOfGuardian())
                                    ? 1 : null);
                            addInfo.setClientCreatedFrom(cg.isDisabled() ? null : cg.getCreatedFrom());
                            addInfo.setDisabled(cg.isDisabled());
                            if (cg.getRepresentType() == null) {
                                addInfo.setRepresentType(ClientGuardianRepresentType.UNKNOWN);
                            } else {
                                addInfo.setRepresentType(cg.getRepresentType());
                            }
                            result.put(child, addInfo);
                        }
                    }
                } else {
                    ClientWithAddInfo addInfo = new ClientWithAddInfo();
                    addInfo.setInformedSpecialMenu(null);
                    addInfo.setClientCreatedFrom(ClientCreatedFromType.DEFAULT);
                    addInfo.setRepresentType(ClientGuardianRepresentType.UNKNOWN);
                    result.put(DAOUtils.findClient(session, londId), addInfo);
                }
            }
        }

        return result;
    }
}
