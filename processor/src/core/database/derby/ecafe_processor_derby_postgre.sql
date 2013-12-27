-- Copyright (c) 2009 Axetta LLC. All Rights Reserved.
-- Version 0.2.2.26

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
  OKATO                   VARCHAR(11)       DEFAULT '',
  CorrAccount             VARCHAR(20),
  Account                 VARCHAR(20),
  CreatedDate             BIGINT            NOT NULL,
  LastUpdate              BIGINT            NOT NULL,
  PublicKey               VARCHAR(1024)     NOT NULL,
  PublicKeyGOSTAlias varchar(64), --v22
  NeedAccountTranslate    INTEGER           NOT NULL,
  KPP VARCHAR(10) NOT NULL DEFAULT '',  --v39
  OGRN VARCHAR(15) NOT NULL DEFAULT '', --v39
  RequestNotifyMailList character varying(1024) default null, --v52
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
  IdOfContragent BIGINT DEFAULT NULL,
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
  Tag                     VARCHAR(256),
  City                    VARCHAR(128),
  District                VARCHAR(128),
  Location                VARCHAR (128),
  Latitude                VARCHAR(12),
  Longitude               VARCHAR(12),
  mailingListReportsOnNutrition character varying(1024),
  mailingListReportsOnVisits character varying(1024),
  mailingListReports1 character varying(1024),
  mailingListReports2 character varying(1024),
  IdOfConfigurationProvider bigint, --v20
  guid varchar(40), --v20
  idofcontract bigint, --v21,
  lastSucBalanceSync BIGINT, --v21
  lastUnsucBalanceSync BIGINT, --v21
  RefectoryType integer NULL, --v25
  ClientVersion           VARCHAR(16)     , -- v31
  RemoteAddress           VARCHAR(20)     , -- v31
  FullSyncParam INTEGER NOT NULL default 0, -- v42
  CommodityAccounting integer NOT NULL DEFAULT 0, --v51
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
  SubBalance1             BIGINT            ,  --v51
  Limits                  BIGINT            NOT NULL,
  ExpenditureLimit        BIGINT            NOT NULL DEFAULT 0,
  CategoriesDiscounts     VARCHAR(60)       NOT NULL DEFAULT '',
  San                     VARCHAR(11),
  GuardSan                VARCHAR(64),
  ExternalId              BIGINT, --v17
  ClientGUID              VARCHAR(40), --v17
  CanConfirmGroupPayment  INTEGER           NOT NULL DEFAULT 0,
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

create index cf_cards_idofclient_idx on cf_cards(idofclient); --v25

CREATE TABLE CF_Transactions (
  IdOfTransaction         BIGINT            NOT NULL,
  IdOfClient              BIGINT            NOT NULL,
  IdOfCard                BIGINT,
  TransactionSum          BIGINT            NOT NULL,
  Source                  VARCHAR(50)       NOT NULL,
  SourceType              INTEGER           NOT NULL,
  TransactionDate         BIGINT            NOT NULL,
  BalanceBefore           BIGINT,
  IdOfOrg                 BIGINT, --v37
  TransactionSubBalance1Sum bigint, --v51
  SubBalance1BeforeTransaction bigint, --v51
  SourceBalanceNumber bigint, --v51
  CONSTRAINT CF_Transactions_pk PRIMARY KEY (IdOfTransaction),
  CONSTRAINT CF_Transactions_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Transactions_IdOfCard_fk FOREIGN KEY (IdOfCard) REFERENCES CF_Cards (IdOfCard)
);

create index cf_transactions_idofclient_idx on cf_transactions(idofclient); --v25
create index cf_transactions_trdate_idx on cf_transactions(transactiondate); --v25

CREATE TABLE CF_Users (
  IdOfUser                BIGINT            NOT NULL,
  Version                 BIGINT            NOT NULL,
  UserName                VARCHAR(64)       NOT NULL,
  Password                VARCHAR(128)      NOT NULL,
  LastChange              BIGINT            NOT NULL,
  Phone                   VARCHAR(32)       NOT NULL,
  email                   VARCHAR(128),                           --v14
  IdOfRole                BIGINT            NOT NULL DEFAULT 0,   --v32
  RoleName                VARCHAR(128),                           --v32
  LastEntryIP             VARCHAR(15),         --v43
  LastEntryTime           BIGINT,              --v43
  IsBlocked               BOOLEAN NOT NULL,    --v43
  Region                  VARCHAR(10)       default null, --v52
  CONSTRAINT CF_Users_pk PRIMARY KEY (IdOfUser),
  CONSTRAINT CF_Users_ShortName UNIQUE (UserName)
--,  CONSTRAINT CF_Users_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent) --v42
);

--v42
-- Добавление возможности закреплять несколько контрагентов за пользователем
-- Необходимо для отображения содержимого процессанга в контексте пользователя
create table CF_UserContragents (
  IdOfUser        BIGINT        NOT NULL,
  IdOfContragent  BIGINT        NOT NULL,
  CONSTRAINT CF_UserContragents_pk PRIMARY KEY (IdOfUser, IdOfContragent),
  CONSTRAINT CF_UserContragents_IdOfUser_fk FOREIGN KEY (IdOfUser) REFERENCES CF_Users (IdOfUser),
  CONSTRAINT CF_UserContragents_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
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

--v24
CREATE TABLE  cf_goods_groups (
  IdOfGoodsGroup BigSerial NOT NULL,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  IdOfConfigurationProvider bigint, --v51
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NameOfGoodsGroup character varying(128) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_groups_pk PRIMARY KEY (IdOfGoodsGroup ),
  CONSTRAINT cf_goods_groups_guid_key UNIQUE (guid )
);

-- Справочник базовых товаров
CREATE TABLE CF_Goods_BasicBasket
(
  IdOfBasicGood   bigserial              NOT NULL,
  Guid            character varying(36)  NOT NULL,
  CreatedDate     bigint                 NOT NULL,
  --! Значение даты последнего обновления при создании объекта равно дате из поля CreatedDate,
  --! используется в качестве версии базового товара
  LastUpdate      bigint                 NOT NULL,
  NameOfGood      character varying(512) NOT NULL,
  UnitsScale      integer                NOT NULL  DEFAULT 0,
  NetWeight       bigint                 NOT NULL,

  CONSTRAINT CF_Goods_BasicBasket_PK                  PRIMARY KEY (IdOfBasicGood),
  CONSTRAINT CF_Goods_BasicBasket_BasicGoodNumber_Key UNIQUE      (Guid),
  CONSTRAINT CF_Goods_BasicBasket_NameOfGood_Key      UNIQUE      (NameOfGood)

);
-- Добавлен расчет суммы базовой корзины
--! Добавлен справочник цен базовых товаров
CREATE TABLE Cf_Good_Basic_Basket_Price
(
  IdOfGoodBasicBasketPrice bigserial NOT NULL,
  IdOfBasicGood bigint NOT NULL,
  IdOfGood bigint,
  GlobalVersion bigint,
  GlobalVersionOnCreate bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean DEFAULT FALSE,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 1,
  OrgOwner bigint,
  IdOfConfigurationProvider bigint, --v51
  Price bigint,
  CONSTRAINT Cf_Good_Basic_Basket_Price_PK                  PRIMARY KEY (IdOfGoodBasicBasketPrice),
  CONSTRAINT Cf_Good_Basic_Basket_Price_BasicGoodNumber_Key UNIQUE      (Guid)
);

--v24
CREATE TABLE  CF_Goods (
  IdOfGood BigSerial NOT NULL,
  IdOfGoodsGroup bigint NOT NULL,
  IdOfTechnologicalMaps bigint,
  IdOfProducts bigint,
  IdOfBasicGood bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  IdOfConfigurationProvider bigint, --v51
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
  IdOfUserCreate bigint,
  IdOfUserEdit bigint,
  IdOfUserDelete bigint,
  BasicGoodLastUpdate bigint,
  CONSTRAINT cf_goods_pk PRIMARY KEY (IdOfGood ),
  CONSTRAINT cf_goods_group_fk FOREIGN KEY (IdOfGoodsGroup)
  REFERENCES cf_goods_groups (IdOfGoodsGroup),
  CONSTRAINT CF_Goods_IdOfBasicGood_FK FOREIGN KEY (IdOfBasicGood)
  REFERENCES CF_Goods_BasicBasket(IdOfBasicGood),
  CONSTRAINT cf_goods_guid_key UNIQUE (guid )
);

CREATE TABLE CF_Menu (
  IdOfMenu      BIGINT        NOT NULL,
  IdOfOrg       BIGINT        NOT NULL,
  MenuDate      BIGINT        NOT NULL,
  CreatedDate   BIGINT        NOT NULL,
  MenuSource    INTEGER       NOT NULL,
  Flags         INTEGER       NOT NULL,
  DetailsHashCode INTEGER, --v51
  CONSTRAINT CF_Menu_pk PRIMARY KEY (IdOfMenu),
  CONSTRAINT CF_Menu_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg)
);

