-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.3 to protoVersion 0.1.4

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Contragents ADD NeedAccountTranslate INTEGER NOT NULL DEFAULT 0;

CREATE TABLE CF_ContragentClientAccounts (
  IdOfContragent          BIGINT            NOT NULL,
  IdOfAccount             BIGINT            NOT NULL,
  IdOfClient              BIGINT            NOT NULL,
  CONSTRAINT CF_ContragentClientAccounts_pk PRIMARY KEY (IdOfContragent, IdOfAccount),
  CONSTRAINT CF_ContragentClientAccounts_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ContragentClientAccounts_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);