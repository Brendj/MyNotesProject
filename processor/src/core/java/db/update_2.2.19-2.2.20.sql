-- Поле с номером организации в таблице глобального объекта
ALTER TABLE cf_product_guide ADD COLUMN idoforg bigint;
-- Добавлена таблица версий распределенных объектов
CREATE TABLE cf_do_version
(
  idofdoobject bigserial,
  distributedobjectclassname character varying(64),
  currentversion bigint,
  CONSTRAINT cf_do_version_pk PRIMARY KEY (idofdoobject )
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
  CONSTRAINT cf_do_conflicts_pk PRIMARY KEY (idofdoconflict )
);

-- Удаление не нужных таблиц
DROP TABLE IF EXISTS cf_product_guide;
DROP TABLE IF EXISTS cf_products;
DROP TABLE IF EXISTS cf_product;
DROP TABLE IF EXISTS cf_technologicalmap;
DROP TABLE IF EXISTS cf_provider_configurations;

-- Добавлена таблица групп продуктов
CREATE TABLE cf_product_groups
(
   IdOfProductGroups BigSerial,
   NameOfGroup character varying(128) NOT NULL,
   GUID character varying(36) NOT NULL UNIQUE,
   GlobalVersion BIGINT DEFAULT NULL,
   DeletedState boolean NOT NULL DEFAULT false,
   OrgOwner BIGINT DEFAULT NULL,
   CreatedDate bigint NOT NULL,
   LastUpdate bigint,
   DeleteDate bigint,
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
  OkpCode character varying(32),
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
   LastUpdate bigint,
   DeleteDate bigint,
   CONSTRAINT cf_technological_map_groups_pk PRIMARY KEY (IdOfTechMapGroups )
);

-- Добавлена таблица (справочник) технологических карт
CREATE TABLE  cf_technological_map(
  IdOfTechnologicalMaps BigSerial,
  IdOfTechMapGroups bigint NOT NULL,
  NameOfTechnologicalMap character varying(128) NOT NULL,
  NumberOfTechnologicalMap BIGINT NOT NULL,
  TechnologyOfPreparation character varying(4096) NOT NULL,
  TermOfRealization character varying(128) DEFAULT NULL,
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
  NetWeight double precision NOT NULL DEFAULT 0,
  GrossWeight double precision NOT NULL DEFAULT 0,
  GUID character varying(36) NOT NULL UNIQUE,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpDate bigint,
  DeleteDate bigint,
  NameOfProduct character varying(256),
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
  name character varying(64) NOT NULL,
  CONSTRAINT pk_configuration_provider PRIMARY KEY (IdOfConfigurationProvider ),
  CONSTRAINT cf_provider_configurations_name_key UNIQUE (name )
);

--В организациях Добавлена ссылка на производственную конфигурацию
ALTER TABLE cf_orgs ADD COLUMN IdOfConfigurationProvider bigint;

CREATE TABLE cf_publs
(
  idofpubl bigint NOT NULL,
  data text,
  author character varying(255),
  title character varying(512),
  title2 character varying(255),
  publicationdate character varying(15),
  publisher character varying(255),
  version bigint NOT NULL,
  isbn character varying(15),
  hash character varying(32),
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

CREATE INDEX cf_publ_idx
  ON cf_publs
  USING btree
  (author COLLATE pg_catalog."default" , title COLLATE pg_catalog."default" , title2 COLLATE pg_catalog."default" , publicationdate COLLATE pg_catalog."default" , publisher COLLATE pg_catalog."default" );

CREATE TABLE cf_circuls
(
  idofcircul bigint NOT NULL,
  idofclient bigint NOT NULL,
  idofpubl bigint NOT NULL,
  idoforg bigint NOT NULL,
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
      REFERENCES cf_publs (idofpubl) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_circul_unq UNIQUE (idofclient , idofpubl , idoforg ),
  CONSTRAINT cf_circuls_guid_key UNIQUE (guid )
);



