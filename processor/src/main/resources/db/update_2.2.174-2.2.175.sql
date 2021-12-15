--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.175

--Новый производственный календарь
CREATE TABLE cf_production_calendar
(
  idOfProductionCalendar bigserial,
  day bigint,
  createdDate bigint,
  lastUpdate bigint,
  CONSTRAINT cf_production_calendar_pk PRIMARY KEY (idOfProductionCalendar),
  CONSTRAINT cf_production_calendar_day_unique UNIQUE (day)
);

--поле "Источник"
alter table cf_client_dtiszn_discount_info
  add column source character varying(5);

--таблица с флагами отправки заявок по предзаказам в ОО
create table cf_org_good_requests
(
  idOfOrgGoodRequest bigserial NOT NULL,
  idOfOrg bigint NOT NULL,
  day bigint NOT NULL,
  createdDate bigint NOT NULL,
  issent integer NOT NULL DEFAULT 0,
  sendDate bigint,
  CONSTRAINT cf_org_good_request_pk PRIMARY KEY (idOfOrgGoodRequest),
  CONSTRAINT cf_org_good_request_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_org_good_requests_day_idx ON cf_org_good_requests USING btree (day);

--Временно создаем копию таблицы заявок
CREATE TABLE cf_goods_requests_temp
(
  idofgoodsrequest bigserial NOT NULL,
  idofstaff bigint,
  guid character varying(36) NOT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  createddate bigint NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  numberofgoodsrequest character varying(128) NOT NULL,
  dateofgoodsrequest bigint,
  state integer NOT NULL DEFAULT 0,
  donedate bigint,
  comment character varying(512),
  sendall integer DEFAULT 0,
  globalversiononcreate bigint,
  requesttype integer NOT NULL DEFAULT 0,
  CONSTRAINT cf_goods_requests_temp_pk PRIMARY KEY (idofgoodsrequest)
)
WITH (
OIDS=FALSE
);

CREATE TABLE cf_goods_requests_positions_temp
(
  idofgoodsrequestposition bigserial NOT NULL,
  idofgoodsrequest bigint NOT NULL,
  idofgood bigint,
  idofproducts bigint,
  guid character varying(36) NOT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  createddate bigint NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  unitsscale integer NOT NULL DEFAULT 0,
  totalcount bigint NOT NULL,
  netweight bigint NOT NULL,
  sendall integer DEFAULT 0,
  globalversiononcreate bigint,
  dailysamplecount bigint,
  lastdailysamplecount bigint,
  lasttotalcount bigint,
  notified boolean DEFAULT true,
  tempclientscount bigint,
  lasttempclientscount bigint,
  CONSTRAINT cf_goods_requests_positions_temp_pk PRIMARY KEY (idofgoodsrequestposition)
)
WITH (
OIDS=FALSE
);

--! ФИНАЛИЗИРОВАН 08.02.2019, НЕ МЕНЯТЬ