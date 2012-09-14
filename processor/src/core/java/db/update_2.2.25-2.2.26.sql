-- Бибилиотека
DROP TABLE cf_circulations;
DROP TABLE cf_issuable;

ALTER TABLE cf_publs DROP COLUMN hash;
ALTER TABLE cf_publs DROP COLUMN isbn;
ALTER TABLE cf_publs DROP COLUMN data;
ALTER TABLE cf_publs ADD COLUMN isbn character varying(255);
ALTER TABLE cf_publs ADD COLUMN hash integer NOT NULL;
ALTER TABLE cf_publs ADD COLUMN data bytea NOT NULL;

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