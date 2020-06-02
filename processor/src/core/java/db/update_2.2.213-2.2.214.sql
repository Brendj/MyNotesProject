--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 214

-- Создание таблицы для каталога Education Level
CREATE TABLE cf_kf_ct_EducationLevel
(
    global_id BIGINT NOT NULL
        CONSTRAINT cf_kf_ct_EducationLevel_pkey
            PRIMARY KEY,
    createdate TIMESTAMP NOT NULL,
    lastupdate TIMESTAMP NOT NULL,
    is_deleted INTEGER DEFAULT 0,
    system_object_id BIGINT,
    title varchar(255),
    id INTEGER,
    shortname varchar(36)
);

--Расширение таблиц предзаказа новыми колонками
ALTER TABLE cf_preorder_menudetail
    ADD COLUMN mobile character varying(32),
    ADD COLUMN mobileGroupOnCreate integer;

alter table cf_preorder_complex
    add column mobileGroupOnCreate integer;

alter table cf_regular_preorders
    add column mobileGroupOnCreate integer;

--Новые значения справочника степени родства представителей
update cf_client_guardian
set version = (select max(version) + 1 from cf_client_guardian),
    relation = 3
where relation in (4, 5);

update cf_client_guardian
set version = (select max(version) + 1 from cf_client_guardian),
    relation = 2
where relation in (6, 7);

--475 Добавление колонки, которая показывает, отправлено ли клиенту уведомление об окончании данной льготы
ALTER TABLE cf_client_dtiszn_discount_info ADD sendnotification bool NULL;

ALTER TABLE cf_kf_organization_registry
    ADD COLUMN subordination_value VARCHAR(512),
    ADD COLUMN founder VARCHAR(512),
    DROP COLUMN subordination_id;

ALTER TABLE cf_orgs
    ADD COLUMN subordination VARCHAR(512),
    ADD COLUMN founder VARCHAR(512),
    ADD COLUMN organizationIdFromNSI BIGINT;

WITH ids AS (
    SELECT global_id AS organizationIdFromNSI,
           eo_id     AS ekisID
    FROM cf_kf_organization_registry
)
UPDATE cf_orgs AS o
SET organizationIdFromNSI = ids.organizationIdFromNSI
FROM ids
WHERE o.ekisid = ids.ekisID;

ALTER TABLE cf_orgregistrychange
    ADD COLUMN subordination VARCHAR(512),
    ADD COLUMN founder VARCHAR(512),
    ADD COLUMN subordinationFrom VARCHAR(512),
    ADD COLUMN founderFrom VARCHAR(512);

ALTER TABLE cf_orgregistrychange_item
    ADD COLUMN subordination VARCHAR(512),
    ADD COLUMN founder VARCHAR(512),
    ADD COLUMN subordinationFrom VARCHAR(512),
    ADD COLUMN founderFrom VARCHAR(512);

-- СОЗДАНИЕ ТАБЛИЦ ВЕБ ТЕХНОЛОГА

CREATE TABLE cf_wt_agegroup_items (
    idofagegroupitem bigserial NOT NULL,
    description character varying(255) NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT cf_wt_agegroup_items_description_check CHECK (description !~ similar_escape(' *', NULL))
);
COMMENT ON COLUMN cf_wt_agegroup_items.idofagegroupitem IS 'ID записи';
COMMENT ON COLUMN cf_wt_agegroup_items.description IS 'Описание элемента';
COMMENT ON COLUMN cf_wt_agegroup_items.version IS 'Версия (для АРМ)';


CREATE TABLE cf_wt_category_items (
    idofcategoryitem bigserial NOT NULL,
    createdate timestamp without time zone NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    idofuser bigint NOT NULL,
    guid character varying(36) NOT NULL,
    description character varying(255) NOT NULL,
    CONSTRAINT cf_wt_category_items_description_check CHECK (description !~ similar_escape(' *', NULL))
);
COMMENT ON COLUMN cf_wt_category_items.idofcategoryitem IS 'ID записи';
COMMENT ON COLUMN cf_wt_category_items.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_wt_category_items.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_wt_category_items.version IS 'Версия (для АРМ)';
COMMENT ON COLUMN cf_wt_category_items.idofuser IS 'ID создателя записи';
COMMENT ON COLUMN cf_wt_category_items.guid IS 'GUID (для АРМ)';
COMMENT ON COLUMN cf_wt_category_items.description IS 'Описание элемента';


