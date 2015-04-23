--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.96


ALTER TABLE cf_cards ADD COLUMN IdOfOrg BIGINT;

ALTER TABLE cf_enterevents ADD COLUMN IdOfClientGroup BIGINT;
