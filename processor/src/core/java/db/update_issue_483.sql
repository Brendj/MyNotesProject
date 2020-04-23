--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 483

ALTER TABLE cf_kf_organization_registry
  ADD COLUMN subordination_value VARCHAR(512),
  ADD COLUMN founder VARCHAR(512),
  DROP COLUMN subordination_id;

ALTER TABLE cf_orgs
  ADD COLUMN subordination VARCHAR(512),
  ADD COLUMN founder VARCHAR(512),
  ADD COLUMN organizationIdFromNSI BIGINT;

WITH ids AS (
    SELECT global_id AS organizationIdFromNSI,
           eo_id     AS ekisID
    FROM cf_kf_organization_registry
)
UPDATE cf_orgs AS o
SET organizationIdFromNSI = ids.organizationIdFromNSI
FROM ids
WHERE o.ekisid = ids.ekisID;

ALTER TABLE cf_orgregistrychange
  ADD COLUMN subordination VARCHAR(512),
  ADD COLUMN founder VARCHAR(512),
  ADD COLUMN subordinationFrom VARCHAR(512),
  ADD COLUMN founderFrom VARCHAR(512);

ALTER TABLE cf_orgregistrychange_item
  ADD COLUMN subordination VARCHAR(512),
  ADD COLUMN founder VARCHAR(512),
  ADD COLUMN subordinationFrom VARCHAR(512),
  ADD COLUMN founderFrom VARCHAR(512);
