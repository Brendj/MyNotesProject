--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений ATOL

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