-- Пакет обновлений 2.2.46
-- Изменения в связи с ECAFE-1154
ALTER TABLE CF_SubscriptionFee DROP CONSTRAINT CF_SubscriptionFee_pk;
ALTER TABLE CF_SubscriptionFee ADD COLUMN IdOfSubscriptionFee BIGSERIAL;
ALTER TABLE CF_SubscriptionFee ADD CONSTRAINT CF_SubscriptionFee_pk PRIMARY KEY (IdOfSubscriptionFee);
ALTER TABLE CF_SubscriptionFee ADD COLUMN SubscriptionType INTEGER NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН (Калимуллин, 130926) НЕ МЕНЯТЬ