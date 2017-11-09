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
  name character varying(16) NOT NULL DEFAULT ''::character varying,
  ext character varying(5) NOT NULL DEFAULT ''::character varying,
  displayname character varying(16) NOT NULL DEFAULT ''::character varying,
  date bigint NOT NULL,
  idofarm bigint NOT NULL,
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
ALTER TABLE cf_client_guardian ADD COLUMN lastupdate bigint NOT NULL DEFAULT (date_part('epoch'::text, '2000-01-01'::date) * (1000)::double precision);

--Заполняем колонку lastupdate в cf_client_guardian текущей датой
UPDATE cf_client_guardian SET lastupdate=(date_part('epoch'::text, current_date) * (1000)::double precision);

--Создаем индекс на поле lastupdate в таблице cf_client_guardian 
CREATE INDEX cf_client_guardian_lastupdate_idx ON cf_client_guardian USING btree (lastupdate);
