--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 214

-- Задача 395: добавление версий справочникам веб-технолога

ALTER TABLE cf_wt_typeofproduction_items add column version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN cf_wt_typeofproduction_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_agegroup_items add column version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN cf_wt_agegroup_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_diet_type add column version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN cf_wt_diet_type.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_complex_group_items add column version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN cf_wt_complex_group_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_group_items add column version bigint NOT NULL DEFAULT 0;
COMMENT ON COLUMN cf_wt_group_items.version IS 'Версия (для АРМ)';

-- Таблица льготных правил веб-технолога
CREATE TABLE cf_wt_discountrules
(
    idofrule bigint NOT NULL,
    description character varying(100) NOT NULL DEFAULT '',
    priority integer NOT NULL DEFAULT 0,
    rate integer NOT NULL DEFAULT 0,
    operationor boolean NOT NULL DEFAULT false,
    subcategory character varying(100) DEFAULT '',
    CONSTRAINT cf_wt_discountrules_pk PRIMARY KEY (idofrule)
);
COMMENT ON COLUMN cf_wt_discountrules.idofrule IS 'Идентификатор записи';
COMMENT ON COLUMN cf_wt_discountrules.description IS 'Описание';
COMMENT ON COLUMN cf_wt_discountrules.priority IS 'Приоритет';
COMMENT ON COLUMN cf_wt_discountrules.rate IS 'Ставка дисконтирования';
COMMENT ON COLUMN cf_wt_discountrules.operationor IS 'Объединение комплексов';
COMMENT ON COLUMN cf_wt_discountrules.subcategory IS 'Cуперкатегория';

-- Таблица связки льготных правил с комплексами веб-технолога
CREATE TABLE cf_wt_discountrules_complexes
(
    idofrule bigint NOT NULL,
    idofcomplex bigserial NOT NULL,
    CONSTRAINT cf_wt_discountrules_complexes_pk PRIMARY KEY (idofrule, idofcomplex),
    CONSTRAINT cf_wt_discountrules_complexes_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountrules_complexes_complexes_fk FOREIGN KEY (idofcomplex)
        REFERENCES cf_wt_complexes (idofcomplex) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON COLUMN cf_wt_discountrules_complexes.idofrule IS 'Идентификатор льготного правила';
COMMENT ON COLUMN cf_wt_discountrules_complexes.idofcomplex IS 'Идентификатор комплекса';

-- Таблица связки льготных правил с cf_categoryorg
CREATE TABLE cf_wt_discountrules_categoryorg
(
    idofrule bigint NOT NULL,
    idofcategoryorg bigserial NOT NULL,
    CONSTRAINT cf_wt_discountrules_categoryorg_pk PRIMARY KEY (idofrule, idofcategoryorg),
    CONSTRAINT cf_wt_discountrules_categoryorg_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountrules_categoryorg_categoryorg_fk FOREIGN KEY (idofcategoryorg)
        REFERENCES cf_categoryorg (idofcategoryorg) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS=FALSE
    );

-- Таблица связки льготных правил с льготными категориями
CREATE TABLE cf_wt_discountrules_categorydiscount
(
    idofrule bigint NOT NULL,
    idofcategorydiscount bigint NOT NULL,
    CONSTRAINT cf_wt_discountrules_categorydiscount_pk PRIMARY KEY (idofrule, idofcategorydiscount),
    CONSTRAINT cf_wt_discountrules_categorydiscount_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountrules_categorydiscount_categorydiscount_fk FOREIGN KEY (idofcategorydiscount)
        REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
);
COMMENT ON COLUMN cf_wt_discountrules_categorydiscount.idofrule IS 'Идентификатор льготного правила';
COMMENT ON COLUMN cf_wt_discountrules_categorydiscount.idofcategorydiscount IS 'Идентификатор льготной категории';

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

--! ФИНАЛИЗИРОВАН 26.03.2020, НЕ МЕНЯТЬ