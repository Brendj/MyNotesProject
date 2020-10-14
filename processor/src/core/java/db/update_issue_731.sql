/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

alter table cf_wt_dishes alter column code set not null;
create sequence cf_wt_category_items_idofcategoryitem_seq
    maxvalue 2147483648;

CREATE SEQUENCE cf_wt_category_items_idofcategoryitem_seq1
    INCREMENT 1
    START 126
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1;

create sequence cf_wt_menu_group_id_seq
    maxvalue 2147483648;

create sequence cf_wt_complex_exclude_days_seq
    maxvalue 2147483647;

create sequence cf_wt_menu_group_relationships_seq
    maxvalue 2147483647;

alter sequence cf_wt_menu_group_relationships_id_seq maxvalue 214748364;

create table cf_wt_complexes_temp
(
    idofcomplextemp bigint not null
        constraint cf_wt_complexes_temp_pk
            primary key,
    complex json not null,
    idofuser bigint not null
        constraint cf_wt_complexes_temp_user_fk
            references cf_users
);

alter table cf_wt_complexes drop constraint cf_wt_complexes_contragent_name_price_uk;

alter table cf_wt_dishes alter column code set not null;

alter table cf_wt_menu_group_relationships alter column idofmenu set not null;

alter table cf_wt_menu_group_relationships alter column idofmenugroup set not null;

CREATE OR REPLACE FUNCTION dish_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(d.version) FROM cf_wt_dishes d);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END ;
$$ language plpgsql;
CREATE OR REPLACE FUNCTION complex_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(c.version) FROM cf_wt_complexes c);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END;
$$ language plpgsql;
CREATE OR REPLACE FUNCTION exclude_day_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(e.version) FROM cf_wt_complex_exclude_days e);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END;
$$ language plpgsql;
CREATE OR REPLACE FUNCTION menu_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(m.version) FROM cf_wt_menu m);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END;
$$ language plpgsql;
CREATE OR REPLACE FUNCTION menu_group_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(mg.version) FROM cf_wt_menu_groups mg);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END;
$$ language plpgsql;
CREATE OR REPLACE FUNCTION org_group_inc_version() RETURNS TRIGGER AS
$$
DECLARE
    max_version bigint;
BEGIN
    max_version := (SELECT max(g.version) FROM cf_wt_org_groups g);
    IF (max_version IS NULL) THEN
        max_version = 0;
    END IF;
    NEW.version := max_version + 1;
    RETURN NEW;
END;
$$ language plpgsql;
CREATE TRIGGER dish_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_dishes
    FOR EACH ROW
EXECUTE PROCEDURE dish_inc_version();
CREATE TRIGGER complex_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_complexes
    FOR EACH ROW
EXECUTE PROCEDURE complex_inc_version();
CREATE TRIGGER exclude_day_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_complex_exclude_days
    FOR EACH ROW
EXECUTE PROCEDURE exclude_day_inc_version();
CREATE TRIGGER menu_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_menu
    FOR EACH ROW
EXECUTE PROCEDURE menu_inc_version();
CREATE TRIGGER menu_group_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_menu_groups
    FOR EACH ROW
EXECUTE PROCEDURE menu_group_inc_version();
CREATE TRIGGER org_group_inc_version
    BEFORE INSERT OR UPDATE
    ON cf_wt_org_groups
    FOR EACH ROW
EXECUTE PROCEDURE org_group_inc_version();