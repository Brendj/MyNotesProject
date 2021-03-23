/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDTSZNDiscountInfo;
import ru.iteco.msp.models.ClientDiscountHistory;
import ru.iteco.msp.repo.assign.*;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

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
        return clientRepo.getAllByMeshGuidIsNotNullAndDiscountsNotNull(pageable);
    }

    public List<Client> getClientsWithMeshGuidAndGreaterThenIdOfClient(Long idOfClient, Pageable pageable){
        return clientRepo.getAllByMeshGuidIsNotNullAndDiscountsNotNullAndIdOfClientGreaterThan(idOfClient, pageable);
    }

    public ClientDTSZNDiscountInfo getLastInfoByClientAndCode(Client client, Integer code) {
        return clientDTSZNDiscountInfoRepo.findFirstByDTISZNCodeAndClientOrderByLastUpdateDesc(code.longValue(), client);
    }

    public List<ClientDiscountHistory> getNewHistoryByTime(Date date) {
        return clientDiscountHistoryRepo.getAllByRegistryDateGreaterThanEqualAndClientMeshGuidIsNotNull(date.getTime());
    }
}
