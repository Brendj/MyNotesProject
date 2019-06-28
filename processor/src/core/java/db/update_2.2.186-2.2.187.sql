--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.187

--таблица истории взаимодействия с РНиПом
--97

ALTER TABLE cf_card_signs ADD COLUMN publickeyprovider bytea;
ALTER TABLE cf_card_signs ADD COLUMN privatekeycard bytea;
ALTER TABLE cf_card_signs ADD COLUMN signtypeprov int4;
ALTER TABLE cf_card_signs ADD COLUMN newtypeprovider bool;