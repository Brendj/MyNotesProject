--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.181

-- Добавление новой колонки "Версия MySQL"
ALTER TABLE cf_orgs_sync ADD COLUMN sqlServerVersion VARCHAR(20);