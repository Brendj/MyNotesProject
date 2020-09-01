/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE SEQUENCE cf_specialdates_version_seq;
select setval('cf_specialdates_version_seq', (select coalesce(max(version), 0) + 1 from cf_specialdates));