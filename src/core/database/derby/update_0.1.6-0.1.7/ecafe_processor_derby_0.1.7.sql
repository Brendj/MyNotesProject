-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.6 to protoVersion 0.1.7

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_OrderDetails ADD RootMenu VARCHAR(32) NOT NULL DEFAULT '';