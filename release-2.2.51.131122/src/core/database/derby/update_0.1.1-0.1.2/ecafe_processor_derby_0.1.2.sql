-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Update from protoVersion 0.1.1 to protoVersion 0.1.2

CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_Users ADD Version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Orgs ADD Version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Contragents ADD Version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Clients ADD Version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Cards ADD Version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CF_Registry ADD Version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Registry ADD SmsId CHAR(32) NOT NULL DEFAULT '00000000000000000000000000000000';
ALTER TABLE CF_Registry ADD IdOfOrder BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Registry ADD IdOfOrderDetail BIGINT NOT NULL DEFAULT 0;

CREATE TABLE CF_ClientSms (
  IdOfSms                 CHAR(32)        NOT NULL,
  Version                 BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  Phone                   VARCHAR(32)     NOT NULL,        
  ContentsType            INTEGER         NOT NULL,
  TextContents            VARCHAR(70)     NOT NULL,
  DeliveryStatus          INTEGER         NOT NULL,
  ServiceSendDate         BIGINT          NOT NULL,
  SendDate                BIGINT,
  DeliveryDate            BIGINT,
  Price                   BIGINT          NOT NULL,        
  CONSTRAINT CF_ClientSms_pk PRIMARY KEY (IdOfSms),
  CONSTRAINT CF_ClientSms_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

ALTER TABLE CF_Orgs ADD SmsSender VARCHAR(32);
ALTER TABLE CF_Orgs ADD PriceOfSms BIGINT NOT NULL DEFAULT 0;
DROP TABLE CF_Tariffs;

INSERT INTO CF_Persons(IdOfPerson, FirstName, Surname, SecondName, IDDocument) values (0, '', '', '', '');
INSERT INTO CF_Orgs(
  IdOfOrg, Version, ShortName, OfficialName,
  Address, IdOfOfficialPerson, OfficialPosition, ContractId,
  ContractDate, State, CardLimit, PublicKey,
  IdOfPacket, LastClientContractId, PriceOfSms)
VALUES (
  0, 0, 'Novaya shkola', 'Novaya shkola',
  '', 0, '', '',
  0, 1, 0, '',
  0, 0, 0);

DROP TABLE CF_Events;
ALTER TABLE CF_Generators DROP IdOfEvent;


     

 

