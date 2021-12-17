--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 195

--Таблица с реквизитами Компании
create table cf_atol_company
(
  idofatolcompany bigserial NOT NULL,
  email_org character varying(128),
  tax_type character varying(128),
  inn character varying(12),
  place character varying(256),
  email_check character varying(128),
  CONSTRAINT cf_atol_company_pk PRIMARY KEY (idofatolcompany)
);

--связь многие-ко-многим Компания-Контрагент
CREATE TABLE cf_atol_company_contragents
(
  idofatolcompanycontragent bigserial NOT NULL,
  idofatolcompany bigint,
  idofcontragent bigint,
  CONSTRAINT cf_atol_company_contragents_pk PRIMARY KEY (idofatolcompanycontragent),
  CONSTRAINT cf_atol_company_contragents_idofatolcompany FOREIGN KEY (idofatolcompany)
  REFERENCES cf_atol_company (idofatolcompany) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_atol_company_contragents_idofcontragent FOREIGN KEY (idofcontragent)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--Таблица для хранения платежей, подлежащих отправке в сторонние сервисы
create table cf_clientPayment_addons
(
  IdOfClientPaymentAddon bigserial NOT NULL,
  IdOfClientPayment bigint NOT NULL,
  createdDate bigint,
  atolStatus integer,
  atolUpdate bigint,
  CONSTRAINT cf_IdOfClientPaymentAddon_pk PRIMARY KEY (IdOfClientPaymentAddon),
  CONSTRAINT cf_cf_clientPayment_addons_clientpayment FOREIGN KEY (IdOfClientPayment)
  REFERENCES cf_clientpayments (idofclientpayment) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_clientPayment_addons_atolstatus_partial_idx
  ON cf_clientPayment_addons
  USING btree (atolStatus)
  WHERE atolStatus = 0;

--таблица отправленных пакетов в Атол
CREATE TABLE cf_atol_packets
(
  idOfAtolPacket bigserial NOT NULL,
  IdOfClientPaymentAddon bigint NOT NULL,
  request text NOT NULL,
  response text,
  atolUUid character varying(36),
  createdDate bigint NOT NULL,
  lastUpdate bigint,
  CONSTRAINT cf_atol_packets_pk PRIMARY KEY (idOfAtolPacket),
  CONSTRAINT cf_atol_packets_clientpaymentaddon FOREIGN KEY (IdOfClientPaymentAddon)
  REFERENCES cf_clientpayment_addons (IdOfClientPaymentAddon) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_atol_packets_idofclientpaymentaddon
ON cf_atol_packets
USING btree (IdOfClientPaymentAddon);

--! ФИНАЛИЗИРОВАН 02.10.2019, НЕ МЕНЯТЬ