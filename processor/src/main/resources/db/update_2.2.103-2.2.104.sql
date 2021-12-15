--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.103

ALTER TABLE cf_clientsms ADD COLUMN evtdate bigint;
ALTER TABLE cf_clientsms ADD COLUMN idoforg bigint;
ALTER TABLE cf_clientsms_resending ADD COLUMN evtdate bigint;
ALTER TABLE cf_clientsms_resending ADD COLUMN idoforg bigint;

--! ФИНАЛИЗИРОВАН (Семенов, 150915) НЕ МЕНЯТЬ
