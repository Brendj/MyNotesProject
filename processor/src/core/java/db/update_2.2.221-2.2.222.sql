--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 222

--Для сервиса ЭЖД
DROP VIEW cf_ezd_request;
DROP VIEW cf_ezd_request_special_date;

CREATE OR REPLACE VIEW cf_ezd_request
    AS SELECT row_number() OVER () AS id,
           datas.idoforg,
           datas.guid,
           datas.ekisid,
           datas.usewebarm,
           datas.groupname
       FROM ( SELECT DISTINCT co.idoforg,
                  co.guid,
                  co.ekisid,
                  co.usewebarm,
                  cgto.groupname
              FROM cf_orgs co
                  LEFT JOIN cf_groupnames_to_orgs cgto ON cgto.idoforg = co.idoforg
                  LEFT JOIN cf_clientgroups cg ON cg.idoforg = co.idoforg AND cg.groupname = cgto.groupname
                  LEFT JOIN cf_clients cc ON cc.idofclientgroup = cg.idofclientgroup AND cc.idoforg = co.idoforg
              WHERE co.state = 1 AND co.organizationtype <> 2 AND co.preorderlp IS TRUE AND cg.idofclientgroup < 1100000000 AND cgto.groupname IS NOT NULL AND co.guid IS NOT NULL AND cc.idofclient IS NOT NULL
              ORDER BY co.idoforg, co.ekisid, cgto.groupname) datas;

CREATE OR REPLACE VIEW cf_ezd_request_new
    AS SELECT row_number() OVER () AS id,
           datas.idoforg,
           datas.guid,
           datas.ekisid,
           datas.idofcomplex,
           datas.complexname,
           datas.menudate
       FROM ( SELECT DISTINCT co.idoforg,
                  co.guid,
                  co.ekisid,
                  cci.idofcomplex,
                  cci.complexname,
                  cci.menudate
              FROM cf_orgs co
                  LEFT JOIN cf_complexinfo cci ON cci.idoforg = co.idoforg
              WHERE co.state = 1 AND co.organizationtype <> 2 AND co.preorderlp IS TRUE AND co.guid IS NOT NULL AND cci.modefree = 1 AND cci.menudate > cast((cast(round(date_part(cast('epoch' as text), now())) * 1000 as double precision)) as bigint)
              ORDER BY co.guid, co.ekisid, cci.menudate) datas;

CREATE OR REPLACE VIEW cf_ezd_request_special_date
    AS SELECT row_number() OVER () AS id,
           resulted.date,
           resulted.groupname,
           resulted.idoforg,
           resulted.isweekend
       FROM ( SELECT DISTINCT sda.date,
                  cgto.groupname,
                  sda.idoforg,
                  sda.isweekend
              FROM cf_specialdates sda
                  LEFT JOIN cf_orgs co ON co.idoforg = sda.idoforg
                  LEFT JOIN cf_groupnames_to_orgs cgto ON cgto.idoforg = sda.idoforg
                  LEFT JOIN cf_clientgroups cg ON cg.idoforg = co.idoforg AND cg.groupname = cgto.groupname
                  LEFT JOIN cf_clients cc ON cc.idofclientgroup = cg.idofclientgroup AND cc.idoforg = co.idoforg
              WHERE co.state = 1 AND co.organizationtype <> 2 AND co.preorderlp IS TRUE AND sda.idoforg IS NOT NULL AND sda.deleted = 0 AND sda.date > cast((cast(round(date_part(cast('epoch' as text), now())) * 1000 as double precision)) as bigint) AND cc.idofclient IS NOT NULL
              ORDER BY sda.idoforg, cgto.groupname, sda.date) resulted;

--! ФИНАЛИЗИРОВАН 10.08.2020, НЕ МЕНЯТЬ