CREATE TABLE cf_wt_complex_exclude_days (
    id bigserial NOT NULL,
    date timestamp without time zone NOT NULL,
    idofcomplex bigint NOT NULL,
    deletestate integer DEFAULT 0,
    version bigint DEFAULT 0 NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    create_by_id bigint NOT NULL,
    update_by_id bigint NOT NULL,
    createdate timestamp without time zone NOT NULL
);


CREATE TABLE cf_wt_complex_group_items (
    idofcomplexgroupitem bigint NOT NULL,
    description character varying(255) NOT NULL,
    version bigint DEFAULT 0 NOT NULL
);
COMMENT ON COLUMN cf_wt_complex_group_items.version IS 'Версия (для АРМ)';


CREATE TABLE cf_wt_complex_items_dish (
    idofcomplexitem bigint NOT NULL,
    idofdish bigint NOT NULL
);


CREATE TABLE cf_wt_complexes (
    idofcomplex bigserial NOT NULL,
    name character varying(256) NOT NULL,
    price numeric(10,2) DEFAULT 0,
    begindate timestamp without time zone,
    enddate timestamp without time zone,
    cyclemotion integer,
    dayincycle integer,
    version bigint DEFAULT 0 NOT NULL,
    guid character varying(36) NOT NULL,
    createdate timestamp without time zone NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    create_by_id bigint NOT NULL,
    update_by_id bigint,
    deletestate integer DEFAULT 0 NOT NULL,
    idofcomplexgroupitem bigint NOT NULL,
    idofagegroupitem bigint NOT NULL,
    idofdiettype bigint NOT NULL,
    idofcontragent bigint NOT NULL,
    idoforggroup bigint,
    composite boolean DEFAULT false NOT NULL,
    is_portal boolean DEFAULT false NOT NULL,
    start_cycle_day integer DEFAULT 1 NOT NULL,
    barcode character varying(16),
    CONSTRAINT cf_wt_complexes_name_check CHECK (name !~ similar_escape(' *', NULL)),
    CONSTRAINT cf_wt_complexes_price_check CHECK (price >= 0)
);


CREATE TABLE cf_wt_complexes_items (
    idofcomplexitem bigserial NOT NULL,
    idofcomplex bigint NOT NULL,
    cycle_day integer NOT NULL,
    count_dishes integer DEFAULT 0
);


CREATE TABLE cf_wt_complexes_org (
    idofcomplex bigint NOT NULL,
    idoforg bigint NOT NULL
);


CREATE TABLE cf_wt_diet_type (
    idofdiettype bigint NOT NULL,
    description character varying(255) NOT NULL,
    version bigint DEFAULT 0 NOT NULL
);
COMMENT ON COLUMN cf_wt_diet_type.version IS 'Версия (для АРМ)';


CREATE TABLE cf_wt_discountrules (
    idofrule bigint NOT NULL,
    description character varying(100) DEFAULT '' NOT NULL,
    priority integer DEFAULT 0 NOT NULL,
    rate integer DEFAULT 0 NOT NULL,
    operationor boolean DEFAULT false NOT NULL,
    subcategory character varying(100) DEFAULT ''
);


CREATE TABLE cf_wt_discountrules_categorydiscount (
    idofrule bigint NOT NULL,
    idofcategorydiscount bigint NOT NULL
);


CREATE TABLE cf_wt_discountrules_categoryorg (
    idofrule bigint NOT NULL,
    idofcategoryorg bigserial NOT NULL
);


CREATE TABLE cf_wt_discountrules_complexes (
    idofrule bigint NOT NULL,
    idofcomplex bigserial NOT NULL
);


CREATE TABLE cf_wt_dish_categoryitem_relationships (
    idofdish bigint NOT NULL,
    idofcategoryitem bigint NOT NULL
);


CREATE TABLE cf_wt_dish_groupitem_relationships (
    idofdish bigint NOT NULL,
    idofgroupitem bigint NOT NULL
);


