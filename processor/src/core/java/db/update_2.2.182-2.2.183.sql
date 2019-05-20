--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.183

alter table cf_subscriber_feeding
  add column idOfOrgLastChange bigint;

alter table cf_clients_cycle_diagrams
  add column idOfOrgLastChange bigint;