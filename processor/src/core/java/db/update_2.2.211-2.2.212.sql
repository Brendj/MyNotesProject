--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 212

create table cf_kf_organization_registry
(
    global_id bigint not null
        constraint cf_kf_organization_registry_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    system_object_id varchar(255),
    arhiv boolean,
    director varchar(512),
    egisso_id varchar(128),
    eo_id bigint,
    full_name varchar(512),
    inn bigint,
    ogrn varchar(64),
    short_name varchar(512),
    type2_id integer,
    subordination_id integer
);

create table cf_kf_eo_address
(
    global_id bigint not null
        constraint cf_kf_eo_address_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    system_object_id varchar(255),
    address varchar(512),
    address_asur varchar(512),
    address_description varchar(1024),
    eo_id bigint,
    is_bti boolean,
    queue_from_bti varchar(128),
    queue_not_from_bti varchar(128),
    unad bigint,
    unique_address_id bigint,
    unom bigint,
    global_object_id bigint
        constraint cf_kf_organization_eo_address
            references cf_kf_organization_registry,
    area varchar(100),
    district varchar(100)
);

create table cf_kf_ct_parallel
(
    global_id bigint not null
        constraint cf_kf_ct_parallel_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    is_deleted integer default 0,
    system_object_id bigint,
    title varchar(255),
    id integer
);

create table cf_kf_ct_legal_represent
(
    global_id bigint not null
        constraint cf_kf_ct_legal_represent_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    is_deleted integer default 0,
    system_object_id bigint,
    title varchar(255),
    id integer
);

create table cf_kf_ct_contact_type
(
    global_id bigint not null
        constraint cf_kf_ct_contract_type_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    is_deleted integer default 0,
    system_object_id bigint,
    title varchar(255),
    id integer
);

create table cf_kf_ct_cityareas
(
    global_id bigint not null
        constraint cf_kf_ct_cityareas_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    is_deleted integer default 0,
    system_object_id bigint,
    title varchar(255),
    id integer,
    parent_id varchar(9),
    bti_id varchar(9),
    bti_title varchar(255)
);

create table cf_kf_ct_admin_district
(
    global_id bigint not null
        constraint cf_kf_ct_admin_district_pkey
            primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    is_deleted integer default 0,
    system_object_id bigint,
    title varchar(255)
);
--! ФИНАЛИЗИРОВАН 05.03.2020, НЕ МЕНЯТЬ