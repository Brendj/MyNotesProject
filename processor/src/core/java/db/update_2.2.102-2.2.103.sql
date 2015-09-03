--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.102

CREATE INDEX cf_clients_phone_idx ON cf_clients USING btree (phone COLLATE pg_catalog."default");

CREATE INDEX cf_clients_mobile_idx ON cf_clients USING btree (mobile COLLATE pg_catalog."default");

