CREATE TABLE cf_clientscomplexdiscounts
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

-- версия администратора/кассира
ALTER TABLE CF_SyncHistory ADD COLUMN clientversion character varying(16);
ALTER TABLE CF_Orgs ADD COLUMN clientversion character varying(16);
-- ip адресс клинта
ALTER TABLE CF_SyncHistory ADD COLUMN remoteaddress character varying(20);
ALTER TABLE CF_Orgs ADD COLUMN remoteaddress character varying(20);

-- разрешает клиенту подтверждать оплату групового питания, он будет доступен для клиентов входящих в группы: пед состав, администрация
ALTER TABLE cf_clients ADD COLUMN canconfirmgrouppayment integer NOT NULL Default 0;
-- Тип категории скидок
ALTER TABLE cf_categorydiscounts ADD COLUMN categorytype integer Default 0;

-- Таблица вопросов анкеты
CREATE TABLE cf_qa_questionaries
(
  idofquestionary bigserial NOT NULL,
  questionname character varying(90) NOT NULL,
  question character varying(90) NOT NULL,
  description character varying(255),
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
  answer character varying(90) NOT NULL,
  description character varying(255),
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
      REFERENCES cf_clients (idofclient),
  CONSTRAINT cf_good_complaintbook_idofgood_fk FOREIGN KEY (idofgood)
      REFERENCES cf_goods (idofgood),
  CONSTRAINT cf_goods_complaintbook_idofclient_idofgood_key UNIQUE (idofclient, idofgood)
);

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
      REFERENCES cf_goods_complaintbook (idofcomplaint),
  CONSTRAINT cf_goods_complaint_iterations_idofcomplaint_iterationnumber_key UNIQUE (idofcomplaint, iterationnumber)
);

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
      REFERENCES cf_goods_complaint_iterations (idofiteration),
  CONSTRAINT cf_goods_complaint_causes_idofiteration_cause_key UNIQUE (idofiteration, cause)
);

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
      REFERENCES cf_goods_complaint_iterations (idofiteration),
  CONSTRAINT cf_goods_complaint_orders_idoforderorg_fk FOREIGN KEY (idoforderorg)
      REFERENCES cf_orgs (idoforg),
  CONSTRAINT cf_goods_complaint_orders_idoforderdetail_fk FOREIGN KEY (idoforderorg, idoforderdetail)
      REFERENCES cf_orderdetails (idoforg, idoforderdetail),
  CONSTRAINT cf_goods_complaint_orders_idofiteration_idoforderorg_idoforderd UNIQUE (idofiteration, idoforderorg, idoforderdetail)
);

-- Возможные причины подачи жалоб
CREATE TABLE cf_possible_complaint_causes
(
  causenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_causes_pk PRIMARY KEY (causenumber)
);

-- Названия статусов итераций жалоб
CREATE TABLE cf_possible_complaint_iteration_states
(
  statenumber bigint NOT NULL,
  description character varying NOT NULL,
  CONSTRAINT cf_possible_complaint_iteration_states_pk PRIMARY KEY (statenumber )
);

-- Добавление атрибута "Версия на момент создания" для всех распределенных объектов
ALTER TABLE cf_acts_of_inventarization ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_acts_of_waybill_difference ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_acts_of_waybill_difference_positions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_requests ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_requests_positions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_internal_disposing_documents ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_internal_disposing_document_positions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_internal_incoming_documents ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_internal_incoming_document_positions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_staffs ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_state_changes ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_waybills ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_waybills_positions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_accompanyingdocuments ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_circulations ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_funds ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_instances ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_inventorybooks ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_issuable ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_journals ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_journalitems ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_ksu1records ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_ksu2records ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_libvisits ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_publications ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_retirementreasons ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_sources ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_typesofaccompanyingdocuments ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_complaintbook ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_complaint_causes ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_complaint_iterations ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_complaint_orders ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_goods_groups ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_products ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_product_groups ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_dish_prohibitions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_dish_prohibition_exclusions ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_technological_map ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_technological_map_groups ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_technological_map_products ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_trade_material_goods ADD COLUMN globalversiononcreate bigint;
ALTER TABLE cf_ECafeSettings ADD COLUMN globalversiononcreate bigint;

--! ФИНАЛИЗИРОВАН (Кадыров, 130110) НЕ МЕНЯТЬ