DROP TABLE CF_EnterEvents;
CREATE TABLE CF_EnterEvents (
  IdOfEnterEvent          bigint    NOT NULL,
  IdOfOrg                 bigint NOT NULL,
  EnterName               varchar(100) NOT NULL,
  TurnstileAddr           varchar(20) NOT NULL,
  PassDirection           integer NOT NULL,
  EventCode               integer NOT NULL,
  IdOfCard                bigint,
  IdOfClient              bigint,
  IdOfTempCard            bigint,
  EvtDateTime             bigint NOT NULL,
  CONSTRAINT CF_EnterEvents_pk PRIMARY KEY (IdOfEnterEvent, IdOfOrg)
);

CREATE TABLE CF_Publications (
  IdOfPublication bigint NOT NULL,
  IdOfOrg bigint NOT NULL,
  RecordStatus varchar(1) NOT NULL default 'n',
  RecordType varchar(1) NOT NULL default 'a',
  BibliographicLevel varchar(1) NOT NULL default 'm',
  HierarchicalLevel varchar(1) NOT NULL default '',
  CodingLevel varchar(1) NOT NULL default '3',
  formOfCatalogingDescription VARCHAR(1) NOT NULL default '',
  Data text,
  Author varchar(255) default NULL,
  Title varchar(512) default NULL,
  Title2 varchar(255) default NULL,
  PublicationDate varchar(15) default NULL,
  Publisher varchar(255) default NULL,
  Version bigint NOT NULL default '0',
  CONSTRAINT CF_Publication_pk PRIMARY KEY (IdOfPublication, IdOfOrg)
);

CREATE TABLE CF_Circulations (
  IdOfCirculation bigint NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfPublication bigint NOT NULL,
  IdOfOrg bigint NOT NULL,
  IssuanceDate bigint NOT NULL default 0,
  RefundDate bigint NOT NULL default 0,
  RealRefundDate bigint,
  Status int NOT NULL default 0,
  Version bigint NOT NULL default 0,
  CONSTRAINT CF_Circulation_pk PRIMARY KEY (IdOfCirculation, IdOfOrg),
  CONSTRAINT CF_Circulation_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Circulation_IdOfPublication_fk FOREIGN KEY (IdOfPublication, IdOfOrg) REFERENCES CF_Publications (IdOfPublication, IdOfOrg)
);


-- Добавление протоколирования контрагента-получателя в БД для возможности последующего фильтра
alter table CF_ReportInfo add column IdOfContragentReceiver bigint default null;
alter table CF_ReportInfo add column ContragentReceiver varchar(128) default null;
alter table CF_Contragents add column OKATO varchar(11) default '';