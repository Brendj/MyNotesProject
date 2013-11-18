-- Добавление протоколирования контрагента в БД для возможности последующего фильтра
alter table CF_ReportInfo add column IdOfContragent bigint default null;
alter table CF_ReportInfo add column Contragent varchar(128) default null;