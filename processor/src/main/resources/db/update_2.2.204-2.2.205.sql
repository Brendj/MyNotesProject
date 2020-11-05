--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 205

-- 239: добавление поля в карточку клиента
ALTER TABLE cf_clients add column confirmvisualrecognition integer NOT NULL DEFAULT 0; -- Согласие на видеоидентификацию

COMMENT ON COLUMN cf_clients.confirmvisualrecognition IS 'Согласие на видеоидентификацию';

alter table cf_orgs add column preorderSyncParam integer;

--! ФИНАЛИЗИРОВАН 30.12.2019, НЕ МЕНЯТЬ