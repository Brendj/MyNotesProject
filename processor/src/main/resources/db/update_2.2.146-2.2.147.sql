--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.147

--флаг шестидневной недели по группам
alter table cf_groupnames_to_orgs add column issixdaysworkweek integer;

--таблица связок Базовый Товар - Производственная Конфигурация
create table cf_basicbasketgood_provider
(
  idofbasicbasketgoodprovider bigserial NOT NULL,
  idofbasicgood bigint NOT NULL,
  idofconfigurationprovider bigint NOT NULL,
  CONSTRAINT pk_basicbasketgood_provider PRIMARY KEY (idofbasicbasketgoodprovider),
  CONSTRAINT cf_basicbasketgood_provider_idofbasicgood_fk FOREIGN KEY (idofbasicgood)
  REFERENCES cf_goods_basicbasket (idofbasicgood) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_basicbasketgood_provider_idofconfigurationprovider_fk FOREIGN KEY (idofconfigurationprovider)
  REFERENCES cf_provider_configurations (idofconfigurationprovider) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Убираем уникальность по названию товара
alter table cf_goods_basicbasket drop constraint cf_goods_basicbasket_nameofgood_key;

--таблица описания файлов организаций
CREATE TABLE cf_orgfile
(
  idoforgfile bigserial NOT NULL,
  idoforg bigint NOT NULL,
  name character varying(16) NOT NULL DEFAULT '',
  ext character varying(5) NOT NULL DEFAULT '',
  displayname character varying(256) NOT NULL DEFAULT '',
  date bigint NOT NULL,
  size bigint NOT NULL,
  CONSTRAINT cf_orgfile_pk PRIMARY KEY (idoforgfile),
  CONSTRAINT cf_orgfile_idoforg_fk FOREIGN KEY (idoforg)
      REFERENCES cf_orgs (idoforg) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
COMMENT ON TABLE cf_orgfile
  IS 'описание файлов организации';

--Добавляем колонку lastupdate в cf_client_guardian
ALTER TABLE cf_client_guardian ADD COLUMN lastupdate bigint NOT NULL DEFAULT (extract(epoch from now()) * 1000);

--Создаем индекс на поле lastupdate в таблице cf_client_guardian
CREATE INDEX cf_client_guardian_lastupdate_idx ON cf_client_guardian USING btree (lastupdate);

--Сверка по сотрудникам из файла
CREATE TABLE cf_registry_employee_file
(
  firstname character varying(64),
  secondname character varying(128),
  surname character varying(128),
  birthdate character varying(32),
  gender character varying(10),
  snils character varying(32),
  guidoforg character varying(40)
)
WITH (
OIDS=FALSE
);

CREATE INDEX cf_registry_employee_file_guidoforg_idx ON cf_registry_employee_file USING btree (guidoforg COLLATE pg_catalog."default");

--Разногласия по сотрудникам
CREATE TABLE cf_registrychange_employee
(
  idofregistrychange bigserial NOT NULL,
  idoforg bigint NOT NULL,
  createdate bigint NOT NULL,
  clientguid character varying(40),
  firstname character varying(64) NOT NULL,
  secondname character varying(128) NOT NULL,
  surname character varying(128) NOT NULL,
  groupname character varying(64) NOT NULL,
  firstnamefrom character varying(64),
  secondnamefrom character varying(128),
  surnamefrom character varying(128),
  groupnamefrom character varying(64),
  idofmigrateorgto bigint,
  idofmigrateorgfrom bigint,
  idofclient bigint,
  operation integer NOT NULL,
  applied boolean NOT NULL DEFAULT false,
  error character varying(256) DEFAULT NULL,
  type integer NOT NULL DEFAULT 1,
  notificationid character varying(15) DEFAULT NULL,
  gender integer,
  birthdate bigint,
  genderfrom integer,
  birthdatefrom bigint,
  guardianscount integer,
  agetypegroup character varying(128),
  agetypegroupfrom character varying(128),
  checkbenefits integer NOT NULL DEFAULT 0,
  benefitdszn character varying(128),
  benefitdsznfrom character varying(128),
  newdiscounts character varying(128),
  olddiscounts character varying(128),
  CONSTRAINT cf_registrychange_employee_pk PRIMARY KEY (idofregistrychange)
)
WITH (
OIDS=FALSE
);

--! ФИНАЛИЗИРОВАН (Семенов, 171110) НЕ МЕНЯТЬ