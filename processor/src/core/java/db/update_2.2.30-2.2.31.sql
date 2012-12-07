CREATE TABLE IF NOT EXISTS cf_clientscomplexdiscounts
  (
  idofclientcomplexdiscount bigint NOT NULL,
  createdate bigint NOT NULL,
  idofclient bigint NOT NULL,
  idofrule bigint NOT NULL,
  idofcategoryorg bigint NOT NULL,
  priority int NOT NULL,
  operationar int NOT NULL,
  idofcomplex int NOT NULL,

  CONSTRAINT cf_clientscomplexdiscounts_pk PRIMARY KEY (idofclientcomplexdiscount),
  CONSTRAINT cf_clientscomplexdiscounts_u_key UNIQUE (idofclient, idofrule, idofcategoryorg, priority, idofcomplex)
  );

ALTER TABLE cf_complexinfo ADD COLUMN idofgood bigint;
ALTER TABLE cf_complexinfo ADD CONSTRAINT cf_complexinfo_idofgood_fk FOREIGN KEY (idofgood) REFERENCES cf_goods (idofgood);