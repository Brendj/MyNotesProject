-- Пакет обновлений issue 1327

ALTER TABLE cf_orgs ADD COLUMN usePlanWebArm BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN cf_orgs.usePlanWebArm IS 'Включение плана питания на тонком клиенте';