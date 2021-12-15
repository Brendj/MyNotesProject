--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.153

--Поле для инициатора заявки на посещение другой ОО + имя группы + код группы из заявки на посещение другой ОО
alter table cf_migrants add column initiator integer not null default 0,
    add column section character varying(256),
    add column resolutioncodegroup bigint;

--Поле флаг заявка на посещение других ОО
alter table cf_orgs add column requestForVisitsToOtherOrg integer not null default 0;

--Поле для инициатора заявки на посещение другой ОО в истории
alter table cf_visitreqresolutionhist add column initiator integer;

--Промежуточная таблица для обработки файлов мигрантов от ЕСЗ
CREATE TABLE cf_esz_migrants_requests
(
  idofeszmigrantsrequest bigserial NOT NULL,
  idofserviceclass bigint NOT NULL,
  groupname character varying(255),
  clientguid character varying(36),
  visitorginn character varying(32),
  visitorgunom integer,
  dateend bigint,
  datelearnstart bigint,
  datelearnend bigint,
  firstname character varying(64),
  surname character varying(128),
  secondname character varying(128),
  idofesz bigint,
  CONSTRAINT cf_esz_migrants_requests_idofeszmigrantsrequests_pk PRIMARY KEY (idofeszmigrantsrequest)
);

--индекс по ид ОО в cf_categoryorg_orgs
CREATE INDEX cf_categoryorg_orgs_idoforg_idx ON cf_categoryorg_orgs USING btree (idoforg);

--оптимизация запросов
CREATE INDEX cf_menu_idoforg_idx ON cf_menu USING btree (idoforg);
CREATE INDEX cf_menu_menudate_idx ON cf_menu USING btree (menudate);
CREATE INDEX cf_clients_lastupdate_idx ON cf_clients USING btree (lastupdate);
CREATE INDEX cf_org_accessories_composite_idx ON cf_org_accessories USING btree (IdOfSourceOrg, AccessoryType, AccessoryNumber);

--обновление групп для задачи EP-1225
--UPDATE cf_clientgroups SET idofclientgroup = 1100000090 WHERE groupname = 'Обучающиеся других ОО';
--UPDATE cf_clientgroups SET idofclientgroup = 1100000100 WHERE groupname = 'Родители обучающихся других ОО';
--UPDATE cf_clientgroups SET idofclientgroup = 1100000110 WHERE groupname = 'Сотрудники других ОО';

--добавление 3-ех предопределенных групп для школ с типами орг 0,1,3 для задачи EP-1224
INSERT INTO cf_clientgroups (idoforg, idofclientgroup, groupname)
  SELECT cfc.idoforg, 1100000090 AS idofclientgroup, 'Обучающиеся других ОО' AS groupname
  FROM cf_clientgroups cfc LEFT JOIN (SELECT idoforg    FROM cf_orgs
  WHERE organizationtype NOT IN (2) AND idoforg NOT IN (
    SELECT cfc.idoforg FROM cf_clientgroups cfc LEFT JOIN cf_orgs cfo ON cfc.idoforg = cfo.idoforg
    WHERE cfo.idoforg <> 2 AND cfc.groupname IN ('Обучающиеся других ОО', 'Родители обучающихся других ОО', 'Сотрудники других ОО')
    GROUP BY cfc.idoforg)) AS org ON cfc.idoforg = org.idoforg GROUP BY cfc.idoforg;

INSERT INTO cf_clientgroups (idoforg, idofclientgroup, groupname)
  SELECT cfc.idoforg, 1100000100 AS idofclientgroup, 'Родители обучающихся других ОО' AS groupname
  FROM cf_clientgroups cfc LEFT JOIN (SELECT idoforg    FROM cf_orgs
  WHERE organizationtype NOT IN (2) AND idoforg NOT IN (
    SELECT cfc.idoforg FROM cf_clientgroups cfc LEFT JOIN cf_orgs cfo ON cfc.idoforg = cfo.idoforg
    WHERE cfo.idoforg <> 2 AND cfc.groupname IN ('Обучающиеся других ОО', 'Родители обучающихся других ОО', 'Сотрудники других ОО')
    GROUP BY cfc.idoforg)) AS org ON cfc.idoforg = org.idoforg GROUP BY cfc.idoforg;

INSERT INTO cf_clientgroups (idoforg, idofclientgroup, groupname)
  SELECT cfc.idoforg, 1100000110 AS idofclientgroup, 'Сотрудники других ОО' AS groupname
  FROM cf_clientgroups cfc LEFT JOIN (SELECT idoforg    FROM cf_orgs
  WHERE organizationtype NOT IN (2) AND idoforg NOT IN (
    SELECT cfc.idoforg FROM cf_clientgroups cfc LEFT JOIN cf_orgs cfo ON cfc.idoforg = cfo.idoforg
    WHERE cfo.idoforg <> 2 AND cfc.groupname IN ('Обучающиеся других ОО', 'Родители обучающихся других ОО', 'Сотрудники других ОО')
    GROUP BY cfc.idoforg)) AS org ON cfc.idoforg = org.idoforg GROUP BY cfc.idoforg;

--! ФИНАЛИЗИРОВАН 26.02.2018, НЕ МЕНЯТЬ