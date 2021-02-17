/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */


ALTER TABLE cf_orgs
    ADD COLUMN useLongCardNo BOOLEAN;

COMMENT ON COLUMN cf_orgs.useLongCardNo IS 'Режим работы с длинными UID карт';