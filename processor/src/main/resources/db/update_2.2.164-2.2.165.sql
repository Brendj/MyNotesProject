--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.165

--индекс по дате создания миграции клиента в другую ОО
CREATE INDEX cf_clientmigrationhistory_registrationdate_idx ON cf_clientmigrationhistory USING btree(registrationdate);

--индекс по идентификатору платежа
CREATE INDEX cf_clientpayments_idofpayment_idx ON cf_clientpayments USING btree(idofpayment);

INSERT INTO cf_options(idofoption, optiontext)VALUES (100093,'2777058000000');

--таблица блокировок баланса после перехода к другому поставщику
ALTER TABLE cf_clientbalance_hold
  add column declarerInn character varying(20),
  add column declarerAccount character varying(20),
  add column declarerBank character varying(90),
  add column declarerBik character varying(20),
  add column declarerCorrAccount character varying(20);

-- Флаг "Режим выдачи нескольких активных карт"
ALTER TABLE cf_clients
ADD COLUMN multiCardMode INTEGER;

-- Флаг "Использование обучающимися нескольких идентификаторов в ОО"
ALTER TABLE cf_orgs
ADD COLUMN multiCardModeEnabled INTEGER;

-- Таблица "Заявления на ЛП"
CREATE TABLE cf_applications_for_food
(
    idofapplicationforfood bigserial NOT NULL,
    idofclient bigint NOT NULL,
    dtiszncode integer,
    createddate bigint NOT NULL,
    status character varying(10) NOT NULL,
    mobile character varying(32) NOT NULL,
    applicantName character varying(128) NOT NULL,
    applicantSecondName character varying(128),
    applicantSurname character varying(128) NOT NULL,
    lastupdate bigint NOT NULL,
    archived integer,
    idoforgoncreate bigint,
    version bigint NOT NULL,
    CONSTRAINT cf_applications_for_food_pk PRIMARY KEY (idofapplicationforfood)
)
WITH (
    OIDS = FALSE
);

-- Таблица "История заявлений на ЛП"
CREATE TABLE cf_applications_for_food_history
(
    idofapplicationforfoodhistory bigserial NOT NULL,
    idofapplicationforfood bigint NOT NULL,
    status character varying(10),
    createddate bigint NOT NULL,
    senddate bigint,
    version bigint NOT NULL,
    CONSTRAINT cf_applications_for_food_history_pk PRIMARY KEY (idofapplicationforfoodhistory),
    CONSTRAINT cf_applications_for_food_history_uq UNIQUE (idofapplicationforfood, status)
)
WITH (
    OIDS = FALSE
);

-- Таблица "Льготы ДТиСЗН"
CREATE TABLE cf_client_dtiszn_discount_info
(
    idofclientdtiszndiscountinfo bigserial NOT NULL,
    idofclient bigint NOT NULL,
    dtiszncode integer NOT NULL,
    dtiszndescription character varying(512),
    status integer NOT NULL,
    datestart bigint NOT NULL,
    dateend bigint NOT NULL,
    createddate bigint NOT NULL,
    lastupdate bigint NOT NULL,
    version bigint NOT NULL,
    CONSTRAINT cf_clients_categorydiscount_info_pk PRIMARY KEY (idofclientdtiszndiscountinfo),
    CONSTRAINT cf_clients_categorydiscount_info_idofclient_dtiszncode_uq UNIQUE (idofclient, dtiszncode),
    CONSTRAINT cf_client_dtiszn_discount_info_idofclient_fk FOREIGN KEY (idofclient)
        REFERENCES cf_clients (idofclient) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)
WITH (
    OIDS = FALSE
);

--! ФИНАЛИЗИРОВАН 26.10.2018, НЕ МЕНЯТЬ