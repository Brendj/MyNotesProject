/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 214

-- 395: добавление версий справочникам веб-технолога

ALTER TABLE cf_wt_typeofproduction_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_typeofproduction_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_agegroup_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_agegroup_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_diet_type add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_diet_type.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_complex_group_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_complex_group_items.version IS 'Версия (для АРМ)';

ALTER TABLE cf_wt_group_items add column version bigint NOT NULL DEFAULT 0; -- Версия (для АРМ)
COMMENT ON COLUMN cf_wt_group_items.version IS 'Версия (для АРМ)';

-- 431: создание новых таблиц льгот

-- Таблица льготных правил веб-технолога
CREATE TABLE cf_wt_discountrules
(
    idofrule bigint NOT NULL, -- Идентификатор записи
    description character varying(100) NOT NULL DEFAULT ''::character varying, -- Описание
    priority integer NOT NULL DEFAULT 0, -- Приоритет
    rate integer NOT NULL DEFAULT 0, -- Ставка дисконтирования
    operationor boolean NOT NULL DEFAULT false, -- Объединение комплексов
    subcategory character varying(100) DEFAULT ''::character varying, -- Cуперкатегория
    CONSTRAINT cf_wt_discountrules_pk PRIMARY KEY (idofrule)
)
    WITH (
        OIDS=FALSE
    );

-- Таблица связки льготных правил с комплексами веб-технолога
CREATE TABLE cf_wt_discountrules_complexes
(
    idofrule bigint NOT NULL, -- Идентификатор льготного правила
    idofcomplex bigserial NOT NULL, -- Идентификатор комплекса
    CONSTRAINT cf_wt_discountrules_complexes_pk PRIMARY KEY (idofrule, idofcomplex),
    CONSTRAINT cf_wt_discountrules_complexes_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountrules_complexes_complexes_fk FOREIGN KEY (idofcomplex)
        REFERENCES cf_wt_complexes (idofcomplex) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS=FALSE
    );

-- Таблица связки льготных правил с cf_categoryorg
CREATE TABLE cf_wt_discountrules_categoryorg
(
    idofrule bigint NOT NULL, -- Идентификатор льготного правила
    idofcategoryorg bigserial NOT NULL, -- Идентификатор
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
    idofrule bigint NOT NULL, -- Идентификатор льготного правила
    idofcategorydiscount bigint NOT NULL, -- Идентификатор льготной категории
    CONSTRAINT cf_wt_discountrules_categorydiscount_pk PRIMARY KEY (idofrule, idofcategorydiscount),
    CONSTRAINT cf_wt_discountrules_categorydiscount_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountrules_categorydiscount_categorydiscount_fk FOREIGN KEY (idofcategorydiscount)
        REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS=FALSE
    );

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