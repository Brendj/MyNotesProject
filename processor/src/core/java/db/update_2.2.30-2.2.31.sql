CREATE TABLE IF NOT EXISTS cf_clientscomplexdiscounts
  (
  idofclientcomplexdiscount bigserial NOT NULL,
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

ALTER TABLE cf_goods ADD COLUMN idofusercreate bigint;
ALTER TABLE cf_goods ADD COLUMN idofuseredit bigint;
ALTER TABLE cf_goods ADD COLUMN idofuserdelete bigint;

--! Выше скрипт выполнен на тестовом процесинге (78.46.34.200)

-- Связь таблицы пунктов заказа и товаров
ALTER TABLE cf_orderdetails ADD COLUMN guidofgoods character varying(36);

-- Жалобная книга на товары
CREATE TABLE cf_goods_complaintbook
(
  idofcomplaint bigserial NOT NULL,
  globalversion bigint,
  deletedstate boolean,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  idofclient bigint NOT NULL,
  idofgood bigint NOT NULL,
  comment character varying(512),
  CONSTRAINT cf_good_complaintbook_pk PRIMARY KEY (idofcomplaint),
  CONSTRAINT cf_goods_complaintbook_idofclient_fk FOREIGN KEY (idofclient)
      REFERENCES cf_clients (idofclient) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaintbook_idofgood_fk FOREIGN KEY (idofgood)
      REFERENCES cf_goods (idofgood) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaintbook
  OWNER TO postgres;

-- Причины жалобы на товар
CREATE TABLE cf_goods_complaintcauses
(
  idofcause bigserial NOT NULL,
  globalversion bigint,
  deletedstate boolean,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  idofcomplaint bigint NOT NULL,
  CONSTRAINT cf_goods_complaintcauses_pk PRIMARY KEY (idofcause),
  CONSTRAINT cf_goods_complaintcauses_idofcomplaint_fk FOREIGN KEY (idofcomplaint)
      REFERENCES cf_goods_complaintbook (idofcomplaint) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaintcauses
  OWNER TO postgres;