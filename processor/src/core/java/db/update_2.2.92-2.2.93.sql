--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.92


-- --! Изменение таблицы cf_account_operations добавление поля IdOfClientPayment
ALTER TABLE cf_account_operations ADD COLUMN IdOfClientPayment bigint;

-- --! Добавление поля notifyviapush в таблицу cf_clients
ALTER TABLE cf_clients ADD COLUMN notifyviapush smallint NOT NULL DEFAULT 0;