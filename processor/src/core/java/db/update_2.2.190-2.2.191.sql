--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 191

--Расширяем поле cf_bank_subscriptions.paymentid
ALTER TABLE cf_bank_subscriptions ALTER COLUMN paymentid TYPE character varying(36);