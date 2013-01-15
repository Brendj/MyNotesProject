--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся

-- Пакет обновлений 2.2.32
-- Добавлена настройка текстого сообщения для принтера
--! в таблицу cf_ecafesettings добавлено строковое необязательное значение. занесен в ecafe_processor_derby_postgre.sql
ALTER TABLE CF_ECafeSettings DROP COLUMN IF EXISTS SettingText;
ALTER TABLE CF_ECafeSettings ADD COLUMN SettingText character varying(128);
