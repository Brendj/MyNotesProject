--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.71
ALTER TABLE cf_orgs ADD COLUMN DisableEditingClientsFromAISReestr  integer NOT NULL DEFAULT 0;
update cf_orgs set DisableEditingClientsFromAISReestr =0;
