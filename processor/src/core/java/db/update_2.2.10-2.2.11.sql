CREATE TABLE CF_TransactionJournal
(
  idOfTransactionJournal bigserial NOT NULL,
  transDate bigint NOT NULL,
  idOfOrg bigint NOT NULL,
  idOfInternalOperation bigint NOT NULL,
  OGRN character varying(32),
  clientSan character varying(15),
  clientType character varying(32),
  enterName character varying(100),
  serviceCode character varying(10),
  transactionCode character varying(10),
  cardTypeCode character varying(10),
  cardIdentityCode character varying(10),
  cardIdentityName character varying(32),
  contractId bigint,
  financialAmount bigint,
  accountingDate bigint,
  CONSTRAINT cf_transaction_journal_pk PRIMARY KEY (idOfTransactionJournal)
);
