/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

create sequence cf_wt_complexes_dishes_repeatable_seq
    maxvalue 2147483648;

create table cf_wt_complexes_dishes_repeatable
(
    idofrelation bigint    not null default nextval('cf_wt_complexes_dishes_repeatable_seq'::regclass),
    idofcomplex  bigint    not null,
    idofdish     bigint    not null,
    deletestate  integer   not null,
    createdate   timestamp not null,
    create_by_id bigint    not null,
    lastupdate   timestamp not null,
    update_by_id bigint    not null,
    constraint cf_wt_complexes_dishes_repeatable_pk primary key (idofrelation),
    constraint cf_wt_complexes_dishes_repeatable_idofcomplex_fk
        foreign key (idofcomplex) references cf_wt_complexes (idofcomplex) on delete cascade,
    constraint cf_wt_complexes_dishes_repeatable_idofdish_fk
        foreign key (idofdish) references cf_wt_dishes (idofdish) on delete cascade,
    constraint cf_wt_complexes_dishes_repeatable_pk_create_by_id_fk
        foreign key (create_by_id) references cf_users (idofuser),
    constraint cf_wt_complexes_dishes_repeatable_pk_update_by_id_fk
        foreign key (update_by_id) references cf_users (idofuser)
);

alter table cf_wt_complexes_dishes_repeatable
    add constraint cf_wt_complexes_dishes_repeatable_idofcomplex_idofdish_unique unique (idofcomplex, idofdish);

alter table cf_wt_org_relation_aud
    add column versionoforggroup bigint;

insert into cf_wt_diet_type
values ((select max(idofdiettype) + 1 from cf_wt_diet_type),
        'Завтрак 2',
        (select max(version) + 1 from cf_wt_diet_type));

insert into cf_wt_diet_type
values ((select max(idofdiettype) + 1 from cf_wt_diet_type),
        'Вода',
        (select max(version) + 1 from cf_wt_diet_type));

insert into cf_wt_diet_type
values ((select max(idofdiettype) + 1 from cf_wt_diet_type),
        'Ужин 2',
        (select max(version) + 1 from cf_wt_diet_type));
