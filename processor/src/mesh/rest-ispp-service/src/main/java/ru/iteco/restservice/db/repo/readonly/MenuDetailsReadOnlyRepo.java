/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import io.swagger.v3.oas.annotations.Parameter;
import ru.iteco.restservice.model.MenuDetail;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MenuDetailsReadOnlyRepo extends CrudRepository<MenuDetail, Long> {
    @Query(value = "SELECT distinct cfm FROM MenuDetail cfm left join cfm.menu cm "
            + "WHERE cfm.idOfMenuFromSync = :idOfMenu AND cm.menuDate between :start and :end "
            + "AND cfm.menuDetailName like :name AND cm.org.idOfOrg = :idOfOrg ")
    List<MenuDetail> getDetailsByPeriodAndIdOfMenu(@Parameter(name = "idOfMenu") Long idOfMenu,
                                             @Parameter(name = "start") Long start,
                                             @Parameter(name = "end") Long end,
                                             @Parameter(name = "name") String name,
                                             @Parameter(name = "idOfOrg") Long idOfOrg,
                                             Pageable pageable);
}
