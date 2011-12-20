-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.8 to protoVersion 0.1.9

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Clients ADD Balance_temp BIGINT;
ALTER TABLE CF_Clients ADD Limit_temp BIGINT;

ALTER TABLE CF_Clients ADD Balance BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Clients ADD Limit BIGINT NOT NULL DEFAULT 0;

UPDATE CF_Clients SET Balance_temp = (SELECT SUM(Balance) FROM CF_Cards WHERE CF_Clients.IdOfClient = CF_Cards.IdOfClient);
UPDATE CF_Clients SET Balance_temp = 0 WHERE CF_Clients.Balance_temp IS NULL;
UPDATE CF_Clients SET Balance = Balance_temp;

UPDATE CF_Clients SET Limit_temp = (SELECT MIN(Limit) FROM CF_Cards WHERE CF_Clients.IdOfClient = CF_Cards.IdOfClient);
UPDATE CF_Clients SET Limit_temp = 0 WHERE CF_Clients.Limit_temp IS NULL;
UPDATE CF_Clients SET Limit = Limit_temp;

ALTER TABLE CF_Clients DROP Balance_temp;
ALTER TABLE CF_Clients DROP Limit_temp;
ALTER TABLE CF_Cards DROP Balance;
ALTER TABLE CF_Cards DROP Limit;