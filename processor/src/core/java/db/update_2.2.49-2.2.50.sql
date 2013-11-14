--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.50
-- Добавление протоколирования контрагента-получателя в БД для возможности последующего фильтра
alter table CF_ReportInfo add column IdOfContragentReceiver bigint default null;
alter table CF_ReportInfo add column ContragentReceiver varchar(128) default null;

-- Добавление поля ОКАТО дялконтрагента
alter table CF_Contragents add column OKATO varchar(11) default '';


-- Возможность указания присутствия ученика в школе для тонкого клиента
alter table cf_temporary_orders add column InBuilding int not null default 2;