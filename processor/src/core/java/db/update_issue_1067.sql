/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

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