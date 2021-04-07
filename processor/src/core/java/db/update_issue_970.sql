/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */


ALTER TABLE cf_orgs
    ADD COLUMN governmentContract BOOLEAN;

COMMENT ON COLUMN CF_Orgs.governmentContract IS 'Наличие государственного контракта';