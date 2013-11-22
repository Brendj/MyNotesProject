-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.0.1 to protoVersion 0.0.2

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_OrderDetails ADD MenuDetailNameTemp VARCHAR(256) NOT NULL DEFAULT '';
UPDATE CF_OrderDetails SET MenuDetailNameTemp = MenuDetailName;
ALTER TABLE CF_OrderDetails DROP MenuDetailName;
ALTER TABLE CF_OrderDetails ADD MenuDetailName VARCHAR(256) NOT NULL DEFAULT '';
UPDATE CF_OrderDetails SET MenuDetailName = MenuDetailNameTemp;
ALTER TABLE CF_OrderDetails DROP MenuDetailNameTemp;

ALTER TABLE CF_MenuDetails ADD MenuDetailNameTemp VARCHAR(256) NOT NULL DEFAULT '';
UPDATE CF_MenuDetails SET MenuDetailNameTemp = MenuDetailName;
ALTER TABLE CF_MenuDetails DROP MenuDetailName;
ALTER TABLE CF_MenuDetails ADD MenuDetailName VARCHAR(256) NOT NULL DEFAULT '';
UPDATE CF_MenuDetails SET MenuDetailName = MenuDetailNameTemp;
ALTER TABLE CF_MenuDetails DROP MenuDetailNameTemp;

ALTER TABLE CF_Clients ADD FreePayMaxCount INTEGER;
ALTER TABLE CF_Clients ADD FreePayCount INTEGER NOT NULL DEFAULT 0;
ALTER TABLE CF_Clients ADD LastFreePayTime BIGINT;
ALTER TABLE CF_Clients ADD DiscountMode INTEGER NOT NULL DEFAULT 0;

ALTER TABLE CF_Orders ADD GrantSum BIGINT NOT NULL DEFAULT 0; 