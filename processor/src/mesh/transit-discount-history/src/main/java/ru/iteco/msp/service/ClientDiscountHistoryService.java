/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.service;

import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDiscountHistory;
import ru.iteco.msp.models.enums.OperationType;
import ru.iteco.msp.repo.ClientDiscountHistoryRepo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class ClientDiscountHistoryService {
    private final Logger log = LoggerFactory.getLogger(ClientDiscountHistoryService.class);

    private final ClientDiscountHistoryRepo clientDiscountHistoryRepo;

    public ClientDiscountHistoryService(ClientDiscountHistoryRepo clientDiscountHistoryRepo){
        this.clientDiscountHistoryRepo = clientDiscountHistoryRepo;
    }

    public void save(@NonNull CategoryDiscount discount, @NonNull Client client, @NonNull Long registrationDate,
            @NonNull String comment, @NonNull OperationType type) {
        ClientDiscountHistory history = new ClientDiscountHistory();
        history.setClient(client);
        history.setCategoryDiscount(discount);
        history.setOperationType(type);
        history.setRegistryDate(registrationDate);
        history.setComment(comment);

        clientDiscountHistoryRepo.save(history);
    }
}