CREATE TABLE cf_wt_dishes (
    idofdish bigserial NOT NULL,
    dishname character varying(128) NOT NULL,
    componentsofdish character varying(512),
    code integer,
    price numeric(10,2) DEFAULT 0 NOT NULL,
    dateofbeginmenuincluding timestamp without time zone,
    dateofendmenuincluding timestamp without time zone,
    createdate timestamp without time zone NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    deletestate integer DEFAULT 0 NOT NULL,
    create_by_id bigint NOT NULL,
    guid character varying(36) NOT NULL,
    idofagegroupitem bigint NOT NULL,
    idoftypeofproductionitem bigint NOT NULL,
    update_by_id bigint,
    protein integer,
    fat integer,
    carbohydrates integer,
    calories integer,
    qty character varying(32),
    idofcontragent bigint DEFAULT 1 NOT NULL,
    barcode character varying(16),
    CONSTRAINT cf_wt_dishes_dishname_check CHECK (dishname !~ similar_escape(' *', NULL)),
    CONSTRAINT cf_wt_dishes_price_check CHECK (price >= 0)
);
COMMENT ON COLUMN cf_wt_dishes.idofdish IS 'ID записи';
COMMENT ON COLUMN cf_wt_dishes.dishname IS 'Название блюда';
COMMENT ON COLUMN cf_wt_dishes.componentsofdish IS 'Состав блюда';
COMMENT ON COLUMN cf_wt_dishes.code IS 'Код блюда';
COMMENT ON COLUMN cf_wt_dishes.price IS 'Цена';
COMMENT ON COLUMN cf_wt_dishes.dateofbeginmenuincluding IS 'Дата начала включения в меню';
COMMENT ON COLUMN cf_wt_dishes.dateofendmenuincluding IS 'Дата окончания включения в меню';
COMMENT ON COLUMN cf_wt_dishes.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_wt_dishes.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_wt_dishes.version IS 'Версия (для АРМ)';
COMMENT ON COLUMN cf_wt_dishes.deletestate IS 'Признак архивности';
COMMENT ON COLUMN cf_wt_dishes.create_by_id IS 'ID создателя записи';
COMMENT ON COLUMN cf_wt_dishes.guid IS 'GUID (для АРМ)';
COMMENT ON COLUMN cf_wt_dishes.idofagegroupitem IS 'ID возрастной группы';
COMMENT ON COLUMN cf_wt_dishes.idoftypeofproductionitem IS 'ID типа производства';
COMMENT ON COLUMN cf_wt_dishes.update_by_id IS 'ID пользователя, последний изменивший запись ';
COMMENT ON COLUMN cf_wt_dishes.protein IS 'Белки';
COMMENT ON COLUMN cf_wt_dishes.fat IS 'Жиры';
COMMENT ON COLUMN cf_wt_dishes.carbohydrates IS 'Углеводы';
COMMENT ON COLUMN cf_wt_dishes.calories IS 'Калории';
COMMENT ON COLUMN cf_wt_dishes.qty IS 'Кол-во/Масса ';


CREATE TABLE cf_wt_group_items (
    idofgroupitem bigserial NOT NULL,
    description character varying(255) NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT cf_wt_group_items_description_check CHECK (description !~ similar_escape(' *', NULL))
);
COMMENT ON COLUMN cf_wt_group_items.idofgroupitem IS 'ID записи';
COMMENT ON COLUMN cf_wt_group_items.description IS 'Описание элемента';
COMMENT ON COLUMN cf_wt_group_items.version IS 'Версия (для АРМ)';


CREATE TABLE cf_wt_menu (
    idofmenu bigserial NOT NULL,
    menuname character varying(128) NOT NULL,
    begindate timestamp without time zone NOT NULL,
    enddate timestamp without time zone NOT NULL,
    createdate timestamp without time zone NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    idoforggroup bigint,
    create_by_id bigint NOT NULL,
    update_by_id bigint,
    version bigint DEFAULT 1 NOT NULL,
    idofcontragent bigint NOT NULL,
    deletestate integer DEFAULT 0 NOT NULL
);
COMMENT ON COLUMN cf_wt_menu.idofmenu IS 'ID записи';
COMMENT ON COLUMN cf_wt_menu.menuname IS 'Название меню';
COMMENT ON COLUMN cf_wt_menu.begindate IS 'Время нача действия';
COMMENT ON COLUMN cf_wt_menu.enddate IS 'Время завершения действия';
COMMENT ON COLUMN cf_wt_menu.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_wt_menu.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_wt_menu.idoforggroup IS 'Ссылка на группу ОО';
COMMENT ON COLUMN cf_wt_menu.create_by_id IS 'ID создателя записи';
COMMENT ON COLUMN cf_wt_menu.update_by_id IS 'ID пользователя, последний изменивший запись ';
COMMENT ON COLUMN cf_wt_menu.version IS 'Версия (для АРМ) ';


