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
ALTER TABLE cf_orderdetails ADD COLUMN idofgood bigint;
ALTER TABLE cf_orderdetails ADD CONSTRAINT cf_orderdetails_idofgood_fk FOREIGN KEY (idofgood) REFERENCES cf_goods(idofgood);

-- Таблиц жалоб на товары из совершенных заказов
CREATE TABLE cf_goods_complaintbook
(
  idofcomplaint bigserial NOT NULL,
  idofclient bigint NOT NULL,
  globalversion bigint,
  guid character varying(36) NOT NULL,
  deletedstate boolean DEFAULT FALSE,
  deletedate bigint,
  lastupdate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  CONSTRAINT cf_good_complaintbook_pk PRIMARY KEY (idofcomplaint),
  CONSTRAINT cf_goods_complaintbook_idofclient_fk FOREIGN KEY (idofclient)
      REFERENCES cf_clients (idofclient) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaintbook
  OWNER TO postgres;

-- Таблица итераций подачи жалоб
CREATE TABLE cf_goods_complaint_iterations
(
  idofiteration bigserial NOT NULL,
  idofcomplaint bigint NOT NULL,
  iterationnumber integer NOT NULL DEFAULT 0,
  iterationstatus integer NOT NULL DEFAULT 0,
  problemdescription character varying(512),
  conclusion character varying(512),
  globalversion bigint,
  deletedstate boolean DEFAULT FALSE,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  CONSTRAINT cf_goods_complaint_iterations_pk PRIMARY KEY (idofiteration),
  CONSTRAINT cf_goods_complaint_iterations_idofcomplaint_fk FOREIGN KEY (idofcomplaint)
      REFERENCES cf_goods_complaintbook (idofcomplaint) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaint_iterations
  OWNER TO postgres;

-- Таблица списков причин подачи жалобы
CREATE TABLE cf_goods_complaint_causes
(
  idofcause bigserial NOT NULL,
  idofiteration bigint NOT NULL,
  cause integer NOT NULL,
  globalversion bigint,
  deletedstate boolean DEFAULT FALSE,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  CONSTRAINT cf_goods_complaint_causes_pk PRIMARY KEY (idofcause),
  CONSTRAINT cf_goods_complaint_causes_idofiteration_fk FOREIGN KEY (idofiteration)
      REFERENCES cf_goods_complaint_iterations (idofiteration) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaint_causes
  OWNER TO postgres;

-- Таблица деталей заказов, к товарам из состава которых у клиента возникли претензии
CREATE TABLE cf_goods_complaint_orders
(
  idoforder bigserial NOT NULL,
  idoforderdetail bigint NOT NULL,
  globalversion bigint,
  deletedstate boolean DEFAULT FALSE,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint NOT NULL,
  CONSTRAINT cf_goods_complaint_orders_pk PRIMARY KEY (idoforder),
  CONSTRAINT cf_goods_complaint_orders_orgowner_fk FOREIGN KEY (orgowner)
      REFERENCES cf_orgs (idoforg) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_orders_idoforderdetail_fk FOREIGN KEY (orgowner, idoforderdetail)
      REFERENCES cf_orderdetails (idoforg, idoforderdetail) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaint_orders
  OWNER TO postgres;

--! Предыдущая версия книги жалоб

-- -- Жалобная книга на товары
-- CREATE TABLE cf_goods_complaintbook
-- (
--   idofcomplaint bigserial NOT NULL,
--   globalversion bigint,
--   deletedstate boolean,
--   guid character varying(36) NOT NULL,
--   lastupdate bigint,
--   deletedate bigint,
--   createddate bigint NOT NULL,
--   sendall integer DEFAULT 4,
--   orgowner bigint,
--   idofclient bigint NOT NULL,
--   idofgood bigint NOT NULL,
--   description character varying(512),
--   CONSTRAINT cf_good_complaintbook_pk PRIMARY KEY (idofcomplaint),
--   CONSTRAINT cf_goods_complaintbook_idofclient_fk FOREIGN KEY (idofclient)
--       REFERENCES cf_clients (idofclient) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION,
--   CONSTRAINT cf_goods_complaintbook_idofgood_fk FOREIGN KEY (idofgood)
--       REFERENCES cf_goods (idofgood) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION
-- )
-- WITH (
--   OIDS=FALSE
-- );
-- ALTER TABLE cf_goods_complaintbook
--   OWNER TO postgres;
--
-- -- Причины жалобы на товар
-- CREATE TABLE cf_goods_complaintcauses
-- (
--   idofcause bigserial NOT NULL,
--   globalversion bigint,
--   deletedstate boolean,
--   guid character varying(36) NOT NULL,
--   lastupdate bigint,
--   deletedate bigint,
--   createddate bigint NOT NULL,
--   sendall integer DEFAULT 4,
--   orgowner bigint,
--   idofcomplaint bigint NOT NULL,
--   cause integer NOT NULL,
--   CONSTRAINT cf_goods_complaintcauses_pk PRIMARY KEY (idofcause),
--   CONSTRAINT cf_goods_complaintcauses_idofcomplaint_fk FOREIGN KEY (idofcomplaint)
--       REFERENCES cf_goods_complaintbook (idofcomplaint) MATCH SIMPLE
--       ON UPDATE NO ACTION ON DELETE NO ACTION
-- )
-- WITH (
--   OIDS=FALSE
-- );
-- ALTER TABLE cf_goods_complaintcauses
--   OWNER TO postgres;