/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

alter table cf_mh_entity_changes
    add column uid varchar(36);

create table cf_mh_classes
(
    id               bigserial primary key,
    uid              varchar(36)  not null unique,
    organizationid   bigint,
    name             varchar(128) not null,
    parallelId       integer,
    educationStageId integer,
    createdate       timestamp    not null,
    lastupdate       timestamp    not null
);

alter table cf_mh_persons
    add column idofclass BIGINT references cf_mh_classes (id) on update no action on delete set null;