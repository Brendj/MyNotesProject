/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.EnterEvent;
import ru.iteco.restservice.model.compositid.EnterEventId;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface EnterEventsReadOnlyRepo extends CrudRepository<EnterEvent, EnterEventId> {

    @Query(value = "SELECT CASE WHEN ee.eventcode IN (0, 6, 100, 101, 102, 112) THEN TRUE ELSE FALSE END "
            + "FROM cf_enterevents ee "
            + "WHERE ee.idofclient = :idOfClient AND evtdatetime >= :today "
            + "ORDER BY evtdatetime DESC "
            + "LIMIT 1", nativeQuery = true)
    Optional<Boolean> clientIsInside(@NotNull @Param("idOfClient") Long idOfClient,
                                     @NotNull @Param("today") Long today);

    @Query(value = "SELECT ee.evtdatetime                                                                    AS \"dateTime\",\n"
            + "       ee.passdirection                                                                  AS \"direction\",\n"
            + "       CASE\n"
            + "           WHEN ee.passdirection = 0 THEN 'Вход'\n"
            + "           WHEN ee.passdirection = 1 THEN 'Выход'\n"
            + "           WHEN ee.passdirection = 2 THEN 'Проход запрещен'\n"
            + "           WHEN ee.passdirection = 3 THEN 'Взлом турникета'\n"
            + "           WHEN ee.passdirection = 4 THEN 'Событие без прохода'\n"
            + "           WHEN ee.passdirection = 5 THEN 'Отказ от прохода'\n"
            + "           WHEN ee.passdirection = 6 THEN 'Повторный вход'\n"
            + "           WHEN ee.passdirection = 7 THEN 'Повторный выход'\n"
            + "           WHEN ee.passdirection = 8 THEN 'Запрос на вход'\n"
            + "           WHEN ee.passdirection = 9 THEN 'Запрос на выход'\n"
            + "           WHEN ee.passdirection = 100 THEN 'Обнаружен на подносе карты внутри здания'\n"
            + "           WHEN ee.passdirection = 101 THEN 'Отмечен в классном журнале через внешнюю систему'\n"
            + "           WHEN ee.passdirection = 102 THEN 'Отмечен учителем внутри здания'\n"
            + "           WHEN ee.passdirection = 112 THEN 'Проход без карты'\n"
            + "           WHEN ee.passdirection = 202 THEN 'Посетитель из черного списка'\n"
            + "           END                                                                           AS \"directionText\",\n"
            + "       o.shortaddress                                                                    AS \"address\",\n"
            + "       o.shortNameInfoService                                                            AS \"shortNameInfoService\",\n"
            + "       checker_cp.firstname || ' ' || checker_cp.secondname || ' ' || checker_cp.surname AS \"childPassChecker\",\n"
            + "       ee.ChildPassChecker                                                               AS \"childPassCheckerMethod\",\n"
            + "       guard_cp.firstname || ' ' || guard_cp.secondname || ' ' || guard_cp.surname       AS \"repName\"\n"
            + "FROM cf_enterevents AS ee\n"
            + "         JOIN cf_orgs AS o ON ee.idoforg = o.idoforg\n"
            + "         JOIN cf_clients AS target ON target.idofclient = ee.idofclient\n"
            + "         LEFT JOIN cf_clients AS guard ON guard.idofclient = ee.guardianid\n"
            + "         LEFT JOIN cf_clients AS checker ON checker.idofclient = ee.childpasscheckerid\n"
            + "         LEFT JOIN cf_persons AS guard_cp ON guard_cp.idofperson = guard.idofperson\n"
            + "         LEFT JOIN cf_persons AS checker_cp ON checker_cp.idofperson = checker.idofperson\n"
            + "WHERE target.contractid = :contractId\n AND ee.evtdatetime BETWEEN :startDate AND :endDate \n"
            + "UNION\n"
            + "SELECT exe.evtdatetime    AS \"dateTime\",\n"
            + "       CASE\n"
            + "           WHEN exe.evttype = 0 AND exe.evtstatus = 0 THEN 1000\n"
            + "           WHEN exe.evttype = 0 AND exe.evtstatus = 1 THEN 1001\n"
            + "           WHEN exe.evttype = 1 AND exe.evtstatus = 0 THEN 2000\n"
            + "           WHEN exe.evttype = 1 AND exe.evtstatus = 1 THEN 2001\n"
            + "           END            AS \"direction\",\n"
            + "       CASE\n"
            + "           WHEN exe.evttype = 0 AND exe.evtstatus = 0 THEN 'Выдан билет в музее'\n"
            + "           WHEN exe.evttype = 0 AND exe.evtstatus = 1 THEN 'Возврат билета в музей'\n"
            + "           WHEN exe.evttype = 1 AND exe.evtstatus = 0 THEN 'Вход в здание учреждения культуры'\n"
            + "           WHEN exe.evttype = 1 AND exe.evtstatus = 1 THEN 'Выход из здания учреждения культуры'\n"
            + "           END            AS \"directionText\",\n"
            + "       exe.address        AS \"address\",\n"
            + "       exe.orgname        AS \"shortNameInfoService\",\n"
            + "       cast(null as text) AS \"childPassChecker\",\n"
            + "       cast(null as integer) AS \"childPassCheckerMethod\",\n"
            + "       cast(null as text) AS \"repName\"\n"
            + "FROM cf_externalevents AS exe\n"
            + "         JOIN cf_clients AS target ON target.idofclient = exe.idofclient\n"
            + "WHERE exe.evtstatus in (0,1) and exe.evttype in (0,1) and target.contractid = :contractId "
            + " AND exe.evtdatetime BETWEEN :startDate AND :endDate "
            + "ORDER BY 1 DESC ",
            countQuery = "WITH count_ee AS (SELECT count(ee.*) AS ct \n"
                    + "                  FROM cf_enterevents AS ee \n"
                    + "                           JOIN cf_clients AS target ON target.idofclient = ee.idofclient \n"
                    + "                  WHERE target.contractid = :contractId \n"
                    + "                    AND ee.evtdatetime BETWEEN :startDate AND :endDate), \n"
                    + "count_exe AS (SELECT count(exe.*) AS ct \n"
                    + "                   FROM cf_externalevents AS exe \n"
                    + "                            JOIN cf_clients AS target ON target.idofclient = exe.idofclient \n"
                    + "                   WHERE exe.evtstatus IN (0, 1) \n"
                    + "                     AND exe.evttype IN (0, 1) \n"
                    + "                     AND target.contractid = :contractId \n"
                    + "                     AND exe.evtdatetime BETWEEN :startDate AND :endDate) \n"
                    + "SELECT count_ee.ct + count_exe.ct from count_ee, count_exe",
            nativeQuery = true)
    Page<Object[]> getEnterEventsByClient(
            @NotNull @Param("contractId") Long contractId,
            @NotNull @Param("startDate") Long startDate,
            @NotNull @Param("endDate") Long endDate,
            @NotNull Pageable pageable);
}
