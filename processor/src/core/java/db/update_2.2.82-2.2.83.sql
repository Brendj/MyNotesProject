--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.82

--! расщирим длину колонки до от 10 до 128 символов
ALTER TABLE cf_users ALTER COLUMN region TYPE character varying(128);