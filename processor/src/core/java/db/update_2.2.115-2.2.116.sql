--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.115

CREATE TABLE cf_groupnames_to_orgs
  (
  idofgroupnametoorg bigserial,
  idoforg bigint,
  idofmainorg bigint,
  mainbuilding integer,
  check (mainbuilding = 1),
  groupname character varying(36),
  constraint cf_groupnames_to_orgs_pk primary key (idofgroupnametoorg),
  constraint cf_groupnames_to_orgs_fk foreign key (idoforg)
    references cf_orgs (idoforg)
);

--Дата последней обработки секций синхронизации
CREATE TABLE cf_lastprocesssectionsdates
(
  idoforg BIGINT NOT NULL,
  type INTEGER NOT NULL,
  date BIGINT NOT NULL,
  CONSTRAINT cf_lastprocesssectionsdates_pk PRIMARY KEY (idoforg, type),
  CONSTRAINT cf_lastprocesssectionsdates_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
