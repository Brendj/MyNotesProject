/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 211

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
    idofcategorydiscount bigint, -- Идентификатор категории клиентов
    idofsupercategory bigint, -- Идентификатор суперкатегории
    CONSTRAINT cf_wt_discountrules_pk PRIMARY KEY (idofrule),
    CONSTRAINT cf_wt_discountrules_categorydiscount_fk FOREIGN KEY (idofcategorydiscount)
        REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS=FALSE
    );
ALTER TABLE cf_discountrules
    OWNER TO postgres;

-- Таблица связки льготных правил с комплексами веб-технолога
CREATE TABLE cf_wt_discountrules_complexes
(
    idofdiscountcomplexes bigint NOT NULL, -- Идентификатор записи
    idofrule bigint NOT NULL, -- Идентификатор льготного правила
    idofcomplex bigserial NOT NULL, -- Идентификатор комплекса
    CONSTRAINT cf_wt_discountrules_complexes_pk PRIMARY KEY (idofdiscountcomplexes),
    CONSTRAINT cf_wt_discountcomplexes_rules_fk FOREIGN KEY (idofrule)
        REFERENCES cf_wt_discountrules (idofrule) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION,
    CONSTRAINT cf_wt_discountcomplexes_complexes_fk FOREIGN KEY (idofcomplex)
        REFERENCES cf_wt_complexes (idofcomplex) MATCH SIMPLE
        ON UPDATE NO ACTION ON DELETE NO ACTION
)
    WITH (
        OIDS=FALSE
    );
ALTER TABLE cf_discountrules
    OWNER TO postgres;

