/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.logic;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Service
public class ClientDiscountHistoryService {
    private Logger log = LoggerFactory.getLogger(ClientDiscountHistoryService.class);

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
        for(ClientDiscountHistory h :histories) {
            session.save(h);
        }
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
          if(discountInfo == null){
              return;
          }
          CategoryDiscountDSZN categoryDiscountDSZN = DAOUtils
                  .getCategoryDiscountDSZNByDSZNCode(session, discountInfo.getDtisznCode());
          CategoryDiscount discount = categoryDiscountDSZN.getCategoryDiscount();

         ClientDiscountHistory history = ClientDiscountHistory.build(discountInfo.getClient(),
                 comment, discount, ClientDiscountHistoryOperationTypeEnum.CHANGE);

         session.save(history);
    }
}
