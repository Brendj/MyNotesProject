CREATE TABLE IF NOT EXISTS cf_clientscomplexdiscounts
  (
  createdate bigint NOT NULL,
  idofclient bigint NOT NULL,
  idofrule bigint NOT NULL,
  idofcategoryorg bigint NOT NULL,
  priority int NOT NULL,
  operationar int NOT NULL,
  idofcomplex int NOT NULL,

  CONSTRAINT cf_clientscomplexdiscounts_pk PRIMARY KEY (createdate, idofclient, idofrule, idofcategoryorg, priority, idofcomplex)
  );