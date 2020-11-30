--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 240
COMMENT ON COLUMN public.cf_cr_cardactionclient.idcardactionclient IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idcardactionrequest IS 'Идентификатор запроса (ссылка на cf_cr_cardactionrequests)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idofclient IS 'Идентификатор клиента или сотрудника (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idclientchild IS 'Идентификатор опекаемого (ссылка на cf_clients)';
COMMENT ON COLUMN public.cf_cr_cardactionclient.idofcard IS 'Идентификатор карты (ссылка на cf_cards)';
COMMENT ON COLUMN public.cf_cr_cardactionclient."comment" IS 'Описание результата операции';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.idcardactionrequest IS 'Идентификатор записи';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.requestid IS 'Идентификатор запроса (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.contingentid IS 'Идентификатор клиента (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.staffid IS 'Идентификатор сотрудника (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.firstname IS 'Имя (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.lastname IS 'Фамилия (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.middlename IS 'Отчество (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.birthday IS 'Дата рождения (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.organization_ids IS 'Идентификаторы организаций для сотрудника (приходит из внешней системы)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests."action" IS 'Действие: 0 - Блокировка, 1 - Разблокировка';
COMMENT ON COLUMN public.cf_cr_cardactionrequests."comment" IS 'Результат выполнения операции';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.processed IS 'Успешность: true - Запрос успешно обработан, false - При обработке запроса возникли ошибки';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.createdate IS 'Дата приема запроса';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.lastupdate IS 'Дата обновления запроса';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.idofclient IS 'Идентификатор клиента (для поддержки старых запросов)';
COMMENT ON COLUMN public.cf_cr_cardactionrequests.previdcardrequest IS 'Идентификатор записи на блокировку с таким же requestid';


--756
drop table cf_GuardSan;
ALTER TABLE cf_clients DROP COLUMN guardsan;

--777
ALTER TABLE cf_cr_cardactionclient ADD idoldcards varchar NULL;

--551
ALTER TABLE cf_complex_schedules ALTER COLUMN groupsids TYPE varchar USING cast(groupsids as varchar);

DROP TABLE IF EXISTS cf_plan_orders;
CREATE TABLE cf_plan_orders
(
  idofplanorder BIGSERIAL PRIMARY KEY,
  idoforg bigint ,
  groupname character varying(256),
  idofclient bigint,
  plandate bigint NOT NULL,
  idofcomplex bigint,
  complexname character varying(256),
  useridrequesttopay bigint,
  topay integer NOT NULL,
  idoforder bigint,
  useridconfirmtopay bigint,
  idofdiscountrule bigint,
  createdate bigint NOT NULL DEFAULT cast(extract(epoch from now()) * 1000 as bigint),
  lastupdate bigint NOT NULL DEFAULT cast(extract(epoch from now()) * 1000 as bigint),
  CONSTRAINT planorders_unique_constraint UNIQUE (idoforg, idofclient, plandate, idofcomplex),
  CONSTRAINT useridrequesttopay_fk FOREIGN KEY (useridrequesttopay)
  REFERENCES public.cf_users (idofuser) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION
  NOT VALID,
  CONSTRAINT useridconfirmtopay_fk FOREIGN KEY (useridconfirmtopay)
  REFERENCES public.cf_users (idofuser) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION
  NOT VALID,
  CONSTRAINT idofrule_fk FOREIGN KEY (idofdiscountrule)
  REFERENCES public.cf_discountrules (idofrule) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION
  NOT VALID,
  CONSTRAINT idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES public.cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION
  ON DELETE NO ACTION
  NOT VALID
)
WITH (
OIDS = FALSE
);

COMMENT ON TABLE cf_plan_orders IS 'План питания';

alter table cf_smartwatchs add column vendor varchar(128) not null default 'Geoplaner';

CREATE TABLE cf_smartwatch_vendor
(
  idofvendor          bigserial primary key,
  name                varchar(128) not null unique,
  apikey              varchar(36)  not null unique,
  enableService       bool         not null default true,
  cardsigncertnum     integer,
  enablePushes        bool         not null default false,
  entereventsEndPoint varchar(512),
  purchasesendpoint   varchar(512),
  paymentendpoint     varchar(512)
);

COMMENT ON TABLE cf_smartwatch_vendor IS 'Поставщики смарт-часов';
COMMENT ON COLUMN cf_smartwatch_vendor.idofvendor IS 'ID';
COMMENT ON COLUMN cf_smartwatch_vendor.name IS 'Название поставщика';
COMMENT ON COLUMN cf_smartwatch_vendor.apikey IS 'Ключ доступа к REST-контроллеру';
COMMENT ON COLUMN cf_smartwatch_vendor.enableService IS 'Флаг разрешение на обслуживание в REST-контроллере';
COMMENT ON COLUMN cf_smartwatch_vendor.cardsigncertnum IS 'Номер сертификата (ключа ЭЦП часов как карты)';
COMMENT ON COLUMN cf_smartwatch_vendor.enablePushes IS 'Разрешение отправлять оповещения';
COMMENT ON COLUMN cf_smartwatch_vendor.entereventsEndPoint IS 'Адрес для отправки проходов';
COMMENT ON COLUMN cf_smartwatch_vendor.purchasesendpoint IS 'Адрес для отправки покупок';
COMMENT ON COLUMN cf_smartwatch_vendor.paymentendpoint IS 'Адрес для отправки пополнения счёта';

ALTER TABLE cf_smartwatchs
  ADD COLUMN idofvendor integer references cf_smartwatch_vendor(idofvendor) on delete set null;

alter table cf_clients
  add column idOfVendor bigint references cf_smartwatch_vendor(idofvendor) on delete set null;

alter table cf_geoplaner_notifications_journal
  add column idOfVendor bigint references cf_smartwatch_vendor(idofvendor);

alter table cf_client_dtiszn_discount_info add column archivedate bigint;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.archivedate IS 'Дата архивации льготы';

alter table cf_applications_for_food add column archivedate bigint;
COMMENT ON COLUMN cf_applications_for_food.archivedate IS 'Дата архивации заявления';