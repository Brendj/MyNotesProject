--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 354

alter table cf_orgregistrychange_item
  add column ekisId bigint,
  add column ekisIdFrom bigint;

alter table cf_orgregistrychange
  add column ekisId bigint,
  add column ekisIdFrom bigint;

