/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */


create table cf_mh_service_journal
(
    id bigserial primary key,
    createdate timestamp not null,
    lastupdate timestamp not null,
    message varchar(255) not null,
    exceptionclass varchar(128),
    personguid varchar(36) not null,
    decided boolean not null default true
);

alter table cf_mh_persons
    add column guidnsi varchar(40);

comment on table cf_mh_service_journal is 'Таблица журналирования сервиса взаимодействия с МЭШ.Контингент';
comment on column cf_mh_service_journal.id is 'ID записи';
comment on column cf_mh_service_journal.createdate is 'Дата создания';
comment on column cf_mh_service_journal.lastupdate is 'Дата изменения';
comment on column cf_mh_service_journal.message is 'Текст ошибки';
comment on column cf_mh_service_journal.exceptionclass is 'Класс исключения';
comment on column cf_mh_service_journal.personguid is 'ID проблемного клиента (или сообщения)';
comment on column cf_mh_service_journal.decided is 'Признак решения проблемы';

comment on column cf_mh_persons.guidnsi is 'GUID в НСИ-1';