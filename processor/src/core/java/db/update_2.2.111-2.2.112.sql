--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.111

CREATE TABLE cf_taloon_approval
(
  idoforg bigint NOT NULL,
  taloondate bigint NOT NULL,
  taloonname character varying(256) NOT NULL,
  qty integer NOT NULL,
  price bigint NOT NULL,
  createdtype integer NOT NULL,
  idoforgowner bigint NOT NULL,
  version bigint NOT NULL,
  deletedstate boolean NOT NULL DEFAULT false,
  CONSTRAINT cf_taloon_approval_pk PRIMARY KEY (idoforg, taloondate, taloonname),
  CONSTRAINT cf_taloon_approval_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_taloon_approval_orgowner_fk FOREIGN KEY (idoforgowner)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);

CREATE INDEX cf_taloons_approval_version_idx ON cf_taloon_approval USING btree (version);