CREATE TABLE CF_MenuDetails (
  IdOfMenuDetail          BIGINT            NOT NULL,
  IdOfMenu                BIGINT            NOT NULL,
  MenuPath                VARCHAR(512)      NOT NULL,
  MenuDetailName          VARCHAR(512)       NOT NULL,
  GroupName               VARCHAR(60)       NOT NULL,
  MenuDetailOutput        VARCHAR(32)      ,
  Price                   BIGINT           ,
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
  Flags                   INT              NOT NULL DEFAULT 1,
  Priority                INT              NOT NULL DEFAULT 0,
  IdOfGood                BIGINT,      --v24
  CONSTRAINT CF_MenuDetail_pk PRIMARY KEY (IdOfMenuDetail),
  CONSTRAINT CF_MenuDetail_IdOfMenu_fk FOREIGN KEY (IdOfMenu) REFERENCES CF_Menu (IdOfMenu),
  CONSTRAINT CF_MenuDetail_IdOfGood_fk FOREIGN KEY (IdOfGood) REFERENCES CF_Goods (IdOfGood)  --v24
);

-- Indexes
CREATE index "cf_menudetail_localid_idx" ON CF_MenuDetails (LocalIdOfMenu);
create index cf_menudetail_idofmenu_idx on cf_menudetails(idOfMenu); --v25



CREATE TABLE CF_ComplexInfo (
  IdOfComplexInfo         BIGINT            NOT NULL,
  IdOfComplex             INT               NOT NULL,
  IdOfOrg                 BIGINT            NOT NULL,
  IdOfDiscountDetail      BIGINT            ,
  IdOfMenuDetail          BIGINT            ,
  IdOfGood                BIGINT            ,
  ComplexName             VARCHAR(60)       NOT NULL,
  MenuDate                BIGINT            NOT NULL,
  ModeFree                INT               NOT NULL,
  ModeGrant               INT               NOT NULL,
  ModeOfAdd               INT               NOT NULL,
  UsedSubscriptionFeeding integer,
  UseTrDiscount           INT               DEFAULT 0, --v51
  CurrentPrice            BIGINT,
  CONSTRAINT CF_ComplexInfo_pk PRIMARY KEY (IdOfComplexInfo),
  CONSTRAINT CF_ComplexInfo_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_ComplexInfo_IdOfMenuDetail_fk FOREIGN KEY (IdOfMenuDetail) REFERENCES Cf_MenuDetails (IdOfMenuDetail),
  CONSTRAINT CF_ComplexInfo_IdOfGood_fk FOREIGN KEY (IdOfGood) REFERENCES CF_Goods (IdOfGood)
);

CREATE TABLE CF_ComplexInfoDetail (
  IdOfComplexInfoDetail   BIGINT            NOT NULL,
  IdOfComplexInfo         BIGINT            NOT NULL,
  IdOfMenuDetail          BIGINT            NOT NULL,
  IdOfItem                BIGINT,
  Count                   INT,
  CONSTRAINT CF_ComplexInfoDetail_pk PRIMARY KEY (IdOfComplexInfoDetail),
  CONSTRAINT CF_ComplexInfoDetail_IdOfMenuDetail_fk FOREIGN KEY (IdOfMenuDetail) REFERENCES CF_MenuDetails (IdOfMenuDetail)
);

create index CF_ComplexInfo_md_idx on CF_ComplexInfo(IdOfMenuDetail);
create index CF_ComplexInfoDetail_md_idx on CF_ComplexInfoDetail(IdOfMenuDetail);

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
  OrderDate       BIGINT        NOT NULL,
  SumByCard       BIGINT        NOT NULL,
  SumByCash       BIGINT        NOT NULL,
  State           INT           NOT NULL DEFAULT 0,
  ConfirmerId     BIGINT,
  Comments        VARCHAR(90)   DEFAULT '',
  OrderType       INT           NOT NULL DEFAULT 1,
  CONSTRAINT CF_Orders_pk PRIMARY KEY (IdOfOrg, IdOfOrder),
  CONSTRAINT CF_Orders_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_Orders_IdOfCard_fk FOREIGN KEY (IdOfCard) REFERENCES CF_Cards (IdOfCard),
  CONSTRAINT CF_Orders_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Orders_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction),
  CONSTRAINT CF_Orders_IdOfPos_fk FOREIGN KEY (IdOfPos) REFERENCES CF_POS (IdOfPos),
  CONSTRAINT CF_Orders_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent)
);

create index CF_Orders_SumByCard_idx on CF_Orders(SumByCard); --v42
create index CF_Orders_SumByCash_idx on CF_Orders(SumByCash); --v42

CREATE TABLE CF_OrderDetails (
  IdOfOrg         BIGINT        NOT NULL,
  IdOfOrderDetail BIGINT        NOT NULL,
  IdOfOrder       BIGINT        NOT NULL,
  IdOfGood        BIGINT        ,
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
  ItemCode        VARCHAR(32),
  IdOfRule  BIGINT DEFAULT NULL,
  CONSTRAINT CF_OrderDetails_pk PRIMARY KEY (IdOfOrg, IdOfOrderDetail),
  CONSTRAINT CF_OrderDetails_IdOfOrg_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_OrderDetails_IdOfOrg_IdOfOrder_fk FOREIGN KEY (IdOfOrg, IdOfOrder) REFERENCES CF_Orders (IdOfOrg, IdOfOrder),
  CONSTRAINT CF_OrderDetails_IdOfOrg_IdOfGood_fk FOREIGN KEY (IdOfGood) REFERENCES CF_Goods (IdOfGood)
);

create index cf_orderdetails_fk_idx on cf_orderdetails(idoforg, idoforder); --v25

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

create index cf_clientpayments_idofca_idx on cf_clientpayments(idofcontragent); --v25
create index cf_clientpayments_crdate_idx on cf_clientpayments(createddate); --v25
create index cf_clientpayments_idca_idpay_idx on cf_clientpayments(idofcontragent, idofpayment); --v25
create index CF_ClientPayments_PaySum_idx on CF_ClientPayments(PaySum); --v42

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
  ClientVersion           VARCHAR(16)     ,
  RemoteAddress           VARCHAR(20)     ,
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

CREATE TABLE CF_RegistrySms (
  IdOfRegistrySMS            BIGINT          NOT NULL,
  Version                 BIGINT          NOT NULL,
  SmsId                   CHAR(16)        NOT NULL,
  CONSTRAINT CF_RegistrySms_pk PRIMARY KEY (IdOfRegistrySMS)
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
  StoragePeriod           BIGINT          DEFAULT -1, --v49
  Enabled                 INTEGER         NOT NULL,
  TemplateFilename        VARCHAR(256),
  Tag varchar(12), --v23
  City varchar(128), --v23
  District varchar(128), --v23
  Location varchar(128), --v23
  Latitude varchar(12), --v23
  Longitude varchar(12), --v23
  AllowManualReportRun  INTEGER NOT NULL default 0, --v42 Необходимо добавить возможность активации ручного запуска для правила
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
  IdOfSms                 CHAR(40)        NOT NULL, -- v36
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

CREATE INDEX CF_ClientSms_IdOfClient_idx on CF_ClientSms(IdOfClient); --v25
CREATE INDEX CF_ClientSms_Price_idx on CF_ClientSms(Price); --v42
CREATE INDEX CF_ClientSms_ServiceSendDate_idx ON CF_ClientSms (ServiceSendDate ASC NULLS LAST); --v48

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
  IdOfSubscriptionFee  BIGSERIAL,
  SubscriptionYear     INTEGER            NOT NULL,
  PeriodNo             INTEGER            NOT NULL,
  IdOfTransaction      BIGINT             NOT NULL,
  SubscriptionSum      BIGINT             NOT NULL,
  CreateTime           BIGINT             NOT NULL,
  SubscriptionType     INTEGER            NOT NULL DEFAULT 0,
  CONSTRAINT CF_SubscriptionFee_pk PRIMARY KEY (IdOfSubscriptionFee),
  CONSTRAINT CF_SubscriptionFee_IdOfTransaction_fk FOREIGN KEY (IdOfTransaction) REFERENCES CF_Transactions (IdOfTransaction)
);

create index CF_SubscriptionFee_SubscriptionSum_idx on CF_SubscriptionFee(SubscriptionSum); --v42

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
  MenuData			VARCHAR(52650)		    NOT NULL,
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
INSERT INTO CF_Users(IdOfUser, Version, UserName, Password, LastChange, Phone, Isblocked) VALUES(1, 0, 'admin', 'MTIz', 0, '', false);

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
INSERT INTO CF_Registry(IdOfRegistry, Version, ClientRegistryVersion)
  VALUES(1, 0, 0);

INSERT INTO CF_RegistrySms(IdOfRegistrySMS, Version, SmsId)
  VALUES(1, 0, '0000000000000000');

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
CREATE index "cf_orders_createddate_idx" ON cf_orders(CreatedDate);
CREATE index "cf_orders_client_idx" ON cf_orders(IdOfClient);

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
  GuardianId              BIGINT, --v51
  CONSTRAINT CF_EnterEvents_pk PRIMARY KEY (IdOfEnterEvent, IdOfOrg)
);

