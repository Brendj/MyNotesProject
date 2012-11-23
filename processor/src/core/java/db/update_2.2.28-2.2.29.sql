CREATE TABLE IF NOT EXISTS cf_projectstate_data
  (
  Period bigint NOT NULL,
  Type int NOT NULL,
  StringKey character varying(128),
  StringValue character varying(128),
  generationdate bigint NOT NULL,
  CONSTRAINT cf_projectstate_data_pk PRIMARY KEY (Period, Type, StringKey)
  );

-- Добавление колонки currentprice к таблице cf_complexinfo
ALTER TABLE cf_complexinfo ADD COLUMN currentprice bigint;

-- Добавление колонки count к таблице cf_complexinfodetail
ALTER TABLE cf_complexinfodetail ADD COLUMN count integer;

-- Добавлена связка к документу
ALTER TABLE cf_internal_disposing_document_positions ADD COLUMN IdOfGood bigint NOT NULL;
ALTER TABLE cf_internal_disposing_document_positions ADD CONSTRAINT cf_internal_disposing_document_positions_fk_good FOREIGN KEY (IdOfGood) REFERENCES cf_goods (IdOfGood);

-- Добавление ссылки на таблицу товаров
ALTER TABLE cf_menudetails ADD COLUMN idofgood bigint;
ALTER TABLE cf_menudetails ADD CONSTRAINT cf_menudetail_idofgood_fk FOREIGN KEY (idofgood) REFERENCES cf_goods(idofgood);

-- Выставление ссылок на продукты и технокарты таблице товаров
ALTER TABLE cf_goods ADD CONSTRAINT cf_goods_idoftechnologicalmaps_fk FOREIGN KEY (idoftechnologicalmaps) REFERENCES cf_technological_map (idoftechnologicalmaps);
ALTER TABLE cf_goods ADD CONSTRAINT cf_goods_idofproducts_fk FOREIGN KEY (idofproducts) REFERENCES cf_products (idofproducts);

-- Таблица родительских запретов на определенные блюда
CREATE TABLE cf_dish_prohibitions
(
  idofprohibition bigserial NOT NULL,
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
  idofprohibition bigint NOT NULL,
  idofgood bigint,
  idofgoodsgroup bigint,
  CONSTRAINT cf_dish_prohibition_exclusions_pk PRIMARY KEY (idofexclusion),
  CONSTRAINT cf_dish_prohibition_exclusions_idofprohibition_fk FOREIGN KEY (idofprohibition)
      REFERENCES cf_dish_prohibitions (idofprohibition) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_dish_prohibition_exclusions_check_oneisnotnull CHECK (COALESCE(idofgood, idofgoodsgroup) IS NOT NULL),
  CONSTRAINT cf_dish_prohibition_exclusions_check_oneisnull CHECK ((idofgood + idofgoodsgroup) IS NULL)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE cf_dish_prohibition_exclusions
  OWNER TO postgres;