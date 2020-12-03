/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 243

alter table cf_wt_category_items drop constraint cf_wt_category_items_description_uk;

create unique index cf_wt_category_items_description_uk
    on cf_wt_category_items(LOWER(description), idofcategory) where deletestate = 0;

create unique index cf_wt_categories_description_uk
    on cf_wt_categories(LOWER(description)) where deletestate = 0;

CREATE OR REPLACE FUNCTION wt_category_items_drop_ref_proc() RETURNS trigger
    LANGUAGE 'plpgsql' AS
'
    DECLARE
        id bigint;
    BEGIN
    id := NEW.idofcategoryitem;
    IF (NEW.deletestate = 1) THEN
    DELETE FROM cf_wt_dish_categoryitem_relationships
    WHERE idofcategoryitem = id;
    END IF;
    RETURN NEW;
    end
';

CREATE TRIGGER wt_category_items_drop_ref
    BEFORE INSERT OR UPDATE
    ON cf_wt_category_items
    FOR EACH ROW
EXECUTE PROCEDURE wt_category_items_drop_ref_proc();

CREATE OR REPLACE FUNCTION wt_categories_drop_ref_proc() RETURNS trigger
    LANGUAGE 'plpgsql' AS
'
    DECLARE
        id bigint;
    BEGIN
    id := NEW.idofcategory;
    IF (NEW.deletestate = 1) THEN
    UPDATE cf_wt_dishes as dish
    SET idofcategory = NULL
    WHERE dish.idofcategory = id;
    END IF;
    RETURN NEW;
    end
';

CREATE TRIGGER wt_categories_drop_ref
    BEFORE INSERT OR UPDATE
    ON cf_wt_categories
    FOR EACH ROW
EXECUTE PROCEDURE wt_categories_drop_ref_proc();

--! ФИНАЛИЗИРОВАН 03.12.2020, НЕ МЕНЯТЬ