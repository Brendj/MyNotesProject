--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 192

--190
ALTER TABLE cf_clients ADD COLUMN userop bool;

--195
ALTER TABLE cf_orgs ADD COLUMN participantop bool;

--! ФИНАЛИЗИРОВАН 10.09.2019, НЕ МЕНЯТЬ