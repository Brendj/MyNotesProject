/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

alter table cf_clients
add column meshguid varchar(36);

COMMENT ON COLUMN cf_clients.meshguid is 'GUID в системе МЭШ.Контингент';