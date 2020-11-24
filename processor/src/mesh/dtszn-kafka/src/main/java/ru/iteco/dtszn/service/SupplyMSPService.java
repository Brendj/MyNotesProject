/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.dtszn.service;

import ru.iteco.dtszn.models.Order;
import ru.iteco.dtszn.repo.OrderRepo;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
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

    @Transactional
    public Integer countOrders(Date begin, Date end){
        return orderRepo.countAllByCreatedDateAndOrderTypeIn(begin, end, DISCOUNT_TYPES);
    }

    @Transactional
    public List<Order> getDiscountOrders(Date begin, Date end, Pageable pageable){
        List<Order> orders = orderRepo.findAllByCreatedDateBetweenAndAndOrderTypeIn(begin, end, DISCOUNT_TYPES, pageable);
        if(CollectionUtils.isEmpty(orders)){
            log.warn(String.format("No orders between %s and %s", format.format(begin), format.format(end)));
        }

        List<Order> result = new LinkedList<>();
        for(Order o : orders){
            if(o.getClient() == null || StringUtils.isEmpty(o.getClient().getMeshGuid())){
                continue;
            }
            result.add(o);
        }
        return result;
    }
}
