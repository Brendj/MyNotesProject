-- Пакет обновлений v 294

INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(10025, cast(cast(extract(epoch from now()) * 1000 as bigint) as varchar));

ALTER TABLE cf_client_dtiszn_discount_info
    ADD COLUMN updated_at bigint;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.updated_at
    IS 'Дата последнего изменения записи в системе источнике данных о льготах';

ALTER TABLE cf_orgs ADD COLUMN disableSocCardsReg BOOLEAN NOT NULL DEFAULT TRUE;

--! ФИНАЛИЗИРОВАН 27.10.2022, НЕ МЕНЯТЬ