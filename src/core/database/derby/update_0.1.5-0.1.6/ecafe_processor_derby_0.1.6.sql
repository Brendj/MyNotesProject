-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.5 to protoVersion 0.1.6

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Registry DROP SmsId;
ALTER TABLE CF_Registry ADD SmsId CHAR(16) NOT NULL DEFAULT '0000000000000000';