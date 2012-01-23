-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.2 to protoVersion 0.1.3

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Cards ADD CardPrintedNo BIGINT;
