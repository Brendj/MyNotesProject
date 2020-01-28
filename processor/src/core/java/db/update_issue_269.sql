--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 196

CREATE TABLE cf_regularpayment_status
(
  idofregularpaymentstatus bigserial NOT NULL,
  idofregularpayment bigint NOT NULL,
  errorcode integer,
  description character varying(255),
  statusdate bigint,
  createddate bigint NOT NULL,
  CONSTRAINT cf_regularpayment_status_pk PRIMARY KEY (idofregularpaymentstatus),
  CONSTRAINT cf_regularpayment_status_idofregularpayment_fk FOREIGN KEY (idofregularpayment)
  REFERENCES cf_regular_payments (idofpayment) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

alter table cf_bank_subscriptions add column mobile character varying(32);

alter table cf_regular_payments add column idofclientpayment bigint,
  add column errorcode integer,
  add column errordesc character varying(200);