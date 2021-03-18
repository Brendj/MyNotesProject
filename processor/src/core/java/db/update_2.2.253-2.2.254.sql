--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 254

-- Миграции по задаче issue 930
alter table cf_orgregistrychange add column globalId bigint,
                                 add column globalIdFrom bigint;

alter table cf_orgregistrychange_item add column globalId bigint,
                                      add column globalIdFrom bigint;

COMMENT ON COLUMN cf_orgregistrychange.globalId IS 'Идентификатор НСИ-3';
COMMENT ON COLUMN cf_orgregistrychange.globalIdFrom IS 'Идентификатор НСИ-3 в ИСПП';
COMMENT ON COLUMN cf_orgregistrychange_item.globalId IS 'Идентификатор НСИ-3';
COMMENT ON COLUMN cf_orgregistrychange_item.globalIdFrom IS 'Идентификатор НСИ-3 в ИСПП';

-- Миграции БД при обновлении веб АРМа ПП до v1.25
drop trigger complex_org_inc_ver on cf_wt_complexes_org;
drop trigger menu_org_inc_ver on cf_wt_menu_org;
drop trigger org_group_org_inc_ver on cf_wt_org_group_relations;
drop trigger org_group_org_inc_ver on cf_wt_org_groups;
drop function complex_org_inc_ver();
drop function menu_org_inc_ver();
drop function org_group_org_inc_ver();

alter table cf_wt_org_relation_aud
    add column versionofcomplex bigint,
    add column versionofmenu    bigint;

--! ФИНАЛИЗИРОВАН 04.03.2021, НЕ МЕНЯТЬ