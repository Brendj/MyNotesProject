/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

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