/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 277

-- issue_1060
create table cf_wa_journal_client
(
    idofoperation bigint not null primary key,
    idofclient bigint not null,
    operationtype integer,
    operationtext text,
    idofuser bigint,
    createddate timestamp without time zone not null,
    CONSTRAINT cf_wa_journal_client_idofclient_fk FOREIGN KEY (idofclient)
        REFERENCES cf_clients (idofclient) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wa_journal_client_idofuser_fk FOREIGN KEY (idofuser)
        REFERENCES cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE cf_wa_journal_client_idofoperation_seq
    INCREMENT 256
    START 1;

comment on table cf_wa_journal_client is 'Журнал действий пользователя в веб АРМ администратора';
comment on column cf_wa_journal_client.idofoperation is 'Идентификатор записи';
comment on column cf_wa_journal_client.idofclient is 'Идентификатор клиента';
comment on column cf_wa_journal_client.operationtype is 'Тип операции: 0 - Логирование операции проведения сверки, 1 - Логирование операции исключения группы обучающихся из списка предоставления питания за счет бюджета города Москвы';
comment on column cf_wa_journal_client.operationtext is 'Произведенные изменения';
comment on column cf_wa_journal_client.idofuser is 'Идентификатор пользователя веб арма';
comment on column cf_wa_journal_client.createddate is 'Дата-время выполнения операции';

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

-- issue_1067
create table cf_wa_journal_requests
(
    idofoperation bigint not null primary key,
    idoforg bigint not null,
    idofcomplex bigint not null,
    requestdate timestamp without time zone not null,
    dailySampleCount bigint not null,
    tempClientCount bigint not null,
    totalCount bigint not null,
    operationType integer not null default 0,
    idofuser bigint,
    createddate timestamp without time zone not null,
    idofdish bigint,
    CONSTRAINT cf_wa_journal_requests_idofuser_fk FOREIGN KEY (idofuser)
        REFERENCES cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wa_journal_requests_org_fk FOREIGN KEY (idoforg)
        REFERENCES cf_orgs (idoforg) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wa_journal_requests_complex_fk FOREIGN KEY (idofcomplex)
        REFERENCES cf_wt_complexes (idofcomplex) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE cf_wa_journal_requests_idofoperation_seq
    INCREMENT 128
    START 1;

comment on table cf_wa_journal_requests is 'Логирование операций изменений электронного заказа на питание, направляемого поставщику питания';
comment on column cf_wa_journal_requests.idofoperation is 'Идентификатор записи';
comment on column cf_wa_journal_requests.idoforg is 'Идентификатор организации';
comment on column cf_wa_journal_requests.idofcomplex is 'Идентификатор комплекса';
comment on column cf_wa_journal_requests.requestdate is 'Дата заявки';
comment on column cf_wa_journal_requests.dailySampleCount is 'Количество для суточной пробы';
comment on column cf_wa_journal_requests.tempClientCount is 'Количество для временных клиентов';
comment on column cf_wa_journal_requests.totalCount is 'Общее количество';
comment on column cf_wa_journal_requests.operationType is 'Тип операции: 0 - создание позиции, 1 - редактирование позиции';
comment on column cf_wa_journal_requests.idofuser is 'Идентификатор пользователя веб арма';
comment on column cf_wa_journal_requests.createddate is 'Дата-время выполнения операции';

-- issue_1073
create table сf_wa_journal_applicationsforfood
(
    idofoperation bigint not null primary key,
    idofapplicationforfood bigint,
    operationtype integer,
    idofuser bigint,
    createddate timestamp without time zone not null,
    CONSTRAINT сf_wa_journal_applicationsforfood_idofuser_fk FOREIGN KEY (idofuser)
        REFERENCES cf_users (idofuser) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT сf_wa_journal_applicationsforfood_app_fk FOREIGN KEY (idofapplicationforfood)
        REFERENCES cf_applications_for_food (idofapplicationforfood) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE SEQUENCE сf_wa_journal_applicationsforfood_idofoperation_seq
    INCREMENT 8
    START 1;

comment on table сf_wa_journal_applicationsforfood is 'Логированиe операции управления заявлением, выполненной пользователем';
comment on column сf_wa_journal_applicationsforfood.idofoperation is 'Идентификатор записи';
comment on column сf_wa_journal_applicationsforfood.idofapplicationforfood is 'Идентификатор заявления';
comment on column сf_wa_journal_applicationsforfood.operationType is 'Тип операции: 0 - Отклонение заявление ЛП, 1 - Подтверждение предоставления документов, 2 - Подтверждение заявления ЛП';
comment on column сf_wa_journal_applicationsforfood.idofuser is 'Идентификатор пользователя веб арма';
comment on column сf_wa_journal_applicationsforfood.createddate is 'Дата-время выполнения операции';

--! ФИНАЛИЗИРОВАН 04.10.2021, НЕ МЕНЯТЬ