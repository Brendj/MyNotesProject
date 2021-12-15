CREATE TABLE cf_product_guide
(
  idofproductguide bigint NOT NULL,
  code character varying(16) NOT NULL,
  full_name character varying(1024),
  product_name character varying(512),
  okp_code character varying(32),
  "version" bigint,
  create_date bigint,
  edit_date bigint,
  delete_date bigint,
  idofusercreate bigint,
  idofuseredit bigint,
  idofuserdelete bigint,
  deleted boolean NOT NULL DEFAULT false,
  idofconfigurationprovider bigint,
  CONSTRAINT fk_product_guide PRIMARY KEY (idofproductguide)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_product_guide OWNER TO postgres;

CREATE TABLE cf_provider_configurations
(
  idofconfigurationprovider bigint NOT NULL DEFAULT 0,
  "name" character varying(64) NOT NULL,
  CONSTRAINT pk_configuration_provider PRIMARY KEY (idofconfigurationprovider),
  CONSTRAINT cf_provider_configurations_name_key UNIQUE (name)
)
WITH (
  OIDS=FALSE
);

ALTER TABLE CF_GENERATORS ADD COLUMN
  idofproductguide bigint NOT NULL DEFAULT 0;

ALTER TABLE CF_GENERATORS ADD COLUMN
  idofconfigurationprovider bigint NOT NULL DEFAULT 0;

ALTER TABLE cf_provider_configurations OWNER TO postgres;

alter table cf_reporthandlerules ALTER COLUMN templatefilename type character varying(256);