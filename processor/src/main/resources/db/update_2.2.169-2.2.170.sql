--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.170

-- Новые поля в таблице разногласий по сверке
-- Цифра класса (параллель) из таблицы institution_groups (parallel)
-- Цифра класса из таблицы cf_clients (parallel)
-- Дата начала ДСЗН из таблицы person_benefits (dszn_date_begin)
-- Дата начала ДСЗН из таблицы в cf_client_dtiszn_discount_info(datestart)
-- Дата окончания ДСЗН из таблицы в person_benefits(dszn_date_end)
-- Дата окончания ДСЗН из таблицы в cf_client_dtiszn_discount_info(dateend)

alter table cf_registrychange
  add column parallel varchar(255),
  add column parallelfrom varchar(255),
  add column dszn_date_begin bigint,
  add column dszn_date_beginfrom bigint,
  add column dszn_date_end bigint,
  add column dszn_date_endfrom bigint;

-- Добавление цифра класса (параллель)
alter table cf_clients
  add column parallel varchar(255);

--! ФИНАЛИЗИРОВАН 21.12.2018, НЕ МЕНЯТЬ