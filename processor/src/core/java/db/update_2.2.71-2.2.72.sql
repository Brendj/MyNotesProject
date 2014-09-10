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

