--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.98

ALTER TABLE cf_orgs ADD COLUMN uniqueaddressid bigint;

ALTER TABLE cf_orgregistrychange ADD COLUMN uniqueaddressid bigint, ADD COLUMN uniqueaddressidfrom bigint,
ADD COLUMN inn character varying(32), ADD COLUMN innfrom character varying(32);
ALTER TABLE cf_orgregistrychange_item ADD COLUMN uniqueaddressid bigint, ADD COLUMN uniqueaddressidfrom bigint,
ADD COLUMN inn character varying(32), ADD COLUMN innfrom character varying(32);

--! ФИНАЛИЗИРОВАН (Семенов, 150812) НЕ МЕНЯТЬ