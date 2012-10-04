﻿-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Version 0.2.1.5

-- Create tables
CREATE TABLE CF_Persons (
  IdOfPerson    BIGINT        NOT NULL,
  FirstName     VARCHAR(64)   NOT NULL,
  Surname       VARCHAR(128)  NOT NULL,
  SecondName    VARCHAR(128)  NOT NULL,
  IDDocument    VARCHAR(128),
  CONSTRAINT CF_Persons_pk PRIMARY KEY (IdOfPerson)
);

CREATE TABLE CF_Contragents (
  IdOfContragent          BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  IdOfContactPerson       BIGINT            NOT NULL,
  ParentId                INTEGER,
  ContragentName          VARCHAR(128)      NOT NULL,
  ClassId                 INTEGER           NOT NULL,
  Flags                   INTEGER           NOT NULL,
  Title                   VARCHAR(30)       NOT NULL,
  Address                 VARCHAR(128)      NOT NULL,
  Phone                   VARCHAR(32),
  Mobile                  VARCHAR(32),
  Email                   VARCHAR(128),
  Fax                     VARCHAR(32),
  Remarks                 VARCHAR(1024),
  INN                     VARCHAR(90),
  Bank                    VARCHAR(90),
  BIC                     CHAR(15),
  CorrAccount             VARCHAR(20),
  Account                 VARCHAR(20),
  CreatedDate             BIGINT            NOT NULL,
  LastUpdate              BIGINT            NOT NULL,
  PublicKey               VARCHAR(1024)     NOT NULL,
  PublicKeyGOSTAlias varchar(64), --v22
  NeedAccountTranslate    INTEGER           NOT NULL,
  CONSTRAINT CF_Contragents_pk PRIMARY KEY (IdOfContragent),
  CONSTRAINT CF_Contragents_ContragentName UNIQUE (ContragentName),
  CONSTRAINT CF_Contragents_IdOfContactPerson_fk FOREIGN KEY (IdOfContactPerson) REFERENCES CF_Persons (IdOfPerson)
);

CREATE TABLE CF_POS (
  IdOfPos        BIGINT         NOT NULL,
  IdOfContragent BIGINT         NOT NULL,
  Name           VARCHAR(128)   NOT NULL,
  Description    VARCHAR(128),
  CreatedDate    BIGINT         NOT NULL,
  State          int,
  Flags          int,
  PublicKey      VARCHAR(1024)  NOT NULL,
  CONSTRAINT CF_POS_pk PRIMARY KEY (IdOfPos),
  CONSTRAINT CF_POS_IdOfContragentReceiver_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
);


--v21
-- Контракты оррганизаций
CREATE TABLE cf_contracts
(
  idofcontract bigserial NOT NULL,
  contractnumber character varying(50),
  performer character varying(128),
  customer character varying(128),
  dateofconclusion timestamp with time zone,
  dateofclosing timestamp with time zone,
  contractstate integer NOT NULL DEFAULT 0,
  CONSTRAINT cf_contracts_pk PRIMARY KEY (idofcontract )
);
--

CREATE TABLE CF_Orgs (
  IdOfOrg                 BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  ShortName               VARCHAR(128)      NOT NULL,
  OfficialName            VARCHAR(128)      NOT NULL,
  Address                 VARCHAR(128)      NOT NULL,
  Phone                   VARCHAR(32),
  IdOfOfficialPerson      BIGINT            NOT NULL,
  OfficialPosition        VARCHAR(128)      NOT NULL,
  ContractId              VARCHAR(50)       NOT NULL,
  ContractDate            BIGINT            NOT NULL,
  State                   INTEGER           NOT NULL,
  CardLimit               BIGINT            NOT NULL,
  PublicKey               VARCHAR(1024)     NOT NULL,
  IdOfPacket              BIGINT            NOT NULL,
  LastClientContractId    BIGINT            NOT NULL,
  SSOPassword             VARCHAR(128),
  SmsSender               VARCHAR(32),
  PriceOfSms              BIGINT            NOT NULL,
  SubscriptionPrice       BIGINT            NOT NULL,
  DefaultSupplier         BIGINT            NOT NULL,
  OGRN                    character varying(32),
  INN                     character varying(32),
  mailingListReportsOnNutrition character varying(1024),
  mailingListReportsOnVisits character varying(1024),
  mailingListReports1 character varying(1024),
  mailingListReports2 character varying(1024),
  IdOfConfigurationProvider bigint, --v20
  guid varchar(40), --v20
  idofcontract bigint, --v21,
  lastSucBalanceSync BIGINT, --v21
  lastUnsucBalanceSync BIGINT, --v21
  CONSTRAINT CF_Orgs_pk PRIMARY KEY (IdOfOrg),
  CONSTRAINT CF_Orgs_ShortName UNIQUE (ShortName),
  CONSTRAINT CF_Orgs_IdOfOfficialPerson_fk FOREIGN KEY (IdOfOfficialPerson) REFERENCES CF_Persons (IdOfPerson),
  CONSTRAINT CF_Orgs_DefaultSupplier_fk FOREIGN KEY (DefaultSupplier) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT cf_orgs_contract FOREIGN KEY (idofcontract) REFERENCES cf_contracts (idofcontract) --v21
);

