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

CREATE TABLE cf_linking_tokens
(
  IdOfLinkingToken   BIGSERIAL    NOT NULL,
  IdOfClient         BIGINT       UNIQUE NOT NULL,
  Token              VARCHAR(20)  UNIQUE NOT NULL,
  CONSTRAINT cf_linking_tokens_pk PRIMARY KEY (IdOfLinkingToken)
);
CREATE index "cf_tokens_idofclient_idx" ON cf_linking_tokens (IdOfClient);
CREATE index "cf_tokens_token_idx" ON cf_linking_tokens (Token);
ALTER TABLE cf_linking_tokens ADD CONSTRAINT cf_linking_tokens_idofclient FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient);


-- организации связаны с контрактами многие к одному соответсвенно
ALTER TABLE cf_orgs ADD COLUMN idofcontract bigint;
ALTER TABLE cf_orgs ADD CONSTRAINT cf_orgs_contract FOREIGN KEY (idofcontract) REFERENCES cf_contracts (idofcontract);

--время последней удачной синхронизации балансов
ALTER TABLE cf_orgs ADD COLUMN lastSucBalanceSync BIGINT;
--время последней неудачной синхронизации балансов
ALTER TABLE cf_orgs ADD COLUMN lastUnsucBalanceSync BIGINT;

ALTER TABLE cf_publs DROP COLUMN hash;
ALTER TABLE cf_publs DROP COLUMN isbn;
ALTER TABLE cf_publs ADD COLUMN isbn character varying(32);
ALTER TABLE cf_publs ADD COLUMN hash character varying(128);

--! ФИНАЛИЗИРОВАН (Кадыров, 120812) НЕ МЕНЯТЬ