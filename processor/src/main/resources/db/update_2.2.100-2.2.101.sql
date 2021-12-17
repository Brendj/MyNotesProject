--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.100

--Удаление полей
alter table cf_orgs drop column IdOfPacket
, drop column lastsucbalancesync
, drop column clientversion
, drop column remoteaddress
, drop column lastunsucbalancesync;

--! ФИНАЛИЗИРОВАН (Семенов, 150825) НЕ МЕНЯТЬ