CREATE INDEX cf_enterevents_idofclient_idx on cf_enterevents(idOfClient); --v25
CREATE INDEX cf_enterevents_idevtdt_idx on cf_enterevents(IdOfClient, EvtDateTime); --v25
CREATE index cf_enterevents_org_event_idx ON cf_enterevents(idOfOrg, idOfEnterEvent); --v36
CREATE INDEX cf_enterevents_idvisevtdt_idx ON cf_enterevents (idofvisitor , evtdatetime ); --v43

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
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(3, '0');
-- Option "CLEAN MENU" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(4, '1');
-- Option "MENU DAYS FOR DELETION" (count days)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(5, '30');
-- Option "WRITE JOURNAL TRANSACTIONS" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(6, '0');
-- Option "SEND JOURNAL TRANSACTIONS TO NFP" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(7, '1');
-- Option "NFP SERVICE ADDRESS" (string URL address)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(8, 'http://193.47.154.34:7002/uec-service-war/TransactionService');
-- Option "PASSWORD RESTORE SEED" (long)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(9, '');
-- Option "NOTIFICATION TEXT" (string)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(10, '');
-- Option "DEFAULT OVERDRAFT LIMIT" (long value)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(11, '0');
-- Option "DEFAULT EXPENDITURE LIMIT" (long value)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(12, '20000');
-- Option "SMPP CLIENT STATUS" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(13, '0');
-- Option "DISABLE SMS NOTIFY EDIT IN CLIENT ROOM" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(14, '0');
-- Option "REQUEST SYNC LIMITS" (counts)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(15, '100');
-- Option "OPTION REQUEST SYNC RETRY AFTER" (Timeout millisec)
INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(16, '3600');

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
  CategoryType              INTEGER       NOT NULL DEFAULT 0,
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
  Complex10                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex11                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex12                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex13                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex14                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex15                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex16                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex17                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex18                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex19                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex20                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex21                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex22                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex23                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex24                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex25                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex26                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex27                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex28                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex29                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex30                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex31                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex32                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex33                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex34                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex35                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex36                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex37                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex38                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex39                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex40                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex41                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex42                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex43                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex44                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex45                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex46                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex47                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex48                 INTEGER       NOT NULL DEFAULT 0, --v41
  Complex49                 INTEGER       NOT NULL DEFAULT 0, --v41
  Priority                  Integer       NOT NULL DEFAULT 0,
  CategoriesDiscounts       Character     varying(64),
  OperationOr               integer       NOT NULL DEFAULT 0,
  ComplexesMap              Character     varying(512),
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

--  Таблица категорий Организаций 
CREATE TABLE CF_CategoryOrg
(
  idofcategoryorg bigserial NOT NULL,
  categoryname character varying(255),
  CONSTRAINT cf_categoryorg_pk PRIMARY KEY (idofcategoryorg )
);

--  Таблица связка между CategoryOrg и Org 
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

--  Таблица связка между DiscountRules и CategoryDiscountRule 
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

--  Таблица связка между CategoryOrg и DiscountRule 
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


--  Таблица связка между Client и CategoryDiscountRule 
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
  CONSTRAINT cf_do_confirm_pk PRIMARY KEY (idofdoconfirm ),
  CONSTRAINT cf_do_confirms_uk UNIQUE (distributedobjectclassname, guid, orgowner)  --v51
);

CREATE INDEX cf_do_confirm_all_fields_idx ON cf_do_confirms USING btree (distributedobjectclassname, guid, orgowner); --v51

CREATE TABLE cf_product_groups
(
  IdOfProductGroups BigSerial,
  NameOfGroup character varying(512) NOT NULL,
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  ClassificationCode character varying(32) DEFAULT NULL,
  IdOfConfigurationProvider bigint,
  OrgOwner BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  SendAll integer DEFAULT 0, --v24
  CONSTRAINT cf_product_groups_pk PRIMARY KEY (IdOfProductGroups ),
  CONSTRAINT cf_product_groups_key UNIQUE (guid )
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
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_product_groups (IdOfProductGroups),
  CONSTRAINT cf_products_guid_key UNIQUE (guid )
);

-- Добавлена таблица групп технологических карт
CREATE TABLE cf_technological_map_groups
(
  IdOfTechMapGroups BigSerial,
  NameOfGroup character varying(128) NOT NULL,
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  OrgOwner BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  IdOfConfigurationProvider bigint,
  LastUpdate bigint,
  DeleteDate bigint,
  SendAll integer DEFAULT 0, --v24
  CONSTRAINT cf_technological_map_groups_pk PRIMARY KEY (IdOfTechMapGroups ),
  CONSTRAINT cf_technological_map_groups_guid_key UNIQUE (guid )
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
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_technological_map_groups (IdOfTechMapGroups),
  CONSTRAINT cf_technological_map_guid_key UNIQUE (guid )
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
  globalversiononcreate bigint,
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
  REFERENCES cf_technological_map (IdOfTechnologicalMaps),
  CONSTRAINT cf_technological_map_products_guid_key UNIQUE (guid )
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
  ReportName varchar(512) NOT NULL, --v47
  CreatedDate bigint NOT NULL,
  GenerationTime integer NOT NULL,
  StartDate bigint NOT NULL,
  EndDate bigint NOT NULL,
  ReportFile varchar(256) NOT NULL,
  OrgNum varchar(12),
  IdOfOrg bigint,
  Tag varchar(12),
  IdOfContragentReceiver BIGINT DEFAULT NULL,   --v50
  ContragentReceiver varchar(128) DEFAULT NULL, --v50
  IdOfContragentPayer BIGINT DEFAULT NULL,      --v51
  ContragentPayer varchar(128) DEFAULT NULL,    --v51
  CONSTRAINT cf_report_info_pk PRIMARY KEY (IdOfReportInfo)
);

CREATE index "cf_report_info_start_date_idx" ON CF_ReportInfo (StartDate);
CREATE index "cf_report_info_end_date_idx" ON CF_ReportInfo (EndDate);
CREATE index "cf_report_info_created_date_idx" ON CF_ReportInfo (CreatedDate);
CREATE index "cf_report_info_orgnum_date_idx" ON CF_ReportInfo (OrgNum);
CREATE index "cf_report_info_rulename_idx" ON CF_ReportInfo (RuleName);

-- v24

CREATE TABLE  cf_trade_material_goods (
  IdOfTradeMaterialGood BigSerial NOT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  IdOfConfigurationProvider bigint, --v51
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
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT cf_trade_material_goods_guid_key UNIQUE (guid )
);

