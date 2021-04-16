-- Пакет обновлений 260

-- Изменение структуры таблиц по новой логике
ALTER TABLE cf_hardware_settings_mt
  ADD COLUMN ipHost text;

UPDATE cf_hardware_settings_mt AS hsmt
SET ipHost = (SELECT ipHost
              FROM cf_hardware_settings AS hs
              WHERE hs.idoforg = hsmt.idoforg
                    AND hs.idofhardwaresetting = hsmt.idofhardwaresetting);

ALTER TABLE cf_hardware_settings_mt
  DROP CONSTRAINT cf_hardware_settigs_mt_idofhardwaresetting_fk;

ALTER TABLE cf_hardware_settings
  DROP CONSTRAINT cf_hardwarese_settings_unique_ip_org,
  DROP CONSTRAINT cf_hardware_settings_idofhardwaresetting_pk,
  ADD CONSTRAINT cf_hardware_settings_idofhardwaresetting_pk PRIMARY KEY (idoforg, iphost);

ALTER TABLE cf_hardware_settings_mt
  ADD CONSTRAINT cf_hardware_settigs_mt_hostIp_fk FOREIGN KEY (idoforg, ipHost) REFERENCES cf_hardware_settings (idoforg, iphost);

COMMENT ON COLUMN cf_hardware_settings_mt.ipHost IS 'IP родительского хоста';

CREATE SEQUENCE cf_specialdates_Id_Gen_seq INCREMENT BY 32;
select setval('cf_specialdates_Id_Gen_seq', (select coalesce(max(IdOfSpecialDate), 0) + 1 from cf_specialdates));

CREATE SEQUENCE CF_ComplexInfo_Id_Gen_seq INCREMENT BY 128;
select setval('CF_ComplexInfo_Id_Gen_seq', (select coalesce(max(IdOfComplexInfo), 0) + 1 from CF_ComplexInfo));

CREATE SEQUENCE CF_ComplexInfoDetail_Id_Gen_seq INCREMENT BY 256;
select setval('CF_ComplexInfoDetail_Id_Gen_seq', (select coalesce(max(IdOfComplexInfoDetail), 0) + 1 from CF_ComplexInfoDetail));

--! ФИНАЛИЗИРОВАН 16.04.2021, НЕ МЕНЯТЬ