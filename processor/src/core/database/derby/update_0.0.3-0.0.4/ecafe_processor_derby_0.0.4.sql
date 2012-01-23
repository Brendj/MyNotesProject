-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.0.3 to protoVersion 0.0.4

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Contragents ADD CONSTRAINT CF_Contragents_ContragentName UNIQUE (ContragentName);
ALTER TABLE CF_ClientPaymentOrders DROP PayOrderType;
ALTER TABLE CF_ClientPaymentOrders ADD PaymentMethod INTEGER NOT NULL DEFAULT 0;
ALTER TABLE CF_ClientPaymentOrders ADD OrderStatus INTEGER NOT NULL DEFAULT 0;
ALTER TABLE CF_ClientPaymentOrders ADD IdOfPayment VARCHAR(128) NOT NULL DEFAULT '';
ALTER TABLE CF_ClientPaymentOrders ADD ContragentSum BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CF_ClientPayments ADD PaymentMethod INTEGER NOT NULL DEFAULT 4;

ALTER TABLE CF_Clients ADD CONSTRAINT CF_Clients_ContractId UNIQUE (ContractId);