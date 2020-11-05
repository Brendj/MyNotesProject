--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 207

CREATE TABLE cf_regularpayment_status
(
  idofregularpaymentstatus bigserial NOT NULL,
  idofregularpayment bigint NOT NULL,
  errorcode integer,
  description character varying(255),
  statusdate bigint,
  createddate bigint NOT NULL,
  CONSTRAINT cf_regularpayment_status_pk PRIMARY KEY (idofregularpaymentstatus),
  CONSTRAINT cf_regularpayment_status_idofregularpayment_fk FOREIGN KEY (idofregularpayment)
  REFERENCES cf_regular_payments (idofpayment) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

alter table cf_bank_subscriptions add column mobile character varying(32);

alter table cf_regular_payments add column idofclientpayment bigint,
  add column errorcode integer,
  add column errordesc character varying(200);

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

alter table cf_registry_file add column ekisId character varying(32);

COMMENT ON COLUMN cf_registry_file.ekisId IS 'Ид ЕКИС';

COMMENT ON COLUMN cf_orgs.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgs.municipal_district IS 'Муниципальный округ (район)';

COMMENT ON COLUMN cf_orgregistrychange.ekisId IS 'Ид ЕКИС';
COMMENT ON COLUMN cf_orgregistrychange.ekisIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgregistrychange.egissoIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.municipal_district IS 'Муниципальный округ (район)';
COMMENT ON COLUMN cf_orgregistrychange.municipal_districtFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange.short_address IS 'Короткий адрес';
COMMENT ON COLUMN cf_orgregistrychange.short_addressFrom IS 'Ид ЕКИС в ИСПП';

COMMENT ON COLUMN cf_orgregistrychange_item.ekisId IS 'Ид ЕКИС';
COMMENT ON COLUMN cf_orgregistrychange_item.ekisIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.egissoId IS 'Ид ЕГИССО';
COMMENT ON COLUMN cf_orgregistrychange_item.egissoIdFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.municipal_district IS 'Муниципальный округ (район)';
COMMENT ON COLUMN cf_orgregistrychange_item.municipal_districtFrom IS 'Ид ЕКИС в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.short_address IS 'Короткий адрес';
COMMENT ON COLUMN cf_orgregistrychange_item.short_addressFrom IS 'Ид ЕКИС в ИСПП';


--! ФИНАЛИЗИРОВАН 28.01.2020, НЕ МЕНЯТЬ