-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.0.2 to protoVersion 0.0.3

CONNECT 'jdbc:derby:ecafe_processor_db';

CREATE TABLE CF_ClientPaymentOrders (
  IdOfClientPaymentOrder  BIGINT        NOT NULL,
  IdOfContragent          BIGINT        NOT NULL,
  IdOfClient              BIGINT        NOT NULL,
  PayOrderType            INTEGER       NOT NULL,
  PaySum                  BIGINT        NOT NULL,
  CreateTime              BIGINT        NOT NULL,
  CONSTRAINT CF_ClientPaymentOrders_pk PRIMARY KEY (IdOfClientPaymentOrder),
  CONSTRAINT CF_ClientPaymentOrders_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ClientPaymentOrders_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

ALTER TABLE CF_ClientPayments ADD IdOfPaymentTemp BIGINT NOT NULL DEFAULT 0;
UPDATE CF_ClientPayments SET IdOfPaymentTemp = IdOfPayment;
ALTER TABLE CF_ClientPayments DROP IdOfPayment;
ALTER TABLE CF_ClientPayments ADD IdOfPayment VARCHAR(128) NOT NULL DEFAULT '';
UPDATE CF_ClientPayments SET IdOfPayment = CAST(IdOfPaymentTemp AS CHAR(3));
ALTER TABLE CF_ClientPayments DROP IdOfPaymentTemp;

ALTER TABLE CF_ClientPayments ADD IdOfClientPaymentOrder BIGINT;
ALTER TABLE CF_ClientPayments ADD CONSTRAINT CF_ClientPayments_IdOfClientPaymentOrder_fk FOREIGN KEY (IdOfClientPaymentOrder) REFERENCES CF_ClientPaymentOrders (IdOfClientPaymentOrder);

ALTER TABLE CF_Generators ADD IdOfClientPaymentOrder BIGINT NOT NULL DEFAULT 0;