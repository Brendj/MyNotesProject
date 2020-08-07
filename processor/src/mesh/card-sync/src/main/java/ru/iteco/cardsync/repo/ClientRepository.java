/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import ru.iteco.cardsync.models.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Client findFirstByMeshGuid(String meshGuid);

    @Query(value =
            "SELECT c.idofclient "
            + "FROM cf_clients AS c "
            + "         JOIN cf_persons AS p ON p.idofperson = c.idofperson "
            + "         JOIN cf_clientgroups AS cc ON c.idoforg = cc.idoforg AND c.idofclientgroup = cc.idofclientgroup "
            + "WHERE p.surname || ' ' || p.firstname || ' ' || p.secondname ILIKE ?  "
            + "  AND (cc.idofclientgroup BETWEEN 1100000000 AND 1100000029 " // Все от Пед. состава до Тех. персонала
            + "       OR cc.idofclientgroup BETWEEN 1100000050 AND 1100000059 " // группа "Другое"
            + "       OR cc.idofclientgroup BETWEEN 1100000110 AND 1100000119); ", // Сотрудники других ОО
            nativeQuery = true)
    List<Long> getStaffByFIO(String fio);
}
