--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.164

--таблица блокировок баланса после перехода к другому поставщику
CREATE TABLE cf_clientbalance_hold
(
  idOfClientBalanceHold bigserial NOT NULL,
  guid character varying(36),
  idOfClient bigint NOT NULL,
  idOfDeclarer bigint,
  phoneOfDeclarer character varying(20),
  holdSum bigint NOT NULL,
  idOfTransaction bigint NOT NULL,
  idOfOldOrg bigint NOT NULL,
  idOfNewOrg bigint,
  idOfOldContragent bigint NOT NULL,
  idOfNewContragent bigint,
  createStatus integer not null default 0,
  requestStatus integer not null default 0,
  createdDate bigint NOT NULL,
  lastUpdate bigint,
  version bigint NOT NULL DEFAULT 0,
  CONSTRAINT cf_clientbalance_hold_pk PRIMARY KEY (idOfClientBalanceHold),
  CONSTRAINT cf_clientbalance_hold_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idofdeclarer_fk FOREIGN KEY (idofdeclarer)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idofoldorg_fk FOREIGN KEY (idofoldorg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idofneworg_fk FOREIGN KEY (idofneworg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idofoldcontragent_fk FOREIGN KEY (idofoldcontragent)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idofnewcontragent_fk FOREIGN KEY (idofnewcontragent)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_clientbalance_hold_idoftransaction_fk FOREIGN KEY (idoftransaction)
  REFERENCES cf_transactions (idoftransaction) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
) WITH (
OIDS = FALSE
);

--! ФИНАЛИЗИРОВАН 02.10.2018, НЕ МЕНЯТЬ