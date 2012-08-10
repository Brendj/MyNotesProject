-- Контракты оррганизаций
CREATE TABLE cf_contracts
(
  idofcontract bigserial NOT NULL,
  contractnumber character varying(50),
  performer character varying(128),
  customer character varying(128),
  dateofconclusion timestamp with time zone,
  dateofclosing timestamp with time zone,
  contractstate integer NOT NULL DEFAULT 0,
  CONSTRAINT cf_contracts_pk PRIMARY KEY (idofcontract )
);

-- организации связаны с контрактами многие к одному соответсвенно
ALTER TABLE cf_orgs ADD COLUMN idofcontract bigint;
ALTER TABLE cf_orgs ADD CONSTRAINT cf_orgs_contract FOREIGN KEY (idofcontract) REFERENCES cf_contracts (idofcontract);

--время последней удачной синхронизации балансов
ALTER TABLE cf_orgs ADD COLUMN lastsucbalancesync bigint;
--время последней неудачной синхронизации балансов
ALTER TABLE cf_orgs ADD COLUMN lastunsucbalancesync bigint;

ALTER TABLE cf_publs DROP COLUMN hash;
ALTER TABLE cf_publs DROP COLUMN isbn;
ALTER TABLE cf_publs ADD COLUMN isbn character varying(32);
ALTER TABLE cf_publs ADD COLUMN hash character varying(128);