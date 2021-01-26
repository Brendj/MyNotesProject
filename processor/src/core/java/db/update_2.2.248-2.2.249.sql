/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 249

CREATE INDEX cf_clientgroup_migrationhistory_idofclient_idx
ON cf_clientgroup_migrationhistory
USING btree
(idofclient);

alter table cf_preorder_menudetail alter column groupname type character varying(256);

alter table cf_preorder_complex alter column complexname type character varying(256);

--/arm-pp-back-end/-/issues/92
update cf_wt_dishes set protein = 0 where cf_wt_dishes.protein is null;
update cf_wt_dishes set fat = 0 where fat is null;
update cf_wt_dishes set carbohydrates = 0 where cf_wt_dishes.carbohydrates is null;
update cf_wt_dishes set calories = 0 where cf_wt_dishes.calories is null;
update cf_wt_dishes set qty = '0' where qty is null or qty like '';

alter table cf_wt_dishes alter column protein set not null;
alter table cf_wt_dishes alter column fat set not null;
alter table cf_wt_dishes alter column carbohydrates set not null;
alter table cf_wt_dishes alter column calories set not null;
alter table cf_wt_dishes alter column qty set not null;

create function complex_org_inc_ver() returns trigger
LANGUAGE 'plpgsql' as '
declare
  max_version bigint;
  curr_row record;
begin
  max_version := (select max(c.version) from cf_wt_complexes c);
  if (max_version is null) then
    max_version = 0;
  end if;
  if (tg_op = ''INSERT'') then
    curr_row = new;
  elsif (tg_op = ''DELETE'') then
    curr_row = old;
  end if;
  update cf_wt_complexes set version = max_version + 1 where idofcomplex = curr_row.idofcomplex;
  return curr_row;
end ';

create trigger complex_org_inc_ver before insert or delete
on cf_wt_complexes_org for each row execute procedure complex_org_inc_ver();

create function menu_org_inc_ver() returns trigger
LANGUAGE 'plpgsql' as '
declare
  max_version bigint;
  curr_row record;
begin
  max_version := (select max(m.version) from cf_wt_menu m);
  if (max_version is null) then
    max_version = 0;
  end if;
  if (tg_op = ''INSERT'') then
    curr_row = new;
  elsif (tg_op = ''DELETE'') then
    curr_row = old;
  end if;
  update cf_wt_menu set version = max_version + 1 where idofmenu = curr_row.idofmenu;
  return curr_row;
end ';

create trigger menu_org_inc_ver before insert or delete
on cf_wt_menu_org for each row execute procedure menu_org_inc_ver();

create function org_group_org_inc_ver() returns trigger
LANGUAGE 'plpgsql' as '
declare
  max_version bigint;
  curr_row record;
begin
  max_version := (select max(g.version) from cf_wt_org_groups g);
  if (max_version is null) then
    max_version = 0;
  end if;
  if (tg_op = ''INSERT'') then
    curr_row = new;
  elsif (tg_op = ''DELETE'') then
    curr_row = old;
  end if;
  update cf_wt_org_groups set version = max_version + 1 where idoforggroup = curr_row.idoforggroup;
  return curr_row;
end ';

create trigger org_group_org_inc_ver before insert or delete
on cf_wt_org_groups for each row execute procedure org_group_org_inc_ver();

create function cf_wt_org_group_complex_menu_ver_inc() returns trigger
LANGUAGE 'plpgsql' as '
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
  if (tg_op = ''INSERT'') then
    curr_row = new;
  elsif (tg_op = ''DELETE'') then
    curr_row = old;
  end if;
  update cf_wt_complexes set version = max_complex_version + 1 where idoforggroup = curr_row.idoforggroup;
  update cf_wt_menu set version = max_menu_version + 1 where idoforggroup = curr_row.idoforggroup;
  return curr_row;
end ';

create trigger cf_wt_org_group_complex_menu_ver_inc before insert or delete
on cf_wt_org_group_relations for each row execute procedure cf_wt_org_group_complex_menu_ver_inc();