CREATE TABLE cf_wt_menu_group_dish_relationships (
    idofmenumenugrouprelation bigint NOT NULL,
    idofdish bigint NOT NULL
);


CREATE TABLE cf_wt_menu_group_relationships (
    id bigserial NOT NULL,
    idofmenu bigint,
    idofmenugroup bigint,
    deletestate integer DEFAULT 0 NOT NULL
);


CREATE TABLE cf_wt_menu_groups (
    id bigserial NOT NULL,
    name character varying(64) NOT NULL,
    createdate timestamp without time zone NOT NULL,
    lastupdate timestamp without time zone NOT NULL,
    create_by_id bigint NOT NULL,
    update_by_id bigint NOT NULL,
    version bigint DEFAULT 1 NOT NULL,
    deletestate integer DEFAULT 0 NOT NULL,
    idofcontragent bigint NOT NULL,
    default_flag boolean DEFAULT false NOT NULL
);


CREATE TABLE cf_wt_menu_org (
    idofmenu bigint NOT NULL,
    idoforg bigint NOT NULL
);


CREATE TABLE cf_wt_org_group_relations (
    idoforggroup bigint NOT NULL,
    idoforg bigint NOT NULL
);


CREATE TABLE cf_wt_org_groups (
    idoforggroup bigserial NOT NULL,
    nameoforggroup character varying(128) NOT NULL,
    createdate timestamp with time zone NOT NULL,
    lastupdate timestamp with time zone,
    deletestate integer DEFAULT 0 NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    create_by_id bigint NOT NULL,
    update_by_id bigint,
    idofcontragent bigint
);
COMMENT ON COLUMN cf_wt_org_groups.idoforggroup IS 'ID записи';
COMMENT ON COLUMN cf_wt_org_groups.nameoforggroup IS 'Название группы';
COMMENT ON COLUMN cf_wt_org_groups.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_wt_org_groups.lastupdate IS 'Дата последнего обновления';
COMMENT ON COLUMN cf_wt_org_groups.deletestate IS 'Признак архивности записи';
COMMENT ON COLUMN cf_wt_org_groups.version IS 'Версия (для АРМ) ';
COMMENT ON COLUMN cf_wt_org_groups.create_by_id IS 'ID создателя записи';
COMMENT ON COLUMN cf_wt_org_groups.update_by_id IS 'ID пользователя, последний изменивший запись ';


CREATE TABLE cf_wt_refresh_token (
    hash character varying(128) NOT NULL,
    user_id bigint,
    last_session timestamp without time zone
);


CREATE TABLE cf_wt_typeofproduction_items (
    idoftypeproductionitem bigserial NOT NULL,
    description character varying(255) NOT NULL,
    version bigint DEFAULT 0 NOT NULL,
    CONSTRAINT cf_wt_typeofproduction_items_description_check CHECK (description !~ similar_escape(' *', NULL))
);
COMMENT ON COLUMN cf_wt_typeofproduction_items.idoftypeproductionitem IS 'ID записи';
COMMENT ON COLUMN cf_wt_typeofproduction_items.description IS 'Описание элемента';
COMMENT ON COLUMN cf_wt_typeofproduction_items.version IS 'Версия (для АРМ)';


