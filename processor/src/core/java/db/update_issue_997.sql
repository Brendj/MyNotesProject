-- Пакет обновлений issue 997

alter table cf_specialdates
  add column staffguid character varying(36),
  add column armlastupdate bigint,
  add column createddate bigint,
  add column lastupdate bigint;

comment on column cf_specialdates.staffguid is 'Гуид сотрудника';
comment on column cf_specialdates.armlastupdate is 'Дата-время изменений на АРМе';
comment on column cf_specialdates.createddate is 'Дата создания записи';
comment on column cf_specialdates.lastupdate is 'Дата последнего изменения';

CREATE TABLE cf_specialdates_history
(
  idofspecialdatehistory bigserial NOT NULL,
  idoforg bigint NOT NULL,
  date bigint NOT NULL,
  isweekend integer NOT NULL,
  deleted integer NOT NULL,
  version bigint NOT NULL,
  comment character varying(256) NOT NULL DEFAULT '',
  idoforgowner bigint NOT NULL,
  idofclientgroup bigint,
  staffguid character varying(36),
  armlastupdate bigint,
  createddate bigint,
  CONSTRAINT cf_specialdates_history_pk PRIMARY KEY (idofspecialdatehistory),
  CONSTRAINT cf_specialdates_history_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_specialdates_history_idoforgowner_fk FOREIGN KEY (idoforgowner)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

comment on table cf_specialdates_history is 'История изменений календаря дней питания';