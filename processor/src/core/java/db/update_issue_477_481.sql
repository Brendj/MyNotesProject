/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE UNIQUE INDEX cf_client_meshguid_uk ON cf_clients (meshguid);

ALTER TABLE CF_RegistryChange ADD COLUMN meshGUID varchar(36);