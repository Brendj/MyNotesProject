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

ALTER TABLE cf_orgs ADD COLUMN idofcontract bigint;
ALTER TABLE cf_orgs ADD CONSTRAINT cf_orgs_contract FOREIGN KEY (idofcontract) REFERENCES cf_contracts (idofcontract);
