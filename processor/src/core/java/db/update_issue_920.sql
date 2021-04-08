/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */


ALTER TABLE cf_enterevents ADD COLUMN longCardId BIGINT;

COMMENT ON COLUMN CF_EnterEvents.longCardId IS 'Длинный UID карты';