-- Миграции БД при обновлении веб АРМа ПП до v 1.26

create sequence cf_wt_complexes_dishes_repeatable_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

create table cf_wt_complexes_dishes_repeatable
(
    idofrelation bigint    not null default nextval('cf_wt_complexes_dishes_repeatable_seq'),
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

update cf_wt_diet_type
set description = 'Второй завтрак'
where description = 'Завтрак 2';

update cf_wt_diet_type
set description = 'Второй ужин'
where description = 'Ужин 2';

--952
ALTER TABLE public.cf_clientsms_resending
    ADD nodename varchar NULL;

CREATE SEQUENCE public.cf_clientsms_node_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_clientsms_node_logging
(
    id       int8 DEFAULT nextval('cf_clientsms_node_id') NOT NULL,
    idOfSms  varchar                                      null,
    params   varchar                                      null,
    nodename varchar                                      null
);

CREATE INDEX cf_clientsms_node_idx
    ON cf_clientsms_node_logging
        USING btree
        (idOfSms);

ALTER TABLE public.cf_clientsms_node_logging
    ADD createdate int8 NULL;

--! ФИНАЛИЗИРОВАН 23.03.2021, НЕ МЕНЯТЬ
