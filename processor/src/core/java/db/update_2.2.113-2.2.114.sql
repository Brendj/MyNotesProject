--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.113

--Увеличение размерности поля для условия выборки в отчетах по расписанию
ALTER TABLE cf_ruleconditions ALTER conditionconstant TYPE CHARACTER VARYING(50000);

--Новое поле для краткого наименование организации для поставщика в сверке организаций
ALTER TABLE cf_orgregistrychange_item ADD COLUMN shortnamesupplierfrom CHARACTER VARYING(128);

--Новые поля ручного реестра талонов
ALTER TABLE cf_taloon_approval RENAME COLUMN qty TO soldedqty;
ALTER TABLE cf_taloon_approval ADD COLUMN requestedqty INTEGER, ADD COLUMN shippedqty INTEGER,
  ADD COLUMN ispp_state INTEGER NOT NULL DEFAULT 0, ADD COLUMN pp_state INTEGER NOT NULL DEFAULT 0;

--Связь базовой корзины с детализацией меню
ALTER TABLE cf_good_basic_basket_price ADD COLUMN idofmenudetail BIGINT;

--внешний ключ на таблицу menudetails
ALTER TABLE cf_good_basic_basket_price ADD CONSTRAINT cf_good_basic_basket_price_menudetail_fk FOREIGN KEY (idofmenudetail)
REFERENCES cf_menudetails (idofmenudetail) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

--новый индекс на таблицу cf_ComplexInfo
--CREATE INDEX cf_complexinfo_menudate_idx ON cf_complexinfo USING btree (menudate);

CREATE TABLE cf_interactive_report_data (idofrecord BIGINT, idoforg BIGINT, value VARCHAR(255),
  CONSTRAINT cf_interactivereport_cf_orgs_pk PRIMARY KEY (idoforg, idofrecord));

--Таблица истории льгот
CREATE TABLE cf_discountchangehistory
(
  idofdiscountchange BIGSERIAL NOT NULL,
  idofclient BIGINT NOT NULL, registrationdate BIGINT NOT NULL, discountmode INTEGER NOT NULL,
  olddiscountmode INTEGER NOT NULL, categoriesdiscounts CHARACTER VARYING(60) NOT NULL DEFAULT '' :: CHARACTER VARYING,
  oldcategoriesdiscounts CHARACTER VARYING(60) NOT NULL DEFAULT '' :: CHARACTER VARYING,
  CONSTRAINT cf_discountchangehistory_pk PRIMARY KEY (idofdiscountchange),
  CONSTRAINT cf_discountchangehistory_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
) WITH
(OIDS = FALSE
);

--Таблица с нулевыми транзакциями
CREATE TABLE cf_zerotransactions
(
  idoforg BIGINT NOT NULL,
  transactiondate BIGINT NOT NULL,
  idofcriteria INTEGER NOT NULL,
  targetlevel INTEGER NOT NULL,
  actuallevel INTEGER NOT NULL,
  criterialevel INTEGER NOT NULL,
  idofreason INTEGER,
  comment character varying(256),
  version BIGINT NOT NULL,
  CONSTRAINT cf_zero_transaction_pk PRIMARY KEY (idoforg, transactiondate, idofcriteria),
  CONSTRAINT cf_zero_transaction_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Увеличение длины полей
ALTER TABLE cf_log_infoservice
  ALTER idofsystem TYPE character varying(50),
  ALTER ssoid TYPE character varying(50);

--Таблица свежезагруженных новых непривязанных карт
CREATE TABLE cf_newcards
(
  idofnewcard BIGSERIAL NOT NULL,
  createddate BIGINT NOT NULL,
  cardno BIGINT NOT NULL,
  cardprintedno BIGINT NOT NULL,
  CONSTRAINT cf_newcards_pk PRIMARY KEY (idofnewcard)
);

--Таблица руководителей клиентских групп
CREATE TABLE cf_clientgroup_manager
(
  idofgroupmanager BIGSERIAL              NOT NULL,
  version          BIGINT                 NOT NULL,
  clientgroupname  CHARACTER VARYING(256) NOT NULL,
  idofclient       BIGINT                 NOT NULL,
  idoforg          BIGINT                 NOT NULL,
  managertype      INTEGER                NOT NULL,
  deleted          INTEGER                NOT NULL,
  CONSTRAINT cf_clientgroup_manager_pk PRIMARY KEY (idofgroupmanager),
  CONSTRAINT cf_clientgroup_manager_idofclient_fk FOREIGN KEY (idofclient) REFERENCES cf_clients (idofclient) ON DELETE NO ACTION ON UPDATE NO ACTION
) WITH (OIDS = FALSE
);

--индекс по ид. клиента для таблицы руководители группы
CREATE INDEX cf_clientgroup_manager_client_idx ON cf_clientgroup_manager USING BTREE (idofclient);
--индекс по ид. группы и ид. организации для таблицы руководители группы
CREATE INDEX cf_clientgroup_manager_org_idx ON cf_clientgroup_manager USING BTREE (idoforg);

ALTER TABLE CF_RegistryChange ADD COLUMN gender integer;
ALTER TABLE CF_RegistryChange ADD COLUMN birthDate bigint;
ALTER TABLE CF_RegistryChange ADD COLUMN benefitOnAdmission character varying (3000);