--
CREATE TABLE cf_staffs(
  IdOfStaff BigSerial NOT NULL,
  IdOfClient bigint DEFAULT NULL,
  IdOfRole bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  HashCode integer NOT NULL DEFAULT 0, --v25
  CONSTRAINT cf_staff_pk PRIMARY KEY (IdOfStaff ),
  CONSTRAINT cf_staffs_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_goods_requests (
  IdOfGoodsRequest BigSerial NOT NULL,
  IdOfStaff bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_goods_requests_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_goods_requests_positions (
  IdOfGoodsRequestPosition BigSerial NOT NULL,
  IdOfGoodsRequest bigint NOT NULL,
  IdOfGood bigint,
  IdOfProducts bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale  integer NOT NULL DEFAULT 0,
  TotalCount  bigint NOT NULL,
  DailySampleCount bigint default null, --v52
  UpdateHistory text default null,      --v52
  NetWeight  bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_goods_requests_positions_pk PRIMARY KEY (IdOfGoodsRequestPosition ),
  CONSTRAINT cf_goods_requests_positions_goods_request_fk FOREIGN KEY (IdOfGoodsRequest)
  REFERENCES cf_goods_requests (IdOfGoodsRequest),
  CONSTRAINT cf_goods_requests_positions_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_acts_of_waybill_difference (
  IdOfActOfDifference BigSerial NOT NULL,
  IdOfStaff bigint DEFAULT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_acts_of_waybill_difference_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_acts_of_waybill_difference_positions (
  IdOfActOfDifferencePosition BigSerial NOT NULL,
  IdOfActOfDifference bigint DEFAULT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT cf_acts_of_waybill_difference_positions_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_waybills (
  IdOfWayBill BigSerial NOT NULL,
  IdOfStaff bigint NOT NULL,
  IdOfActOfDifference bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  INN character varying(32),
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_waybills_pk PRIMARY KEY (IdOfWayBill ),
  CONSTRAINT cf_waybills_staff_fk FOREIGN KEY (IdOfStaff)
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_waybills_guid_key UNIQUE (guid )

);

CREATE TABLE  cf_waybills_positions (
  IdOfWayBillPosition BigSerial NOT NULL,
  IdOfWayBill bigint NOT NULL,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT cf_waybills_positions_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_acts_of_inventarization (
  IdOfActOfInventarization  BigSerial NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  DateOfAct bigint NOT NULL,
  SendAll integer DEFAULT 0,
  NumberOfAct  character varying(128) NOT NULL,
  Commission  character varying(512) NOT NULL,
  CONSTRAINT cf_acts_of_inventarization_pk PRIMARY KEY (IdOfActOfInventarization ),
  CONSTRAINT cf_acts_of_inventarization_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_internal_disposing_documents (
  IdOfInternalDisposingDocument BigSerial NOT NULL,
  IdOfStaff bigint NOT NULL,
  IdOfActOfInventarization bigint,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_internal_disposing_documents_guid_key UNIQUE (guid )
);

CREATE TABLE  CF_Internal_Disposing_Document_Positions (
  IdOfInternalDisposingDocumentPositions BigSerial NOT NULL,
  IdOfInternalDisposingDocument bigint NOT NULL,
  IdOfTradeMaterialGood bigint,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  UnitsScale bigint NOT NULL DEFAULT 0,
  TotalCount bigint NOT NULL,
  TotalCountMust bigint, --v42 обавлена колонка указывающее количество которое должно было списаться
  NetWeight bigint NOT NULL,
  DisposePrice bigint NOT NULL,
  NDS bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT CF_Internal_Disposing_Document_Positionscf_internal_disposing_document_positions_pk PRIMARY KEY (IdOfInternalDisposingDocumentPositions ),
  CONSTRAINT CF_Internal_Disposing_Document_Positions_internal_disposing_document_fk FOREIGN KEY (IdOfInternalDisposingDocument)
  REFERENCES cf_internal_disposing_documents (IdOfInternalDisposingDocument),
  CONSTRAINT CF_Internal_Disposing_Document_Positions_fk_good FOREIGN KEY (IdOfGood) REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT CF_Internal_Disposing_Document_Positions_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_internal_incoming_documents (
  IdOfInternalIncomingDocument BigSerial NOT NULL,
  IdOfWayBill bigint,
  IdOfInternalDisposingDocument bigint,
  IdOfActOfInventarization bigint,
  IdOfStaff bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_internal_incoming_documents_guid_key UNIQUE (guid )
);

CREATE TABLE  cf_internal_incoming_document_positions (
  IdOfInternalIncomingDocumentPositions BigSerial NOT NULL,
  IdOfInternalIncomingDocument bigint NOT NULL,
  IdOfTradeMaterialGood bigint,
  IdOfGood bigint NOT NULL,
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT cf_internal_incoming_document_positions_guid_key UNIQUE (guid )
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
  globalversiononcreate BIGINT DEFAULT NULL,
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
  REFERENCES cf_staffs (IdOfStaff),
  CONSTRAINT cf_state_changes_guid_key UNIQUE (guid )
);

--v25

CREATE TABLE CF_Account_Refund (
  IdOfAccountRefund BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClient bigint NOT NULL,
  Reason VARCHAR(256),
  CreatedBy bigint NOT NULL,
  IdOfTransaction bigint NOT NULL,
  RefundSum bigint NOT NULL,
  CONSTRAINT cf_account_refund_pk PRIMARY KEY (IdOfAccountRefund),
  CONSTRAINT cf_account_refund_clt_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_account_refund_tr_fk FOREIGN KEY (IdOfTransaction) REFERENCES cf_transactions (IdOfTransaction)
);

CREATE INDEX cf_account_refund_idofcl_idx on CF_Account_Refund(IdOfClient);

--v26

CREATE TABLE cf_publications
(
  IdOfPublication BigSerial NOT NULL,
  DataOfPublication bytea NOT NULL,
  Author character varying(255),
  Title character varying(512),
  Title2 character varying(255),
  PublicationDate character varying(15),
  Publisher character varying(255),
  ISBN character varying(255),
  ValidISBN boolean NOT NULL DEFAULT false,
  Hash integer NOT NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_publications_pk PRIMARY KEY (IdOfPublication ),
  CONSTRAINT cf_publications_GUID_key UNIQUE (GUID )
);

CREATE INDEX cf_publications_idofpublication_idx ON cf_publications (idofpublication );

--книговыдача
--IdOfParentCirculation - родительская выдача (древовидная структура для продления выдач)
--IdOfReader - читатель --можно выкинуть Readers, тогда связь будет сразу на client
--IdOfIssuable - книга/журнал
--IssuanceDate - дата выдачи
--RefundDate - дата возврата(срок)
--RealRefundDate - дата возврата
--Status - статус(выдано, возвращено, т.п.)
CREATE TABLE cf_circulations
(
  IdOfCirculation BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfParentCirculation bigint,
  IdOfIssuable bigint NOT NULL,
  IssuanceDate bigint NOT NULL DEFAULT 0,
  RefundDate bigint NOT NULL DEFAULT 0,
  RealRefundDate bigint,
  Status integer NOT NULL DEFAULT 0,
  Quantity integer NOT NULL DEFAULT 0,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_circulation_pk PRIMARY KEY (IdOfCirculation ),
  CONSTRAINT cf_circulation_client_fk FOREIGN KEY (IdOfClient)
  REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_circulation_GUID_key UNIQUE (GUID )
);

CREATE INDEX cf_circulations_idofcirculation_idx ON cf_circulations (idofcirculation );

--выдаваемая сущность
--BarCode - штрихкод
--Type - можешь просто смотреть, какое из след. двух полей не null
--IdOfInstance - ид книги
--IdOfJournalItem - ид журнала
--Issuance - текущая незакрытая выдача
--штрихкода уникальны
CREATE TABLE cf_issuable
(
  IdOfIssuable BigSerial NOT NULL,
  BarCode bigint,
  TypeOfIssuable character(1) NOT NULL DEFAULT 'i',
  idofinstance bigint,
  idofjournalitem bigint,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_issuable_pk PRIMARY KEY (IdOfIssuable ),
  CONSTRAINT cf_issuable_guid_key UNIQUE (guid )
);

CREATE INDEX cf_issuable_idofissuable_idx ON cf_issuable (idofissuable );

--тип сопр.документа
--TypeOfAccompanyingDocumentName - название (акт, накладная, т.п.)
CREATE TABLE cf_typesofaccompanyingdocuments (
  IdOfTypeOfAccompanyingDocument bigserial NOT NULL,
  TypeOfAccompanyingDocumentName varchar(45) default NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_typesofaccompanyingdocument_pk PRIMARY KEY (IdOfTypeOfAccompanyingDocument ),
  CONSTRAINT cf_typesofaccompanyingdocuments_guid_key UNIQUE (guid )
);

--источник поступления книг
--SourceName - название источника (минобр, т.п.)
CREATE TABLE cf_sources (
  IdOfSource bigserial NOT NULL,
  SourceName varchar(127) default NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_source_pk PRIMARY KEY (IdOfSource ),
  CONSTRAINT cf_sources_guid_key UNIQUE (guid )
);

--сопр.документы
--IdOfTypeOfAccompanyingDocument - тип(накладная,акт) - можно сразу текстом, вместо связи
--AccompanyingDocumentNumber - номер
--IdOfSource - источник поступления книг можно сразу текстом, вместо связи
CREATE TABLE cf_accompanyingdocuments (
  IdOfAccompanyingDocument bigserial NOT NULL,
  IdOfTypeOfAccompanyingDocument bigint NOT NULL REFERENCES cf_typesofaccompanyingdocuments(IdOfTypeOfAccompanyingDocument),
  AccompanyingDocumentNumber varchar(32) NOT NULL,
  IdOfSource bigint default NULL REFERENCES cf_sources(IdOfSource),
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_accompanyingdocument_pk PRIMARY KEY (IdOfAccompanyingDocument ),
  CONSTRAINT cf_accompanyingdocuments_guid_key UNIQUE (guid )
);

--фонд
--FundName - название
--! `InvBook` bigint(20) NOT NULL,- инвентарная книга фонда
-- Stud - булево поле если true фонд учебников иначе фонд худ. литературы
CREATE TABLE cf_funds (
  IdOfFund bigserial NOT NULL,
  FundName varchar(127) default NULL,
  Stud boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_fund_pk PRIMARY KEY (IdOfFund ),
  CONSTRAINT cf_funds_guid_key UNIQUE (guid )
);

--инвентарная книга
--BookName - название
CREATE TABLE cf_inventorybooks (
  IdOfBook bigserial NOT NULL,
  BookName varchar(255) default NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_inventorybook_pk PRIMARY KEY (IdOfBook ),
  CONSTRAINT cf_inventorybooks_guid_key UNIQUE (guid )
);

--запись КСУ1 (о приходе)
--RecordNumber - номер (каждый уч.год начинается с 1)
--IdOfFund bigint - фонд
--IncomeDate - дата поступления книг
--AccompanyingDocument - сопроводительный документ
CREATE TABLE cf_ksu1records (
  IdOfKSU1Record bigserial NOT NULL,
  RecordNumber int NOT NULL default '0',
  IdOfFund bigint NOT NULL REFERENCES cf_funds(IdOfFund),
  IncomeDate date default NULL,
  AccompanyingDocument bigint default '0' REFERENCES cf_accompanyingdocuments(IdOfAccompanyingDocument),
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_ksu1record_pk PRIMARY KEY (IdOfKSU1Record ),
  CONSTRAINT cf_ksu1records_guid_key UNIQUE (guid )
);

--причина выбытия из фонда
--RetirementReasonName - название причины (в макулатуру, потерялось, съели)
CREATE TABLE cf_retirementreasons (
  IdOfRetirementReason bigserial NOT NULL,
  RetirementReasonName varchar(45) default NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_retirementreason_pk PRIMARY KEY (IdOfRetirementReason ) ,
  CONSTRAINT cf_retirementreason_guid_key UNIQUE (guid )
);

--запись КСУ2 (о списании)
--RecordNumber - номер
--IdOfFund - фонд
--RetirementDate - дата списания
--IdOfRetirementReason - причина выбытия из фонда - можно сразу текстом, тогда соотв таблица не нужна
--нумерация продолжается из года в год, номера уникальны в пределах фонда
--нумерация продолжается из года в год, номера уникальны в пределах фонда
CREATE TABLE cf_ksu2records (
  IdOfKSU2Record bigserial NOT NULL,
  RecordNumber int NOT NULL,
  IdOfFund bigint NOT NULL REFERENCES cf_funds(IdOfFund),
  RetirementDate date NOT NULL,
  IdOfRetirementReason bigint NOT NULL REFERENCES cf_retirementreasons(IdOfRetirementReason),
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_ksu2records_pkey PRIMARY KEY (idofksu2record ),
  CONSTRAINT cf_ksu2records_idoffund_recordnumber_key UNIQUE (idoffund , recordnumber ),
  CONSTRAINT cf_ksu2records_guid_key UNIQUE (guid )
);

--книга
--IdOfPublication - ид библ.записи(автор,название,т.п.)
--InGroup - находится ли книга в учетной карточке
--IdOfFund - фонд
--InvNumber - инв. номер
--InvBook bigint - инв.книга - можно название инвентарной книги строкой, тогда след. таблица не нужна
--IdOfKSU1Record - запись в КСУ1 (о приходе)
--IdOfKSU2Record - запись в КСУ2 (о списании)
--Cost - цена (без переоценки фонда; нам нужна переоценка фонда?)
--в одной инв. книге номера уникальны
CREATE TABLE cf_instances (
  IdOfInstance bigserial NOT NULL,
  IdOfPublication bigint NOT NULL,
  InGroup boolean NOT NULL DEFAULT false,
  IdOfFund bigint default NULL,
  InvNumber varchar(10) default NULL,
  InvBook bigint default NULL,
  IdOfKSU1Record bigint default NULL,
  IdOfKSU2Record bigint default NULL,
  Cost int NOT NULL default '0',
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_instances_pkey PRIMARY KEY (idofinstance ),
  CONSTRAINT cf_instances_idoffund_fkey FOREIGN KEY (idoffund)
  REFERENCES cf_funds (idoffund),
  CONSTRAINT cf_instances_idofksu1record_fkey FOREIGN KEY (idofksu1record)
  REFERENCES cf_ksu1records (idofksu1record),
  CONSTRAINT cf_instances_idofksu2record_fkey FOREIGN KEY (idofksu2record)
  REFERENCES cf_ksu2records (idofksu2record),
  CONSTRAINT cf_instances_invbook_fkey FOREIGN KEY (invbook)
  REFERENCES cf_inventorybooks (idofbook),
  CONSTRAINT cf_instances_invbook_invnumber_key UNIQUE (invbook , invnumber ),
  CONSTRAINT cf_instances_guid_key UNIQUE (guid )
);

CREATE INDEX cf_instances_idofinstance_idx ON cf_instances (idofinstance );

--журналы(тип)
--IdOfFund - фонд
--IsNewspaper - газета или журнал (газета не заносится в фонд)
--IdOfPublication - ид библ.записи(автор,название,т.п.)
--MonthCount - кол-во номеров в месяц
--Count - кол-во подписок
CREATE TABLE cf_journals (
  IdOfJournal bigserial NOT NULL,
  IdOfFund bigint default NULL REFERENCES cf_funds(IdOfFund),
  IsNewspaper boolean NOT NULL DEFAULT false,
  IdOfPublication bigint NOT NULL REFERENCES cf_publications(idofpublication),
  MonthCount int NOT NULL default '0',
  Count int NOT NULL default '0',
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  PRIMARY KEY  (IdOfJournal),
  CONSTRAINT cf_journals_guid_key UNIQUE (guid )
);

--журналы
--IdOfJournal - журнал(тип)
--IdOfFund -фонд
--Date -дата выпуска (для журналов и газет важно)
--Number -номер
--Cost -цена
--IdOfKSU1Record -запись в КСУ1 (о приходе)
--IdOfKSU2Record -запись в КСУ2 (о списании)
CREATE TABLE cf_journalitems (
  IdOfJournalItem bigserial NOT NULL,
  IdOfJournal bigint NOT NULL REFERENCES cf_journals(IdOfJournal),
  IdOfFund bigint default NULL REFERENCES cf_funds(IdOfFund),
  Date date NOT NULL,
  Number varchar(10) NOT NULL default '',
  Cost int NOT NULL default '0',
  IdOfKSU1Record bigint default NULL REFERENCES cf_ksu1records(IdOfKSU1Record),
  IdOfKSU2Record bigint default NULL REFERENCES cf_ksu2records(IdOfKSU2Record),
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  PRIMARY KEY  (IdOfJournalItem),
  CONSTRAINT cf_journalitems_guid_key UNIQUE (guid )
);

--посещение бибилотеки
--IdOfClient bigint - читатель (тут сразу клиент)
--Source - источник поступления (вручную бибилотекарем (возможны накрутки!!!) либо через книговыдачу либо через СКУД)
--Date - время посещения
CREATE TABLE cf_libvisits (
  IdOfLibVisit bigserial NOT NULL,
  IdOfClient bigint default NULL REFERENCES cf_clients(idofclient),
  Source int NOT NULL default '0',
  Date timestamp NOT NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  PRIMARY KEY  (IdOfLibVisit),
  CONSTRAINT cf_libvisits_guid_key UNIQUE (guid )
);


CREATE TABLE CF_ComplexInfo_DiscountDetail
(
  IdOfDiscountDetail bigserial NOT NULL,
  Size double precision NOT NULL,
  IsAllGroups integer NOT NULL,
  IdOfClientGroup bigint,
  MaxCount integer,
  IdOfOrg bigint,
  CONSTRAINT CF_ComplexInfo_DiscountDetail_pk PRIMARY KEY (IdOfDiscountDetail)
);

-- Настройки ECafe администратора
CREATE TABLE cf_ECafeSettings
(
  IdOfECafeSetting bigserial NOT NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 0,
  SettingValue character varying(128),
  Identificator bigint,
  settingtext character varying(128),   -- v32
  CONSTRAINT cf_ECafeSetting_pk PRIMARY KEY (IdOfECafeSetting),
  CONSTRAINT cf_ECafeSettings_key UNIQUE (guid )
);

-- Таблица родительских запретов на определенные блюда
CREATE TABLE CF_Dish_Prohibitions
(
  IdOfProhibition bigserial NOT NULL,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer DEFAULT 0,
  IdOfClient bigint NOT NULL,
  IdOfProducts bigint,
  IdOfProductGroups bigint,
  IdOfGood bigint,
  IdOfGoodsGroup bigint,
  CONSTRAINT CF_Dish_Prohibitions_pk PRIMARY KEY (IdOfProhibition),
  CONSTRAINT CF_Dish_Prohibitions_IdOfClient_fk FOREIGN KEY (IdOfClient)
  REFERENCES CF_clients (IdOfClient),
  CONSTRAINT CF_Dish_Prohibitions_idofgood_fk FOREIGN KEY (IdOfGood)
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT CF_Dish_Prohibitions_IdOfGoodsGroup_fk FOREIGN KEY (IdOfGoodsGroup)
  REFERENCES CF_goods_groups (IdOfGoodsGroup),
  CONSTRAINT CF_Dish_Prohibitions_IdOfProductGroups_fk FOREIGN KEY (IdOfProductGroups)
  REFERENCES CF_product_groups (IdOfProductGroups),
  CONSTRAINT CF_Dish_Prohibitions_IdOfProducts_fk FOREIGN KEY (IdOfProducts)
  REFERENCES CF_Products (IdOfProducts),
  CONSTRAINT CF_Dish_Prohibitions_GUID_key UNIQUE (GUID),
  CONSTRAINT CF_Dish_Prohibitions_Check_OnlyOneIsNotNull CHECK ((
                                                                  CASE
                                                                  WHEN IdOfProducts IS NOT NULL THEN 1
                                                                  ELSE 0
                                                                  END +
                                                                  CASE
                                                                  WHEN IdOfProductGroups IS NOT NULL THEN 1
                                                                  ELSE 0
                                                                  END +
                                                                  CASE
                                                                  WHEN IdOfGood IS NOT NULL THEN 1
                                                                  ELSE 0
                                                                  END +
                                                                  CASE
                                                                  WHEN IdOfGoodsGroup IS NOT NULL THEN 1
                                                                  ELSE 0
                                                                  END) = 1)
);

-- Таблица исключений из запретов для товаров либо групп товаров
CREATE TABLE CF_Dish_Prohibition_Exclusions
(
  IdOfExclusion bigserial NOT NULL,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  OrgOwner bigint,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer DEFAULT 0,
  IdOfProhibition bigint NOT NULL,
  IdOfGood bigint,
  IdOfGoodsGroup bigint,
  CONSTRAINT CF_Dish_Prohibition_Exclusions_pk PRIMARY KEY (IdOfExclusion),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_idofprohibition_fk FOREIGN KEY (IdOfProhibition)
  REFERENCES cf_dish_Prohibitions (IdOfProhibition),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_IdOfGood_fk FOREIGN KEY (IdOfGood)
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_IdOfGoodsGroup_fk FOREIGN KEY (IdOfGoodsGroup)
  REFERENCES CF_Goods_Groups (IdOfGoodsGroup),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_GUID_key UNIQUE (guid),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_Check_OneIsNotNull CHECK (COALESCE(IdOfGood, IdOfGoodsGroup) IS NOT NULL),
  CONSTRAINT CF_Dish_Prohibition_Exclusions_Check_OneIsNull CHECK ((IdOfGood + IdOfGoodsGroup) IS NULL)
);

CREATE TABLE CF_ProjectState_Data
(
  Period bigint NOT NULL,
  Type int NOT NULL,
  StringKey character varying(128),
  StringValue character varying(128),
  GenerationDate bigint NOT NULL,
  Region char(128) default 'Все округа',
  Comments char(255) default '',
  CONSTRAINT CF_ProjectState_Data_PK PRIMARY KEY (Period, Type, Region, StringKey)
);


CREATE TABLE CF_ClientsComplexDiscounts
(
  IdOfClientsComplexDiscount bigserial NOT NULL,
  CreateDate bigint NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfRule bigint NOT NULL,
  IdOfCategoryOrg bigint NOT NULL,
  Priority int NOT NULL,
  OperationAr int NOT NULL,
  IdOfComplex int NOT NULL,
  CONSTRAINT CF_ClientsComplexDiscounts_pk PRIMARY KEY (IdOfClientsComplexDiscount),
  CONSTRAINT CF_ClientsComplexDiscounts_UNIQUE_key UNIQUE (IdOfClient, IdOfRule, IdOfCategoryOrg, Priority, IdOfComplex)
);

-- Таблица вопросов анкеты
CREATE TABLE CF_QA_Questionaries
(
  IdOfQuestionary bigserial NOT NULL,
  QuestionName character varying(90) NOT NULL,
  Question character varying(90) NOT NULL,
  Description character varying(255),
  Status integer NOT NULL DEFAULT 0,
  Type integer DEFAULT 0,
  CreatedDate bigint NOT NULL,
  UpdatedDate bigint,
  ViewDate bigint,
  CONSTRAINT CF_QA_Questionaries_pk PRIMARY KEY (IdOfQuestionary )
);

-- Таблица вариантов ответа
CREATE TABLE CF_QA_Answers
(
  IdOfAnswer bigserial NOT NULL,
  IdOfQuestionary bigint NOT NULL,
  Answer character varying(90) NOT NULL,
  Description character varying(255),
  Weight integer NOT NULL DEFAULT 1,
  CreatedDate bigint NOT NULL,
  UpdatedDate bigint,
  CONSTRAINT CF_QA_Answers_pk PRIMARY KEY (IdOfAnswer ),
  CONSTRAINT CF_QA_Answers_Question_fk FOREIGN KEY (IdOfQuestionary) REFERENCES CF_QA_Questionaries (IdOfQuestionary)
);

-- Таблица отношений анкет и организаций
CREATE TABLE CF_QA_Organization_Questionary
(
  IdOfOrgQuestionary bigserial NOT NULL,
  IdOfQuestionary bigint NOT NULL,
  IdOfOrg bigint NOT NULL,
  CONSTRAINT CF_QA_Organization_Questionary_pk PRIMARY KEY (IdOfOrgQuestionary ),
  CONSTRAINT CF_QA_Organization_Questionary_Org_fk FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
  CONSTRAINT CF_QA_Organization_Questionary_Questionary_fk FOREIGN KEY (IdOfQuestionary) REFERENCES CF_QA_Questionaries (IdOfQuestionary)
);

-- Таблица ответов клиента
CREATE TABLE CF_QA_ClientAnswerByQuestionary
(
  IdOfClientAnswerByQuestionary bigserial NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfAnswer bigint NOT NULL,
  CreatedDate bigint NOT NULL,
  UpdatedDate bigint,
  CONSTRAINT CF_QA_ClientAnswerByQuestionary_pk PRIMARY KEY (IdOfClientAnswerByQuestionary ),
  CONSTRAINT CF_QA_ClientAnswerByQuestionary_Answer FOREIGN KEY (IdOfAnswer) REFERENCES CF_QA_Answers (IdOfAnswer),
  CONSTRAINT CF_QA_ClientAnswerByQuestionary_Client FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);

-- Таблица промежуточных результатов ответа по организациям
-- CREATE TABLE CF_ClientSms
-- (
--   IdOfQuestionaryResultByOrg bigserial NOT NULL,
--   IdOfOrg bigint NOT NULL,
--   IdOfQuestionary bigint NOT NULL,
--   IdOfAnswer bigint NOT NULL,
--   Count bigint NOT NULL DEFAULT 0,
--   UpdatedDate bigint,
--   CONSTRAINT CF_QA_QuestionaryResultByOrg_pk PRIMARY KEY (IdOfQuestionaryResultByOrg ),
--   CONSTRAINT CF_QA_QuestionaryResultByOrg_Answer FOREIGN KEY (IdOfAnswer) REFERENCES CF_QA_Answers (IdOfAnswer),
--   CONSTRAINT CF_QA_QuestionaryResultByOrg_Org FOREIGN KEY (IdOfOrg) REFERENCES CF_Orgs (IdOfOrg),
--   CONSTRAINT CF_QA_QuestionaryResultByOrg_Questionary FOREIGN KEY (IdOfQuestionary) REFERENCES CF_QA_Questionaries (IdOfQuestionary)
-- );

-- Таблица жалоб на товары из совершенных заказов
CREATE TABLE CF_Goods_ComplaintBook
(
  IdOfComplaint bigserial NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfGood bigint NOT NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean DEFAULT FALSE,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 4,
  OrgOwner bigint,
  CONSTRAINT CF_Goods_ComplaintBook_pk PRIMARY KEY (IdOfComplaint),
  CONSTRAINT CF_Goods_ComplaintBook_IdOfClient_fk FOREIGN KEY (IdOfClient)
  REFERENCES CF_Clients (IdOfClient),
  CONSTRAINT CF_Goods_ComplaintBook_IdOfGood_fk FOREIGN KEY (IdOfGood)
  REFERENCES CF_Goods (IdOfGood),
  CONSTRAINT CF_Goods_ComplaintBook_IdOfClient_IdOfGood_key UNIQUE (IdOfClient, IdOfGood)
);

-- Таблица итераций подачи жалоб
CREATE TABLE CF_Goods_Complaint_Iterations
(
  IdOfIteration bigserial NOT NULL,
  IdOfComplaint bigint NOT NULL,
  IterationNumber integer NOT NULL DEFAULT 0,
  IterationStatus integer NOT NULL DEFAULT 0,
  ProblemDescription character varying(512),
  Conclusion character varying(512),
  GlobalVersion bigint,
  globalversiononcreate bigint,
  DeletedState boolean DEFAULT FALSE,
  GUID character varying(36) NOT NULL,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 4,
  OrgOwner bigint,
  CONSTRAINT CF_Goods_Complaint_Iterations_pk PRIMARY KEY (IdOfIteration),
  CONSTRAINT CF_Goods_Complaint_Iterations_IdOfComplaint_fk FOREIGN KEY (IdOfComplaint)
  REFERENCES CF_Goods_ComplaintBook (IdOfComplaint),
  CONSTRAINT CF_Goods_Complaint_Iterations_IdOfComplaint_IterationNumber_key UNIQUE (IdOfComplaint, IterationNumber)
);

-- Таблица списков причин подачи жалобы
CREATE TABLE CF_Goods_Complaint_Causes
(
  IdOfCause bigserial NOT NULL,
  IdOfIteration bigint NOT NULL,
  Cause integer NOT NULL,
  GlobalVersion bigint,
  globalversiononcreate bigint,
  DeletedState boolean DEFAULT FALSE,
  GUID character varying(36) NOT NULL,
  DeleteDate bigint,
  LastUpdate bigint,
  CreatedDate bigint NOT NULL,
  SendAll integer DEFAULT 4,
  OrgOwner bigint,
  CONSTRAINT CF_Goods_Complaint_Causes_pk PRIMARY KEY (IdOfCause),
  CONSTRAINT CF_Goods_Complaint_Causes_idofiteration_fk FOREIGN KEY (IdOfIteration)
  REFERENCES cf_goods_complaint_Iterations (IdOfIteration),
  CONSTRAINT CF_Goods_Complaint_Causes_IdOfIteration_Cause_key UNIQUE (IdOfIteration, Cause)
);

-- Таблица деталей заказов, к товарам из состава которых у клиента возникли претензии
CREATE TABLE CF_Goods_Complaint_Orders
(
  IdOfOrder bigserial NOT NULL,
  IdOfIteration bigint NOT NULL,
  idoforderorg bigint NOT NULL,
  idoforderdetail bigint NOT NULL,
  globalversion bigint,
  globalversiononcreate bigint,
  deletedstate boolean DEFAULT FALSE,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  CONSTRAINT cf_goods_complaint_orders_pk PRIMARY KEY (idoforder),
  CONSTRAINT cf_goods_complaint_orders_idofiteration_fk FOREIGN KEY (idofiteration)
  REFERENCES cf_goods_complaint_iterations (idofiteration),
  CONSTRAINT cf_goods_complaint_orders_idoforderorg_fk FOREIGN KEY (idoforderorg)
  REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_goods_complaint_orders_idoforderdetail_fk FOREIGN KEY (idoforderorg, idoforderdetail)
  REFERENCES cf_orderdetails (idoforg, idoforderdetail),
  CONSTRAINT cf_goods_complaint_orders_idofiteration_idoforderorg_idoforderd UNIQUE (idofiteration, idoforderorg, idoforderdetail)
);

-- Возможные причины подачи жалоб
CREATE TABLE cf_possible_complaint_causes
(
  causenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_causes_pk PRIMARY KEY (causenumber)
);

-- Названия статусов итераций жалоб
CREATE TABLE cf_possible_complaint_iteration_states
(
  statenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_iteration_states_pk PRIMARY KEY (statenumber )
);

create table CF_ClientMigrationHistory
(
  IdOfClientMigration bigserial,
  IdOfClient bigint not null,
  IdOfOrg bigint not null,
  RegistrationDate bigint not null,
  CONSTRAINT CF_ClientMigrationHistory_pk PRIMARY KEY (IdOfClientMigration)
);

CREATE TABLE CF_ClientsNotificationSettings
(
  IdOfSetting   bigserial                      NOT NULL,
  IdOfClient    bigint                         NOT NULL,
  NotifyType    bigint                         NOT NULL,
  CreatedDate   bigint                         NOT NULL,

  CONSTRAINT CF_ClientsSMSSetting_PK           PRIMARY KEY (IdOfSetting),
  CONSTRAINT CF_ClientsSMSSetting_NotifyPair   UNIQUE      (IdOfClient, NotifyType)
);

-- begin v42
-- Таблица регистрации временных карт
CREATE TABLE cf_cards_temp (
  IdOfCartTemp bigserial,
  IdOfOrg bigint,                        --  идентификатор организациии 
  IdOfClient bigInt,                     --  Идентификатор клиента 
  IdOfVisitor bigint,                    --  Идентификатор посетителя 
  VisitorType bigint not null default 0, --  Признак карты посетителя , bit, 1- карта посетителя, 0 — карта клиента, not null 
  CardNo bigint NOT NULL,                --  номер карты 
  CardPrintedNo character varying(24),   --  номер нанесенный на карту 
  CardStation int not null default 0,    --  int16 или int8, not null, значения-  0 — свободна, 1 — выдана , 3 — заблокирована (? не уверен, что блокировка нужна) 
  CreateDate bigint not null,            --  Дата и время регистрации карты 
  ValidDate bigint,                      --  Дата завершения действия карты 
  CONSTRAINT cf_cards_temp_pk PRIMARY KEY (IdOfCartTemp),
  CONSTRAINT cf_cards_temp_organization FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT CardNo_Unique UNIQUE (CardNo)
);

-- Таблица зарегистрированных операций по временным картам
CREATE TABLE cf_card_temp_operations(
  IdOfCardTempOperation bigserial not null,   --  первичный ключ процесинга 
  LocalIdOperation bigint NOT NULL,           --  первичный ключ школы 
  IdOfOrg bigint not null,                    --  внешний ключ на IdOfOrg из соотв. таблицы — равен идентификатору организации, на которую зарегистрирована врем. карта или, в случае врем. карты посетителя — идентификатору организации, в которой была произведена эта операция. 
  IdOfCartTemp bigint not null,               --  внешний ключ или на физ. идентификатор временной карты или на первичный ключ соотв. записи из TempCards 
  IdOfClient bigint,                          --  Идентификатор клиента 
  IdOfVisitor bigint,                         --  Идентификатор посетителя 
  OperationType int not null,                 --  Тип операции- int16 или int8, not null, значения-  0 — регистрация, 1 — выдача ,2 – возврат, 3 — блокировка 
  OperationDate bigint not null,              --  Дата и время операции 
  CONSTRAINT cf_card_temp_operations_pk PRIMARY KEY (IdOfCardTempOperation),
  CONSTRAINT cf_card_temp_operations_organization FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_card_temp_operation_org_local_id UNIQUE (IdOfOrg , LocalIdOperation )
);

-- Таблица посетителей
CREATE TABLE cf_visitors(
  IdOfVisitor bigserial not null,                --  первичный ключ 
  IdOfPerson BIGINT NOT NULL,                    --  внешний ключ на ФИО посетителя 
  PassportNumber varchar(50),                    --  Серийный номер паспорта 
  PassportDate BIGINT,                           --  Дата выдачи паспорта 
  WarTicketNumber varchar(50),                   --  Серийный номер водительского удостоверения (ВУ) 
  WarTicketDate BIGINT,                          --  Дата выдачи ВУ 
  DriverLicenceNumber varchar(50),               --  Серийный номер военного билета (ВБ) 
  DriverLicenceDate BIGINT,                      --  Дата выдачи ВБ 
  VisitorType INTEGER NOT NULL DEFAULT 0,        --  Добавлен тип постетителя (DEFAULT 0 обычный, EMPLOYEE 1 инженер)
  IsDeleted INTEGER NOT NULL DEFAULT 0,          -- v50
  CONSTRAINT cf_visitors_pk PRIMARY KEY (IdOfVisitor),
  CONSTRAINT cf_visitors_IdOfPerson_fk FOREIGN KEY (IdOfPerson) REFERENCES CF_Persons (IdOfPerson)
);

-- Таблица списка ошибок зафиксированных во время синхронизации
CREATE TABLE cf_synchistory_exceptions
(
  idofsynchistoryexception bigserial NOT NULL,
  idoforg bigint NOT NULL,
  idofsync bigint NOT NULL,
  message character varying(512) NOT NULL,
  CONSTRAINT cf_synchistory_exceptions_pk PRIMARY KEY (idofsynchistoryexception),
  CONSTRAINT cf_synchistory_exceptions_organization FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_synchistory_exceptions_sync FOREIGN KEY (idofsync) REFERENCES cf_synchistory (idofsync)
);

-- Таблица привязки GuardSAN и клиентов.
-- Ранее поле GuardSAN располагалось в CF_Clients, чем сильно загружало процессинг
create table CF_GuardSan  (
  IdOfGuardSan      bigserial     NOT NULL,
  IdOfClient        BIGINT        NOT NULL,
  GuardSan          VARCHAR(11)   NOT NULL,
  CONSTRAINT CF_GuardSan_pk PRIMARY KEY (IdOfGuardSan),
  CONSTRAINT CF_Client_GuardSan_IdOfClient_fk FOREIGN KEY (IdOfClient) REFERENCES CF_Clients (IdOfClient)
);
create index CF_GuardSan_GuardSan_idx on CF_GuardSan(GuardSAN);

-- Наименование комплексов
create table CF_ComplexRoles(
  IdOfRole bigint not null,
  RoleName varchar(128),
  ExtendRoleName varchar(128),
  CONSTRAINT CF_ComplexRole_pk PRIMARY KEY (IdOfRole)
);

-- Добавлена таблица "Правила распределения клиентов".
-- v45
CREATE TABLE CF_ClientAllocationRule (
  IdOfClientAllocationRule bigserial,
  IdOfSourceOrg bigint not null,
  IdOfDestinationOrg bigint not null,
  GroupFilter varchar(255) not null,
  IsTempClient boolean not null default false,
  CONSTRAINT CF_ClientAllocationRule_PK PRIMARY KEY (IdOfClientAllocationRule),
  CONSTRAINT CF_ClientAllocationRule_Unique UNIQUE (IdOfSourceOrg, IdOfDestinationOrg, GroupFilter),
  CONSTRAINT CF_ClientAllocationRule_SOrg_FK FOREIGN KEY (IdOfSourceOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT CF_ClientAllocationRule_DOrg_FK FOREIGN KEY (IdOfDestinationOrg) REFERENCES cf_orgs (IdOfOrg)
);

--v47
CREATE TABLE cf_temporary_orders (
  IdOfOrg bigint not null,
  IdOfClient bigInt not null,
  IdOfComplex int not null,
  PlanDate bigint not null,
  Action int not null,
  IdOfReplaceClient bigInt,
  CreationDate bigint not null,
  ModificationDate bigint,
  IdOfOrder bigint default null,
  IdOfUser bigint not null,
  CONSTRAINT cf_temporary_orders_pk PRIMARY KEY (IdOfOrg, IdOfClient, IdOfComplex, PlanDate),
  CONSTRAINT cf_temporary_orders_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_temporary_orders_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

--v47
CREATE TABLE cf_thin_client_users (
  IdOfClient bigint not null,
  UserName varchar(64) not null,
  Password varchar(128) not null,
  Role int not null default 1,
  CreationDate bigint not null,
  ModificationDate bigint,
  CONSTRAINT cf_thin_client_users_pk PRIMARY KEY (UserName),
  CONSTRAINT cf_thin_client_users_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- Таблица для хранения поступивших из Реестров изменений
--v47
create table CF_RegistryChange (
  IdOfRegistryChange bigserial not null,
  IdOfOrg bigint not null,
  CreateDate bigint not null,
  ClientGUID varchar(40) not null,
  FirstName varchar(64) not null,
  SecondName varchar(128) not null,
  Surname varchar(128) not null,
  GroupName varchar(64) not null,
  FirstNameFrom varchar(64),
  SecondNameFrom varchar(128),
  SurnameFrom varchar(128),
  GroupNameFrom varchar(64),
  IdOfMigrateOrgTo bigint,
  IdOfMigrateOrgFrom bigint,
  IdOfClient bigint,
  Operation integer not null,
  Applied boolean not null default false,
  CONSTRAINT cf_registrychange_pk PRIMARY KEY (IdOfRegistryChange),
  CONSTRAINT cf_registrychange_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg)
);

-- Таблица для хранения ошибок по поступившим из Реестров изменениям
create table CF_RegistryChange_Errors (  --v47
  IdOfRegistryChangeError bigserial not null,
  IdOfOrg bigint not null,
  RevisionCreateDate bigint not null,
  Error varchar(256) not null,
  ErrorDetails varchar(256) default '', --v49
  Comment varchar(256) default '',
  CommentAuthor VARCHAR(64) default '',
  CreateDate bigint not null,
  CommentCreateDate bigint,
  CONSTRAINT CF_RegistryChange_Errors_pk PRIMARY KEY (IdOfRegistryChangeError)
);

--! ECAFE-1188 - Реализовать функцию авто-пополнения счета по банковской карте через Банк Москвы - Acquiropay
-- Информация о подписках клиентов на автопополнение баланса с банк. карты.
-- уникальный идентификатор подписки на услугу в ИС ПП */
-- сумма пополнения */
-- пороговое значение баланса, при достижении ко/го баланс автопополняется */
-- срок действия подписки (число месяцев c даты подключения) */
-- дата, до ко\ой подписка активна (вычисляется относит-но даты подключения) */
-- дата подключения подписки */
-- дата отключения подписки */
-- флаг активности подписки */
-- статус подписки */
-- id подписки в системе МФР */
-- клиент */
-- СНИЛС клиента */
-- идентификатор системы, через ко/ую происходит автопополнение баланса */
-- дата последнего успешного платежа */
-- дата последнего неуспешного платежа */
-- количество неуспешных платежей подряд */
-- статус последнего платежа по подписке */
-- маскированный номер карты Плательщика, вида 400000|0002 */
-- имя держателя карты */
-- срок действия карты, месяц */
-- срок действия, год */
CREATE TABLE cf_bank_subscriptions (
  IdOfSubscription BIGSERIAL,
  PaymentAmount BIGINT NOT NULL,
  ThresholdAmount BIGINT NOT NULL,
  MonthsCount INTEGER NOT NULL,
  ValidToDate BIGINT,
  ActivationDate BIGINT,
  DeactivationDate BIGINT,
  IsActive INTEGER,
  Status VARCHAR(255),
  PaymentId VARCHAR(32),
  IdOfClient BIGINT NOT NULL,
  San VARCHAR(11),
  PaySystem INTEGER NOT NULL,
  LastSuccessfulPaymentDate BIGINT,
  LastUnsuccessfulPaymentDate BIGINT,
  UnsuccessfulPaymentsCount INTEGER,
  LastPaymentStatus VARCHAR(255),
  MaskedCardNumber VARCHAR(11),
  CardHolder VARCHAR(255),
  ExpMonth INTEGER,
  ExpYear INTEGER,
  CONSTRAINT cf_bank_subscriptions_pk PRIMARY KEY (IdOfSubscription),
  CONSTRAINT cf_bank_subscriptions_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- Пакет обновлений 2.2.49
--!  Таблица отправленных запросов ИС ПП в МФР (на подключение подписки, ее отключение, списание средств).
-- уникальный идентификатор запроса ИС ПП в МФР */
-- уникальный идентификатор подписки на услугу в ИС ПП */
-- идентификатор системы, через ко/ую проходят платежи */
-- тип запроса (подключение, отключение, списание средств) */
-- URL запроса */
-- дата и время запроса */
-- флаг успешности запроса: 1 - на запрос пришел ответ, 0 - иначе */
-- статус ответа на запрос */
-- обрабатываемый клиент */
-- СНИЛС клиента */
-- описание ошибки в случае неудачного запроса */
CREATE TABLE cf_mfr_requests (
  IdOfRequest BIGSERIAL,
  IdOfSubscription BIGINT NOT NULL,
  PaySystem INTEGER NOT NULL,
  RequestType INTEGER NOT NULL,
  RequestURL VARCHAR(255) NOT NULL,
  RequestTime BIGINT NOT NULL,
  IsSuccess INTEGER NOT NULL,
  ResponseStatus VARCHAR(255),
  IdOfClient BIGINT NOT NULL,
  San VARCHAR(11),
  ErrorDescription VARCHAR(255),
  CONSTRAINT cf_mfr_requests_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_mfr_requests_pk PRIMARY KEY (IdOfRequest),
  CONSTRAINT cf_mfr_requests_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

CREATE TABLE CF_RegistrySms (
  IdOfRegistrySMS            BIGINT          NOT NULL,
  Version                 BIGINT          NOT NULL,
  SmsId                   CHAR(16)        NOT NULL,
  CONSTRAINT CF_RegistrySms_pk PRIMARY KEY (IdOfRegistrySMS)
);

--! Таблица ежемесячных платежей, осуществляемых по банковской подписке.
-- id платежа */
-- уникальный идентификатор подписки на услугу в ИС ПП, по ко/ой совершается платеж */
-- уникальный идентификатор запроса ИС ПП в МФР */
-- сумма платежа */
-- дата и время платежа */
-- клиент */
-- баланс клиента на момент запуска платежа */
-- установленное у подписки пороговое значение баланса на момент запуска */
-- результат платежа (осуществлен или нет) */
-- статус платежа */
-- код авторизации */
-- RRN транзакции */
CREATE TABLE cf_regular_payments (
  IdOfPayment BIGSERIAL,
  IdOfSubscription BIGINT NOT NULL,
  IdOfRequest BIGINT NOT NULL,
  PaymentAmount BIGINT NOT NULL,
  PaymentDate BIGINT,
  IdOfClient BIGINT NOT NULL,
  ClientBalance BIGINT NOT NULL,
  ThresholdAmount BIGINT NOT NULL,
  IsSuccess INTEGER NOT NULL,
  Status VARCHAR(255),
  AuthCode VARCHAR(6),
  RRN BIGINT,
  CONSTRAINT cf_regular_payments_pk PRIMARY KEY (IdOfPayment),
  CONSTRAINT cf_regular_payments_subscription_fk FOREIGN KEY (IdOfSubscription) REFERENCES cf_bank_subscriptions (IdOfSubscription),
  CONSTRAINT cf_regular_payments_request_fk FOREIGN KEY (IdOfRequest) REFERENCES cf_mfr_requests (IdOfRequest),
  CONSTRAINT cf_regular_payments_client_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- Пакет обновлений 2.2.51
-- Добавлены "Клиентские циклограммы" в обработку распределенных объектов
CREATE TABLE cf_clients_cycle_diagrams
(
  IdOfCycleDiagram BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  OrgOwner bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  GlobalVersionOnCreate BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer NOT NULL DEFAULT 0,

  DateActivationDiagram bigint NOT NULL,
  StateDiagram integer NOT NULL DEFAULT 0,
  Monday character varying(255),
  MondayPrice bigint NOT NULL,
  Tuesday character varying(255),
  TuesdayPrice bigint NOT NULL,
  Wednesday character varying(255),
  WednesdayPrice bigint NOT NULL,
  Thursday character varying(255),
  ThursdayPrice bigint NOT NULL,
  Friday character varying(255),
  FridayPrice bigint NOT NULL,
  Saturday character varying(255),
  SaturdayPrice bigint NOT NULL,
  Sunday character varying(255),
  SundayPrice bigint NOT NULL,
  CONSTRAINT cf_clients_cycle_diagrams_pk PRIMARY KEY (IdOfCycleDiagram),
  CONSTRAINT cf_clients_cycle_diagrams_clients_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);
-- Добавлены "Подписки на услугу абонементного питания" в обработку распределенных объектов
CREATE TABLE cf_subscriber_feeding
(
  IdOfServiceSubscriberFeeding BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  OrgOwner bigint,
  GUID character varying(36) NOT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  GlobalVersion bigint,
  GlobalVersionOnCreate BIGINT DEFAULT NULL,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  SendAll integer NOT NULL DEFAULT 0,

  DateActivateService bigint NOT NULL,
  LastDatePauseService bigint,
  DateDeactivateService bigint,
  ServiceState integer NOT NULL DEFAULT 0,
  wassuspended boolean NOT NULL DEFAULT false,
  CONSTRAINT cf_service_subscriber_feeding_pk PRIMARY KEY (IdOfServiceSubscriberFeeding),
  CONSTRAINT cf_service_subscriber_feeding_clients_fk FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- Регистрация переводов средств между субсчетами
CREATE TABLE CF_SubAccount_Transfers (
  IdOfSubAccountTransfer BigSerial NOT NULL,
  CreatedDate bigint NOT NULL,
  IdOfClientTransfer bigint NOT NULL,
  balanceBenefactor bigint NOT NULL,
  balanceBeneficiary bigint NOT NULL,
  Reason VARCHAR(256),
  IdOfTransactionOnBenefactor bigint NOT NULL,
  IdOfTransactionOnBeneficiary bigint NOT NULL,
  TransferSum bigint NOT NULL,
  CONSTRAINT cf_subaccount_transfer_pk PRIMARY KEY (IdOfSubAccountTransfer),
  CONSTRAINT cf_subaccount_transfer_c_ctr_fk FOREIGN KEY (IdOfClientTransfer) REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_subaccount_transfer_t_bctr_fk FOREIGN KEY (IdOfTransactionOnBenefactor) REFERENCES cf_transactions (IdOfTransaction),
  CONSTRAINT cf_subaccount_transfer_t_bcry_fk FOREIGN KEY (IdOfTransactionOnBeneficiary) REFERENCES cf_transactions (IdOfTransaction)
);

-- v53
CREATE TABLE cf_do_org_current_version (
  IdDOOrgCurrentVersion bigserial NOT NULL,
  ObjectId integer not null,
  IdOfOrg bigint not null,
  LastVersion bigint not null,
  CONSTRAINT cf_do_org_current_version_pk PRIMARY KEY (IdDOOrgCurrentVersion)
);

-- НЕ ЗАБЫВАТЬ ИЗМЕНЯТЬ ПРИ ВЫПУСКЕ НОВОЙ ВЕРСИИ
insert into CF_Schema_version_info(MajorVersionNum, MiddleVersionNum, MinorVersionNum, BuildVersionNum, UpdateTime, CommitText)
  VALUES(2, 2, 52, 131213, 0, '');

