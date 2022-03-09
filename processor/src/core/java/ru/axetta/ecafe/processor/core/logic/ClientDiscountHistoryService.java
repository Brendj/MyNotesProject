/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import java.util.*;

@Service
public class ClientDiscountHistoryService {
    private static final Logger log = LoggerFactory.getLogger(ClientDiscountHistoryService.class);


    private static final List<ClientDiscountHistoryOperationTypeEnum> VALID_OPERATION_FOR_LAST_RECORD = Arrays.asList(
            ClientDiscountHistoryOperationTypeEnum.ADD,
            ClientDiscountHistoryOperationTypeEnum.CHANGE
    );

    public void saveClientDiscountHistoryByOldScheme(Session session, Client client,
            Set<CategoryDiscount> oldDiscounts, Set<CategoryDiscount> newDiscounts, String comment){
        List<ClientDiscountHistory> histories = new LinkedList<>();
        if(!oldDiscounts.equals(newDiscounts)) {
            List<CategoryDiscount> disjunctions = (List<CategoryDiscount>) CollectionUtils
                    .disjunction(newDiscounts, oldDiscounts);
            for (CategoryDiscount uncommon : disjunctions) {
                ClientDiscountHistoryOperationTypeEnum type = ClientDiscountHistoryOperationTypeEnum
                        .getType(oldDiscounts, newDiscounts, uncommon);

                histories.add(ClientDiscountHistory.build(client, comment, uncommon, type));
            }
        } else {
            Date begin = CalendarUtils.startOfDay(new Date());
            Date end = CalendarUtils.endOfDay(new Date());
            for(CategoryDiscount discount : newDiscounts){
                discount = (CategoryDiscount) session.merge(discount);
                ClientDtisznDiscountInfo info = getInfoByDiscount(client.getCategoriesDSZN(), discount);
                if(info != null && CalendarUtils.betweenDate(info.getLastUpdate(), begin, end)){
                    histories.add(ClientDiscountHistory.build(client, comment, discount,
                            ClientDiscountHistoryOperationTypeEnum.CHANGE));
                }
            }
        }
        histories.sort(ClientDiscountHistory::compareTo);

        boolean noCreateOperation = histories.stream().noneMatch(h -> h.getOperationType().equals(ClientDiscountHistoryOperationTypeEnum.ADD));
        if(noCreateOperation){
            ClientDiscountHistory lastHistory = getLastDiscountHistoryByClient(session, client);

            if(lastHistory == null || !VALID_OPERATION_FOR_LAST_RECORD.contains(lastHistory.getOperationType())){
                log.error(String.format("For client ID %d can't save DiscountHistory " +
                        " because there is no creation/change records in the DB!", client.getIdOfClient()));

                return;
            }
        }

        for(ClientDiscountHistory h : histories) {
            session.save(h);
        }
    }

    private ClientDiscountHistory getLastDiscountHistoryByClient(Session session, Client client) {
        Criteria c = session.createCriteria(ClientDiscountHistory.class);

        c.add(Restrictions.eq("client", client))
         .addOrder(Order.desc("registryDate"))
         .setMaxResults(1);

        return (ClientDiscountHistory) c.uniqueResult();
    }

    private ClientDtisznDiscountInfo getInfoByDiscount(Set<ClientDtisznDiscountInfo> categoriesDSZN,
            CategoryDiscount discount) {
        for(ClientDtisznDiscountInfo info : categoriesDSZN){
            for(CategoryDiscountDSZN discountDSZN : discount.getCategoriesDiscountDSZN()){
                if(info.getDtisznCode().equals(discountDSZN.getCode().longValue())){
                    return info;
                }
            }
        }
        return null;
    }

    public void saveChangeHistoryByDiscountInfo(Session session, ClientDtisznDiscountInfo discountInfo, String comment) {
        if (discountInfo == null) {
            return;
        }
        CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                .getCategoryDiscountDSZNByDSZNCode(session, discountInfo.getDtisznCode());
        CategoryDiscount discount = categoryDiscountDSZN.getCategoryDiscount();

        Client c = discountInfo.getClient();

        if (c.getCategories() == null || !c.getCategories().contains(discount)) {
            return;
        }

        ClientDiscountHistory history = ClientDiscountHistory.build(discountInfo.getClient(),
                comment, discount, ClientDiscountHistoryOperationTypeEnum.CHANGE);

        session.save(history);
    }
}
