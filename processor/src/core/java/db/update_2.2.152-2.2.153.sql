--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.153

--Поле для инициатора заявки на посещение другой ОО
alter table cf_migrants add column initiator integer not null default 0;

--Поле флаг заявка на посещение других ОО
alter table cf_orgs add column requestForVisitsToOtherOrg integer not null default 0;