Дмитрий


CREATE TABLE cf_orgregistrychange (
  idoforgregistrychange BIGSERIAL,
  idoforg BIGINT,
  createdate BIGINT NOT NULL,
  operationtype INTEGER NOT NULL,

  organizationtype INTEGER NOT NULL DEFAULT 0,
  organizationtypefrom INTEGER,
  shortname VARCHAR(255) NOT NULL,
  shortnamefrom VARCHAR(255),
  officialname VARCHAR(255) NOT NULL,
  officialnamefrom VARCHAR(255),

  applied BOOLEAN NOT NULL DEFAULT FALSE,

  address VARCHAR(255) NOT NULL,
  addressfrom VARCHAR(255),
  city VARCHAR(255) NOT NULL,
  cityfrom VARCHAR(255),
  region VARCHAR(255) NOT NULL,
  regionfrom VARCHAR(255),

  unom BIGINT,
  unomfrom BIGINT,
  unad BIGINT,
  unadfrom BIGINT,

  guid VARCHAR(255) NOT NULL,
  guidfrom VARCHAR(255),
  additionalid BIGINT NOT NULL,

  CONSTRAINT cf_orgregistrychange_pk PRIMARY KEY (idoforgregistrychange)
);
create index cf_orgregistrychange_createDate_idx on cf_orgregistrychange(createdate);


--  Добавление индексов для синхронизации с реестрами
create index cf_registrychange_org_idx on cf_registrychange(idOfOrg);
create index cf_registrychange_createDate_idx on cf_registrychange(createDate);
create index cf_registrychange_type_idx on cf_registrychange(type);


-- Расширение таблицы для повторной отправки смс
alter table cf_clientsms_resending alter column paramscontents type text;
alter table cf_clientsms_resending alter column textcontents type text;



