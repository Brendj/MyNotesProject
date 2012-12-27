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

-- разрешает клиенту подтверждать оплату групового питания, он будет доступен для клиентов входящих в группы: пед состав, администрация
ALTER TABLE cf_clients ADD COLUMN canconfirmgrouppayment integer NOT NULL Default 0;
-- Тип категории скидок
ALTER TABLE cf_categorydiscounts ADD COLUMN categorytype integer;

-- Таблица вопросов анкеты
CREATE TABLE cf_qa_questionaries
(
  idofquestionary bigserial NOT NULL,
  question character varying(255) NOT NULL,
  status integer NOT NULL DEFAULT 0,
  type integer DEFAULT 0,
  createddate bigint NOT NULL,
  updateddate bigint,
  CONSTRAINT cf_qa_questionaries_pk PRIMARY KEY (idofquestionary )
);

-- Таблица вариантов ответа
CREATE TABLE cf_qa_answers
(
  idofanswer bigserial NOT NULL,
  idofquestionary bigint NOT NULL,
  answer character varying(255) NOT NULL,
  weight integer NOT NULL DEFAULT 1,
  createddate bigint NOT NULL,
  updateddate bigint,
  CONSTRAINT cf_qa_answers_pk PRIMARY KEY (idofanswer ),
  CONSTRAINT cf_qa_answers_question_fk FOREIGN KEY (idofquestionary) REFERENCES cf_qa_questionaries (idofquestionary)
);

-- Таблица отношений анкет и организаций
CREATE TABLE cf_qa_organization_questionary
(
  idoforgquestionary bigserial NOT NULL,
  idofquestionary bigint NOT NULL,
  idoforg bigint NOT NULL,
  CONSTRAINT cf_qa_organization_questionary_pk PRIMARY KEY (idoforgquestionary ),
  CONSTRAINT cf_qa_organization_questionary_org_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_qa_organization_questionary_questionary_fk FOREIGN KEY (idofquestionary) REFERENCES cf_qa_questionaries (idofquestionary)
);

-- Таблица ответов клиента
CREATE TABLE cf_qa_clientanswerbyquestionary
(
  idofclientanswerbyquestionary bigserial NOT NULL,
  idofclient bigint NOT NULL,
  idofanswer bigint NOT NULL,
  createddate bigint NOT NULL,
  updateddate bigint,
  CONSTRAINT cf_qa_clientanswerbyquestionary_pk PRIMARY KEY (idofclientanswerbyquestionary ),
  CONSTRAINT cf_qa_clientanswerbyquestionary_answer FOREIGN KEY (idofanswer) REFERENCES cf_qa_answers (idofanswer),
  CONSTRAINT cf_qa_clientanswerbyquestionary_client FOREIGN KEY (idofclient) REFERENCES cf_clients (idofclient)
);

-- Таблица промежуточных результатов ответа по организациям
CREATE TABLE cf_qa_questionaryresultbyorg
(
  idofquestionaryresultbyorg bigserial NOT NULL,
  idoforg bigint NOT NULL,
  idofquestionary bigint NOT NULL,
  idofanswer bigint NOT NULL,
  count bigint NOT NULL DEFAULT 0,
  updateddate bigint,
  CONSTRAINT cf_qa_questionaryresultbyorg_pk PRIMARY KEY (idofquestionaryresultbyorg ),
  CONSTRAINT cf_qa_questionaryresultbyorg_answer FOREIGN KEY (idofanswer) REFERENCES cf_qa_answers (idofanswer),
  CONSTRAINT cf_qa_questionaryresultbyorg_org FOREIGN KEY (idoforg) REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_qa_questionaryresultbyorg_questionary FOREIGN KEY (idofquestionary) REFERENCES cf_qa_questionaries (idofquestionary)
);

-- Связь таблицы пунктов заказа и товаров
ALTER TABLE cf_orderdetails ADD COLUMN idofgood bigint;
ALTER TABLE cf_orderdetails ADD CONSTRAINT cf_orderdetails_idofgood_fk FOREIGN KEY (idofgood) REFERENCES cf_goods(idofgood);

-- Таблица жалоб на товары из совершенных заказов
CREATE TABLE cf_goods_complaintbook
(
  idofcomplaint bigserial NOT NULL,
  idofclient bigint NOT NULL,
  idofgood bigint NOT NULL,
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
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_good_complaintbook_idofgood_fk FOREIGN KEY (idofgood)
      REFERENCES cf_goods (idofgood) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaintbook_idofclient_idofgood_key UNIQUE (idofclient, idofgood)
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
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_iterations_idofcomplaint_iterationnumber_key UNIQUE (idofcomplaint, iterationnumber)
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
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_causes_idofiteration_cause_key UNIQUE (idofiteration, cause)
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
  idofiteration bigint NOT NULL,
  idoforderorg bigint NOT NULL,
  idoforderdetail bigint NOT NULL,
  globalversion bigint,
  deletedstate boolean DEFAULT FALSE,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 4,
  orgowner bigint,
  CONSTRAINT cf_goods_complaint_orders_pk PRIMARY KEY (idoforder),
  CONSTRAINT cf_goods_complaint_orders_idofiteration_fk FOREIGN KEY (idofiteration)
      REFERENCES cf_goods_complaint_iterations (idofiteration) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_orders_idoforderorg_fk FOREIGN KEY (idoforderorg)
      REFERENCES cf_orgs (idoforg) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_orders_idoforderdetail_fk FOREIGN KEY (idoforderorg, idoforderdetail)
      REFERENCES cf_orderdetails (idoforg, idoforderdetail) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_goods_complaint_orders_idofiteration_idoforderorg_idoforderd UNIQUE (idofiteration, idoforderorg, idoforderdetail)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_goods_complaint_orders
  OWNER TO postgres;

-- Возможные причины подачи жалоб
CREATE TABLE cf_possible_complaint_causes
(
  causenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_causes_pk PRIMARY KEY (causenumber)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_possible_complaint_causes
  OWNER TO postgres;
INSERT INTO cf_possible_complaint_causes VALUES ('0', 'Неприятный вкус');
INSERT INTO cf_possible_complaint_causes VALUES ('1', 'Неприятный запах');
INSERT INTO cf_possible_complaint_causes VALUES ('2', 'Недомогание после употребления');
INSERT INTO cf_possible_complaint_causes VALUES ('3', 'Подозрение на некачественные продукты в составе блюда');
INSERT INTO cf_possible_complaint_causes VALUES ('4', 'Просроченность');
INSERT INTO cf_possible_complaint_causes VALUES ('5', 'Завышенная цена');

-- Названия статусов итераций жалоб
CREATE TABLE cf_possible_complaint_iteration_states
(
  statenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_iteration_states_pk PRIMARY KEY (statenumber )
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_possible_complaint_iteration_states
  OWNER TO postgres;

INSERT INTO cf_possible_complaint_iteration_states VALUES ('0', 'Создание');
INSERT INTO cf_possible_complaint_iteration_states VALUES ('1', 'Рассмотрение');
INSERT INTO cf_possible_complaint_iteration_states VALUES ('2', 'Расследование');
INSERT INTO cf_possible_complaint_iteration_states VALUES ('3', 'Заключение');
