-- Регистрация ОУ, не планируемых к эксплуатации в ИС ПП
CREATE TABLE cf_not_planned_orgs
(
  guid character varying(40),
  shortname character varying(128) NOT NULL,
  officialname character varying(128) NOT NULL,
  tag character varying(256),
  city character varying(128),
  district character varying(128),
  address character varying(128) NOT NULL,
  btiunom bigint,
  btiunad bigint,
  introductionqueue character varying(64),
  additionalidbuilding bigint,
  CONSTRAINT cf_not_planned_orgs_unique UNIQUE (shortname, additionalidbuilding)
);