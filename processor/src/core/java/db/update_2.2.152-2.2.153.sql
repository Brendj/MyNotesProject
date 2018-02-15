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

--обновление групп для задачи EP-1225
UPDATE cf_clientgroups SET idofclientgroup = 1100000090 WHERE groupname = 'Обучающиеся других ОО';
UPDATE cf_clientgroups SET idofclientgroup = 1100000100 WHERE groupname = 'Родители обучающихся других ОО';
UPDATE cf_clientgroups SET idofclientgroup = 1100000110 WHERE groupname = 'Сотрудники других ОО';