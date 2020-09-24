/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.repo;

import org.springframework.data.repository.query.Param;
import ru.iteco.cardsync.models.Client;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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

    @Query(value =
            "SELECT c.* "
                    + "FROM cf_clients AS c "
                    + "         left join cf_orgs AS co ON co.idoforg = c.idoforg "
                    + "         left join cf_persons AS p ON p.idofperson = c.idofperson "
                    + "         left join cf_clientgroups AS cc ON c.idoforg = cc.idoforg AND c.idofclientgroup = cc.idofclientgroup "
                    + "WHERE lower(p.surname) = lower(:lastName) " +
                    "  and lower(p.firstname) = lower(:firstName) " +
                    "  and lower(p.secondname) = lower(:middleName)  " +
                    " and co.organizationidfromnsi in (:orgids)"
                    + "  AND (cc.idofclientgroup = 1100000000 " // "Пед. состав"
                    + "       OR cc.idofclientgroup BETWEEN 1100000010 AND 1100000029 " // "Администрация", "Тех.персонал"
                    + "       OR cc.idofclientgroup BETWEEN 1100000050 AND 1100000059); ", // "Другое"
            nativeQuery = true)
    List<Client> getStaffByFIOandOrgId(@Param("firstName") String firstName,
                             @Param("lastName") String lastName,
                             @Param("middleName") String middleName,
                               @Param("orgids") List<Long> orgIds);

//    @Query(value =
//            "SELECT c.* "
//                    + "FROM cf_clients AS c "
//                    + "         left join cf_orgs AS co ON co.idoforg = c.idoforg "
//                    + "         left join cf_persons AS p ON p.idofperson = c.idofperson "
//                    + "         left join cf_clientgroups AS cc ON c.idoforg = cc.idoforg AND c.idofclientgroup = cc.idofclientgroup "
//                    + "WHERE lower(p.surname) = lower(:lastName) " +
//                    "  and lower(p.firstname) = lower(:firstName) " +
//                    " and co.organizationidfromnsi in (:orgids)"
//                    + "  AND (cc.idofclientgroup BETWEEN 1100000000 AND 1100000029 " // Все от Пед. состава до Тех. персонала
//                    + "       OR cc.idofclientgroup BETWEEN 1100000050 AND 1100000059 " // группа "Другое"
//                    + "       OR cc.idofclientgroup BETWEEN 1100000110 AND 1100000119); ", // Сотрудники других ОО
//            nativeQuery = true)
//    List<Client> getStaffByFIOandOrgIdnoMiddle(@Param("firstName") String firstName,
//                                       @Param("lastName") String lastName,
//                                       @Param("orgids") List<Long> orgIds);

    @Query(value =
            "SELECT c.idofclient "
                    + "FROM cf_clients AS c "
                    + "WHERE c.idofperson = :idOfPerson", // Сотрудники других ОО
            nativeQuery = true)
    List<Long> test(@Param("idOfPerson") Long idOfPerson);
}