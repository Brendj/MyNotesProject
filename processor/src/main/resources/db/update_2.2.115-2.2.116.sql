--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.115

CREATE TABLE cf_groupnames_to_orgs
  (
  idofgroupnametoorg BIGSERIAL,
  idoforg BIGINT,
  idofmainorg BIGINT,
  mainbuilding INTEGER,
  version BIGINT,
  check (mainbuilding = 1),
  groupname CHARACTER VARYING(256),
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

--Хранение хэш-наименований фото клиентов
CREATE TABLE cf_clientphoto
(
  idofclient BIGINT NOT NULL,
  name CHARACTER VARYING(16) NOT NULL DEFAULT '',
  isnew INTEGER NOT NULL,
  CONSTRAINT cf_clientphoto_pk PRIMARY KEY (idofclient),
  CONSTRAINT cf_clientphoto_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_clientphoto_client_idx ON cf_clientphoto USING BTREE (idofclient);

--Тип карт
ALTER TABLE cf_newcards
  ADD COLUMN cardtype integer;

--Признак удаления опекунской связи
ALTER TABLE cf_client_guardian ADD COLUMN deletedstate boolean NOT NULL DEFAULT false,
ADD COLUMN deletedate bigint;

--Поле Соисполнитель поставщика по умолчанию
ALTER TABLE cf_orgs ADD COLUMN cosupplier BIGINT,
  ADD CONSTRAINT cf_orgs_contragent_cosupplier_fk FOREIGN KEY (cosupplier)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

--! ФИНАЛИЗИРОВАН (Семенов, 030816) НЕ МЕНЯТЬ