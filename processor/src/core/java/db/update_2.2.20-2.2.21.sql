DROP TABLE cf_product_guide;
DROP TABLE cf_products;

CREATE TABLE cf_products
(
  IdOfProducts bigserial,
  Code character varying(16) NOT NULL,
  FullName character varying(1024),
  ProductName character varying(512),
  OkpCode character varying(32),
  GUID character varying(36) NOT NULL,
  GroupName character varying(512),
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
  CONSTRAINT cf_products_pk PRIMARY KEY (idOfProducts )
);
-- Таблица (справочник) технологических карт
CREATE TABLE  cf_technological_map(
  IdOfTechnologicalMaps bigserial,
  NameOfTechnologicalMap character varying(128) NOT NULL,
  NumberOfTechnologicalMap BIGINT NOT NULL,
  TechnologyOfPreparation character varying(4096) NOT NULL,
  TermOfRealization character varying(128) DEFAULT NULL,
  TempOfPreparation character varying(32) DEFAULT NULL,
  GroupName character varying(512),
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
  GUID character varying(36) NOT NULL,
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CONSTRAINT cf_technological_map_pk PRIMARY KEY (IdOfTechnologicalMaps )
);

CREATE TABLE cf_technological_map_products
(
  IdOfTechnoMapProducts bigserial NOT NULL,
  IdOfTechnologicalMaps bigint NOT NULL,
  IdOfProducts bigint NOT NULL,
  NetWeight double precision NOT NULL DEFAULT 0,
  GrossWeight double precision NOT NULL DEFAULT 0,
  GUID character varying(36) NOT NULL,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastupDate bigint,
  DeleteDate bigint,
  NameOfProduct character varying(256),
  CONSTRAINT cf_technological_map_products_pk PRIMARY KEY (IdOfTechnoMapProducts ),
  CONSTRAINT cf_technological_map_products_product FOREIGN KEY (IdOfProducts)
      REFERENCES cf_products (IdOfProducts),
  CONSTRAINT cf_technological_map_products_technological_map_fk FOREIGN KEY (IdOfTechnologicalMaps)
      REFERENCES cf_technological_map (IdOfTechnologicalMaps)
);

