ALTER TABLE CF_GENERATORS ADD COLUMN 
  idofproduct bigint NOT NULL DEFAULT 0;
ALTER TABLE CF_GENERATORS ADD COLUMN 
  idofproducts bigint NOT NULL DEFAULT 0;
ALTER TABLE CF_GENERATORS ADD COLUMN 
  idoftechnologicalmap bigint NOT NULL DEFAULT 0;

CREATE TABLE cf_product
(
  idofproduct bigint NOT NULL,
  nameofproduct character varying(256),
  grossmass real,
  netmass real,
  CONSTRAINT cf_product_pkey PRIMARY KEY (idofproduct)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_product OWNER TO postgres;
  
CREATE TABLE cf_products
(
  idOfProducts bigint NOT NULL,
  idOfProduct1 bigint,
  idOfProduct2 bigint,
  idOfProduct3 bigint,
  idoftechnologicalmap bigint NOT NULL,
  "index" integer NOT NULL,
  CONSTRAINT pk_products PRIMARY KEY (idofproducts),
  CONSTRAINT fk_p1 FOREIGN KEY (idofproduct1)
      REFERENCES cf_product (idofproduct) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_p2 FOREIGN KEY (idofproduct2)
      REFERENCES cf_product (idofproduct) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT fk_p3 FOREIGN KEY (idofproduct3)
      REFERENCES cf_product (idofproduct) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_products OWNER TO postgres;  
  
CREATE TABLE cf_technologicalmap
(
  idoftechnologicalmap bigint NOT NULL,  
  proteins real,
  carbohydrates real,
  fats real,
  ca real,
  mg real,
  p real,
  fe real,
  energyvalue real,
  a real,
  b1 real,
  b2 real,
  pp real,
  c real,
  e real,
  technologyofpreparation character varying(4096),
  termofrealization integer,
  nameoftechnologicalmap character varying(128),
  CONSTRAINT pk_tm PRIMARY KEY (idoftechnologicalmap)
)
WITH (
  OIDS=FALSE
);
--! ФИНАЛИЗИРОВАН (Кадыров, 060812) НЕ МЕНЯТЬ