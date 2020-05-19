--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 510

alter table cf_categorydiscounts
  add column deletedState integer NOT NULL DEFAULT 0,
  add column deleteDate bigint;
