/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.ClientDiscountHistory;
import ru.iteco.msp.repo.assign.*;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DiscountsService {
    private final DiscountChangeHistoryRepo discountChangeHistoryRepo;
    private final ClientDTSZNDiscountInfoRepo clientDTSZNDiscountInfoRepo;
    private final CategoryDiscountRepo categoryDiscountRepo;
    private final CategoryDiscountDTSZNRepo categoryDiscountDTSZNRepo;
    private final ClientDiscountHistoryRepo clientDiscountHistoryRepo;
    private final ClientRepo clientRepo;

    public final static List<Long> DISCOUNTS_CODES_WITH_END_DATE = Arrays.asList(
            24L, // Инвалидность
            41L, // Пенсионер
            48L, // Малообеспеченные
            52L, // Сироты
            56L, // Родитель с инвалидностью
            66L, // Многодетные
            0L   // Иное
    );

    private final static Integer DISCOUNT_TYPE = 0;

    public DiscountsService(
            DiscountChangeHistoryRepo discountChangeHistoryRepo,
            ClientDTSZNDiscountInfoRepo clientDTSZNDiscountInfoRepo,
            CategoryDiscountRepo categoryDiscountRepo,
            CategoryDiscountDTSZNRepo categoryDiscountDTSZNRepo,
            ClientDiscountHistoryRepo clientDiscountHistoryRepo,
            ClientRepo clientRepo){
        this.discountChangeHistoryRepo = discountChangeHistoryRepo;
        this.clientDTSZNDiscountInfoRepo = clientDTSZNDiscountInfoRepo;
        this.categoryDiscountRepo = categoryDiscountRepo;
        this.categoryDiscountDTSZNRepo = categoryDiscountDTSZNRepo;
        this.clientDiscountHistoryRepo = clientDiscountHistoryRepo;
        this.clientRepo = clientRepo;
    }

    public List<Client> getClientsWithMeshGuid(Pageable pageable){
        return clientRepo.findDistinctByMeshGuidIsNotNullAndDiscountsNotNull(pageable);
    }

    public List<Client> getClientsWithMeshGuidAndGreaterThenIdOfClient(Long idOfClient, Pageable pageable){
        return clientRepo.findDistinctByMeshGuidIsNotNullAndDiscountsNotNullAndIdOfClientGreaterThan(idOfClient, pageable);
    }

    public ClientDTSZNDiscountInfo getLastInfoByClientAndCode(Client client, Integer code) {
        return clientDTSZNDiscountInfoRepo.findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(code.longValue(), client);
    }

    public List<ClientDiscountHistory> getNewHistoryByTime(Date date) {
        return clientDiscountHistoryRepo
                .getAllByRegistryDateGreaterThanEqualAndClientMeshGuidIsNotNullAndCategoryDiscountCategoryType(date.getTime(), DISCOUNT_TYPE);
    }

    public ClientDiscountHistory getLastHistoryByClientAndCategory(Client c, CategoryDiscount discount) {
        return clientDiscountHistoryRepo
                .getFirstByCategoryDiscountAndClientOrderByRegistryDateDesc(discount, c);
    }

    public List<CategoryDiscount> getDiscountsByClient(Client c) {
        return categoryDiscountRepo.findAllByClients(c);
    }
}
