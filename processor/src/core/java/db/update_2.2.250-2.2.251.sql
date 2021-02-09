/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

--895
CREATE INDEX cf_applications_for_food_client_idx ON cf_applications_for_food USING btree (idofclient);
CREATE INDEX cf_applications_for_food_clientStatusArchived_idx ON cf_applications_for_food USING btree (idofclient,status,archived);

--! ФИНАЛИЗИРОВАН 04.02.2021, НЕ МЕНЯТЬ