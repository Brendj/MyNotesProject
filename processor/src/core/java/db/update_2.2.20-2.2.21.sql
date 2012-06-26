DROP TABLE cf_product_guide;
DROP TABLE cf_products;

CREATE TABLE cf_products
(
  IdOfProducts bigserial,
  Code character varying(16) NOT NULL,
  FullName character varying(1024),
  ProductName character varying(512),
  OkpCode character varying(32),
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
  IdOfTechnoMap bigserial,
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
  GlobalVersion BIGINT DEFAULT NULL,
  OrgOwner BIGINT DEFAULT NULL,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CONSTRAINT cf_technological_map_pk PRIMARY KEY (IdOfTechnoMap )
);

CREATE TABLE cf_technological_map_products
(
  IdOfTechnoMapProducts bigserial NOT NULL,
  IdOfTechnoMap bigint NOT NULL,
  IdOfProducts bigint NOT NULL,
  NameOfProduct character varying(512) not null,
  NetWeight double precision NOT NULL DEFAULT 0,
  GrossWeight double precision NOT NULL DEFAULT 0,
  GlobalVersion bigint,
  OrgOwner bigint,
  DeletedState boolean NOT NULL DEFAULT false,
  CreatedDate bigint NOT NULL,
  LastUpdate bigint,
  DeleteDate bigint,
  CONSTRAINT cf_technological_map_products_pk PRIMARY KEY (IdOfTechnoMapProducts ),
  CONSTRAINT cf_technological_map_products_technological_map_fk FOREIGN KEY (IdOfTechnoMap) REFERENCES cf_technological_map (IdOfTechnoMap),
  CONSTRAINT cf_technological_map_products_products_fk FOREIGN KEY (idOfProducts) REFERENCES cf_products (idOfProducts)
);