CREATE TABLE cf_wt_menus_temp (
    idofuser bigint NOT NULL,
    menu json NOT NULL,
    idofmenutemp bigserial NOT NULL,
    CONSTRAINT cf_wt_menus_temp_pk PRIMARY KEY (idofmenutemp),
    CONSTRAINT cf_wt_menus_temp_cf_users_idofuser_fk FOREIGN KEY (idofuser)
    REFERENCES cf_users (idofuser) MATCH SIMPLE
    ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE ONLY cf_wt_agegroup_items
    ADD CONSTRAINT cf_wt_agegroup_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_agegroup_items
    ADD CONSTRAINT cf_wt_agegroup_pk PRIMARY KEY (idofagegroupitem);

ALTER TABLE ONLY cf_wt_category_items
    ADD CONSTRAINT cf_wt_category_items_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_category_items
    ADD CONSTRAINT cf_wt_category_items_guid_uk UNIQUE (guid);

ALTER TABLE ONLY cf_wt_category_items
    ADD CONSTRAINT cf_wt_category_items_pk PRIMARY KEY (idofcategoryitem);

ALTER TABLE ONLY cf_wt_complex_exclude_days
    ADD CONSTRAINT cf_wt_complex_exclude_days_pk PRIMARY KEY (id);

ALTER TABLE ONLY cf_wt_complex_group_items
    ADD CONSTRAINT cf_wt_complex_group_items_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_complex_group_items
    ADD CONSTRAINT cf_wt_complex_group_items_pk PRIMARY KEY (idofcomplexgroupitem);

ALTER TABLE ONLY cf_wt_complex_items_dish
    ADD CONSTRAINT cf_wt_complex_items_dish_pk PRIMARY KEY (idofcomplexitem, idofdish);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_contragent_name_price_uk UNIQUE (idofcontragent, name, price);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_guid_key UNIQUE (guid);

ALTER TABLE ONLY cf_wt_complexes_items
    ADD CONSTRAINT cf_wt_complexes_items_pkey PRIMARY KEY (idofcomplexitem);

ALTER TABLE ONLY cf_wt_complexes_org
    ADD CONSTRAINT cf_wt_complexes_org_pk PRIMARY KEY (idoforg, idofcomplex);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_pkey PRIMARY KEY (idofcomplex);

ALTER TABLE ONLY cf_wt_diet_type
    ADD CONSTRAINT cf_wt_diet_type_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_diet_type
    ADD CONSTRAINT cf_wt_diet_type_pk PRIMARY KEY (idofdiettype);

ALTER TABLE ONLY cf_wt_discountrules_categorydiscount
    ADD CONSTRAINT cf_wt_discountrules_categorydiscount_pk PRIMARY KEY (idofrule, idofcategorydiscount);

ALTER TABLE ONLY cf_wt_discountrules_categoryorg
    ADD CONSTRAINT cf_wt_discountrules_categoryorg_pk PRIMARY KEY (idofrule, idofcategoryorg);

ALTER TABLE ONLY cf_wt_discountrules_complexes
    ADD CONSTRAINT cf_wt_discountrules_complexes_pk PRIMARY KEY (idofrule, idofcomplex);

ALTER TABLE ONLY cf_wt_discountrules
    ADD CONSTRAINT cf_wt_discountrules_pk PRIMARY KEY (idofrule);

ALTER TABLE ONLY cf_wt_dish_categoryitem_relationships
    ADD CONSTRAINT cf_wt_dish_categoryitem_relationships_pk PRIMARY KEY (idofdish, idofcategoryitem);

ALTER TABLE ONLY cf_wt_dish_groupitem_relationships
    ADD CONSTRAINT cf_wt_dish_groupitem_relationships_pk PRIMARY KEY (idofdish, idofgroupitem);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_code_contragent UNIQUE (idofcontragent, code);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_guid_uk UNIQUE (guid);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_pk PRIMARY KEY (idofdish);

ALTER TABLE ONLY cf_wt_group_items
    ADD CONSTRAINT cf_wt_group_items_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_group_items
    ADD CONSTRAINT cf_wt_group_items_pk PRIMARY KEY (idofgroupitem);

ALTER TABLE ONLY cf_wt_menu_group_relationships
    ADD CONSTRAINT cf_wt_menu_group_relationships_uniq UNIQUE (idofmenu, idofmenugroup);

ALTER TABLE ONLY cf_wt_menu_groups
    ADD CONSTRAINT cf_wt_menu_groups_name_uniq UNIQUE (name, idofcontragent);

ALTER TABLE ONLY cf_wt_menu_groups
    ADD CONSTRAINT cf_wt_menu_groups_pk PRIMARY KEY (id);

ALTER TABLE ONLY cf_wt_menu_group_relationships
    ADD CONSTRAINT cf_wt_menu_menu_group_relations_pk PRIMARY KEY (id);

ALTER TABLE ONLY cf_wt_menu_org
    ADD CONSTRAINT cf_wt_menu_org_pk PRIMARY KEY (idoforg, idofmenu);

ALTER TABLE ONLY cf_wt_menu
    ADD CONSTRAINT cf_wt_menu_pk PRIMARY KEY (idofmenu);

ALTER TABLE ONLY cf_wt_menu_group_dish_relationships
    ADD CONSTRAINT cf_wt_mmd_pk PRIMARY KEY (idofmenumenugrouprelation, idofdish);

ALTER TABLE ONLY cf_wt_org_group_relations
    ADD CONSTRAINT cf_wt_org_group_relations_pk PRIMARY KEY (idoforggroup, idoforg);

ALTER TABLE ONLY cf_wt_org_groups
    ADD CONSTRAINT cf_wt_org_groups_pk PRIMARY KEY (idoforggroup);

ALTER TABLE ONLY cf_wt_refresh_token
    ADD CONSTRAINT cf_wt_refresh_token_pkey PRIMARY KEY (hash);

ALTER TABLE ONLY cf_wt_typeofproduction_items
    ADD CONSTRAINT cf_wt_typeofproduction_description_uk UNIQUE (description);

ALTER TABLE ONLY cf_wt_typeofproduction_items
    ADD CONSTRAINT cf_wt_typeofproduction_pk PRIMARY KEY (idoftypeproductionitem);

CREATE UNIQUE INDEX cf_wt_menu_groups_id_uindex ON cf_wt_menu_groups USING btree (id);

ALTER TABLE ONLY cf_wt_category_items
    ADD CONSTRAINT cf_wt_category_items_fk FOREIGN KEY (idofuser) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_complex_exclude_days
    ADD CONSTRAINT cf_wt_complex_exclude_days_complex_fk FOREIGN KEY (idofcomplex) REFERENCES cf_wt_complexes(idofcomplex);

ALTER TABLE ONLY cf_wt_complex_exclude_days
    ADD CONSTRAINT cf_wt_complex_exclude_days_create_fk FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_complex_exclude_days
    ADD CONSTRAINT cf_wt_complex_exclude_days_update_fk FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_complex_items_dish
    ADD CONSTRAINT cf_wt_complex_items_dish_idofcomplexitem_fkey FOREIGN KEY (idofcomplexitem) REFERENCES cf_wt_complexes_items(idofcomplexitem) ON DELETE CASCADE;

ALTER TABLE ONLY cf_wt_complex_items_dish
    ADD CONSTRAINT cf_wt_complex_items_dish_idofdish_fkey FOREIGN KEY (idofdish) REFERENCES cf_wt_dishes(idofdish) ON DELETE CASCADE;

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_create_by_id_fkey FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_idofagegroupitem_fkey FOREIGN KEY (idofagegroupitem) REFERENCES cf_wt_agegroup_items(idofagegroupitem);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_idofcomplexgroupitem_fkey FOREIGN KEY (idofcomplexgroupitem) REFERENCES cf_wt_complex_group_items(idofcomplexgroupitem);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_idofcontragent_fkey FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_idofdiettype_fkey FOREIGN KEY (idofdiettype) REFERENCES cf_wt_diet_type(idofdiettype);

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_idoforggroup_fkey FOREIGN KEY (idoforggroup) REFERENCES cf_wt_org_groups(idoforggroup) ON DELETE SET NULL;

ALTER TABLE ONLY cf_wt_complexes_items
    ADD CONSTRAINT cf_wt_complexes_items_idofcomplex_fkey FOREIGN KEY (idofcomplex) REFERENCES cf_wt_complexes(idofcomplex);

ALTER TABLE ONLY cf_wt_complexes_org
    ADD CONSTRAINT cf_wt_complexes_org_idofcomplex_fkey FOREIGN KEY (idofcomplex) REFERENCES cf_wt_complexes(idofcomplex) ON DELETE CASCADE;

ALTER TABLE ONLY cf_wt_complexes_org
    ADD CONSTRAINT cf_wt_complexes_org_idoforg_fkey FOREIGN KEY (idoforg) REFERENCES cf_orgs(idoforg) ON DELETE CASCADE;

ALTER TABLE ONLY cf_wt_complexes
    ADD CONSTRAINT cf_wt_complexes_update_by_id_fkey FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_discountrules_categorydiscount
    ADD CONSTRAINT cf_wt_discountrules_categorydiscount_categorydiscount_fk FOREIGN KEY (idofcategorydiscount) REFERENCES cf_categorydiscounts(idofcategorydiscount);

ALTER TABLE ONLY cf_wt_discountrules_categorydiscount
    ADD CONSTRAINT cf_wt_discountrules_categorydiscount_rules_fk FOREIGN KEY (idofrule) REFERENCES cf_wt_discountrules(idofrule);

ALTER TABLE ONLY cf_wt_discountrules_categoryorg
    ADD CONSTRAINT cf_wt_discountrules_categoryorg_categoryorg_fk FOREIGN KEY (idofcategoryorg) REFERENCES cf_categoryorg(idofcategoryorg);

ALTER TABLE ONLY cf_wt_discountrules_categoryorg
    ADD CONSTRAINT cf_wt_discountrules_categoryorg_rules_fk FOREIGN KEY (idofrule) REFERENCES cf_wt_discountrules(idofrule);

ALTER TABLE ONLY cf_wt_discountrules_complexes
    ADD CONSTRAINT cf_wt_discountrules_complexes_complexes_fk FOREIGN KEY (idofcomplex) REFERENCES cf_wt_complexes(idofcomplex);

ALTER TABLE ONLY cf_wt_discountrules_complexes
    ADD CONSTRAINT cf_wt_discountrules_complexes_rules_fk FOREIGN KEY (idofrule) REFERENCES cf_wt_discountrules(idofrule);

ALTER TABLE ONLY cf_wt_dish_categoryitem_relationships
    ADD CONSTRAINT cf_wt_dish_categoryitem_relationships_idofcategoryitem_fkey FOREIGN KEY (idofcategoryitem) REFERENCES cf_wt_category_items(idofcategoryitem);

ALTER TABLE ONLY cf_wt_dish_categoryitem_relationships
    ADD CONSTRAINT cf_wt_dish_categoryitem_relationships_idofdish_fkey FOREIGN KEY (idofdish) REFERENCES cf_wt_dishes(idofdish);

ALTER TABLE ONLY cf_wt_dish_groupitem_relationships
    ADD CONSTRAINT cf_wt_dish_groupitem_relationships_idofdish_fkey FOREIGN KEY (idofdish) REFERENCES cf_wt_dishes(idofdish);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_agegroup_fk FOREIGN KEY (idofagegroupitem) REFERENCES cf_wt_agegroup_items(idofagegroupitem);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_idofcontragent_fkey FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_typeofproduction_fk FOREIGN KEY (idoftypeofproductionitem) REFERENCES cf_wt_typeofproduction_items(idoftypeproductionitem);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_update_by_id_fkey FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_dishes
    ADD CONSTRAINT cf_wt_dishes_user_fk FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_menu
    ADD CONSTRAINT cf_wt_menu_created_by_id_fkey FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_menu_groups
    ADD CONSTRAINT cf_wt_menu_created_by_id_fkey FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_menu_group_dish_relationships
    ADD CONSTRAINT cf_wt_menu_group_dish_relationships_cf_wt_dishes_idofdish_fk FOREIGN KEY (idofdish) REFERENCES cf_wt_dishes(idofdish);

ALTER TABLE ONLY cf_wt_menu_group_dish_relationships
    ADD CONSTRAINT cf_wt_menu_group_dish_relationships_cf_wt_menu_menu_group_relat FOREIGN KEY (idofmenumenugrouprelation) REFERENCES cf_wt_menu_group_relationships(id);

ALTER TABLE ONLY cf_wt_menu_groups
    ADD CONSTRAINT cf_wt_menu_groups_contragent_fk FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent);

