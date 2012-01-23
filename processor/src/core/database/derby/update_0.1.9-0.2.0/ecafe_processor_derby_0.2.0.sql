-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.9 to protoVersion 0.2.0

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Transactions ADD IdOfClient BIGINT NOT NULL DEFAULT 0;
UPDATE CF_Transactions t SET t.IdOfClient = (select c.IdOfClient from CF_Cards c where c.IdOfCard = t.IdOfCard);
ALTER TABLE CF_Transactions
  ADD CONSTRAINT CF_Transactions_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient);

ALTER TABLE CF_Transactions ADD IdOfCard_temp BIGINT;
UPDATE CF_Transactions SET IdOfCard_temp = IdOfCard;
ALTER TABLE CF_Transactions DROP CONSTRAINT CF_Transactions_IdOfCard_fk;
ALTER TABLE CF_Transactions DROP IdOfCard;
ALTER TABLE CF_Transactions ADD IdOfCard BIGINT;
UPDATE CF_Transactions SET IdOfCard = IdOfCard_temp;
ALTER TABLE CF_Transactions
  ADD CONSTRAINT CF_Transactions_IdOfCard_fk FOREIGN KEY (IdOfCard) REFERENCES CF_Cards (IdOfCard);
ALTER TABLE CF_Transactions DROP IdOfCard_temp;

ALTER TABLE CF_ClientPayments DROP CONSTRAINT CF_ClientPayments_IdOfClient_fk;
ALTER TABLE CF_ClientPayments DROP IdOfClient;
ALTER TABLE CF_ClientPayments DROP CONSTRAINT CF_ClientPayments_IdOfCard_fk;
ALTER TABLE CF_ClientPayments DROP IdOfCard;

ALTER TABLE CF_Orgs ADD SubscriptionPrice BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CF_ClientSms ADD IdOfTransaction BIGINT;
ALTER TABLE CF_ClientSms ADD
  CONSTRAINT CF_ClientSms_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction);

CREATE TABLE CF_SubscriptionFee (
  SubscriptionYear     INTEGER            NOT NULL,
  PeriodNo             INTEGER            NOT NULL,
  IdOfTransaction      BIGINT             NOT NULL,
  SubscriptionSum      BIGINT             NOT NULL,
  CreateTime           BIGINT             NOT NULL,
  CONSTRAINT CF_SubscriptionFee_pk PRIMARY KEY (SubscriptionYear, PeriodNo),
  CONSTRAINT CF_SubscriptionFee_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction)
);

-- Delete SMS transactions, orders and order details with SumByCard = 0
DELETE FROM CF_OrderDetails d WHERE d.IdOfOrg = 0 AND EXISTS (SELECT * FROM CF_Orders o WHERE o.IdOfOrg = d.IdOfOrg AND o.IdOfOrder = d.IdOfOrder AND o.SumByCard = 0);
ALTER TABLE CF_Orders ADD IdOfTransaction_temp BIGINT;
UPDATE CF_Orders o SET o.IdOfTransaction_temp = o.IdOfTransaction;
UPDATE CF_Orders o SET o.IdOfTransaction = NULL WHERE o.IdOfOrg = 0 AND o.SumByCard = 0;  
DELETE FROM CF_Transactions t WHERE EXISTS (SELECT * FROM CF_Orders o WHERE o.IdOfOrg = 0 AND t.IdOfTransaction = o.IdOfTransaction_temp AND o.SumByCard = 0);
DELETE FROM CF_Orders o WHERE o.IdOfOrg = 0 AND o.SumByCard = 0;
ALTER TABLE CF_Orders DROP IdOfTransaction_temp; 

-- Relink SMS pay transactions to CF_ClientSms and delete related CF_Orders and CF_OrderDetails
UPDATE CF_ClientSms s SET s.IdOfTransaction =
  (SELECT o.IdOfTransaction FROM CF_Orders o
    WHERE o.IdOfOrg = 0 AND s.IdOfClient = o.IdOfClient AND s.ServiceSendDate <= o.CreatedDate AND o.CreatedDate - s.ServiceSendDate < 10000);

-- Delete SMS order detail and orders
DELETE FROM CF_OrderDetails WHERE IdOfOrg = 0;
DELETE FROM CF_Orders WHERE IdOfOrg = 0;  

-- Delete Novaya Shkola org
ALTER TABLE CF_Registry DROP IdOfOrder;
ALTER TABLE CF_Registry DROP IdOfOrderDetail;
DELETE FROM CF_Orgs WHERE IdOfOrg = 0;