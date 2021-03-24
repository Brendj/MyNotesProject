/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.iteco.emias.models.Client;

import java.util.List;

public interface ClientRepository extends JpaRepository<Client, Long> {
    @Query(value =
            "SELECT * "
                    + "FROM cf_clients AS c "
                    + "left join cf_clientgroups AS cc ON c.idoforg = cc.idoforg AND c.idofclientgroup = cc.idofclientgroup "
                    + "WHERE c.meshguid = :meshguid"
                    + "  AND (cc.idofclientgroup < 1100000000 " // не предопределённые группы
                    + "       OR cc.idofclientgroup BETWEEN 1100000060 AND 1100000079);", // группы "Выбывшие" и "Удаленные"
            nativeQuery = true)
    Client findFirstByMeshGuid(@Param("meshguid") String meshguid);
}
