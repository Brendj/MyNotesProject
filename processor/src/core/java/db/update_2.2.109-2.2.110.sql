--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.109

--История контрактов по организациям
CREATE TABLE cf_contract_org
(
  idofcontractorg bigserial not null,
  idofcontract bigint not null,
  idoforg bigint not null,
  lastversionofcontract bigint not null,
  createddate bigint not null,
  CONSTRAINT cf_contract_org_pk PRIMARY KEY (idofcontractorg),
  CONSTRAINT cf_contract_org_idofcontract_fk FOREIGN KEY (idofcontract)
  REFERENCES cf_contracts (idofcontract) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_contract_org_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);

--Таблица для клиентов, отражающая факт перехода баланса клиента в минус или большой плюс.
CREATE TABLE cf_balance_leaving
  (
  idofbalanceleaving bigserial not null,
  idofclient bigint not null,
  datepacketprocessing bigint not null,
  beforereducebalance bigint,
  finalbalance bigint,
  CONSTRAINT cf_idofbalanceleaving PRIMARY KEY (idofbalanceleaving)
);