create trigger org_group_org_inc_ver before insert or delete
on cf_wt_org_group_relations for each row execute procedure org_group_org_inc_ver();

create sequence cf_wt_revision_info_seq maxvalue 2147483647;

create table cf_wt_revision_info
(
  idofrevision bigint primary key,
  createdate   timestamp,
  idofuser     bigint,
  constraint cf_wt_revision_info_user_fk foreign key (idofuser) references cf_users
);

create table cf_wt_menu_aud
(
  idofrevision   bigint,
  revtype        smallint,
  idofmenu       bigint,
  menuname       varchar(128),
  begindate      timestamp,
  enddate        timestamp,
  createdate     timestamp,
  lastupdate     timestamp,
  idoforggroup   bigint,
  create_by_id   bigint,
  update_by_id   bigint,
  version        bigint,
  idofcontragent bigint,
  deletestate    integer,
  constraint cf_wt_menus_aud_pk primary key (idofrevision, idofmenu),
  constraint cf_wt_menus_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_menus_aud_menu_fk foreign key (idofmenu) references cf_wt_menu,
  constraint cf_wt_menus_aud_orggroup_fk foreign key (idoforggroup) references cf_wt_org_groups,
  constraint cf_wt_menus_aud_createuser_fk foreign key (create_by_id) references cf_users,
  constraint cf_wt_menus_aud_updateuser_fk foreign key (update_by_id) references cf_users,
  constraint cf_wt_menus_aud_contragent_fk foreign key (idofcontragent) references cf_contragents
);

create table cf_wt_menu_org_aud
(
  idofrevision bigint,
  revtype      smallint,
  idofmenu     bigint,
  idoforg      bigint,
  constraint cf_wt_menu_org_aud_pk primary key (idofrevision, idofmenu, idoforg),
  constraint cf_wt_menu_org_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_menu_org_aud_menu_fk foreign key (idofmenu) references cf_wt_menu,
  constraint cf_wt_menu_org_aud_org_fk foreign key (idoforg) references cf_orgs
);

create table cf_wt_complexes_aud
(
  idofrevision         bigint,
  revtype              smallint,
  idofcomplex          bigint,
  name                 varchar(128),
  price                numeric(10, 2),
  begindate            timestamp,
  enddate              timestamp,
  cyclemotion          integer,
  dayincycle           integer,
  version              bigint,
  guid                 varchar(36),
  createdate           timestamp,
  lastupdate           timestamp,
  create_by_id         bigint,
  update_by_id         bigint,
  deletestate          integer,
  idofcomplexgroupitem bigint,
  idofagegroupitem     bigint,
  idofdiettype         bigint,
  idofcontragent       bigint,
  idoforggroup         bigint,
  composite            bool,
  is_portal            bool,
  start_cycle_day      integer,
  barcode              varchar(16),
  comment              varchar(128),
  constraint cf_wt_complex_aud_pk primary key (idofrevision, idofcomplex),
  constraint cf_wt_complex_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_complex_aud_complex_fk foreign key (idofcomplex) references cf_wt_complexes,
  constraint cf_wt_complex_aud_createuser_fk foreign key (create_by_id) references cf_users,
  constraint cf_wt_complex_aud_updateuser_fk foreign key (update_by_id) references cf_users,
  constraint cf_wt_complex_aud_complexgroup_fk foreign key (idofcomplexgroupitem) references cf_wt_complex_group_items,
  constraint cf_wt_complex_aud_agegroup_fk foreign key (idofagegroupitem) references cf_wt_agegroup_items,
  constraint cf_wt_complex_aud_diet_fk foreign key (idofdiettype) references cf_wt_diet_type,
  constraint cf_wt_complex_aud_contragent_fk foreign key (idofcontragent) references cf_contragents,
  constraint cf_wt_complex_aud_orggroup_fk foreign key (idoforggroup) references cf_wt_org_groups
);

