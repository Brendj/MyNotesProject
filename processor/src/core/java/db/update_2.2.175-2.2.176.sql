--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.176

--Новые поля в производственном календаре
ALTER TABLE cf_production_calendar
  add column flag integer NOT NULL DEFAULT 1,
  add column version bigint NOT NULL DEFAULT 0;
