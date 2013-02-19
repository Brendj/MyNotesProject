--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.36
ALTER TABLE cf_qa_questionaries ADD COLUMN startdate bigint;
ALTER TABLE cf_qa_questionaries ADD COLUMN enddate bigint;
