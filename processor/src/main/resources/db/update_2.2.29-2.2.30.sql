-- Выставление ограничений уникальности на GUID-ы распределенных объектов
ALTER TABLE cf_acts_of_inventarization ADD UNIQUE(guid);
ALTER TABLE cf_acts_of_waybill_difference ADD UNIQUE(guid);
ALTER TABLE cf_acts_of_waybill_difference_positions ADD UNIQUE(guid);
ALTER TABLE cf_goods_requests ADD UNIQUE(guid);
ALTER TABLE cf_goods_requests_positions ADD UNIQUE(guid);
ALTER TABLE cf_internal_disposing_documents ADD UNIQUE(guid);
ALTER TABLE cf_internal_disposing_document_positions ADD UNIQUE(guid);
ALTER TABLE cf_internal_incoming_documents ADD UNIQUE(guid);
ALTER TABLE cf_internal_incoming_document_positions ADD UNIQUE(guid);
ALTER TABLE cf_staffs ADD UNIQUE(guid);
ALTER TABLE cf_state_changes ADD UNIQUE(guid);
ALTER TABLE cf_waybills ADD UNIQUE(guid);
ALTER TABLE cf_waybills_positions ADD UNIQUE(guid);
ALTER TABLE cf_accompanyingdocuments ADD UNIQUE(guid);
ALTER TABLE cf_circulations ADD UNIQUE(guid);
ALTER TABLE cf_funds ADD UNIQUE(guid);
ALTER TABLE cf_instances ADD UNIQUE(guid);
ALTER TABLE cf_inventorybooks ADD UNIQUE(guid);
ALTER TABLE cf_issuable ADD UNIQUE(guid);
ALTER TABLE cf_journals ADD UNIQUE(guid);
ALTER TABLE cf_journalitems ADD UNIQUE(guid);
ALTER TABLE cf_ksu1records ADD UNIQUE(guid);
ALTER TABLE cf_ksu2records ADD UNIQUE(guid);
ALTER TABLE cf_libvisits ADD UNIQUE(guid);
ALTER TABLE cf_publications ADD UNIQUE(guid);
ALTER TABLE cf_retirementreasons ADD UNIQUE(guid);
ALTER TABLE cf_sources ADD UNIQUE(guid);
ALTER TABLE cf_typesofaccompanyingdocuments ADD UNIQUE(guid);
ALTER TABLE cf_goods ADD UNIQUE(guid);
ALTER TABLE cf_goods_groups ADD UNIQUE(guid);
ALTER TABLE cf_products ADD UNIQUE(guid);
ALTER TABLE cf_product_groups ADD UNIQUE(guid);
ALTER TABLE cf_technological_map ADD UNIQUE(guid);
ALTER TABLE cf_technological_map_groups ADD UNIQUE(guid);
ALTER TABLE cf_technological_map_products ADD UNIQUE(guid);
ALTER TABLE cf_trade_material_goods ADD UNIQUE(guid);
ALTER TABLE cf_ECafeSettings ADD UNIQUE(guid);

-- Добавление ссылки на таблицу товаров
ALTER TABLE cf_menudetails ADD COLUMN idofgood bigint;
ALTER TABLE cf_menudetails ADD CONSTRAINT cf_menudetail_idofgood_fk FOREIGN KEY (idofgood) REFERENCES cf_goods(idofgood);

-- Таблица родительских запретов на определенные блюда
CREATE TABLE cf_dish_prohibitions
(
  idofprohibition bigserial NOT NULL,
  guid character varying(36) NOT NULL,
  deletedstate boolean NOT NULL DEFAULT false,
  globalversion bigint,
  orgowner bigint,
  createddate bigint NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  sendall integer DEFAULT 0,
  idofclient bigint NOT NULL,
  idofproducts bigint,
  idofproductgroups bigint,
  idofgood bigint,
  idofgoodsgroup bigint,
  CONSTRAINT cf_dish_prohibitions_pk PRIMARY KEY (idofprohibition),
  CONSTRAINT cf_dish_prohibitions_idofclient_fk FOREIGN KEY (idofclient)
      REFERENCES cf_clients (idofclient) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_idofgood_fk FOREIGN KEY (idofgood)
      REFERENCES cf_goods (idofgood) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_idofgoodsgroup_fk FOREIGN KEY (idofgoodsgroup)
      REFERENCES cf_goods_groups (idofgoodsgroup) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_idofproductgroups_fk FOREIGN KEY (idofproductgroups)
      REFERENCES cf_product_groups (idofproductgroups) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_idofproducts_fk FOREIGN KEY (idofproducts)
      REFERENCES cf_products (idofproducts) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_guid_key UNIQUE (guid),
  CONSTRAINT cf_dish_prohibitions_check_onlyoneisnotnull CHECK ((
CASE
    WHEN idofproducts IS NOT NULL THEN 1
    ELSE 0
END +
CASE
    WHEN idofproductgroups IS NOT NULL THEN 1
    ELSE 0
END +
CASE
    WHEN idofgood IS NOT NULL THEN 1
    ELSE 0
END +
CASE
    WHEN idofgoodsgroup IS NOT NULL THEN 1
    ELSE 0
END) = 1)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_dish_prohibitions
  OWNER TO postgres;

-- Таблица исключений из запретов для товаров либо групп товаров
CREATE TABLE cf_dish_prohibition_exclusions
(
  idofexclusion bigserial NOT NULL,
  guid character varying(36) NOT NULL,
  deletedstate boolean NOT NULL DEFAULT false,
  globalversion bigint,
  orgowner bigint,
  createddate bigint NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  sendall integer DEFAULT 0,
  idofprohibition bigint NOT NULL,
  idofgood bigint,
  idofgoodsgroup bigint,
  CONSTRAINT cf_dish_prohibition_exclusions_pk PRIMARY KEY (idofexclusion),
  CONSTRAINT cf_dish_prohibition_exclusions_idofprohibition_fk FOREIGN KEY (idofprohibition)
      REFERENCES cf_dish_prohibitions (idofprohibition) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_dish_prohibitions_exclusions_idofgood_fk FOREIGN KEY (idofgood)
      REFERENCES cf_goods (idofgood) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_exclusions_idofgoodsgroup_fk FOREIGN KEY (idofgoodsgroup)
      REFERENCES cf_goods_groups (idofgoodsgroup) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibitions_exclusions_id_key UNIQUE (guid),
  CONSTRAINT cf_dish_prohibition_exclusions_check_oneisnotnull CHECK (COALESCE(idofgood, idofgoodsgroup) IS NOT NULL),
  CONSTRAINT cf_dish_prohibition_exclusions_check_oneisnull CHECK ((idofgood + idofgoodsgroup) IS NULL)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_dish_prohibition_exclusions
  OWNER TO postgres;

--! ФИНАЛИЗИРОВАН (Кадыров, 121204) НЕ МЕНЯТЬ