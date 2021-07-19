/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- issue_1063
create table cf_wa_journal_group
(
    idofoperation bigint not null primary key,
    groupname varchar not null,
    oldidoforg bigint not null,
    newidoforg bigint not null,
    oldidofclientgroup bigint not null,
    newidofclientgroup bigint not null,
    idofuser bigint,
    createddate timestamp without time zone not null,
    CONSTRAINT cf_wa_journal_group_idofuser_fk FOREIGN KEY (idofuser)
        REFERENCES cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wa_journal_group_old_fk FOREIGN KEY (oldidoforg, oldidofclientgroup)
        REFERENCES cf_clientgroups (idoforg, idofclientgroup) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wa_journal_group_new_fk FOREIGN KEY (newidoforg, newidofclientgroup)
        REFERENCES cf_clientgroups (idoforg, idofclientgroup) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE cf_wa_journal_group_idofoperation_seq
    INCREMENT 32
    START 1;

comment on table cf_wa_journal_group is 'Логирование операций привязки группы к зданию ОО, выполняемых пользователем в веб арм ОО';
comment on column cf_wa_journal_group.idofoperation is 'Идентификатор записи';
comment on column cf_wa_journal_group.groupname is 'Название группы';
comment on column cf_wa_journal_group.oldidoforg is 'Идентификатор ОО до изменения';
comment on column cf_wa_journal_group.newidoforg is 'Идентификатор ОО после изменения';
comment on column cf_wa_journal_group.oldidofclientgroup is 'Идентификатор группы внутри ОО до изменения';
comment on column cf_wa_journal_group.newidofclientgroup is 'Идентификатор группы внутри ОО после изменения';
comment on column cf_wa_journal_group.idofuser is 'Идентификатор пользователя веб арма';
comment on column cf_wa_journal_group.createddate is 'Дата-время выполнения операции';