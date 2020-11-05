ALTER TABLE CF_Orders ADD COLUMN  State           INT           NOT NULL DEFAULT 0;
ALTER TABLE CF_OrderDetails ADD COLUMN  State           INT           NOT NULL DEFAULT 0;
ALTER TABLE CF_Transactions ADD COLUMN BalanceBefore  BIGINT;
ALTER TABLE CF_OrderDetails ADD COLUMN ItemCode VARCHAR(32);
