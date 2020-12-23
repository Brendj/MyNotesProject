/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import ru.iteco.msp.models.Order;
import ru.iteco.msp.models.dto.SupplyMSPOrders;
import ru.iteco.msp.repo.OrderRepo;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class SupplyMSPService {
    private final static List<Integer> DISCOUNT_TYPES = Arrays.asList(Order.DISCOUNT_TYPE, Order.DISCOUNT_TYPE_RESERVE);
    private final static SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    private final Logger log = LoggerFactory.getLogger(SupplyMSPService.class);

    private final OrderRepo orderRepo;

    public SupplyMSPService(OrderRepo orderRepo){
        this.orderRepo = orderRepo;
    }

    public Integer countOrders(Date begin, Date end){
        return orderRepo.countDistinctByCreatedDateBetweenAndOrderTypeIn(begin.getTime(), end.getTime(), DISCOUNT_TYPES);
    }

    public List<SupplyMSPOrders> getDiscountOrders(Date begin, Date end, Pageable pageable){
       // List<Order> orders = orderRepo.findAllByCreatedDateBetweenAndAndOrderTypeIn(begin.getTime(), end.getTime(), DISCOUNT_TYPES, pageable);
        List<SupplyMSPOrders> orders = orderRepo.fullInfo(begin.getTime(), end.getTime(), pageable);
        if(CollectionUtils.isEmpty(orders)){
            log.warn(String.format("No orders between %s and %s", format.format(begin), format.format(end)));
            return Collections.emptyList();
        }

        return orders;
    }
}
