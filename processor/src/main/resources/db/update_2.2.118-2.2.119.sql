--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.118

--Таблица для lastClientContractId
CREATE TABLE cf_orgs_contract_ids
(
  idoforg bigint NOT NULL,
  version bigint NOT NULL,
  lastclientcontractid bigint NOT NULL default 0,
  CONSTRAINT cf_orgs_contract_ids_pk PRIMARY KEY (idoforg),
  CONSTRAINT cf_orgs_contract_ids_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);

insert into cf_orgs_contract_ids(idoforg, lastclientcontractid, version)
  select idoforg, lastclientcontractid, 0 from cf_orgs;

--ИНдексы на таблицу истороии синхронизации
CREATE INDEX cf_synchistory_calc2_idoforg_idx ON cf_synchistory_calc2 USING btree (idoforg);
CREATE INDEX cf_synchistory_calc2_calcdateat_idx ON cf_synchistory_calc2 USING btree (calcdateat);

--! ФИНАЛИЗИРОВАН