ALTER TABLE ONLY cf_wt_menu
    ADD CONSTRAINT cf_wt_menu_idofcontragent_fkey FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent);

ALTER TABLE ONLY cf_wt_menu_group_relationships
    ADD CONSTRAINT cf_wt_menu_menu_group_relations_cf_wt_menu_groups_id_fk FOREIGN KEY (idofmenugroup) REFERENCES cf_wt_menu_groups(id);

ALTER TABLE ONLY cf_wt_menu_group_relationships
    ADD CONSTRAINT cf_wt_menu_menu_group_relations_cf_wt_menu_idofmenu_fk FOREIGN KEY (idofmenu) REFERENCES cf_wt_menu(idofmenu);

ALTER TABLE ONLY cf_wt_menu_org
    ADD CONSTRAINT cf_wt_menu_org_idofmenu_fkey FOREIGN KEY (idofmenu) REFERENCES cf_wt_menu(idofmenu);

ALTER TABLE ONLY cf_wt_menu_org
    ADD CONSTRAINT cf_wt_menu_org_idoforg_fkey FOREIGN KEY (idoforg) REFERENCES cf_orgs(idoforg);

ALTER TABLE ONLY cf_wt_menu
    ADD CONSTRAINT cf_wt_menu_orggroup_fk FOREIGN KEY (idoforggroup) REFERENCES cf_wt_org_groups(idoforggroup);

