--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.36
--! Добавлена дата отображения опросника если пусто то отобразится во все дни
ALTER TABLE cf_qa_questionaries ADD COLUMN viewdate bigint;

--!
DROP TABLE IF EXISTS cf_qa_questionaryresultbyorg;

ALTER TABLE CF_ClientSms ALTER COLUMN IdOfSms type CHAR(40);