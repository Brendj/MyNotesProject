-- Бибилиотека
-- Проводим очистку старых таблиц
--! Данные не валидны
DROP TABLE IF EXISTS cf_publications CASCADE;
DROP TABLE IF EXISTS cf_circuls CASCADE;
DROP TABLE IF EXISTS cf_publs CASCADE;
DROP TABLE IF EXISTS cf_issuable CASCADE;
DROP TABLE IF EXISTS cf_typesofaccompanyingdocuments CASCADE;
DROP TABLE IF EXISTS cf_sources CASCADE;
DROP TABLE IF EXISTS cf_accompanyingdocuments CASCADE;
DROP TABLE IF EXISTS cf_readers CASCADE;
DROP TABLE IF EXISTS cf_funds CASCADE;
DROP TABLE IF EXISTS cf_inventorybooks CASCADE;
DROP TABLE IF EXISTS cf_ksu1records CASCADE;
DROP TABLE IF EXISTS cf_retirementreasons CASCADE;
DROP TABLE IF EXISTS cf_ksu2records CASCADE;
DROP TABLE IF EXISTS cf_instances CASCADE;
DROP TABLE IF EXISTS cf_journals CASCADE;
DROP TABLE IF EXISTS cf_journalitems CASCADE;
DROP TABLE IF EXISTS cf_readerreg CASCADE;
DROP TABLE IF EXISTS cf_libvisits CASCADE;
DROP TABLE IF EXISTS cf_issuable CASCADE;
DROP TABLE IF EXISTS cf_circulations;

CREATE TABLE cf_publications
(
  IdOfPublication BigSerial NOT NULL,
  DataOfPublication bytea NOT NULL,
  Author character varying(255),
  Title character varying(512),
  ExtTitle character varying(255),
  PublicationDate character varying(15),
  Publisher character varying(255),
  Version bigint NOT NULL,
  ISBN character varying(255),
  ValidISBN boolean NOT NULL DEFAULT false,
  Hash integer NOT NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  CONSTRAINT cf_publications_pk PRIMARY KEY (idofpubl ),
  CONSTRAINT cf_publications_GUID_key UNIQUE (GUID )
);

CREATE TABLE cf_circulations
(
  IdOfCirculation BigSerial NOT NULL,
  IdOfClient bigint NOT NULL,
  IdOfPublication bigint NOT NULL,
  IdOfOrg bigint NOT NULL,
  IdOfIssuable bigint NOT NULL,
  IssuanceDate bigint NOT NULL DEFAULT 0,
  RefundDate bigint NOT NULL DEFAULT 0,
  RealRefundDate bigint,
  status integer NOT NULL DEFAULT 0,
  Version bigint NOT NULL DEFAULT 0,
  Quantity integer NOT NULL DEFAULT 0,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  CONSTRAINT cf_circulation_pk PRIMARY KEY (IdOfCirculation ),
  CONSTRAINT cf_circulation_client_fk FOREIGN KEY (IdOfClient)
      REFERENCES cf_clients (IdOfClient),
  CONSTRAINT cf_circulation_org_fk FOREIGN KEY (IdOfOrg)
      REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_circulation_publication_fk FOREIGN KEY (IdOfPublication)
      REFERENCES cf_publications (IdOfPublication),
  CONSTRAINT cf_circulation_GUID_key UNIQUE (GUID )
);

CREATE TABLE cf_issuable
(
  IdOfIssuable BigSerial NOT NULL,
  BarCode bigint,
  TypeOfIssuable character(1) NOT NULL DEFAULT 'i',
  IdOfPublication bigint NOT NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  CONSTRAINT cf_issuable_pk PRIMARY KEY (IdOfIssuable ),
  CONSTRAINT cf_issuable_publication_fkey FOREIGN KEY (IdOfPublication)
      REFERENCES cf_publications (IdOfPublication)
);

--тип сопр.документа
--TypeOfAccompanyingDocumentName - название (акт, накладная, т.п.)
CREATE TABLE cf_typesofaccompanyingdocuments (
  IdOfTypeOfAccompanyingDocument bigserial NOT NULL,
  TypeOfAccompanyingDocumentName varchar(45) default NULL;
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
  PRIMARY KEY  (IdOfTypeOfAccompanyingDocument)
);

--источник поступления книг
--SourceName - название источника (минобр, т.п.)
CREATE TABLE cf_sources (
  IdOfSource bigserial NOT NULL,
  SourceName varchar(127) default NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  PRIMARY KEY  (IdOfAccompanyingDocument)
);

--читатель
CREATE TABLE cf_readers (
  IdOfReader bigserial NOT NULL,
  IdOfClient bigint NOT NULL REFERENCES cf_clients(idofclient),
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  PRIMARY KEY  (IdOfReader)
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
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  PRIMARY KEY  (IdOfFund)
);

--инвентарная книга
--BookName - название
CREATE TABLE cf_inventorybooks (
  IdOfBook bigserial NOT NULL,
  BookName varchar(255) default NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  PRIMARY KEY  (IdOfKSU1Record)
);
--причина выбытия из фонда
--RetirementReasonName - название причины (в макулатуру, потерялось, съели)
CREATE TABLE cf_retirementreasons (
  IdOfRetirementReason bigserial NOT NULL,
  RetirementReasonName varchar(45) default NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  HashCode integer NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  IdOfPublication bigint NOT NULL REFERENCES cf_publications(idofpubl) ,
  InGroup bit(1) NOT NULL default '0',
  IdOfFund bigint default NULL REFERENCES cf_funds(IdOfFund),
  InvNumber varchar(10) default NULL,
  InvBook bigint default NULL REFERENCES cf_inventorybooks(IdOfBook),
  IdOfKSU1Record bigint default NULL REFERENCES cf_ksu1records(IdOfKSU1Record),
  IdOfKSU2Record bigint default NULL REFERENCES cf_ksu2records(IdOfKSU2Record),
  Cost int NOT NULL default '0',
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  IdOfPublication bigint NOT NULL REFERENCES cf_publications(idofpubl),
  MonthCount int NOT NULL default '0',
  Count int NOT NULL default '0',
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
  PRIMARY KEY  (IdOfJournalItem)
);

--регистрация читателя (перерег. после перехода в другой класс)
--IdOfClientGroupHist - ссылка на историю переходов по классам
CREATE TABLE cf_readerreg (
  IdOfReg bigserial NOT NULL,
  IdOfReader bigint NOT NULL REFERENCES cf_readers(IdOfReader),
  IdOfClientGroupHist bigint default NULL,
  Date date NOT NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  GUID character varying(36) NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CreatedDate bigint NOT NULL,
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

ALTER TABLE cf_staffs ADD COLUMN HashCode integer NOT NULL DEFAULT 0;
