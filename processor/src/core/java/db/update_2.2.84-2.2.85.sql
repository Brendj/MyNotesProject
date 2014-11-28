--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.84

--! Добавление признаков и инф за кого сделали отметку
ALTER TABLE cf_enterevents ADD COLUMN childpasschecker INTEGER;
ALTER TABLE cf_enterevents ADD COLUMN childpasscheckerid BIGINT;