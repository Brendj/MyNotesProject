--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.168

ALTER TABLE cf_registrychange ALTER COLUMN checkbenefits DROP NOT NULL;
ALTER TABLE cf_registrychange_employee ALTER COLUMN checkbenefits DROP NOT NULL;