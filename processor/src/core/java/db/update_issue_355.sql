--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 354

alter table cf_orgregistrychange_item
  add column ekisId bigint,
  add column ekisIdFrom bigint,
  add column egissoId character varying(128),
  add column egissoIdFrom character varying(128),
  add column municipal_district character varying(256),
  add column municipal_districtFrom character varying(256),
  add column short_address character varying(128),
  add column short_addressFrom character varying(128);

alter table cf_orgregistrychange
  add column ekisId bigint,
  add column ekisIdFrom bigint,
  add column egissoId character varying(128),
  add column egissoIdFrom character varying(128),
  add column municipal_district character varying(256),
  add column municipal_districtFrom character varying(256),
  add column short_address character varying(128),
  add column short_addressFrom character varying(128);

alter table cf_orgs add column egissoId character varying(128),
  add column municipal_district character varying(256);

COMMENT ON COLUMN cf_orgs.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgs.municipal_district IS 'Муниципальный округ';

COMMENT ON COLUMN cf_orgregistrychange.ekisId IS 'Ид ЕКИС';
COMMENT ON COLUMN cf_orgregistrychange.ekisIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgregistrychange.egissoIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.municipal_district IS 'Муниципальный округ';
COMMENT ON COLUMN cf_orgregistrychange.municipal_districtFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.short_address IS 'Короткий адрес';
COMMENT ON COLUMN cf_orgregistrychange.short_addressFrom IS 'Ид ЕКИС в ИСПП';

COMMENT ON COLUMN cf_orgregistrychange_item.ekisId IS 'Ид ЕКИС';
COMMENT ON COLUMN cf_orgregistrychange_item.ekisIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgregistrychange_item.egissoIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.municipal_district IS 'Муниципальный округ';
COMMENT ON COLUMN cf_orgregistrychange_item.municipal_districtFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.short_address IS 'Короткий адрес';
COMMENT ON COLUMN cf_orgregistrychange_item.short_addressFrom IS 'Ид ЕКИС в ИСПП';