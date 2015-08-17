--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.99

ALTER TABLE cf_visitors ADD COLUMN freedocname character varying(1024);
ALTER TABLE cf_visitors ADD COLUMN freedocnumber character varying(50);
ALTER TABLE cf_visitors ADD COLUMN freedocdate bigint;
