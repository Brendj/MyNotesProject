--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.83

--! Отмена ограничения на обязательность заполнения LastRNIPUpdate
alter table cf_contragents alter column lastrnipupdate DROP NOT NULL;

--! Добавление признаков и инф за кого сделали отметку
ALTER TABLE cf_enterevents ADD COLUMN childpasschecker INTEGER;
ALTER TABLE cf_enterevents ADD COLUMN childpasscheckerid BIGINT;