ALTER TABLE ONLY cf_wt_menu
    ADD CONSTRAINT cf_wt_menu_updated_by_id_fkey FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_menu_groups
    ADD CONSTRAINT cf_wt_menu_updated_by_id_fkey FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_org_group_relations
    ADD CONSTRAINT cf_wt_org_group_relations_group_fk FOREIGN KEY (idoforggroup) REFERENCES cf_wt_org_groups(idoforggroup);

ALTER TABLE ONLY cf_wt_org_group_relations
    ADD CONSTRAINT cf_wt_org_group_relations_org_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs(idoforg);

ALTER TABLE ONLY cf_wt_org_groups
    ADD CONSTRAINT cf_wt_org_groups_created_by_id_fkey FOREIGN KEY (create_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_org_groups
    ADD CONSTRAINT cf_wt_org_groups_idofcontragent_fkey FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent);

ALTER TABLE ONLY cf_wt_org_groups
    ADD CONSTRAINT cf_wt_org_groups_updated_by_id_fkey FOREIGN KEY (update_by_id) REFERENCES cf_users(idofuser);

ALTER TABLE ONLY cf_wt_refresh_token
    ADD CONSTRAINT cf_wt_refresh_token_user_id_fkey FOREIGN KEY (user_id) REFERENCES cf_users(idofuser) ON UPDATE CASCADE ON DELETE CASCADE;

INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (1, '1,5-3', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (2, '3-7', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (3, '1-4', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (4, '5-11', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (5, 'Колледж', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (6, 'Сотрудники', 0);
INSERT INTO cf_wt_agegroup_items (idofagegroupitem, description, version) VALUES (7, 'Все', 0);


INSERT INTO cf_wt_complex_group_items (idofcomplexgroupitem, description, version) VALUES (1, 'Льготное питание', 0);
INSERT INTO cf_wt_complex_group_items (idofcomplexgroupitem, description, version) VALUES (2, 'Платное питание', 0);
INSERT INTO cf_wt_complex_group_items (idofcomplexgroupitem, description, version) VALUES (3, 'Все виды питания', 0);


INSERT INTO cf_wt_diet_type (idofdiettype, description, version) VALUES (1, 'Завтрак', 0);
INSERT INTO cf_wt_diet_type (idofdiettype, description, version) VALUES (2, 'Обед', 0);
INSERT INTO cf_wt_diet_type (idofdiettype, description, version) VALUES (4, 'Ужин', 0);
INSERT INTO cf_wt_diet_type (idofdiettype, description, version) VALUES (3, 'Полдник', 0);


INSERT INTO cf_wt_typeofproduction_items (idoftypeproductionitem, description, version) VALUES (1, 'Собственное', 0);
INSERT INTO cf_wt_typeofproduction_items (idoftypeproductionitem, description, version) VALUES (2, 'Закупленное', 0);


INSERT INTO cf_wt_group_items (idofgroupitem, description, version) VALUES (1, 'Льготное питание', 0);
INSERT INTO cf_wt_group_items (idofgroupitem, description, version) VALUES (2, 'Платное питание', 0);
INSERT INTO cf_wt_group_items (idofgroupitem, description, version) VALUES (3, 'Буфет', 0);
INSERT INTO cf_wt_group_items (idofgroupitem, description, version) VALUES (4, 'Коммерческое питание', 0);
INSERT INTO cf_wt_group_items (idofgroupitem, description, version) VALUES (5, 'Все', 0);

--! ФИНАЛИЗИРОВАН 24.04.2020, НЕ МЕНЯТЬ