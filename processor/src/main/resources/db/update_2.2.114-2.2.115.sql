--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.114

ALTER TABLE cf_clients ADD COLUMN gender integer,
ADD COLUMN birthDate bigint,
ADD COLUMN benefitOnAdmission character varying (3000);

--Флаг "Признак получения из синхронизации дружественной организации" у заказа
ALTER TABLE cf_orders ADD COLUMN isfromfriendlyorg boolean NOT NULL DEFAULT false;

--Дата присваивания последнего пароля
ALTER TABLE cf_users ADD COLUMN passworddate bigint NOT NULL default (extract(epoch from now()) * 1000),
ADD COLUMN attemptnumber integer default 0;

--Журнал регистрации событий запуска программ/процессов
CREATE TABLE cf_security_journal_processes
(
  idofjournalprocess bigserial not null,
  eventtype integer not null,
  eventclass integer not null,
  eventdate bigint not null,
  idofuser bigint,
  issuccess boolean not null,
  serveraddress character varying(128),
  CONSTRAINT cf_security_journal_processes_pk PRIMARY KEY (idofjournalprocess),
  CONSTRAINT cf_security_journal_processes_fk FOREIGN KEY (idofuser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Версия для справочника организаций, передаваемого на клиент
ALTER TABLE cf_orgs ADD COLUMN orgStructureVersion BIGINT NOT NULL DEFAULT 0;

--Журнал регистрации событий сохранения файлов отчетов
CREATE TABLE cf_security_journal_reports
(
  idofjournalreport bigserial not null,
  eventtype character varying(256),
  eventdate bigint not null,
  idofuser bigint,
  issuccess boolean not null,
  CONSTRAINT cf_security_journal_reports_pk PRIMARY KEY (idofjournalreport),
  CONSTRAINT cf_security_journal_reports_user_fk FOREIGN KEY (idofuser)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Временные посетители (мигранты)
CREATE TABLE cf_migrants
(
  idofrequest BIGINT NOT NULL,
  idoforgregistry BIGINT NOT NULL,
  idoforgregvendor BIGINT NOT NULL,
  requestNumber CHARACTER VARYING(128) NOT NULL DEFAULT '',
  idofclientmigrate BIGINT NOT NULL,
  idoforgvisit BIGINT NOT NULL,
  visitstartdate BIGINT NOT NULL,
  visitenddate BIGINT NOT NULL,
  syncstate INTEGER NOT NULL,
  CONSTRAINT cf_migrants_pk PRIMARY KEY (idofrequest, idoforgregistry),
  CONSTRAINT cf_migrants_idoforgregistry_fk FOREIGN KEY (idoforgregistry)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_migrants_idoforgregvendor_fk FOREIGN KEY (idoforgregvendor)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_migrants_idofclientmigrate_fk FOREIGN KEY (idofclientmigrate)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_migrants_idoforgvisit_fk FOREIGN KEY (idoforgvisit)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--История резолюций по запросам на посещение (по мигрантам)
CREATE TABLE cf_visitreqresolutionhist
(
  idofrecord BIGINT NOT NULL,
  idofrequest BIGINT NOT NULL,
  idoforgresol BIGINT NOT NULL,
  idoforgregistry BIGINT NOT NULL,
  resolution INTEGER NOT NULL,
  resolutiondatetime BIGINT NOT NULL,
  resolutioncause CHARACTER VARYING(512),
  idofclientresol BIGINT,
  contactinfo CHARACTER VARYING(128),
  syncstate INTEGER NOT NULL,
  CONSTRAINT cf_visitreqresolutionhist_pk PRIMARY KEY (idofrecord, idofrequest, idoforgresol),
  CONSTRAINT cf_visitreqresolutionhist_idoforgresol_fk FOREIGN KEY (idoforgresol)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_visitreqresolutionhist_idoforgregistry_fk FOREIGN KEY (idoforgregistry)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_visitreqresolutionhist_idofclientresol_fk FOREIGN KEY (idofclientresol)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE cf_registrychange ADD COLUMN genderFrom integer,
ADD COLUMN birthDateFrom bigint,
ADD COLUMN benefitOnAdmissionFrom character varying (3000);

CREATE INDEX cf_security_journal_authenticate_eventdate_idx
ON cf_security_journal_authenticate USING btree (eventdate);

CREATE INDEX cf_security_journal_processes_eventdate_idx
ON cf_security_journal_processes USING btree (eventdate);

CREATE INDEX cf_security_journal_reports_eventdate_idx
ON cf_security_journal_reports USING btree (eventdate);

ALTER TABLE cf_security_journal_balances
  ADD COLUMN idoftransaction bigint,
  ADD CONSTRAINT cf_security_journal_balances_transaction_fk FOREIGN KEY (idoftransaction)
  REFERENCES cf_transactions (idoftransaction) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE TABLE CF_CheckSums (
  idOfCheckSums BIGSERIAL NOT NULL,
  checkSumsDate BIGINT NOT NULL,
  distributionVersion CHARACTER VARYING(20),
  checkSumsMd5 CHARACTER VARYING(32),
  CONSTRAINT cf_checksums_pk PRIMARY KEY (idOfCheckSums)
);

--Дата синхронизации с рнип 1.16.2 для получения корректировочных и аннулированных платежей
ALTER TABLE cf_contragents_sync ADD COLUMN lastModifiesUpdate character varying(30) DEFAULT '';
update cf_contragents_sync set lastModifiesUpdate = lastrnipupdate;

ALTER TABLE cf_security_journal_authenticate ADD COLUMN comment character varying(256);

ALTER TABLE cf_checksums ADD COLUMN checkSumOnSettings CHARACTER VARYING(32);

CREATE INDEX cf_cards_lastupdate_idx ON cf_cards USING btree (lastupdate);

ALTER TABLE cf_discountchangehistory
  ADD COLUMN idoforg BIGINT,
  ADD COLUMN comment CHARACTER VARYING(128) NOT NULL DEFAULT '',
  ADD CONSTRAINT cf_discountchangehistory_idoforg_fk FOREIGN KEY (idoforg)
REFERENCES cf_orgs (idoforg) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;

--! ФИНАЛИЗИРОВАН (Семенов, 270616) НЕ МЕНЯТЬ