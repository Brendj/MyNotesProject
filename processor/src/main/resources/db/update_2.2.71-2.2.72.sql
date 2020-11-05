--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.72

-- Добавление поля даты последней загрузки платежей из РНИП
ALTER TABLE cf_contragents ADD COLUMN LastRNIPUpdate VARCHAR(15) NOT NULL DEFAULT '';

-- Новое условие уникальности по полям
ALTER TABLE cf_instances DROP CONSTRAINT cf_instances_invbook_invnumber_key;
ALTER TABLE cf_instances ADD CONSTRAINT cf_instances_invbook_invnumber_key UNIQUE (invbook , invnumber, deletedstate);

ALTER TABLE cf_subscriber_feeding RENAME COLUMN lastdatepauseservice to lastdatepausesubscription;
ALTER TABLE cf_subscriber_feeding RENAME COLUMN dateactivateservice to dateactivatesubscription;

-- Синхронизация переделывание полей с требованиями для недель
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN mondayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN tuesdayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN wednesdayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN thursdayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN fridayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN saturdayprice;
ALTER TABLE cf_clients_cycle_diagrams DROP COLUMN sundayprice;

ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN mondayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN tuesdayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN wednesdayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN thursdayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN fridayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN saturdayprice character varying(255);
ALTER TABLE cf_clients_cycle_diagrams ADD COLUMN sundayprice character varying(255);


--! ФИНАЛИЗИРОВАН (Сунгатов, 140918) НЕ МЕНЯТЬ
