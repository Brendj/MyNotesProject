-- Пакет обновлений issue 1282

ALTER TABLE cf_orgs ADD COLUMN disableSocCardsReg BOOLEAN NOT NULL DEFAULT TRUE;