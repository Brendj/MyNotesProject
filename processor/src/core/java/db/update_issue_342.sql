--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 342

create table cf_plan_orders_restrictions
(
  idOfPlanOrdersRestriction bigserial,
  idOfClient bigint not null,
  idOfOrgOnCreate bigint,
  idOfConfigurationProoviderOnCreate bigint,
  complexName character varying(128),
  armComplexId integer not null,
  planType integer not null,
  version bigint not null,
  createdDate bigint,
  lastUpdate bigint,
  deletedState integer not null default 0,
  constraint cf_plan_orders_restrictions_pk primary key (idOfPlanOrdersRestriction),
  constraint cf_plan_orders_restrictions_client_fk foreign key (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  constraint cf_plan_orders_restrictions_org_fk foreign key (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  constraint cf_plan_orders_restrictions_contragent_fk foreign key (idofcontragent)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_plan_orders_restrictions_version_idx ON cf_plan_orders_restrictions USING btree (version);

CREATE INDEX cf_plan_orders_restrictions_client_idx ON cf_plan_orders_restrictions USING btree (idofclient);

COMMENT ON TABLE cf_plan_orders_restrictions IS 'Ограничения на рационы питания';