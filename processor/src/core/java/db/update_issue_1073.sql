/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- issue_1073
create table сf_wa_journal_applicationsforfood
(
    idofoperation bigint not null primary key,
    idofapplicationforfood bigint,
    operationtype integer,
    idofuser bigint, --кто выполнил операцию
    createddate timestamp without time zone not null, --дата-время выполнения операции
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