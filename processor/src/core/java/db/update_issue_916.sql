--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 916
ALTER TABLE cf_orgregistrychange ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN addressfrom TYPE character varying(512),
  ALTER COLUMN short_address TYPE character varying(512),
  ALTER COLUMN short_addressfrom TYPE character varying(512);

ALTER TABLE cf_orgregistrychange_item ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN addressfrom TYPE character varying(512),
  ALTER COLUMN short_address TYPE character varying(512),
  ALTER COLUMN short_addressfrom TYPE character varying(512);

ALTER TABLE cf_orgs ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN shortaddress TYPE character varying(512);
