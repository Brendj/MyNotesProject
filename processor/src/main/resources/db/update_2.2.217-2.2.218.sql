--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 218

-- 499: добавление новых полей в реестр талонов
ALTER TABLE cf_taloon_approval
    ADD COLUMN complexid bigint,
    ADD COLUMN bywebsupplier boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_taloon_approval.complexid IS 'Идентификатор комплекса';
COMMENT ON COLUMN cf_taloon_approval.bywebsupplier IS 'Комплекс от веб-поставщика (false - нет, true - да)';

-- 499: добавление нового поля в таблицу детализации комплексов предзаказа
ALTER TABLE cf_preorder_menudetail
    ADD COLUMN idofdish bigint;

COMMENT ON COLUMN cf_preorder_menudetail.idofdish IS 'Идентификатор блюда от веб-поставщика';

-- 499: добавление новых полей в реестр талонов
ALTER TABLE cf_taloon_preorder
    ADD COLUMN idofdish bigint,
    ADD COLUMN bywebsupplier boolean NOT NULL DEFAULT false;

COMMENT ON COLUMN cf_taloon_preorder.idofdish IS 'Идентификатор блюда от веб-поставщика';
COMMENT ON COLUMN cf_taloon_preorder.bywebsupplier IS 'Комплекс от веб-поставщика (false - нет, true - да)';

-- 506
ALTER TABLE cf_orderdetails
    ADD COLUMN idofcomplex bigint,
    ADD COLUMN idofdish bigint;

COMMENT ON COLUMN cf_orderdetails.idofcomplex IS 'Идентификатор комплекса от веб-поставщика';
COMMENT ON COLUMN cf_orderdetails.idofdish IS 'Идентификатор блюда от веб-поставщика';

--Таблица ежедневных проверок по предзаказам
CREATE TABLE cf_preorder_check (
    idOfPreorderCheck bigserial NOT NULL primary key,
    date bigint NOT NULL,
    preorderAmount bigint,
    goodRequestAmount bigint,
    createdDate bigint,
    lastUpdate bigint NOT NULL,
    alarm integer NOT NULL DEFAULT 0
);

comment on table cf_preorder_check is 'Таблица ежедневных проверок по предзаказам';
comment on column cf_preorder_check.idOfPreorderCheck is 'ID записи';
comment on column cf_preorder_check.date is 'Дата проверки';
comment on column cf_preorder_check.preorderAmount is 'Количество позиций в предзаказах на дату';
comment on column cf_preorder_check.goodRequestAmount is 'Количество позиций в заявках на дату';
comment on column cf_preorder_check.createdDate is 'Время создания записи';
comment on column cf_preorder_check.lastUpdate is 'Время последнего обновления';
comment on column cf_preorder_check.alarm is 'Флаг состояния проверки. 0 - все ОК, 1 - есть несовпадения';

CREATE SEQUENCE application_for_food_id_seq;
select setval('application_for_food_id_seq', (SELECT coalesce(max(version), 0) + 1 from cf_applications_for_food));

CREATE SEQUENCE application_for_food_history_id_seq;
select setval('application_for_food_history_id_seq', (SELECT coalesce(max(version), 0) + 1 from cf_applications_for_food_history));

CREATE SEQUENCE clientphoto_version_seq;
select setval('clientphoto_version_seq', (select coalesce(max(version), 0) + 1 from cf_clientphoto));

-- Установка значений для Самрт-Часов
--update cf_cards
--set cardsigncertnum = 16
--where cardtype = 10 and cardsigncertnum is null;

alter table cf_clients
    add column meshguid varchar(36);

COMMENT ON COLUMN cf_clients.meshguid is 'GUID в системе МЭШ.Контингент';

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

-- Правки от веб арма ПП
alter table cf_wt_dishes drop constraint cf_wt_dishes_code_contragent;
alter table cf_wt_dishes alter column code set data type varchar (32);

--! ФИНАЛИЗИРОВАН 18.06.2020, НЕ МЕНЯТЬ