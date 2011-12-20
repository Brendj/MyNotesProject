CONNECT 'jdbc:derby:ecafe_processor_db';

ALTER TABLE CF_MenuDetails ADD COLUMN LocalIdOfMenu BIGINT;
ALTER TABLE CF_MenuDetails ADD COLUMN MenuOrigin              INT               NOT NULL DEFAULT 0;
ALTER TABLE CF_MenuDetails ADD COLUMN AvailableNow            INT               NOT NULL DEFAULT 0;
ALTER TABLE CF_MenuDetails ADD COLUMN   MenuPath                VARCHAR(128)      NOT NULL DEFAULT '';
ALTER TABLE CF_OrderDetails ADD COLUMN   MenuType        INT           NOT NULL DEFAULT 0;
ALTER TABLE CF_OrderDetails ADD COLUMN  MenuOutput      VARCHAR(32)   NOT NULL DEFAULT '';
ALTER TABLE CF_OrderDetails ADD COLUMN   MenuOrigin      INT           NOT NULL DEFAULT 0;
ALTER TABLE CF_OrderDetails ADD COLUMN   MenuGroup       VARCHAR(32)   NOT NULL DEFAULT '';

ALTER TABLE CF_Generators ADD COLUMN IdOfAst BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN IdOfComplexInfo BIGINT NOT NULL DEFAULT 0;
ALTER TABLE CF_Generators ADD COLUMN IdOfComplexInfoDetail BIGINT NOT NULL DEFAULT 0;

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
  CONSTRAINT CF_ComplexInfoDetail_IdOfComplexInfo_fk FOREIGN KEY (IdOfComplexInfo) REFERENCES CF_ComplexInfo (IdOfComplexInfo) ON DELETE CASCADE,
  CONSTRAINT CF_ComplexInfoDetail_IdOfMenuDetail_fk FOREIGN KEY (IdOfMenuDetail) REFERENCES CF_MenuDetails (IdOfMenuDetail)
);

CREATE TABLE CF_Assortment (
  IdOfAst                 BIGINT            NOT NULL,  -- surrogate key
  IdOfOrg                 BIGINT            NOT NULL,
  BeginDate               BIGINT            NOT NULL,
  ShortName               VARCHAR(60)       NOT NULL,
  FullName                VARCHAR(60)       NOT NULL,
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