CREATE TABLE CF_ClientGroups (
  IdOfOrg                 BIGINT            NOT NULL,
  IdOfClientGroup         BIGINT            NOT NULL,
  GroupName               VARCHAR(64)       NOT NULL,
  CONSTRAINT CF_ClientGroups_pk PRIMARY KEY (IdOfOrg, IdOfClientGroup),
  CONSTRAINT CF_ClientGroups_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_Clients (
  IdOfClient              BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  IdOfOrg                 BIGINT            NOT NULL,
  IdOfPerson              BIGINT            NOT NULL,
  IdOfContractPerson      BIGINT            NOT NULL,
  IdOfClientGroup         BIGINT,
  ClientRegistryVersion   BIGINT            NOT NULL,
  Flags                   INTEGER           NOT NULL,
  Address                 VARCHAR(128)      NOT NULL,
  Phone                   VARCHAR(32),
  Mobile                  VARCHAR(32),
  Email                   VARCHAR(128),
  fax character varying(32), --v20
  NotifyViaEmail          INTEGER           NOT NULL,
  NotifyViaSMS            INTEGER           NOT NULL,
  Image                   OID,
  Remarks                 VARCHAR(1024),
  LastUpdate              BIGINT            NOT NULL,
  ContractId              BIGINT            NOT NULL,
  ContractDate            BIGINT            NOT NULL,
  ContractState           INTEGER           NOT NULL,
  Password                VARCHAR(128)      NOT NULL,
  PayForSMS               INTEGER           NOT NULL,
  FreePayMaxCount         INTEGER,
  FreePayCount            INTEGER           NOT NULL,
  LastFreePayTime         BIGINT,
  DiscountMode            INTEGER           NOT NULL,
  Balance                 BIGINT            NOT NULL,
  "Limit"                 BIGINT            NOT NULL,
  ExpenditureLimit        BIGINT            NOT NULL DEFAULT 0,
  CategoriesDiscounts     VARCHAR(60)       NOT NULL DEFAULT '',
  San                     VARCHAR(11),
  GuardSan                VARCHAR(64),
  ExternalId              BIGINT, --v17
  ClientGUID              VARCHAR(40), --v17
  CONSTRAINT CF_Clients_pk PRIMARY KEY (IdOfClient),
  CONSTRAINT CF_Clients_ContractId UNIQUE (ContractId),
  CONSTRAINT CF_Clients_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_Clients_IdOfPerson_fk FOREIGN KEY (IdOfPerson) REFERENCES CF_Persons (IdOfPerson),
  CONSTRAINT CF_Clients_IdOfContractPerson_fk FOREIGN KEY (IdOfContractPerson) REFERENCES CF_Persons (IdOfPerson)
);

CREATE index "cf_clients_externalid_idx" ON CF_Clients (ExternalId);
CREATE index "cf_clients_clientguid_idx" ON CF_Clients (ClientGUID);

CREATE TABLE CF_ContragentClientAccounts (
  IdOfContragent          BIGINT            NOT NULL,
  IdOfAccount             BIGINT            NOT NULL,
  IdOfClient              BIGINT            NOT NULL,
  CONSTRAINT CF_ContragentClientAccounts_pk PRIMARY KEY (IdOfContragent, IdOfAccount),
  CONSTRAINT CF_ContragentClientAccounts_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ContragentClientAccounts_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_Cards (
  IdOfCard                BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  IdOfClient              BIGINT            NOT NULL,
  CardNo                  BIGINT            NOT NULL,
  CardType                INTEGER           NOT NULL,
  CreatedDate             BIGINT            NOT NULL,
  LastUpdate              BIGINT            NOT NULL,
  State                   INTEGER           NOT NULL,
  LockReason              VARCHAR(64),
  ValidDate               BIGINT            NOT NULL,
  IssueDate               BIGINT,
  LifeState               INTEGER           NOT NULL,
  CardPrintedNo           BIGINT,
  ExternalId              VARCHAR(32),
  CONSTRAINT CF_Cards_pk PRIMARY KEY (IdOfCard),
  CONSTRAINT CF_Cards_CardNo UNIQUE (CardNo),
  CONSTRAINT CF_Cards_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_Transactions (
  IdOfTransaction         BIGINT            NOT NULL,
  IdOfClient              BIGINT            NOT NULL,
  IdOfCard                BIGINT,
  TransactionSum          BIGINT            NOT NULL,
  Source                  VARCHAR(30)       NOT NULL,
  SourceType              INTEGER           NOT NULL,
  TransactionDate         BIGINT            NOT NULL,
  BalanceBefore           BIGINT,
  CONSTRAINT CF_Transactions_pk PRIMARY KEY (IdOfTransaction),
  CONSTRAINT CF_Transactions_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Transactions_IdOfCard_fk FOREIGN KEY (IdOfCard) REFERENCES CF_Cards (IdOfCard)
);

CREATE TABLE CF_Users (
  IdOfUser                BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  UserName                VARCHAR(64)       NOT NULL,
  Password                VARCHAR(128)      NOT NULL,
  LastChange              BIGINT            NOT NULL,
  Phone                   VARCHAR(32)       NOT NULL,
  IdOfContragent          BIGINT,
  email character varying(128), --v14
  CONSTRAINT CF_Users_pk PRIMARY KEY (IdOfUser),
  CONSTRAINT CF_Users_ShortName UNIQUE (UserName),
  CONSTRAINT CF_Users_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
);

CREATE TABLE CF_Functions (
  IdOfFunction  BIGINT        NOT NULL,
  FunctionName  VARCHAR(10)   NOT NULL,
  CONSTRAINT CF_Functions_pk PRIMARY KEY (IdOfFunction)
);

CREATE TABLE CF_Permissions (
  IdOfUser      BIGINT        NOT NULL,
  IdOfFunction  BIGINT        NOT NULL,
  CONSTRAINT CF_Permissions_pk PRIMARY KEY (IdOfUser, IdOfFunction),
  CONSTRAINT CF_Permissions_IdOfUser_fk FOREIGN KEY (IdOfUser) REFERENCES CF_Users (IdOfUser),
  CONSTRAINT CF_Permissions_IdOfFunction_fk FOREIGN KEY (IdOfFunction) REFERENCES CF_Functions (IdOfFunction)
);

CREATE TABLE CF_Menu (
  IdOfMenu      BIGINT        NOT NULL,
  IdOfOrg       BIGINT        NOT NULL,
  MenuDate      BIGINT        NOT NULL,
  CreatedDate   BIGINT        NOT NULL,
  MenuSource    INTEGER       NOT NULL,
  Flags         INTEGER       NOT NULL,
  CONSTRAINT CF_Menu_pk PRIMARY KEY (IdOfMenu),
  CONSTRAINT CF_Menu_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_MenuDetails (
  IdOfMenuDetail          BIGINT            NOT NULL,
  IdOfMenu                BIGINT            NOT NULL,
  MenuPath                VARCHAR(128)      NOT NULL,
  MenuDetailName          VARCHAR(128)       NOT NULL,
  GroupName               VARCHAR(60)       NOT NULL,
  MenuDetailOutput        VARCHAR(32)       NOT NULL,
  Price                   BIGINT            NOT NULL,
  MenuOrigin              INT               NOT NULL,
  AvailableNow            INT               NOT NULL,
  LocalIdOfMenu           BIGINT,
  Protein                 DECIMAL(5, 2),
  Fat                     DECIMAL(5, 2),
  Carbohydrates           DECIMAL(5, 2),
  Calories                DECIMAL(5, 2),
  VitB1                   DECIMAL(5, 2),
  VitC                    DECIMAL(5, 2),
  VitA                    DECIMAL(5, 2),
  VitE                    DECIMAL(5, 2),
  MinCa                   DECIMAL(5, 2),
  MinP                    DECIMAL(5, 2),
  MinMg                   DECIMAL(5, 2),
  MinFe                   DECIMAL(5, 2),
  CONSTRAINT CF_MenuDetail_pk PRIMARY KEY (IdOfMenuDetail),
  CONSTRAINT CF_MenuDetail_IdOfMenu_fk FOREIGN KEY (IdOfMenu) REFERENCES CF_Menu (IdOfMenu)
);

-- Indexes
CREATE index "cf_menudetail_localid_idx" ON CF_MenuDetails (LocalIdOfMenu);



CREATE TABLE CF_ComplexInfo (
  IdOfComplexInfo         BIGINT            NOT NULL,
  IdOfComplex             INT               NOT NULL,
  IdOfOrg                 BIGINT            NOT NULL,
  ComplexName             VARCHAR(60)       NOT NULL,
  MenuDate                BIGINT            NOT NULL,
  ModeFree                int               NOT NULL,
  ModeGrant               int               NOT NULL,
  ModeOfAdd               int               NOT NULL,
  CONSTRAINT CF_ComplexInfo_pk PRIMARY KEY (IdOfComplexInfo),
  CONSTRAINT CF_ComplexInfo_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_ComplexInfoDetail (
  IdOfComplexInfoDetail   BIGINT            NOT NULL,
  IdOfComplexInfo         BIGINT            NOT NULL,
  IdOfMenuDetail          BIGINT            NOT NULL,
  CONSTRAINT CF_ComplexInfoDetail_pk PRIMARY KEY (IdOfComplexInfoDetail),
  CONSTRAINT CF_ComplexInfoDetail_IdOfMenuDetail_fk FOREIGN KEY (IdOfMenuDetail) REFERENCES CF_MenuDetails (IdOfMenuDetail)
);

CREATE TABLE CF_Assortment (
  IdOfAst                 BIGINT            NOT NULL,  -- surrogate key
  IdOfOrg                 BIGINT            NOT NULL,
  BeginDate               BIGINT            NOT NULL,
  ShortName               VARCHAR(128)      NOT NULL, --v16
  FullName                VARCHAR(128)      NOT NULL, --v16
  GroupName               VARCHAR(60)       NOT NULL,
  MenuOrigin              INT               NOT NULL,
  MenuOutput              VARCHAR(32)       NOT NULL,
  Price                   BIGINT            NOT NULL,
  Protein                 DECIMAL(5, 2),
  Fat                     DECIMAL(5, 2),
  Carbohydrates           DECIMAL(5, 2),
  Calories                DECIMAL(5, 2),
  VitB1                   DECIMAL(5, 2),
  VitC                    DECIMAL(5, 2),
  VitA                    DECIMAL(5, 2),
  VitE                    DECIMAL(5, 2),
  MinCa                   DECIMAL(5, 2),
  MinP                    DECIMAL(5, 2),
  MinMg                   DECIMAL(5, 2),
  MinFe                   DECIMAL(5, 2),
  CONSTRAINT CF_Assortment_pk PRIMARY KEY (IdOfAst),
  CONSTRAINT CF_Assortment_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);


CREATE TABLE CF_Orders (
  IdOfOrg         BIGINT        NOT NULL,
  IdOfOrder       BIGINT        NOT NULL,
  IdOfCard        BIGINT,
  IdOfClient      BIGINT,
  IdOfTransaction BIGINT,
  IdOfCashier     BIGINT        NOT NULL,
  IdOfPos         BIGINT,
  IdOfContragent  BIGINT        NOT NULL,
  RSum            BIGINT        NOT NULL,
  SocDiscount     BIGINT        NOT NULL DEFAULT 0,
  TrdDiscount     BIGINT        NOT NULL DEFAULT 0,
  GrantSum        BIGINT        NOT NULL,
  CreatedDate     BIGINT        NOT NULL,
  SumByCard       BIGINT        NOT NULL,
  SumByCash       BIGINT        NOT NULL,
  State           INT           NOT NULL DEFAULT 0,
  CONSTRAINT CF_Orders_pk PRIMARY KEY (IdOfOrg, IdOfOrder),
  CONSTRAINT CF_Orders_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_Orders_IdOfCard_fk FOREIGN KEY (IdOfCard) REFERENCES CF_Cards (IdOfCard),
  CONSTRAINT CF_Orders_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Orders_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction),
  CONSTRAINT CF_Orders_IdOfPos_fk FOREIGN KEY (IdOfPos) REFERENCES CF_POS (IdOfPos),
  CONSTRAINT CF_Orders_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
);

CREATE TABLE CF_OrderDetails (
  IdOfOrg         BIGINT        NOT NULL,
  IdOfOrderDetail BIGINT        NOT NULL,
  IdOfOrder       BIGINT        NOT NULL,
  Qty             INTEGER       NOT NULL,
  Discount        BIGINT        NOT NULL,
  SocDiscount     BIGINT        NOT NULL,
  rPrice          BIGINT        NOT NULL,
  MenuDetailName  VARCHAR(256)  NOT NULL,
  RootMenu        VARCHAR(32)   NOT NULL,
  MenuGroup       VARCHAR(60)   NOT NULL,
  MenuType        INT           NOT NULL,
  MenuOutput      VARCHAR(32)   NOT NULL,
  MenuOrigin      INT           NOT NULL,
  State           INT           NOT NULL DEFAULT 0,
  ItemCode VARCHAR(32),
  CONSTRAINT CF_OrderDetails_pk PRIMARY KEY (IdOfOrg, IdOfOrderDetail),
  CONSTRAINT CF_OrderDetails_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_OrderDetails_IdOfOrg_IdOfOrder_fk FOREIGN KEY (IdOfOrg, IdOfOrder) REFERENCES CF_Orders (IdOfOrg, IdOfOrder)
);

CREATE TABLE CF_ContragentPayments (
  IdOfContragentPayment   BIGINT        NOT NULL,
  IdOfContragent          BIGINT        NOT NULL,
  IdOfTransaction         BIGINT        NOT NULL,
  PaySum                  BIGINT        NOT NULL,
  PayType                 INTEGER       NOT NULL,
  State                   INTEGER       NOT NULL,
  CreatedDate             BIGINT        NOT NULL,
  PaymentDate             BIGINT,
  CONSTRAINT CF_ContragentPayments_pk PRIMARY KEY (IdOfContragentPayment),
  CONSTRAINT CF_ContragentPayments_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ContragentPayments_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction)
);

CREATE TABLE CF_ClientPaymentOrders (
  IdOfClientPaymentOrder  BIGINT        NOT NULL,
  IdOfContragent          BIGINT        NOT NULL,
  IdOfClient              BIGINT        NOT NULL,
  PaymentMethod           INTEGER       NOT NULL,
  OrderStatus             INTEGER       NOT NULL,
  PaySum                  BIGINT        NOT NULL,
  ContragentSum           BIGINT        NOT NULL,
  CreateTime              BIGINT        NOT NULL,
  IdOfPayment             VARCHAR(128)  NOT NULL,
  CONSTRAINT CF_ClientPaymentOrders_pk PRIMARY KEY (IdOfClientPaymentOrder),
  CONSTRAINT CF_ClientPaymentOrders_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ClientPaymentOrders_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_ClientPayments (
  IdOfClientPayment       BIGINT        NOT NULL,
  IdOfTransaction         BIGINT        NOT NULL,
  PaymentMethod           INTEGER       NOT NULL,
  PaySum                  BIGINT        NOT NULL,
  PayType                 INTEGER       NOT NULL,
  CreatedDate             BIGINT        NOT NULL,
  IdOfContragent          BIGINT,
  IdOfPayment             VARCHAR(128)  NOT NULL,
  IdOfClientPaymentOrder  BIGINT,
  AddPaymentMethod        VARCHAR(1024),
  AddIdOfPayment          VARCHAR(1024),
  IdOfContragentReceiver bigint, --v22
  CONSTRAINT CF_ClientPayments_pk PRIMARY KEY (IdOfClientPayment),
  CONSTRAINT CF_ClientPayments_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction),
  CONSTRAINT CF_ClientPayments_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_ClientPayments_IdOfClientPaymentOrder_fk FOREIGN KEY (IdOfClientPaymentOrder) REFERENCES CF_ClientPaymentOrders (IdOfClientPaymentOrder),
  CONSTRAINT cf_clientpayments_to_ca_rcvr_fk FOREIGN KEY (IdOfContragentReceiver) REFERENCES CF_Contragents (IdOfContragent) --v22
);

CREATE TABLE CF_DiaryClasses (
  IdOfOrg                 BIGINT        NOT NULL,
  IdOfClass               BIGINT        NOT NULL,
  ClassName               VARCHAR(64)   NOT NULL,
  CONSTRAINT CF_DiaryClasses_pk PRIMARY KEY (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryClasses_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_DiaryTimesheet (
  IdOfOrg                 BIGINT        NOT NULL,
  IdOfClientGroup         BIGINT        NOT NULL,
  RecDate                 BIGINT        NOT NULL,
  C0                      BIGINT,
  C1                      BIGINT,
  C2                      BIGINT,
  C3                      BIGINT,
  C4                      BIGINT,
  C5                      BIGINT,
  C6                      BIGINT,
  C7                      BIGINT,
  C8                      BIGINT,
  C9                      BIGINT,
  CONSTRAINT CF_DiaryTimesheet_pk PRIMARY KEY (IdOfOrg, IdOfClientGroup, RecDate),
  CONSTRAINT CF_DiaryTimesheet_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_DiaryTimesheet_IdOfOrg_IdOfClientGroup FOREIGN KEY (IdOfOrg, IdOfClientGroup) REFERENCES CF_ClientGroups (IdOfOrg, IdOfClientGroup),
  CONSTRAINT CF_DiaryTimesheet_C0 FOREIGN KEY (IdOfOrg, C0) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C1 FOREIGN KEY (IdOfOrg, C1) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C2 FOREIGN KEY (IdOfOrg, C2) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C3 FOREIGN KEY (IdOfOrg, C3) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C4 FOREIGN KEY (IdOfOrg, C4) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C5 FOREIGN KEY (IdOfOrg, C5) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C6 FOREIGN KEY (IdOfOrg, C6) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C7 FOREIGN KEY (IdOfOrg, C7) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C8 FOREIGN KEY (IdOfOrg, C8) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryTimesheet_C9 FOREIGN KEY (IdOfOrg, C9) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass)
);

CREATE TABLE CF_DiaryValues (
  IdOfOrg                 BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  IdOfClass               BIGINT          NOT NULL,
  RecDate                 BIGINT          NOT NULL,
  VType                   INT             NOT NULL,
  Value                   VARCHAR(10)     NOT NULL,
  CONSTRAINT CF_DiaryValues_pk PRIMARY KEY (IdOfOrg, IdOfClient, IdOfClass, RecDate, VType),
  CONSTRAINT CF_DiaryValues_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_DiaryValues_IdOfOrg_IdOfClass_fk FOREIGN KEY (IdOfOrg, IdOfClass) REFERENCES CF_DiaryClasses (IdOfOrg, IdOfClass),
  CONSTRAINT CF_DiaryValues_IdOfClient FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_Calls (
  IdOfCall                BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  CallTime                BIGINT          NOT NULL,
  Reason                  VARCHAR(1024)   NOT NULL,
  CallType                INTEGER         NOT NULL,
  State                   INTEGER         NOT NULL,
  CONSTRAINT CF_Calls_pk PRIMARY KEY (IdOfCall),
  CONSTRAINT CF_Calls_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_Notifications (
  IdOfNotification        BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  NotificationTime        BIGINT          NOT NULL,
  NotificationType        INTEGER         NOT NULL,
  NotificationText        VARCHAR(1024)   NOT NULL,
  CONSTRAINT CF_Notifications_pk PRIMARY KEY (IdOfNotification),
  CONSTRAINT CF_Notifications_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

CREATE TABLE CF_SyncHistory (
  IdOfSync                BIGINT          NOT NULL,
  IdOfOrg                 BIGINT          NOT NULL,
  SyncStartTime           BIGINT          NOT NULL,
  SyncEndTime             BIGINT,
  SyncResult              INTEGER,
  IdOfPacket              BIGINT          NOT NULL,
  CONSTRAINT CF_SyncHistory_pk PRIMARY KEY (IdOfSync),
  CONSTRAINT CF_SyncHistory_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_Registry (
  IdOfRegistry            BIGINT          NOT NULL,
  Version                 BIGINT          NOT NULL,
  ClientRegistryVersion   BIGINT          NOT NULL,
  SmsId                   CHAR(16)        NOT NULL,
  CONSTRAINT CF_ClientRegistry_pk PRIMARY KEY (IdOfRegistry)
);

CREATE TABLE CF_ReportHandleRules (
  IdOfReportHandleRule    BIGINT          NOT NULL,
  RuleName                VARCHAR(64)     NOT NULL,
  Subject                 VARCHAR(128)    NOT NULL,
  DocumentFormat          INTEGER         NOT NULL,
  Route0                  VARCHAR(128)    NOT NULL,
  Route1                  VARCHAR(128),
  Route2                  VARCHAR(128),
  Route3                  VARCHAR(128),
  Route4                  VARCHAR(128),
  Route5                  VARCHAR(128),
  Route6                  VARCHAR(128),
  Route7                  VARCHAR(128),
  Route8                  VARCHAR(128),
  Route9                  VARCHAR(128),
  Remarks                 VARCHAR(1024),
  Enabled                 INTEGER         NOT NULL,
  templatefilename        VARCHAR(256),
  Tag varchar(12), --v23
  City varchar(128), --v23
  District varchar(128), --v23
  Location varchar(128), --v23
  Latitude varchar(12), --v23
  Longitude varchar(12), --v23
  CONSTRAINT CF_ReportHandleRules_pk PRIMARY KEY (IdOfReportHandleRule)
);

CREATE TABLE CF_RuleConditions (
  IdOfRuleCondition       BIGINT          NOT NULL,
  IdOfReportHandleRule    BIGINT          NOT NULL,
  ConditionOperation      INTEGER         NOT NULL,
  ConditionArgument       VARCHAR(128),
  ConditionConstant       VARCHAR(128),
  CONSTRAINT CF_RuleConditions_pk PRIMARY KEY (IdOfRuleCondition),
  CONSTRAINT CF_RuleConditions_IdOfReportHandleRule_fk FOREIGN KEY (IdOfReportHandleRule) REFERENCES CF_ReportHandleRules (IdOfReportHandleRule)
);

CREATE TABLE CF_ClientSms (
  IdOfSms                 CHAR(32)        NOT NULL,
  Version                 BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  IdOfTransaction         BIGINT,
  Phone                   VARCHAR(32)     NOT NULL,
  ContentsType            INTEGER         NOT NULL,
  TextContents            VARCHAR(70)     NOT NULL,
  DeliveryStatus          INTEGER         NOT NULL,
  ServiceSendDate         BIGINT          NOT NULL,
  SendDate                BIGINT,
  DeliveryDate            BIGINT,
  Price                   BIGINT          NOT NULL,
  CONSTRAINT CF_ClientSms_pk PRIMARY KEY (IdOfSms),
  CONSTRAINT CF_ClientSms_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_ClientSms_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction)
);

CREATE TABLE CF_SchedulerJobs (
  IdOfSchedulerJob        BIGINT          NOT NULL,
  JobClass                VARCHAR(512)    NOT NULL,
  CronExpression          VARCHAR(128)    NOT NULL,
  JobName                 VARCHAR(128)    NOT NULL,
  Enabled                 INTEGER         NOT NULL,
  CONSTRAINT CF_SchedulerJobs_pk PRIMARY KEY (IdOfSchedulerJob)
);

CREATE TABLE CF_Generators (
  IdOfPerson              BIGINT          NOT NULL,
  IdOfOrg                 BIGINT          NOT NULL,
  IdOfClient              BIGINT          NOT NULL,
  IdOfContragent          BIGINT          NOT NULL,
  IdOfCard                BIGINT          NOT NULL,
  IdOfTransaction         BIGINT          NOT NULL,
  IdOfUser                BIGINT          NOT NULL,
  IdOfFunction            BIGINT          NOT NULL,
  IdOfMenu                BIGINT          NOT NULL,
  IdOfMenuDetail          BIGINT          NOT NULL,
  IdOfContragentPayment   BIGINT          NOT NULL,
  IdOfClientPaymentOrder  BIGINT          NOT NULL,
  IdOfClientPayment       BIGINT          NOT NULL,
  IdOfCall                BIGINT          NOT NULL,
  IdOfNotification        BIGINT          NOT NULL,
  IdOfSync                BIGINT          NOT NULL,
  IdOfReportHandleRule    BIGINT          NOT NULL,
  IdOfRuleCondition       BIGINT          NOT NULL,
  IdOfSchedulerJob        BIGINT          NOT NULL,
  IdOfAst                 BIGINT          NOT NULL,
  IdOfComplexInfo         BIGINT          NOT NULL,
  idOfComplexInfoDetail   BIGINT          NOT NULL,
  IdOfPos                 BIGINT          NOT NULL,
  IdOfSettlement          BIGINT          NOT NULL,
  IdOfPosition            BIGINT          NOT NULL,
  IdOfAddPayment          BIGINT          NOT NULL,
  IdOfCategoryDiscount    BIGINT          NOT NULL,
  IdOfRule		  BIGINT                  NOT NULL,
  idofproductguide        bigint          NOT NULL DEFAULT 0,
  idofconfigurationprovider bigint        NOT NULL DEFAULT 0,
  idofproduct             bigint          NOT NULL DEFAULT 0,
  idofproducts            bigint          NOT NULL DEFAULT 0,
  idoftechnologicalmap    bigint          NOT NULL DEFAULT 0
);

CREATE TABLE CF_SubscriptionFee (
  SubscriptionYear     INTEGER            NOT NULL,
  PeriodNo             INTEGER            NOT NULL,
  IdOfTransaction      BIGINT             NOT NULL,
  SubscriptionSum      BIGINT             NOT NULL,
  CreateTime           BIGINT             NOT NULL,
  CONSTRAINT CF_SubscriptionFee_pk PRIMARY KEY (SubscriptionYear, PeriodNo),
  CONSTRAINT CF_SubscriptionFee_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction)
);

-- CREATE TABLE CF_SochiClients (
--   ContractId              BIGINT            NOT NULL,
--   CreateTime              BIGINT            NOT NULL,
--   UpdateTime              BIGINT            NOT NULL,
--   FullName                VARCHAR(255)      NOT NULL,
--   Address                 VARCHAR(255),
--   CONSTRAINT CF_SochiClients_pk PRIMARY KEY (ContractId)
-- );
--
-- CREATE TABLE CF_SochiClientPayments (
--   PaymentId               BIGINT            NOT NULL,
--   ContractId              BIGINT            NOT NULL,
--   PaymentSum              BIGINT            NOT NULL,
--   PaymentSumF             BIGINT            NOT NULL,
--   PaymentTime             BIGINT            NOT NULL,
--   TerminalId              BIGINT            NOT NULL,
--   CreateTime              BIGINT            NOT NULL,
--   CONSTRAINT CF_SochiClientPayments_pk PRIMARY KEY (PaymentId),
--   CONSTRAINT CF_SochiClientPayments_ContractId_fk FOREIGN KEY (ContractId) REFERENCES CF_SochiClients (ContractId)
-- );

CREATE TABLE CF_MenuExchange (
  MenuDate                BIGINT            NOT NULL,
  IdOfOrg                 BIGINT            NOT NULL,
  MenuData			VARCHAR(32650)		    NOT NULL,
  Flags                   INT               NOT NULL,
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

-- Default user
--        username: admin
--        password: Base64(SHA1(123))
INSERT INTO CF_Users(IdOfUser, Version, UserName, Password, LastChange, Phone) VALUES(1, 0, 'admin', 'MTIz', 0, '');

-- Default functions
INSERT INTO CF_Functions(IdOfFunction, FunctionName) VALUES(1, 'createUser');
INSERT INTO CF_Functions(IdOfFunction, FunctionName) VALUES(2, 'editUser');
INSERT INTO CF_Functions(IdOfFunction, FunctionName) VALUES(3, 'deleteUser');
INSERT INTO CF_Functions(IdOfFunction, FunctionName) VALUES(4, 'payProcess');

INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(1, 1);
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(1, 2);
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(1, 3);
INSERT INTO CF_Permissions(IdOfUser, IdOfFunction) VALUES(1, 4);

-- Default data

-- Default registry data
INSERT INTO CF_Registry(IdOfRegistry, Version, ClientRegistryVersion, SmsId)
  VALUES(1, 0, 0, '0000000000000000');

-- Default generators' values
INSERT INTO CF_Generators(
  IdOfPerson, IdOfOrg,  IdOfClient,  IdOfContragent,
  IdOfCard, IdOfTransaction, IdOfUser, IdOfFunction,
  IdOfMenu, IdOfMenuDetail, IdOfContragentPayment, IdOfClientPaymentOrder,
  IdOfClientPayment, IdOfCall, IdOfNotification, IdOfSync,
  IdOfReportHandleRule, IdOfRuleCondition, IdOfSchedulerJob, IdOfAst,
  IdOfComplexInfo, IdOfComplexInfoDetail, IdOfPos, IdOfSettlement,
  IdOfPosition, IdOfAddPayment, IdOfCategoryDiscount, IdOfRule)
VALUES(
  0, 0, 0, 0,
  0, 0, 1, 2,
  0, 0, 0, 0,
  0, 0, 0, 0,
  0, 0, 0, 0,
  0, 0, 0, 0,
  0, 0, 0, 0);

-- Alter table CF_ComplexInfoDetail
ALTER TABLE CF_ComplexInfoDetail DROP CONSTRAINT CF_ComplexInfoDetail_IdOfMenuDetail_fk;
ALTER TABLE CF_ComplexInfoDetail ADD CONSTRAINT CF_ComplexInfoDetail_IdOfMenuDetail_fk FOREIGN KEY (IdOfMenuDetail) REFERENCES CF_MenuDetails (IdOfMenuDetail) ON DELETE CASCADE;

-- Indexes
CREATE index "cf_orders_createddate_idx" ON "cf_orders" (CreatedDate);
CREATE index "cf_orders_client_idx" ON "cf_orders" (IdOfClient);

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
  IdOfVisitor             bigint,
  VisitorFullName         varchar(110),
  DocType                 integer,
  DocSerialNum            varchar(45),
  IssueDocDate            BIGINT,
  VisitDateTime           BIGINT,
  CONSTRAINT CF_EnterEvents_pk PRIMARY KEY (IdOfEnterEvent, IdOfOrg)
);

CREATE TABLE CF_Publications (
  IdOfPublication             bigint       NOT NULL,
  IdOfOrg                     bigint       NOT NULL,
  RecordStatus                varchar(1)   NOT NULL default 'n',
  RecordType                  varchar(1)   NOT NULL default 'a',
  BibliographicLevel          varchar(1)   NOT NULL default 'm',
  HierarchicalLevel           varchar(1)   NOT NULL default '',
  CodingLevel                 varchar(1)   NOT NULL default '3',
  formOfCatalogingDescription VARCHAR(1)   NOT NULL default '',
  Data                        text,
  Author                      varchar(255) default NULL,
  Title                       varchar(512) default NULL,
  Title2                      varchar(255) default NULL,
  PublicationDate             varchar(15)  default NULL,
  Publisher                   varchar(255) default NULL,
  Version                     bigint       NOT NULL default '0',
  CONSTRAINT CF_Publication_pk PRIMARY KEY (IdOfPublication, IdOfOrg)
);

CREATE TABLE CF_Circulations (
  IdOfCirculation bigint  NOT NULL,
  IdOfClient      bigint  NOT NULL,
  IdOfPublication bigint  NOT NULL,
  IdOfOrg         bigint  NOT NULL,
  IssuanceDate    bigint  NOT NULL default 0,
  RefundDate      bigint  NOT NULL default 0,
  RealRefundDate  bigint,
  Status          int     NOT NULL default 0,
  Version         bigint  NOT NULL default 0,
  CONSTRAINT CF_Circulation_pk PRIMARY KEY (IdOfCirculation, IdOfOrg),
  CONSTRAINT CF_Circulation_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Circulation_IdOfPublication_fk FOREIGN KEY (IdOfPublication, IdOfOrg) REFERENCES CF_Publications (IdOfPublication, IdOfOrg)
);

CREATE TABLE CF_Options (
  IdOfOption    BIGINT  NOT NULL,
  OptionText    text,
  CONSTRAINT CF_Option_pk PRIMARY KEY (IdOfOption)
);

-- Configuration
INSERT INTO CF_Options(IdOfOption, OptionText)
  VALUES(1, '');
-- Option "with/without operator" (0 - without, 1 - with)
INSERT INTO CF_Options(IdOfOption, OptionText)
  VALUES(2, 0);
  -- Option "notify via SMS about enter events" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText)
  VALUES(3, 0);

CREATE TABLE CF_Settlements (
  IdOfSettlement         BIGINT         NOT NULL,
  IdOfContragentPayer    BIGINT         NOT NULL,
  IdOfContragentReceiver BIGINT         NOT NULL,
  CreatedDate            BIGINT         NOT NULL,
  PaymentDate            BIGINT,
  PaymentDoc             VARCHAR(128),
  Summa                  BIGINT         NOT NULL,
  CONSTRAINT CF_Settlements_pk PRIMARY KEY (IdOfSettlement),
  CONSTRAINT CF_Settlements_IdOfContragentPayer_fk FOREIGN KEY (IdOfContragentPayer) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_Settlements_IdOfContragentReceiver_fk FOREIGN KEY (IdOfContragentReceiver) REFERENCES CF_Contragents (IdOfContragent)
);

CREATE TABLE CF_CurrentPositions (
  IdOfPosition            BIGINT  NOT NULL,
  IdOfContragentDebtor    BIGINT  NOT NULL,
  IdOfContragentCreditor  BIGINT  NOT NULL,
  Summa                   BIGINT  NOT NULL,
  CONSTRAINT CF_CurrentPositions_pk PRIMARY KEY (IdOfPosition),
  CONSTRAINT CF_CurrentPositions_IdOfContragentDebtor_fk FOREIGN KEY (IdOfContragentDebtor) REFERENCES CF_Contragents (IdOfContragent),
  CONSTRAINT CF_CurrentPositions_IdOfContragentCreditor_fk FOREIGN KEY (IdOfContragentCreditor) REFERENCES CF_Contragents (IdOfContragent)
);

-- payments between contragents
CREATE TABLE CF_AddPayments (
   IdOfAddPayment           BIGINT        NOT NULL,
   IdOfContragentPayer      BIGINT        NOT NULL,
   IdOfContragentReceiver   BIGINT        NOT NULL,
   Summa                    BIGINT        NOT NULL,
   Comment                  VARCHAR(128),
   FromDate                 BIGINT,
   ToDate                   BIGINT,
   CONSTRAINT CF_AddPayments_pk PRIMARY KEY (IdOfAddPayment),
   CONSTRAINT CF_AddPayments_IdOfContragentPayer_fk FOREIGN KEY (IdOfContragentPayer) REFERENCES CF_Contragents (IdOfContragent),
   CONSTRAINT CF_AddPayments_IdOfContragentReceiver_fk FOREIGN KEY (IdOfContragentReceiver) REFERENCES CF_Contragents (IdOfContragent)
);

CREATE TABLE CF_CategoryDiscounts (
  IdOfCategoryDiscount      BIGINT        NOT NULL,
  CategoryName              VARCHAR(100)  NOT NULL DEFAULT '',
  DiscountRules             character     varying(60),
  Description               VARCHAR(100)  NOT NULL DEFAULT '',
  CreatedDate               BIGINT        DEFAULT NULL,
  LastUpdate                BIGINT        NOT NULL,
  CONSTRAINT CF_CategoryDiscounts_pk PRIMARY KEY (IdOfCategoryDiscount)
);

CREATE TABLE CF_DiscountRules (
  IdOfRule                  BIGINT        NOT NULL,
  Description               VARCHAR(100)  NOT NULL DEFAULT '',
  Complex0                  INTEGER       NOT NULL DEFAULT 0,
  Complex1                  INTEGER       NOT NULL DEFAULT 0,
  Complex2                  INTEGER       NOT NULL DEFAULT 0,
  Complex3                  INTEGER       NOT NULL DEFAULT 0,
  Complex4                  INTEGER       NOT NULL DEFAULT 0,
  Complex5                  INTEGER       NOT NULL DEFAULT 0,
  Complex6                  INTEGER       NOT NULL DEFAULT 0,
  Complex7                  INTEGER       NOT NULL DEFAULT 0,
  Complex8                  INTEGER       NOT NULL DEFAULT 0,
  Complex9                  INTEGER       NOT NULL DEFAULT 0,
  Priority                  Integer       NOT NULL DEFAULT 0,
  CategoriesDiscounts         Character     varying(64),
  OperationOr               integer       NOT NULL DEFAULT 0,
  CONSTRAINT CF_DiscountRules_pk PRIMARY KEY (IdOfRule)
);

create table CF_Schema_version_info (
    SchemaVersionInfoId     bigserial                not null,
    MajorVersionNum         int                      not null,
    MiddleVersionNum        int                      not null,
    MinorVersionNum         int                      not null,
    BuildVersionNum         int                      not null,
    UpdateTime              BIGINT                   not null,
    CommitText              text,
    constraint "CF_schema_version_info_pk" primary key (SchemaVersionInfoId)
);

CREATE TABLE CF_TransactionJournal
(
  idOfTransactionJournal bigserial NOT NULL,
  transDate bigint NOT NULL,
  idOfOrg bigint NOT NULL,
  idOfInternalOperation bigint NOT NULL,
  OGRN character varying(32),
  clientSan character varying(15),
  clientType character varying(32),
  enterName character varying(100),
  serviceCode character varying(10),
  transactionCode character varying(10),
  cardTypeCode character varying(10),
  cardIdentityCode character varying(10),
  cardIdentityName character varying(32),
  uecid character varying(10),
  contractId bigint,
  financialAmount bigint,
  accountingDate bigint,
  CONSTRAINT cf_transaction_journal_pk PRIMARY KEY (idOfTransactionJournal)
);

/* Таблица категорий Организаций */
CREATE TABLE CF_CategoryOrg
(
  idofcategoryorg bigserial NOT NULL,
  categoryname character varying(255),
  CONSTRAINT cf_categoryorg_pk PRIMARY KEY (idofcategoryorg )
);

/* Таблица связка между CategoryOrg и Org */
CREATE TABLE CF_CategoryOrg_Orgs
(
  idoforgscategories bigserial NOT NULL,
  idoforg bigint,
  idofcategoryorg bigint,
  CONSTRAINT cf_orgscategories_pk PRIMARY KEY (idoforgscategories ),
  CONSTRAINT cf_orgscategories_idofcategoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES CF_CategoryOrg (idofcategoryorg),
  CONSTRAINT cf_orgscategories_idoforg FOREIGN KEY (idoforg)
  REFERENCES Cf_Orgs (idoforg)
);

/* Таблица связка между DiscountRules и CategoryDiscountRule */
CREATE TABLE CF_DiscountRules_CategoryDiscounts
(
  idofdrcd bigserial NOT NULL,
  idofrule bigint NOT NULL,
  idofcategorydiscount bigint NOT NULL,
  CONSTRAINT cf_discountrulescategorydiscount_pk PRIMARY KEY (idofrule , idofcategorydiscount ),
  CONSTRAINT cf_discountrulescategorydiscount_idofcategorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES CF_CategoryDiscounts (idofcategorydiscount),
  CONSTRAINT cf_discountrulescategorydiscount_idofdiscountrules FOREIGN KEY (idofrule)
  REFERENCES CF_DiscountRules (idofrule)
);

/* Таблица связка между CategoryOrg и DiscountRule */
CREATE TABLE CF_DiscountRules_CategoryOrg
(
  idofcatorgdiscrule bigserial NOT NULL,
  idofcategoryorg bigint,
  idofrule bigint,
  CONSTRAINT cf_catorgdiscrule_pk PRIMARY KEY (idofcatorgdiscrule),
  CONSTRAINT cf_catorgdiscrule_categoryorg FOREIGN KEY (idofcategoryorg)
  REFERENCES CF_CategoryOrg (idofcategoryorg),
  CONSTRAINT cf_catorgdiscrule_discountrule FOREIGN KEY (idofrule)
  REFERENCES CF_DiscountRules (idofrule)
);


/* Таблица связка между Client и CategoryDiscountRule */
CREATE TABLE CF_Clients_CategoryDiscounts
(
  idofclienscategorydiscount bigserial NOT NULL,
  idofclient bigint,
  idofcategorydiscount bigint,
  CONSTRAINT cf_clienscategorydiscount_pk PRIMARY KEY (idofclienscategorydiscount ),
  CONSTRAINT cf_clienscategorydiscount_categorydiscount FOREIGN KEY (idofcategorydiscount)
  REFERENCES CF_CategoryDiscounts (idofcategorydiscount),
  CONSTRAINT cf_clienscategorydiscount_client FOREIGN KEY (idofclient)
  REFERENCES CF_Clients (idofclient)
);

--v20

CREATE TABLE cf_do_versions
(
  idofdoobject bigserial,
  distributedobjectclassname character varying(64),
  currentversion bigint,
  CONSTRAINT cf_do_version_pk PRIMARY KEY (idofdoobject ),
  CONSTRAINT cf_do_versions_distributedobjectclassname_unique UNIQUE (distributedobjectclassname)
);
-- Добавлена таблица конфликтов для распределенных объектов
CREATE TABLE cf_do_conflicts
(
  idofdoconflict bigserial,
  idoforg bigint,
  distributedobjectclassname character varying(64),
  createconflictdate bigint,
  gversion_inc bigint,
  gversion_cur bigint,
  val_inc character varying(16548),
  val_cur character varying(16548),
  gversion_result bigint, --v24
  CONSTRAINT cf_do_conflicts_pk PRIMARY KEY (idofdoconflict )
);

CREATE TABLE cf_do_confirms
(
  idofdoconfirm bigserial,
  distributedobjectclassname character varying(64) NOT NULL,
  GUID character varying(36) NOT NULL,
  OrgOwner BIGINT DEFAULT NULL,
  CONSTRAINT cf_do_confirm_pk PRIMARY KEY (idofdoconfirm )
);

CREATE TABLE cf_product_groups
(
   IdOfProductGroups BigSerial,
   NameOfGroup character varying(512) NOT NULL,
   GUID character varying(36) NOT NULL UNIQUE,
   GlobalVersion BIGINT DEFAULT NULL,
   DeletedState boolean NOT NULL DEFAULT false,
   ClassificationCode character varying(32) DEFAULT NULL,
   IdOfConfigurationProvider bigint,
   OrgOwner BIGINT DEFAULT NULL,
   CreatedDate bigint NOT NULL,
   LastUpdate bigint,
   DeleteDate bigint,
   SendAll integer DEFAULT 0, --v24
   CONSTRAINT cf_product_groups_pk PRIMARY KEY (IdOfProductGroups )
);

-- Добавлена таблица справочника продуктов
CREATE TABLE cf_products
(
  IdOfProducts BigSerial,
  IdOfProductGroups BigINT NOT NULL,
  Code character varying(16) NOT NULL,
  FullName character varying(1024),
  ProductName character varying(512),
  OkpCode character varying(128),
  ClassificationCode character varying(32) DEFAULT NULL,
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  IdOfUserCreate bigint,
  IdOfUserEdit bigint,
  IdOfUserDelete bigint,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  IdOfConfigurationProvider bigint,
  SendAll integer DEFAULT 0, --v24
  Density  FLOAT DEFAULT NULL, --v24
  CONSTRAINT cf_products_pk PRIMARY KEY (idOfProducts ),
  CONSTRAINT cf_products_product_groups_fk FOREIGN KEY (IdOfProductGroups)
      REFERENCES cf_product_groups (IdOfProductGroups)
);

-- Добавлена таблица групп технологических карт
CREATE TABLE cf_technological_map_groups
(
   IdOfTechMapGroups BigSerial,
   NameOfGroup character varying(128) NOT NULL,
   GUID character varying(36) NOT NULL UNIQUE,
   GlobalVersion BIGINT DEFAULT NULL,
   DeletedState boolean NOT NULL DEFAULT false,
   OrgOwner BIGINT DEFAULT NULL,
   CreatedDate bigint NOT NULL,
   IdOfConfigurationProvider bigint,
   LastUpdate bigint,
   DeleteDate bigint,
   SendAll integer DEFAULT 0, --v24
   CONSTRAINT cf_technological_map_groups_pk PRIMARY KEY (IdOfTechMapGroups )
);

-- Добавлена таблица (справочник) технологических карт
CREATE TABLE  cf_technological_map(
  IdOfTechnologicalMaps BigSerial,
  IdOfTechMapGroups bigint NOT NULL,
  NameOfTechnologicalMap character varying(128) NOT NULL,
  NumberOfTechnologicalMap character varying(128), --v24
  TechnologyOfPreparation character varying(4096) NOT NULL,
  TempOfPreparation character varying(32) DEFAULT NULL,
  EnergyValue FLOAT DEFAULT NULL,
  Proteins FLOAT DEFAULT NULL,
  Carbohydrates FLOAT DEFAULT NULL,
  Fats FLOAT DEFAULT NULL,
  MicroElCa FLOAT DEFAULT NULL,
  MicroElMg FLOAT DEFAULT NULL,
  MicroElP FLOAT DEFAULT NULL,
  MicroElFe FLOAT DEFAULT NULL,
  VitaminA FLOAT DEFAULT NULL,
  VitaminB1 FLOAT DEFAULT NULL,
  VitaminB2 FLOAT DEFAULT NULL,
  VitaminPp FLOAT DEFAULT NULL,
  VitaminC FLOAT DEFAULT NULL,
  VitaminE FLOAT DEFAULT NULL,
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  IdOfUserCreate bigint,
  IdOfUserEdit bigint,
  IdOfUserDelete bigint,
  IdOfConfigurationProvider bigint,
  SendAll integer DEFAULT 0, --v24
  LifeTime integer NOT NULL DEFAULT 0, --v24
  CONSTRAINT cf_technological_map_pk PRIMARY KEY (IdOfTechnologicalMaps ),
  CONSTRAINT cf_technological_map_technological_map_groups_fk FOREIGN KEY (IdOfTechMapGroups)
      REFERENCES cf_technological_map_groups (IdOfTechMapGroups)
);

-- Добавлена таблица продукты технологических карт
CREATE TABLE cf_technological_map_products
(
  IdOfTechnoMapProducts BigSerial NOT NULL,
  IdOfTechnologicalMaps bigint NOT NULL,
  IdOfProducts bigint NOT NULL,
  NetWeight integer, --v24
  GrossWeight integer, --v24
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  IdOfConfigurationProvider bigint,
  SendAll integer DEFAULT 0, --v24
  NumberGroupReplace integer, --v24
  CONSTRAINT cf_technological_map_products_pk PRIMARY KEY (IdOfTechnoMapProducts ),
  CONSTRAINT cf_technological_map_products_product FOREIGN KEY (IdOfProducts)
      REFERENCES cf_products (IdOfProducts),
  CONSTRAINT cf_technological_map_products_technological_map_fk FOREIGN KEY (IdOfTechnologicalMaps)
      REFERENCES cf_technological_map (IdOfTechnologicalMaps)
);
--Добавлена таблица производственную конфигурацию
CREATE TABLE cf_provider_configurations
(
  IdOfConfigurationProvider BigSerial NOT NULL,
  nameOfConfigurationProvider character varying(64) NOT NULL,
  IdOfUserCreate bigint,
  IdOfUserEdit bigint,
  IdOfUserDelete bigint,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CONSTRAINT pk_configuration_provider PRIMARY KEY (IdOfConfigurationProvider ),
  CONSTRAINT cf_provider_configurations_name_key UNIQUE (nameOfConfigurationProvider )
);

CREATE TABLE cf_publs
(
  idofpubl BigSerial NOT NULL,
  data text,
  author character varying(255),
  title character varying(512),
  title2 character varying(255),
  publicationdate character varying(15),
  publisher character varying(255),
  version bigint NOT NULL,
  isbn character varying(32), --v21
  hash character varying(128), --v21
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  CONSTRAINT cf_publ_pk PRIMARY KEY (idofpubl ),
  CONSTRAINT cf_publ_uk UNIQUE (author , title , title2 , publicationdate , publisher ),
  CONSTRAINT cf_publs_guid_key UNIQUE (guid )
);

-- CREATE INDEX cf_publ_idx
--   ON cf_publs
--   USING btree
--   (author, title, title2, publicationdate, publisher);

CREATE TABLE cf_circuls
(
  idofcircul BigSerial NOT NULL,
  idofclient bigint NOT NULL,
  idofpubl bigint NOT NULL,
  idoforg bigint NOT NULL,
  idofissuable bigint NOT NULL,
  issuancedate bigint NOT NULL DEFAULT 0,
  refunddate bigint NOT NULL DEFAULT 0,
  realrefunddate bigint,
  status integer NOT NULL DEFAULT 0,
  version bigint NOT NULL DEFAULT 0,
  quantity integer NOT NULL DEFAULT 0,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  CONSTRAINT cf_circul_pk PRIMARY KEY (idofcircul ),
  CONSTRAINT cf_circul_idofclient_fk FOREIGN KEY (idofclient)
      REFERENCES cf_clients (idofclient),
  CONSTRAINT cf_circul_idoforg_fk FOREIGN KEY (idoforg)
      REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_circul_idofpubl_fk FOREIGN KEY (idofpubl)
      REFERENCES cf_publs (idofpubl),
  CONSTRAINT cf_circul_unq UNIQUE (idofclient , idofpubl , idoforg ),
  CONSTRAINT cf_circuls_guid_key UNIQUE (guid )
);

CREATE TABLE cf_issuable
(
  idofissuable BigSerial NOT NULL,
  barcode bigint,
  type character(1) NOT NULL DEFAULT 'i',
  idofpubl bigint NOT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  CONSTRAINT cf_iissuable_pk PRIMARY KEY (idofissuable ),
  CONSTRAINT cf_issuable_idofpubl_fkey FOREIGN KEY (idofpubl)
      REFERENCES cf_publs (idofpubl)
);

-- Табдица связи между дружественными организациями
CREATE TABLE cf_friendly_organization
(
  idOfFriendlyOrg BigSerial NOT NULL,
  currentOrg bigint NOT NULL,
  friendlyOrg bigint NOT NULL,
  CONSTRAINT cf_friendly_organization_pk PRIMARY KEY (idoffriendlyorg ),
  CONSTRAINT cf_friendly_organization_current_idoforg_fk FOREIGN KEY (currentorg)
      REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_friendly_organization_friend_idoforg_fk FOREIGN KEY (friendlyorg)
      REFERENCES cf_orgs (idoforg)
);


--v21
CREATE TABLE cf_linking_tokens
(
  IdOfLinkingToken   BIGSERIAL    NOT NULL,
  IdOfClient         BIGINT       UNIQUE NOT NULL,
  Token              VARCHAR(20)  UNIQUE NOT NULL,
  CONSTRAINT cf_linking_tokens_pk PRIMARY KEY (IdOfLinkingToken)
);
CREATE index "cf_tokens_idofclient_idx" ON cf_linking_tokens (IdOfClient);
CREATE index "cf_tokens_token_idx" ON cf_linking_tokens (Token);
ALTER TABLE cf_linking_tokens ADD CONSTRAINT cf_linking_tokens_idofclient FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient);


--v22
CREATE TABLE cf_banks
(
  name character varying(128),
  logourl character varying(128),
  terminalsurl character varying(128),
  enrollmenttype character varying(128),
  idofbank bigserial NOT NULL,
  minrate double precision,
  rate double precision,
  CONSTRAINT cf_banks_pkey PRIMARY KEY (idofbank)
);


--v22
CREATE TABLE CF_Account_Transfers (
  IdOfAccountTransfer BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClientBenefactor bigint NOT NULL,
  IdOfClientBeneficiary bigint NOT NULL,
  Reason VARCHAR(256),
  CreatedBy bigint NOT NULL,
  IdOfTransactionOnBenefactor bigint NOT NULL,
  IdOfTransactionOnBeneficiary bigint NOT NULL,
  TransferSum bigint NOT NULL,
  CONSTRAINT cf_account_transfer_pk PRIMARY KEY (IdOfAccountTransfer),
  CONSTRAINT cf_account_transfer_c_bctr_fk FOREIGN KEY (IdOfClientBenefactor) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_transfer_c_bcry_fk FOREIGN KEY (IdOfClientBeneficiary) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_transfer_t_bctr_fk FOREIGN KEY (IdOfTransactionOnBenefactor) REFERENCES cf_transactions (IdOfTransaction),
  CONSTRAINT cf_account_transfer_t_bcry_fk FOREIGN KEY (IdOfTransactionOnBeneficiary) REFERENCES cf_transactions (IdOfTransaction)
);

--v23
CREATE TABLE CF_ReportInfo (
  IdOfReportInfo BigSerial NOT NULL,
  RuleName varchar(64) NOT NULL,
  DocumentFormat integer,
  ReportName varchar(128) NOT NULL,
  CreatedDate bigint NOT NULL,
  GenerationTime integer NOT NULL,
  StartDate bigint NOT NULL,
  EndDate bigint NOT NULL,
  ReportFile varchar(256) NOT NULL,
  OrgNum varchar(12),
  IdOfOrg bigint,
  Tag varchar(12),
  CONSTRAINT cf_report_info_pk PRIMARY KEY (IdOfReportInfo)
);

CREATE index "cf_report_info_start_date_idx" ON CF_ReportInfo (StartDate);
CREATE index "cf_report_info_end_date_idx" ON CF_ReportInfo (EndDate);
CREATE index "cf_report_info_created_date_idx" ON CF_ReportInfo (CreatedDate);
CREATE index "cf_report_info_orgnum_date_idx" ON CF_ReportInfo (OrgNum);
CREATE index "cf_report_info_rulename_idx" ON CF_ReportInfo (RuleName);


--v24

CREATE TABLE  cf_goods_groups (
  IdOfGoodsGroup BigSerial NOT NULL,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NameOfGoodsGroup character varying(128) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_groups_pk PRIMARY KEY (IdOfGoodsGroup )
);

CREATE TABLE  cf_goods (
  IdOfGood BigSerial NOT NULL,
  IdOfGoodsGroup bigint NOT NULL,
  IdOfTechnologicalMaps bigint,
  IdOfProducts bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NameOfGood  character varying(512) NOT NULL,
  FullName character varying(1024) DEFAULT NULL,
  GoodsCode character varying(32) NOT NULL,
  UnitsScale  integer NOT NULL DEFAULT 0,
  NetWeight  bigint NOT NULL,
  LifeTime  bigint DEFAULT 0,
  Margin  bigint DEFAULT 0,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_pk PRIMARY KEY (IdOfGood ),
  CONSTRAINT cf_goods_group_fk FOREIGN KEY (IdOfGoodsGroup)
      REFERENCES cf_goods_groups (IdOfGoodsGroup)
);

CREATE TABLE  cf_trade_material_goods (
  IdOfTradeMaterialGood BigSerial NOT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  GoodsCreationDate bigint,
  LifeTime  bigint NOT NULL,
  UnitsScale  integer NOT NULL DEFAULT 0,
  TotalCount  bigint NOT NULL,
  NetWeight  bigint NOT NULL,
  SelfPrice  bigint NOT NULL,
  NDS  bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_trade_material_goods_pk PRIMARY KEY (IdOfTradeMaterialGood ),
  CONSTRAINT cf_trade_material_goods_good_fk FOREIGN KEY (IdOfGood)
      REFERENCES cf_goods (IdOfGood)
);

--
CREATE TABLE cf_staffs(
  IdOfStaff BigSerial NOT NULL,
  IdOfClient bigint DEFAULT NULL,
  IdOfRole bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  ParentId BIGINT DEFAULT NULL,
  Flags integer DEFAULT NULL,
  SurName character varying(30) DEFAULT NULL,
  FirstName character varying(30) DEFAULT NULL,
  SecondName character varying(30) DEFAULT NULL,
  StaffPosition character varying(30) DEFAULT NULL,
  PersonalCode character varying(128) DEFAULT NULL,
  Rights character varying(256) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_staff_pk PRIMARY KEY (IdOfStaff )
);

CREATE TABLE  cf_goods_requests (
  IdOfGoodsRequest BigSerial NOT NULL,
  IdOfStaff bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NumberOfGoodsRequest  character varying(128) NOT NULL,
  DateOfGoodsRequest bigint,
  State  integer NOT NULL DEFAULT 0,
  DoneDate bigint,
  Comment  character varying(512) DEFAULT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_requests_pk PRIMARY KEY (IdOfGoodsRequest ),
  CONSTRAINT cf_goods_requests_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);

CREATE TABLE  cf_goods_requests_positions (
  IdOfGoodsRequestPosition BigSerial NOT NULL,
  IdOfGoodsRequest bigint NOT NULL,
  IdOfGood bigint,
  IdOfProducts bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale  integer NOT NULL DEFAULT 0,
  TotalCount  bigint NOT NULL,
  NetWeight  bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_requests_positions_pk PRIMARY KEY (IdOfGoodsRequestPosition ),
  CONSTRAINT cf_goods_requests_positions_goods_request_fk FOREIGN KEY (IdOfGoodsRequest)
      REFERENCES cf_goods_requests (IdOfGoodsRequest)
);

CREATE TABLE  cf_acts_of_waybill_difference (
  IdOfActOfDifference BigSerial NOT NULL,
  IdOfStaff bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  DateOfActOfDifference bigint NOT NULL,
  NumberOfActOfDifference  character varying(128) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_acts_of_waybill_difference_pk PRIMARY KEY (IdOfActOfDifference ),
  CONSTRAINT cf_acts_of_waybill_difference_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);

CREATE TABLE  cf_acts_of_waybill_difference_positions (
  IdOfActOfDifferencePosition BigSerial NOT NULL,
  IdOfActOfDifference bigint DEFAULT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale  integer NOT NULL DEFAULT 0,
  TotalCount  bigint NOT NULL,
  NetWeight  bigint NOT NULL,
  GrossWeight  bigint DEFAULT NULL,
  GoodsCreationDate  bigint NOT NULL,
  LifeTime  bigint NOT NULL,
  Price  bigint NOT NULL,
  NDS  bigint DEFAULT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_acts_of_waybill_difference_positions_pk PRIMARY KEY (IdOfActOfDifferencePosition ),
  CONSTRAINT cf_acts_of_waybill_difference_positions_act_of_waybill_difference_fk FOREIGN KEY (IdOfActOfDifference)
      REFERENCES cf_acts_of_waybill_difference (IdOfActOfDifference),
  CONSTRAINT cf_acts_of_waybill_difference_positions_good_fk FOREIGN KEY (IdOfGood)
      REFERENCES cf_goods (IdOfGood)
);

CREATE TABLE  cf_waybills (
  IdOfWayBill BigSerial NOT NULL,
  IdOfStaff bigint NOT NULL,
  IdOfActOfDifference bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NumberOfWayBill  character varying(128) NOT NULL,
  DateOfWayBill  bigint,
  State integer NOT NULL,
  Shipper  character varying(128) NOT NULL,
  Receiver  character varying(128) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_waybills_pk PRIMARY KEY (IdOfWayBill ),
  CONSTRAINT cf_waybills_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);

CREATE TABLE  cf_waybills_positions (
  IdOfWayBillPosition BigSerial NOT NULL,
  IdOfWayBill bigint NOT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale bigint NOT NULL DEFAULT 0,
  TotalCount bigint NOT NULL,
  NetWeight bigint NOT NULL,
  GrossWeight bigint NOT NULL,
  GoodsCreationDate  bigint NOT NULL,
  LifeTime bigint NOT NULL,
  Price bigint NOT NULL,
  NDS bigint DEFAULT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_waybills_positions_pk PRIMARY KEY (IdOfWayBillPosition ),
  CONSTRAINT cf_waybills_positions_waybill_fk FOREIGN KEY (IdOfWayBill)
      REFERENCES cf_waybills (IdOfWayBill),
  CONSTRAINT cf_waybills_positions_good_fk FOREIGN KEY (IdOfGood)
      REFERENCES cf_goods (IdOfGood)
);

CREATE TABLE  cf_acts_of_inventarization (
  IdOfActOfInventarization  BigSerial NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  DateOfAct bigint NOT NULL,
  SendAll integer DEFAULT 0,
  NumberOfAct  character varying(128) NOT NULL,
  Commission  character varying(512) NOT NULL,
  CONSTRAINT cf_acts_of_inventarization_pk PRIMARY KEY (IdOfActOfInventarization )
);

CREATE TABLE  cf_internal_disposing_documents (
  IdOfInternalDisposingDocument BigSerial NOT NULL,
  IdOfStaff bigint NOT NULL,
  IdOfActOfInventarization bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  TypeOfInternalDisposingDocument integer NOT NULL,
  DateOfInternalDisposingDocument bigint NOT NULL,
  State integer NOT NULL,
  Comments character varying(1024),
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_internal_disposing_documents_pk PRIMARY KEY (IdOfInternalDisposingDocument ),
  CONSTRAINT cf_internal_disposing_documents_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);

CREATE TABLE  cf_internal_disposing_document_positions (
  IdOfInternalDisposingDocumentPositions BigSerial NOT NULL,
  IdOfInternalDisposingDocument bigint NOT NULL,
  IdOfTradeMaterialGood bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale bigint NOT NULL DEFAULT 0,
  TotalCount bigint NOT NULL,
  NetWeight bigint NOT NULL,
  DisposePrice bigint NOT NULL,
  NDS bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_internal_disposing_document_positions_pk PRIMARY KEY (IdOfInternalDisposingDocumentPositions ),
  CONSTRAINT cf_internal_disposing_document_positions_internal_disposing_document_fk FOREIGN KEY (IdOfInternalDisposingDocument)
      REFERENCES cf_internal_disposing_documents (IdOfInternalDisposingDocument)
);

CREATE TABLE  cf_internal_incoming_documents (
  IdOfInternalIncomingDocument BigSerial NOT NULL,
  IdOfWayBill bigint,
  IdOfInternalDisposingDocument bigint,
  IdOfActOfInventarization bigint,
  IdOfStaff bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  DateOfInternalIncomingDocument bigint NOT NULL,
  State integer NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_internal_incoming_documents_pk PRIMARY KEY (IdOfInternalIncomingDocument ),
  CONSTRAINT cf_internal_incoming_documents_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);

CREATE TABLE  cf_internal_incoming_document_positions (
  IdOfInternalIncomingDocumentPositions BigSerial NOT NULL,
  IdOfInternalIncomingDocument bigint NOT NULL,
  IdOfTradeMaterialGood bigint,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  GoodsCreationDate  bigint NOT NULL,
  LifeTime  bigint NOT NULL,
  UnitsScale bigint NOT NULL DEFAULT 0,
  TotalCount bigint NOT NULL,
  NetWeight bigint NOT NULL,
  IncomingPrice bigint NOT NULL,
  NDS bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_internal_incoming_document_positions_pk PRIMARY KEY (IdOfInternalIncomingDocumentPositions ),
  CONSTRAINT cf_internal_incoming_document_positions_internal_incoming_document_fk FOREIGN KEY (IdOfInternalIncomingDocument)
      REFERENCES cf_internal_incoming_documents (IdOfInternalIncomingDocument),
  CONSTRAINT cf_internal_incoming_document_positions_good_fk FOREIGN KEY (IdOfGood)
      REFERENCES cf_goods (IdOfGood)
);

CREATE TABLE  cf_state_changes (
  IdOfStateChange BigSerial NOT NULL,
  IdOfWayBill bigint,
  IdOfInternalDisposingDocument bigint,
  IdOfGoodsRequest bigint,
  IdOfStaff bigint,
  IdOfInternalIncomingDocument bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  DateOfStateChange bigint NOT NULL,
  StateFrom bigint NOT NULL,
  StateTo bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_state_changes_pk PRIMARY KEY (IdOfStateChange ),
  CONSTRAINT cf_state_changes_staff_fk FOREIGN KEY (IdOfStaff)
      REFERENCES cf_staffs (IdOfStaff)
);


-- НЕ ЗАБЫВАТЬ ИЗМЕНЯТЬ ПРИ ВЫПУСКЕ НОВОЙ ВЕРСИИ
insert into CF_Schema_version_info(MajorVersionNum, MiddleVersionNum, MinorVersionNum, BuildVersionNum, UpdateTime)
VALUES(2, 2, 24, 121004, 0, "");


