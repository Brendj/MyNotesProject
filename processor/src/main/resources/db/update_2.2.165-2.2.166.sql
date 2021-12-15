--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.166

-- таблица заявлений на льготное питание
ALTER TABLE cf_applications_for_food
    add column servicenumber character varying(128) NOT NULL,
    add column creatortype integer NOT NULL DEFAULT 0,
    add column idofdocorder character varying(128),
    add column docorderdate bigint,
    drop column idoforgoncreate,
    add CONSTRAINT cf_applications_for_food_servicenumber_uq UNIQUE(servicenumber);

CREATE INDEX cf_applications_for_food_servicenumber_idx on cf_applications_for_food(servicenumber);

ALTER TABLE cf_client_dtiszn_discount_info
    ALTER column dtiszncode TYPE bigint;

--таблица для хранения входящих сообщений из очереди ЕТП
create table cf_etp_incoming_message
(
    etpMessageId character varying(30) NOT NULL,
    etpMessagePayload text NOT NULL,
    createdDate bigint NOT NULL,
    lastUpdate bigint,
    isProcessed integer NOT NULL DEFAULT 0,
    CONSTRAINT cf_etp_incoming_message_pk PRIMARY KEY (etpMessageId)
)
WITH (
OIDS = FALSE
);

--таблица отправленных статусов заявлений
create table cf_etp_outgoing_message
(
    idOfEtpOutgoingMessage bigserial,
    etpMessageId character varying(30) NOT NULL,
    etpMessagePayload text NOT NULL,
    createdDate bigint NOT NULL,
    lastUpdate bigint,
    isSent integer NOT NULL DEFAULT 0,
    CONSTRAINT cf_etp_outgoing_message_pk PRIMARY KEY (idOfEtpOutgoingMessage)
)
WITH (
OIDS = FALSE
);

--таблица очереди BK на сообщения из ЕТП
create table cf_etp_bk_message
(
    idOfEtpBKMessage bigserial NOT NULL ,
    message text NOT NULL,
    createdDate bigint NOT NULL,
    lastUpdate bigint,
    isSent integer NOT NULL DEFAULT 0,
    CONSTRAINT cf_etp_bk_message_pk PRIMARY KEY (idOfEtpBKMessage)
)
WITH (
OIDS = FALSE
);

-- код льготы ЕТП
ALTER TABLE cf_categorydiscounts_dszn
    add column etpcode bigint;

--! ФИНАЛИЗИРОВАН 26.11.2018, НЕ МЕНЯТЬ