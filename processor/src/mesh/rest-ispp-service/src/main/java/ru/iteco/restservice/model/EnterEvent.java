/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model;

import ru.iteco.restservice.model.compositid.EnterEventId;
import ru.iteco.restservice.model.enums.PassdirectionType;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_enterevents")
@SqlResultSetMappings({
        @SqlResultSetMapping(
                name = "enterEventsResponse",
                classes = {
                        @ConstructorResult(
                                targetClass = ru.iteco.restservice.controller.enterevents.responsedto.EnterEventResponseDTO.class,
                                columns = {
                                        @ColumnResult(name = "dateTime", type = Long.class),
                                        @ColumnResult(name = "direction", type = Integer.class),
                                        @ColumnResult(name = "directionText", type = String.class),
                                        @ColumnResult(name = "address", type = String.class),
                                        @ColumnResult(name = "shortNameInfoService", type = String.class),
                                        @ColumnResult(name = "childPassChecker", type = String.class),
                                        @ColumnResult(name = "childPassCheckerMethod", type = Integer.class),
                                        @ColumnResult(name = "repName", type = String.class),
                                }
                        )
                }
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                name = "getEnterEvents",
                resultSetMapping = "enterEventsResponse",
                query = "SELECT ee.evtdatetime                                                                    AS \"dateTime\",\n"
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
                        + "FROM cf_enterevents ee\n"
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
                        + "FROM cf_externalevents exe\n"
                        + "         JOIN cf_clients AS target ON target.idofclient = exe.idofclient\n"
                        + "WHERE exe.evtstatus in (0,1) and exe.evttype in (0,1) and target.contractid = :contractId "
                        + " AND exe.evtdatetime BETWEEN :startDate AND :endDate"
        )
})
public class EnterEvent {
    @EmbeddedId
    private EnterEventId enterEventId;

    @Column(name = "passdirection")
    private PassdirectionType passDirection;

    @ManyToOne
    @JoinColumn(name = "idoforg", insertable = false, updatable = false)
    private Org org;

    @ManyToOne
    @JoinColumn(name = "idofclient", insertable = false, updatable = false)
    private Client client;

    @ManyToOne
    @JoinColumn(name = "childpasscheckerid", insertable = false, updatable = false)
    private Client passChecker;

    @ManyToOne
    @JoinColumn(name = "guardianid", insertable = false, updatable = false)
    private Client guardian;

    public EnterEventId getEnterEventId() {
        return enterEventId;
    }

    public void setEnterEventId(EnterEventId enterEventId) {
        this.enterEventId = enterEventId;
    }

    public PassdirectionType getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(PassdirectionType passDirection) {
        this.passDirection = passDirection;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Client getPassChecker() {
        return passChecker;
    }

    public void setPassChecker(Client passChecker) {
        this.passChecker = passChecker;
    }

    public Client getGuardian() {
        return guardian;
    }

    public void setGuardian(Client guardian) {
        this.guardian = guardian;
    }

    @Override
    public int hashCode() {
        return Objects.hash(enterEventId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EnterEvent that = (EnterEvent) o;
        return Objects.equals(enterEventId, that.enterEventId);
    }
}
