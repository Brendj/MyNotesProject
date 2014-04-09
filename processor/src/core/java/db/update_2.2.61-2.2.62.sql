--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.62

ALTER TABLE cf_subscriber_feeding ADD column dateCreateService bigint;
ALTER TABLE cf_subscriber_feeding ADD column reasonWasSuspended CHARACTER VARYING(1024);