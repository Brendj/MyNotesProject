--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.181

--97
ALTER TABLE cf_card_signs ADD COLUMN publickeyprovider bytea;
ALTER TABLE cf_card_signs ADD COLUMN privatekeycard bytea;
ALTER TABLE cf_card_signs ADD COLUMN signtypeprov int4;
ALTER TABLE cf_card_signs ADD COLUMN newtypeprovider bool;

-- Добавление новой колонки "Версия MySQL"
ALTER TABLE cf_orgs_sync ADD COLUMN sqlServerVersion VARCHAR(20);