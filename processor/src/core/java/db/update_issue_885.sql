/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

--/arm-pp-back-end/-/issues/92
update cf_wt_dishes set protein = 0 where cf_wt_dishes.protein is null;
update cf_wt_dishes set fat = 0 where fat is null;
update cf_wt_dishes set carbohydrates = 0 where cf_wt_dishes.carbohydrates is null;
update cf_wt_dishes set calories = 0 where cf_wt_dishes.calories is null;
update cf_wt_dishes set qty = '0' where qty is null || qty like '';

alter table cf_wt_dishes alter column protein set not null;
alter table cf_wt_dishes alter column fat set not null;
alter table cf_wt_dishes alter column carbohydrates set not null;
alter table cf_wt_dishes alter column calories set not null;
alter table cf_wt_dishes alter column qty set not null;

--/arm-pp-back-end/-/issues/96
create function complex_org_inc_ver() returns trigger as
$$
declare
    max_version bigint;
    curr_row record;
begin
    max_version := (select max(c.version) from cf_wt_complexes c);
    if (max_version is null) then
        max_version = 0;
    end if;
    if (tg_op = 'INSERT') then
        curr_row = new;
    elsif (tg_op = 'DELETE') then
        curr_row = old;
    end if;
    update cf_wt_complexes set version = max_version + 1 where idofcomplex = curr_row.idofcomplex;
    return curr_row;
end;
$$ language plpgsql;

create trigger complex_org_inc_ver before insert or delete
    on cf_wt_complexes_org for each row execute procedure complex_org_inc_ver();

create function menu_org_inc_ver() returns trigger as
$$
declare
    max_version bigint;
    curr_row record;
begin
    max_version := (select max(m.version) from cf_wt_menu m);
    if (max_version is null) then
        max_version = 0;
    end if;
    if (tg_op = 'INSERT') then
        curr_row = new;
    elsif (tg_op = 'DELETE') then
        curr_row = old;
    end if;
    update cf_wt_menu set version = max_version + 1 where idofmenu = curr_row.idofmenu;
    return curr_row;
end;
$$ language plpgsql;

create trigger menu_org_inc_ver before insert or delete
    on cf_wt_menu_org for each row execute procedure menu_org_inc_ver();

create function org_group_org_inc_ver() returns trigger as
$$
declare
    max_version bigint;
    curr_row record;
begin
    max_version := (select max(g.version) from cf_wt_org_groups g);
    if (max_version is null) then
        max_version = 0;
    end if;
    if (tg_op = 'INSERT') then
        curr_row = new;
    elsif (tg_op = 'DELETE') then
        curr_row = old;
    end if;
    update cf_wt_org_groups set version = max_version + 1 where idoforggroup = curr_row.idoforggroup;
    return curr_row;
end;
$$ language plpgsql;

create trigger org_group_org_inc_ver before insert or delete
    on cf_wt_org_groups for each row execute procedure org_group_org_inc_ver();

--/arm-pp-back-end/-/issues/97
create function cf_wt_org_group_complex_menu_ver_inc() returns trigger as
$$
declare
    max_complex_version bigint;
    max_menu_version bigint;
    curr_row record;
begin
    max_complex_version := (select max(version) from cf_wt_complexes);
    if (max_complex_version is null ) then
        max_complex_version = 0;
    end if;
    max_menu_version := (select max(version) from cf_wt_menu);
    if (max_menu_version is null ) then
        max_menu_version = 0;
    end if;
    if (tg_op = 'INSERT') then
        curr_row = new;
    elsif (tg_op = 'DELETE') then
        curr_row = old;
    end if;
    update cf_wt_complexes set version = max_complex_version + 1 where idoforggroup = curr_row.idoforggroup;
    update cf_wt_menu set version = max_menu_version + 1 where idoforggroup = curr_row.idoforggroup;
    return curr_row;
end;
$$ language plpgsql;
create trigger cf_wt_org_group_complex_menu_ver_inc before insert or delete
    on cf_wt_org_group_relations for each row execute procedure cf_wt_org_group_complex_menu_ver_inc();

drop trigger org_group_org_inc_ver on cf_wt_org_group_relations;
create trigger org_group_org_inc_ver before insert or delete
    on cf_wt_org_group_relations for each row execute procedure org_group_org_inc_ver();

