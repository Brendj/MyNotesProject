--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 228

ALTER TABLE cf_discountrules
    ADD COLUMN deletedstate boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_discountrules.deletedstate IS 'Признак удаления (true - удален, false - не удален)';

ALTER TABLE cf_wt_discountrules
    ADD COLUMN deletedstate boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_wt_discountrules.deletedstate IS 'Признак удаления (true - удален, false - не удален)';

-- Перенос существующих категорий
ALTER TABLE cf_wt_category_items
    RENAME TO cf_wt_categories;
ALTER TABLE cf_wt_categories
    RENAME COLUMN idofcategoryitem TO idofcategory;

-- Создание подкатегорий
create table cf_wt_category_items
(
    idofcategoryitem bigserial         not null
        constraint cf_wt_category_items_pk_2
        primary key,
    createdate       timestamp         not null,
    lastupdate       timestamp         not null,
    version          bigint  default 0 not null,
    idofuser         bigint            not null
        constraint cf_wt_category_items_fk_2
        references cf_users,
    guid            varchar(36)       not null unique,
    description      varchar(255)      not null unique
        constraint cf_wt_category_items_description_check_2
        CHECK (description NOT SIMILAR TO ' *'),
    deletestate      integer default 0,
    idofcategory bigint not null references cf_wt_categories(idofcategory)
);

comment on column cf_wt_category_items.idofcategoryitem is 'ID записи';
comment on column cf_wt_category_items.createdate is 'Дата создания';
comment on column cf_wt_category_items.lastupdate is 'Дата последнего обновления';
comment on column cf_wt_category_items.version is 'Версия (для АРМ)';
comment on column cf_wt_category_items.idofuser is 'ID создателя записи';
comment on column cf_wt_category_items.guid is 'GUID (для АРМ)';
comment on column cf_wt_category_items.description is 'Описание элемента';

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

--! ФИНАЛИЗИРОВАН 01.09.2020, НЕ МЕНЯТЬ