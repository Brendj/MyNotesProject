/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

drop trigger complex_org_inc_ver on cf_wt_complexes_org;
drop trigger menu_org_inc_ver on cf_wt_menu_org;
drop trigger org_group_org_inc_ver on cf_wt_org_group_relations;
drop function complex_org_inc_ver;
drop function menu_org_inc_ver;
drop function org_group_org_inc_ver();
drop trigger org_group_org_inc_ver on cf_wt_org_groups;

alter table cf_wt_org_relation_aud
    add column versionofcomplex bigint,
    add column versionofmenu    bigint;