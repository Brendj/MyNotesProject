/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.order;

import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientItem;
import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;
import ru.axetta.ecafe.processor.core.persistence.dao.order.OrdersRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: shamil
 * Date: 10.10.14
 * Time: 11:33
 */
@Service
public class OrderService {

    @Autowired
    private OrdersRepository ordersRepository;

    public List<OrderItem> findOrders(long idOfOrg,  Date startTime, Date endTime) {
        return ordersRepository.findOrdersByClientIds(idOfOrg, startTime, endTime);
    }

    public List<OrderItem> findOrdersByClientIds(long idOfOrg, String clientIds, Date startTime, Date endTime) {
        return ordersRepository.findOrdersByClientIds(idOfOrg, clientIds, startTime, endTime);
    }

    public List<OrderItem> findOrdersByClientIds(Long idOfOrg, List<ClientItem> clientItemList, Date startTime,
            Date endTime) {
        if (clientItemList.size() == 0) {
            return new ArrayList<OrderItem>();
        }
        StringBuilder clientIds = new StringBuilder();
        for (ClientItem item : clientItemList) {
            clientIds.append(item.getId()).append(",");
        }
        clientIds.deleteCharAt(clientIds.length() - 1);
        return findOrdersByClientIds(idOfOrg, clientIds.toString(), startTime, endTime);
    }
}
