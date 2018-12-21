--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.170

alter table cf_registrychange
  add column parallel varchar(255),     -- Цифра класса (параллель) из таблицы institution_groups (parallel)
  add column parallelfrom varchar(255), -- Цифра класса из таблицы cf_clients (parallel)
  add column dszn_date_begin bigint,    -- Дата начала ДСЗН из таблицы person_benefits (dszn_date_begin)
  add column dszn_date_beginfrom bigint,-- Дата начала ДСЗН из таблицы в cf_client_dtiszn_discount_info(datestart)
  add column dszn_date_end bigint,      -- Дата окончания ДСЗН из таблицы в person_benefits(dszn_date_end)
  add column dszn_date_endfrom bigint;  -- Дата окончания ДСЗН из таблицы в cf_client_dtiszn_discount_info(dateend)

-- Добавление цифра класса (параллель)
alter table cf_clients
  add column parallel varchar(255);
