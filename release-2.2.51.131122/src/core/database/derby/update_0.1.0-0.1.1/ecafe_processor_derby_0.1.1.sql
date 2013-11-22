-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.0 to protoVersion 0.1.1

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Orgs ADD SSOPassword VARCHAR(128);

-- For Gymn19
update CF_Orgs set SSOPassword = 'Z0h2ZXRIRG5pMzc1MzAyZGVmd21raDRGR0tNYjgzZnJu' where IdOfOrg = 1; 

 

