--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 354

alter table cf_orgregistrychange_item
  add column ekisId bigint,
  add column ekisIdFrom bigint,
  add column egissoId character varying(128),
  add column egissoIdFrom character varying(128);

alter table cf_orgregistrychange
  add column ekisId bigint,
  add column ekisIdFrom bigint,
  add column egissoId character varying(128),
  add column egissoIdFrom character varying(128);

alter table cf_orgs add column egissoId character varying(128);