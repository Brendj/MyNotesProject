--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 157

--Размер БД в Арме ОО
ALTER TABLE cf_orgs_sync ADD COLUMN databaseSize numeric (10, 2);