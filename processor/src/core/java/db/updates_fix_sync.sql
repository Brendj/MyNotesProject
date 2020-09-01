/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE SEQUENCE cf_specialdates_version_seq;
select setval('cf_specialdates_version_seq', (select coalesce(max(version), 0) + 1 from cf_specialdates));

CREATE SEQUENCE cf_menus_calendar_version_seq;
select setval('cf_menus_calendar_version_seq', (select coalesce(max(version), 0) + 1 from cf_menus_calendar));

CREATE SEQUENCE cf_GroupNames_To_Orgs_version_seq;
select setval('cf_GroupNames_To_Orgs_version_seq', (select coalesce(max(version), 0) + 1 from cf_GroupNames_To_Orgs));

CREATE SEQUENCE cf_zerotransactions_version_seq;
select setval('cf_zerotransactions_version_seq', (select coalesce(max(version), 0) + 1 from cf_zerotransactions));

CREATE SEQUENCE cf_complex_schedules_version_seq;
select setval('cf_complex_schedules_version_seq', (select coalesce(max(version), 0) + 1 from cf_complex_schedules));

CREATE SEQUENCE cf_taloon_preorder_version_seq;
select setval('cf_taloon_preorder_version_seq', (select coalesce(max(version), 0) + 1 from cf_taloon_preorder));

CREATE SEQUENCE cf_taloon_approval_version_seq;
select setval('cf_taloon_approval_version_seq', (select coalesce(max(version), 0) + 1 from cf_taloon_approval));

CREATE SEQUENCE cf_clientbalance_hold_version_seq;
select setval('cf_clientbalance_hold_version_seq', (select coalesce(max(version), 0) + 1 from cf_clientbalance_hold));

CREATE SEQUENCE cf_info_messages_version_seq;
select setval('cf_info_messages_version_seq', (select coalesce(max(version), 0) + 1 from cf_info_messages));

CREATE SEQUENCE cf_card_requests_version_seq;
select setval('cf_card_requests_version_seq', (select coalesce(max(version), 0) + 1 from cf_card_requests));

CREATE SEQUENCE CF_CategoryDiscounts_DSZN_version_seq;
select setval('CF_CategoryDiscounts_DSZN_version_seq', (select coalesce(max(version), 0) + 1 from CF_CategoryDiscounts_DSZN));

CREATE SEQUENCE cf_turnstile_settings_version_seq;
select setval('cf_turnstile_settings_version_seq', (select coalesce(max(version), 0) + 1 from cf_turnstile_settings));

CREATE SEQUENCE cf_externalevents_version_seq;
select setval('cf_externalevents_version_seq', (select coalesce(max(version), 0) + 1 from cf_externalevents));

CREATE SEQUENCE cf_helprequests_version_seq;
select setval('cf_helprequests_version_seq', (select coalesce(max(version), 0) + 1 from cf_helprequests));

CREATE SEQUENCE cf_client_dtiszn_discount_info_version_seq;
select setval('cf_client_dtiszn_discount_info_version_seq', (select coalesce(max(version), 0) + 1 from cf_client_dtiszn_discount_info));