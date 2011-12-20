-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.2.0 to protoVersion 0.2.1.5

CONNECT 'jdbc:derby:ecafe_processor_db';

CREATE TABLE CF_SochiClients (
  ContractId              BIGINT            NOT NULL,
  CreateTime              BIGINT            NOT NULL,
  UpdateTime              BIGINT            NOT NULL,
  FullName                VARCHAR(255)      NOT NULL,
  Address                 VARCHAR(255),
  CONSTRAINT CF_SochiClients_pk PRIMARY KEY (ContractId)
);

CREATE TABLE CF_SochiClientPayments (
  PaymentId               BIGINT            NOT NULL,
  ContractId              BIGINT            NOT NULL,
  PaymentSum              BIGINT            NOT NULL,
  PaymentSumF             BIGINT            NOT NULL,
  PaymentTime             BIGINT            NOT NULL,
  TerminalId              BIGINT            NOT NULL,
  CreateTime              BIGINT            NOT NULL,
  CONSTRAINT CF_SochiClientPayments_pk PRIMARY KEY (PaymentId),
  CONSTRAINT CF_SochiClientPayments_ContractId_fk FOREIGN KEY (ContractId) REFERENCES CF_SochiClients (ContractId)
);