create table cf_wt_complexes_org_aud
(
  idofrevision bigint,
  revtype      smallint,
  idofcomplex  bigint,
  idoforg      bigint,
  constraint cf_wt_complex_org_aud_pk primary key (idofrevision, idofcomplex, idoforg),
  constraint cf_wt_complex_org_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_complex_org_aud_complex_fk foreign key (idofcomplex) references cf_wt_complexes,
  constraint cf_wt_complex_org_aud_org_fk foreign key (idoforg) references cf_orgs
);

create table cf_wt_org_groups_aud
(
  idofrevision   bigint,
  revtype        smallint,
  idoforggroup   bigint,
  nameoforggroup varchar(128),
  createdate     timestamp,
  lastupdate     timestamp,
  deletestate    integer,
  version        bigint,
  create_by_id   bigint,
  update_by_id   bigint,
  idofcontragent bigint,
  constraint cf_wt_org_groups_aud_pk primary key (idofrevision, idoforggroup),
  constraint cf_wt_org_groups_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_org_groups_aud_orggroup_fk foreign key (idoforggroup) references cf_wt_org_groups,
  constraint cf_wt_org_groups_aud_createuser_fk foreign key (create_by_id) references cf_users,
  constraint cf_wt_org_groups_aud_updateuser_fk foreign key (update_by_id) references cf_users,
  constraint cf_wt_org_groups_aud_contragent_fk foreign key (idofcontragent) references cf_contragents
);

create table cf_wt_org_group_relations_aud
(
  idofrevision bigint,
  revtype      smallint,
  idoforggroup bigint,
  idoforg      bigint,
  constraint cf_wt_org_group_relation_aud_pk primary key (idofrevision, idoforggroup, idoforg),
  constraint cf_wt_org_group_relation_aud_revision_fk foreign key (idofrevision) references cf_wt_revision_info,
  constraint cf_wt_org_group_relation_aud_orggroup_fk foreign key (idoforggroup) references cf_wt_org_groups,
  constraint cf_wt_org_group_relation_aud_org_fk foreign key (idoforg) references cf_orgs
);


--879
ALTER TABLE public.cf_clients_mobile_history ADD oldssoid varchar NULL;

--862
update cf_users set rolename ='Настраиваемая роль' where rolename = '1. Настраиваемая роль';
update cf_users set rolename ='Администратор' where rolename = '2. Администратор';
update cf_users set rolename ='Поставщик питания' where rolename = '3. Поставщик питания';
update cf_users set rolename ='Мониторинг' where rolename = '4. Мониторинг';
update cf_users set rolename ='Администратор ИБ' where rolename = '5. Администратор ИБ';
update cf_users set rolename ='Отчетность поставщика питания' where rolename = '6. Отчетность поставщика питания';
update cf_users set rolename ='Оператор по картам' where rolename = '7. Оператор по картам';
update cf_users set rolename ='Директор школы' where rolename = '8. Директор школы';
update cf_users set rolename ='Заведующий производством' where rolename = '9. Заведующий производством';
update cf_users set rolename ='Оператор ИС' where rolename = '10. Оператор ИС';
update cf_users set rolename ='Классный руководитель' where rolename = '11. Классный руководитель';
update cf_users set rolename ='Классный руководитель с оплатой питания' where rolename = '12. Классный руководитель с оплатой питания';

--865
delete from CF_Permissions cp
where cp.idofuser in (select cu.idofuser from cf_users cu where cu.idofrole = 5)
      and cp.idoffunction in (select cf.idoffunction from cf_functions cf
where cf.functionname in ('enterEventRprt', 'financialControl', 'informRprts', 'cardRprts', 'onlineRprtBenefit', 'onlineRprtActivity',
                          'clientsBenefitsRprt','manualRprt', 'messageARMinOO'));

--! ФИНАЛИЗИРОВАН 26.01.2021, НЕ МЕНЯТЬ