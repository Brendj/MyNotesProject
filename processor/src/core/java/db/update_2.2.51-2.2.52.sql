--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.52

-- суточная проба
alter table cf_goods_requests_positions add column DailySampleCount bigint default null;

CREATE TABLE cf_do_org_current_version (
  IdDOOrgCurrentVersion bigserial NOT NULL,
  ObjectId integer not null,
  IdOfOrg bigint not null,
  LastVersion bigint not null,
  CONSTRAINT cf_do_org_current_version_pk PRIMARY KEY (IdDOOrgCurrentVersion)
);


alter table cf_users add column region varchar(10) default null;