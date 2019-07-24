--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.188

--188
ALTER TABLE cf_externalevents ADD COLUMN address varchar;
ALTER TABLE cf_externalevents ADD COLUMN orgShortName varchar;

--! ФИНАЛИЗИРОВАН 24.07.2019, НЕ МЕНЯТЬ