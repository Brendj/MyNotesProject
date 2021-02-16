/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

ALTER TABLE cf_cards
    ADD COLUMN longCardNo BIGINT;
CREATE INDEX cf_cards_longCardNo_idx ON cf_cards(longCardNo);

COMMENT ON COLUMN cf_cards.longCardNo IS 'Длинный UID-идентификатор карты';