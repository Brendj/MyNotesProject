--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Добавление протоколирования контрагента в БД для возможности последующего фильтра
alter table CF_ReportInfo add column IdOfContragent bigint default null;
alter table CF_ReportInfo add column Contragent varchar(128) default null;

-- Добавлено поле ИНН в накладной
ALTER TABLE CF_WayBills ADD COLUMN inn character varying(32);