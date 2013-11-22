-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Additional update from protoVersion 0.1.1 to protoVersion 0.1.2 for Gymn19 (IdOfSms)

CONNECT 'jdbc:derby:ecafe_processor_db';

UPDATE CF_ClientSms SET DeliveryStatus = 2 WHERE DeliveryStatus = 0 OR DeliveryStatus = 1;  
UPDATE CF_Registry SET SmsId = '00000000000000000000000000001000';





     

 

