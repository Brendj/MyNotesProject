--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.85

--! Добавление признаков и инф за кого сделали отметку
-- Таблица "Агрегирование данных отчет Xml"
CREATE TABLE cf_daily_formation_registries (
  idOfDailyFormationRegistries BIGSERIAL,
  generatedDate BIGINT NOT NULL,
  idOfContragent BIGINT NOT NULL,
  contragentName VARCHAR(255),
  orgNum VARCHAR(30),
  idOfOrg BIGINT NOT NULL,
  officialName VARCHAR(128),
  address VARCHAR(128),
  totalBalance BIGINT NOT NULL,
  rechargeAmount BIGINT NOT NULL,
  salesAmount BIGINT NOT NULL,
  CONSTRAINT cf_daily_formation_registries_pk PRIMARY KEY (idOfDailyFormationRegistries)
);

--! Добавление таблицы исходящих заявок на обмен книгами между школьными библиотеками
CREATE TABLE cf_exchangeout
(
  idofexchangeout bigserial NOT NULL,
  caption character varying(255) DEFAULT NULL,
  incomedate bigint,
  commentin character varying(255) DEFAULT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 0,
  globalversiononcreate bigint,
  school character varying(255),
  commentout character varying(255) DEFAULT NULL,
  status character varying(30) DEFAULT NULL,
  CONSTRAINT cf_exchangeout_pkey PRIMARY KEY (idofexchangeout),
  CONSTRAINT cf_exchangeout_guid_key UNIQUE (guid)
)
WITH (
OIDS=FALSE
);
ALTER TABLE cf_exchangeout
OWNER TO postgres;

--! Добавление таблицы позиций исходящих заявок на обмен книгами между школьными библиотеками
CREATE TABLE cf_exchangeoutpos
(
  idofexchangeoutpos bigserial NOT NULL,
  idofexchangeout bigint NOT NULL,
  idofpublication bigint NOT NULL,
  orgexchange bigint NOT NULL,
  school character varying(255) DEFAULT NULL,
  requiredcount integer,
  confirmedcount integer,
  excomment character varying(255) DEFAULT NULL,
  status character varying(30) DEFAULT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 0,
  globalversiononcreate bigint,
  CONSTRAINT cf_exchangeoutpos_pkey PRIMARY KEY (idofexchangeoutpos),
  CONSTRAINT cf_exchangeoutpos_exchangeout_fk FOREIGN KEY (idofexchangeout)
  REFERENCES cf_exchangeout (idofexchangeout) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_exchangeoutpos_org_fk FOREIGN KEY (orgexchange)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_exchangeoutpos_publication_fk FOREIGN KEY (idofpublication)
  REFERENCES cf_publications (idofpublication) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_exchangeoutpos_guid_key UNIQUE (guid)
)
WITH (
OIDS=FALSE
);
ALTER TABLE cf_exchangeoutpos
OWNER TO postgres;

--! Добавление поля idofexchangein и idofexchangeout в таблицу cf_instances
ALTER TABLE cf_instances ADD COLUMN idofexchangein bigint DEFAULT NULL;
ALTER TABLE cf_instances ADD CONSTRAINT cf_instances_idofexchangein_fkey FOREIGN KEY (idofexchangein) REFERENCES cf_exchangeout (idofexchangeout);
ALTER TABLE cf_instances ADD COLUMN idofexchangeout bigint DEFAULT NULL;
ALTER TABLE cf_instances ADD CONSTRAINT cf_instances_idofexchangeout_fk FOREIGN KEY (idofexchangeout) REFERENCES cf_exchangeout (idofexchangeout);

--! Добавление поля idofexchangeout в таблицу cf_circulation
ALTER TABLE cf_circulations ADD COLUMN idofexchangeout bigint DEFAULT NULL;
ALTER TABLE cf_circulations ADD CONSTRAINT cf_circulations_idofexchangeout_fkey FOREIGN KEY (idofexchangeout) REFERENCES cf_exchangeout (idofexchangeout);