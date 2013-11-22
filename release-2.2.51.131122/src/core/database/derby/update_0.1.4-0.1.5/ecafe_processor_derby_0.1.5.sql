-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.4 to protoVersion 0.1.5

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_ClientPayments ADD AddPaymentMethod VARCHAR(1024);
ALTER TABLE CF_ClientPayments ADD AddIdOfPayment VARCHAR(1024);