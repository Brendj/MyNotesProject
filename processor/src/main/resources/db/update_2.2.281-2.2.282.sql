/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 282

ALTER TABLE cf_orgs
    ADD COLUMN cardSyncParam              boolean,
    ADD COLUMN photoSyncParam             boolean,
    ADD COLUMN zeroTransactionsSyncParam  boolean,
    ADD COLUMN discountPreordersSyncParam boolean,
    ADD COLUMN foodApplicationSyncParam   boolean;

COMMENT ON COLUMN cf_orgs.cardSyncParam IS 'Директива на проведение синхранизации данных по картам';
COMMENT ON COLUMN cf_orgs.photoSyncParam IS 'Директива на проведение синхранизации фотографий';
COMMENT ON COLUMN cf_orgs.zeroTransactionsSyncParam IS 'Директива на проведение синхранизации нулевых транзакций';
COMMENT ON COLUMN cf_orgs.discountPreordersSyncParam IS 'Директива на проведение синхранизации заявлений на ЛП';
COMMENT ON COLUMN cf_orgs.foodApplicationSyncParam IS 'Директива на проведение синхранизации заявок на питание';

--! ФИНАЛИЗИРОВАН 21.01.2022, НЕ МЕНЯТЬ