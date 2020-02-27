--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 210

-- issue 200
ALTER TABLE cf_orgs
    ADD COLUMN menusSyncParam BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN clientsSyncParam BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN orgSettingsSyncParam BOOLEAN NOT NULL DEFAULT FALSE;

-- Таблица для блокированных на арме предзаказах
CREATE TABLE cf_preorder_status (
    idOfPreorderStatus bigserial NOT NULL,
    date bigint,
    guid character varying(36),
    status integer,
    storno integer NOT NULL,
    version bigint,
    deletedState integer,
    createddate bigint NOT NULL,
    lastupdate bigint,
    idoforgoncreate bigint NOT NULL,
    CONSTRAINT cf_preorder_block_pk PRIMARY KEY (idOfPreorderStatus)
);

CREATE UNIQUE INDEX cf_preorder_status_guid_date_unique
ON cf_preorder_status
USING btree
(guid, date);

--ecafe.processor.planOrdersRestrictions.serviceNode - новый параметр в конфигурацию, имя ноды для сервисных операций по функционалу ограничения рационов

create table cf_plan_orders_restrictions
(
    idOfPlanOrdersRestriction bigserial,
    idOfClient bigint not null,
    idOfOrgOnCreate bigint,
    idOfConfigurationProoviderOnCreate bigint,
    complexName character varying(128),
    armComplexId integer not null,
    planType integer not null,
    resol integer,
    version bigint not null,
    createdDate bigint,
    lastUpdate bigint,
    deletedState integer not null default 0,
    constraint cf_plan_orders_restrictions_pk primary key (idOfPlanOrdersRestriction),
    constraint cf_plan_orders_restrictions_client_fk foreign key (idofclient)
    REFERENCES cf_clients (idofclient) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    constraint cf_plan_orders_restrictions_org_fk foreign key (idoforgoncreate)
    REFERENCES cf_orgs (idoforg) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    constraint cf_plan_orders_restrictions_config_fk foreign key (idOfConfigurationProoviderOnCreate)
    REFERENCES cf_provider_configurations (idofconfigurationprovider) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_plan_orders_restrictions_unique UNIQUE (idOfClient, idOfOrgOnCreate, armComplexId)
);

CREATE INDEX cf_plan_orders_restrictions_version_idx ON cf_plan_orders_restrictions USING btree (version);

CREATE INDEX cf_plan_orders_restrictions_client_idx ON cf_plan_orders_restrictions USING btree (idofclient);

COMMENT ON TABLE cf_plan_orders_restrictions IS 'Ограничения на рационы питания';

--! ФИНАЛИЗИРОВАН 27.02.2020, НЕ МЕНЯТЬ