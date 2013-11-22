-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.2.0 to protoVersion 0.2.1.5

CONNECT 'jdbc:derby:ecafe_processor_db';

CREATE TABLE CF_MenuExchange (
  MenuDate                BIGINT            NOT NULL,
  IdOfOrg                 BIGINT            NOT NULL,
  MenuData			VARCHAR(32650)		    NOT NULL,
  CONSTRAINT CF_MenuExchange_pk PRIMARY KEY (MenuDate, IdOfOrg),
  CONSTRAINT CF_MenuExchange_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg) ON DELETE CASCADE
);

CREATE TABLE CF_MenuExchangeRules (
  IdOfSourceOrg           BIGINT            NOT NULL,
  IdOfDestOrg             BIGINT            NOT NULL,
  CONSTRAINT CF_MenuExchangeRules_pk PRIMARY KEY (IdOfSourceOrg, IdOfDestOrg),
  CONSTRAINT CF_MenuExchangeRules_IdOfSourceOrg_fk FOREIGN KEY (IdOfSourceOrg) REFERENCES CF_Orgs (IdOfOrg) ON DELETE CASCADE,
  CONSTRAINT CF_MenuExchangeRules_IdOfDestOrg_fk FOREIGN KEY (IdOfDestOrg) REFERENCES CF_Orgs (IdOfOrg) ON DELETE CASCADE
);

