--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 210

-- issue 200
ALTER TABLE cf_orgs
    ADD COLUMN menusSyncParam BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN clientsSyncParam BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN orgSettingsSyncParam BOOLEAN NOT NULL DEFAULT FALSE;
