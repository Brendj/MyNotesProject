
ALTER TABLE cf_products ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_technological_map ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_technological_map_products ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_product_groups ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_technological_map_groups ADD COLUMN SendAll integer DEFAULT 0;


-- добавляем условие уникальности для имен классов
ALTER TABLE cf_do_versions ADD CONSTRAINT cf_do_versions_distributedobjectclassname_unique UNIQUE (distributedobjectclassname);

-- колонка результирующей версии после обновления объекта
ALTER TABLE cf_do_conflicts ADD COLUMN gversion_result bigint;

-- Плотность – плотность продукта (для жидкостей, для вычисления массы по объему и наоборот)
ALTER TABLE cf_products ADD COLUMN Density  FLOAT DEFAULT NULL;

-- Срок годности – срок годности (минут)
ALTER TABLE cf_technological_map DROP COLUMN termOfRealization;
ALTER TABLE cf_technological_map ADD COLUMN LifeTime integer NOT NULL DEFAULT 0;
ALTER TABLE cf_technological_map ALTER COLUMN NumberOfTechnologicalMap TYPE character varying(128);

-- № группы замены – одинаковый для альтернативных продуктов
ALTER TABLE cf_technological_map_products DROP COLUMN NameOfProduct;
ALTER TABLE cf_technological_map_products ADD COLUMN NumberGroupReplace integer;

-- количество в граммах (нетто) на 100 гр блюда
ALTER TABLE cf_technological_map_products ALTER COLUMN NetWeight TYPE integer;
ALTER TABLE cf_technological_map_products ALTER COLUMN GrossWeight TYPE integer;


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

--! ФИНАЛИЗИРОВАН (Кадыров, 120831) НЕ МЕНЯТЬ