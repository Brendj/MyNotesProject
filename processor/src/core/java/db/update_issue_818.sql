/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */


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