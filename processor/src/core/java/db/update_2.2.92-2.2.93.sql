--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.92


-- --! Изменение таблицы cf_account_operations добавление поля IdOfClientPayment
ALTER TABLE cf_account_operations ADD COLUMN IdOfClientPayment bigint;

-- --! Добавление поля notifyviapush в таблицу cf_clients
ALTER TABLE cf_clients ADD COLUMN notifyviapush integer NOT NULL DEFAULT 0;
UPDATE cf_clients SET notifyviapush = 0;