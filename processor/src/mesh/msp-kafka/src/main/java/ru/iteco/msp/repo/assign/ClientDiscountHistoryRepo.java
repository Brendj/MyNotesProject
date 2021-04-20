/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.CategoryDiscount;
import ru.iteco.msp.models.Client;
import ru.iteco.msp.models.ClientDiscountHistory;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ClientDiscountHistoryRepo extends CrudRepository<ClientDiscountHistory, Long> {
    List<ClientDiscountHistory> getAllByRegistryDateGreaterThanEqualAndClientMeshGuidIsNotNullAndCategoryDiscountCategoryType(Long time, Integer type);

    ClientDiscountHistory getFirstByCategoryDiscountAndClientOrderByRegistryDateDesc(CategoryDiscount categoryDiscount, Client client);
}
