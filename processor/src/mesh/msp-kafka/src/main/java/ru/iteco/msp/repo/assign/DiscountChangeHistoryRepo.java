/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.msp.repo.assign;

import ru.iteco.msp.models.DiscountChangeHistory;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DiscountChangeHistoryRepo extends JpaRepository<DiscountChangeHistory, Long> {
    List<DiscountChangeHistory> getAllByRegistrationDateGreaterThanEqual(Long time);

    @Query(value = " SELECT DISTINCT ON (dh.idofclient) dh.* "
            + " from cf_discountchangehistory dh\n"
            + " join cf_clients cc on dh.idofclient = cc.idofclient\n"
            + " where (cc.idofclientgroup < 1100000000 or cc.idofclientgroup in (1100000120, 1100000080))\n"
            + " and cc.meshguid is not null\n"
            + " and dh.categoriesdiscounts not like ''\n",
            nativeQuery = true)
    List<DiscountChangeHistory> getHistoryByDistinctClient(Pageable pageable);
}
