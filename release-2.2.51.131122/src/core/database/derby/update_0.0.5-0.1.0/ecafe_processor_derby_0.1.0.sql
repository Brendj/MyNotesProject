-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.0.5 to protoVersion 0.1.0

CONNECT 'jdbc:derby:ecafe_processor_db';

INSERT INTO CF_Functions(IdOfFunction, FunctionName) VALUES(4, 'payProcess');
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(1, 4);
update CF_Generators set IdOfFunction = 2 where IdOfFunction = 1;

 

