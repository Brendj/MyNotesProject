
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

-- Бибилиотека
DROP TABLE cf_circulations;
DROP TABLE cf_issuable;

--тип сопр.документа
--TypeOfAccompanyingDocumentName - название (акт, накладная, т.п.)
CREATE TABLE cf_typesofaccompanyingdocuments (
  IdOfTypeOfAccompanyingDocument bigserial NOT NULL,
  TypeOfAccompanyingDocumentName varchar(45) default NULL;
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfTypeOfAccompanyingDocument)
);
--источник поступления книг
--SourceName - название источника (минобр, т.п.)
CREATE TABLE cf_sources (
  IdOfSource bigserial NOT NULL,
  SourceName varchar(127) default NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfSource)
);

--сопр.документы
--IdOfTypeOfAccompanyingDocument - тип(накладная,акт) - можно сразу текстом, вместо связи
--AccompanyingDocumentNumber - номер
--IdOfSource - источник поступления книг можно сразу текстом, вместо связи
CREATE TABLE cf_accompanyingdocuments (
  IdOfAccompanyingDocument bigserial NOT NULL,
  IdOfTypeOfAccompanyingDocument bigint NOT NULL REFERENCES cf_typesofaccompanyingdocument(IdOfTypeOfAccompanyingDocument),
  AccompanyingDocumentNumber varchar(32) NOT NULL,
  IdOfSource bigint default NULL REFERENCES cf_sources(IdOfSource),
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfAccompanyingDocument)
);

--читатель
CREATE TABLE cf_readers (
  IdOfReader bigserial NOT NULL,
  IdOfClient bigint NOT NULL REFERENCES cf_clients(idofclient),
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfReader)
);

--фонд
--FundName - название
CREATE TABLE cf_funds (
  IdOfFund bigserial NOT NULL,
  FundName varchar(127) default NULL,
 --! `InvBook` bigint(20) NOT NULL,--инвентарная книга фонда
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfFund)
);

--инвентарная книга
--BookName - название
CREATE TABLE cf_inventorybooks (
  IdOfBook bigserial NOT NULL,
  BookName varchar(255) default NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfBook)
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
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfKSU1Record)
);
--причина выбытия из фонда
--RetirementReasonName - название причины (в макулатуру, потерялось, съели)
CREATE TABLE cf_retirementreasons (
  IdOfRetirementReason bigserial NOT NULL,
  RetirementReasonName varchar(45) default NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfRetirementReason)
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
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfKSU2Record),
  UNIQUE (IdOfFund,RecordNumber)
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
  IdOfPublication bigint NOT NULL REFERENCES cf_publs(idofpubl) ,
  InGroup bit(1) NOT NULL default '0',
  IdOfFund bigint default NULL REFERENCES cf_funds(IdOfFund),
  InvNumber varchar(10) default NULL,
  InvBook bigint default NULL REFERENCES cf_inventorybooks(IdOfBook),
  IdOfKSU1Record bigint default NULL REFERENCES cf_ksu1records(IdOfKSU1Record),
  IdOfKSU2Record bigint default NULL REFERENCES cf_ksu2records(IdOfKSU2Record),
  Cost int NOT NULL default '0',
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfInstance),
  UNIQUE (InvBook,InvNumber)
);

--журналы(тип)
--IdOfFund - фонд
--IsNewspaper - газета или журнал (газета не заносится в фонд)
--IdOfPublication - ид библ.записи(автор,название,т.п.)
--MonthCount - кол-во номеров в месяц
--Count - кол-во подписок
CREATE TABLE cf_journals (
  IdOfJournal bigserial NOT NULL,
  IdOfFund bigint default NULL REFERENCES cf_funds(IdOfFund),
  IsNewspaper bit(1) NOT NULL default '0',
  IdOfPublication bigint NOT NULL REFERENCES cf_publs(idofpubl),
  MonthCount int NOT NULL default '0',
  Count int NOT NULL default '0',
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfJournal)
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
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfJournalItem)
);

--регистрация читателя (перерег. после перехода в другой класс)
--IdOfClientGroupHist - ссылка на историю переходов по классам
--IdOfClientGroupHist - ссылка на историю переходов по классам
CREATE TABLE cf_readerreg (
  IdOfReg bigserial NOT NULL,
  IdOfReader bigint NOT NULL REFERENCES cf_readers(IdOfReader),
  IdOfClientGroupHist bigint default NULL,
  Date date NOT NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfReg)
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
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfLibVisit)
);
--выдаваемая сущность
--BarCode - штрихкод
--Type - можешь просто смотреть, какое из след. двух полей не null
--IdOfInstance - ид книги
--IdOfJournalItem - ид журнала
--Issuance - текущая незакрытая выдача
--штрихкода уникальны
CREATE TABLE cf_issuable (
  IdOfIssuable bigserial NOT NULL,
  BarCode bigint default NULL,
  Type char(1) NOT NULL default 'i',
  IdOfInstance bigint default NULL REFERENCES cf_instances(IdOfInstance),
  IdOfJournalItem bigint default NULL REFERENCES cf_journalitems(IdOfJournalItem),
  Issuance bigint default NULL,
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfIssuable),
  UNIQUE  (BarCode)
);

--книговыдача
--IdOfParentCirculation - родительская выдача (древовидная структура для продления выдач)
--IdOfReader - читатель --можно выкинуть Readers, тогда связь будет сразу на client
--IdOfIssuable - книга/журнал
--IssuanceDate - дата выдачи
--RefundDate - дата возврата(срок)
--RealRefundDate - дата возврата
--Status - статус(выдано, возвращено, т.п.)
CREATE TABLE cf_circulations (
  IdOfCirculation bigserial NOT NULL,
  IdOfParentCirculation bigint default NULL REFERENCES cf_circulations(IdOfCirculation),
  IdOfReader bigint NOT NULL REFERENCES cf_readers(IdOfReader),
  IdOfIssuable bigint NOT NULL REFERENCES cf_issuable(IdOfIssuable),
  IssuanceDate timestamp NOT NULL,
  RefundDate timestamp NOT NULL,
  RealRefundDate timestamp NULL default NULL,
  Status int NOT NULL default '0',
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  PRIMARY KEY  (IdOfCirculation)
);

ALTER TABLE cf_accompanyingdocuments ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_circulations ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_funds ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_instances ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_inventorybooks ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_issuable ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_journalitems ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_journals ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_ksu1records ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_ksu2records ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_libvisits ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_readerreg ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_readers ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_retirementreasons ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_sources ADD COLUMN SendAll integer DEFAULT 0;
ALTER TABLE cf_typesofaccompanyingdocument ADD COLUMN SendAll integer DEFAULT 0;