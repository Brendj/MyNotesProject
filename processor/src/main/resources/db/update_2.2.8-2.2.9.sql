ALTER TABLE CF_Orgs ADD COLUMN OGRN                    character varying(32);
ALTER TABLE CF_Orgs ADD COLUMN INN                     character varying(32);
ALTER TABLE CF_Orgs ADD COLUMN  DefaultSupplier         BIGINT            NOT NULL default 1;
ALTER TABLE CF_Orgs ADD CONSTRAINT CF_Orgs_DefaultSupplier_fk FOREIGN KEY (DefaultSupplier) REFERENCES CF_Contragents (IdOfContragent);

ALTER TABLE CF_Cards ADD COLUMN ExternalId            VARCHAR(32);

ALTER TABLE CF_Clients ADD COLUMN CategoriesDiscounts     VARCHAR(60)       NOT NULL DEFAULT '';
ALTER TABLE CF_Clients ADD COLUMN San                     VARCHAR(11);
ALTER TABLE CF_Clients ADD COLUMN GuardSan                VARCHAR(64);


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

ALTER TABLE CF_Orders RENAME COLUMN Discount TO SocDiscount;
ALTER TABLE CF_Orders ADD COLUMN  IdOfPos         BIGINT;
ALTER TABLE CF_Orders ADD COLUMN   IdOfContragent  BIGINT        NOT NULL DEFAULT 1;
ALTER TABLE CF_Orders ADD COLUMN   TrdDiscount     BIGINT        NOT NULL DEFAULT 0;
ALTER TABLE CF_Orders ADD CONSTRAINT CF_Orders_IdOfPos_fk FOREIGN KEY (IdOfPos) REFERENCES CF_POS (IdOfPos);
ALTER TABLE CF_Orders ADD CONSTRAINT CF_Orders_IdOfContragent_fk FOREIGN KEY (IdOfContragent) REFERENCES CF_Contragents (IdOfContragent);

ALTER TABLE CF_OrderDetails ADD COLUMN SocDiscount BIGINT NOT NULL DEFAULT 0;

ALTER TABLE CF_Generators ADD COLUMN   IdOfPos                 BIGINT          NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN   IdOfSettlement          BIGINT          NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN   IdOfPosition            BIGINT          NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN   IdOfAddPayment          BIGINT          NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN   IdOfCategoryDiscount    BIGINT          NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN   IdOfRule		  BIGINT          NOT NULL DEFAULT 0;

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

-- Option "clean database" (0 - disabled, 1 - enabled)
INSERT INTO CF_Options(IdOfOption, OptionText)
  VALUES(4, 0);

-- Option "date for database cleaning (long)"
INSERT INTO CF_Options(IdOfOption, OptionText)
  VALUES(5, 0);

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

-- начисленная плата за обслуживание
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
  CategoriesDiscounts       Character     varying(64),
  OperationOr               integer       NOT NULL DEFAULT 0,
  CONSTRAINT CF_DiscountRules_pk PRIMARY KEY (IdOfRule)
);

 ALTER TABLE CF_EnterEvents ADD COLUMN IdOfVisitor             BIGINT;
 ALTER TABLE CF_EnterEvents ADD COLUMN VisitorFullName         varchar(110);
 ALTER TABLE CF_EnterEvents ADD COLUMN DocType                 integer;
 ALTER TABLE CF_EnterEvents ADD COLUMN DocSerialNum            varchar(45);
 ALTER TABLE CF_EnterEvents ADD COLUMN IssueDocDate            BIGINT;
 ALTER TABLE CF_EnterEvents ADD COLUMN VisitDateTime           BIGINT;