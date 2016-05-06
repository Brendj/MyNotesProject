--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.114

ALTER TABLE cf_clients ADD COLUMN gender integer;
ALTER TABLE cf_clients ADD COLUMN birthDate bigint;
ALTER TABLE cf_clients ADD COLUMN benefitOnAdmission character varying (3000);

--Флаг "Признак получения из синхронизации дружественной организации" у заказа
ALTER TABLE cf_orders ADD COLUMN isfromfriendlyorg boolean NOT NULL DEFAULT false;