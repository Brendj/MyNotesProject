--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 253

create index cf_wt_dish_categoryitem_relationships_idofdish_idx ON cf_wt_dish_categoryitem_relationships USING btree (idofdish);
create index cf_wt_dish_categoryitem_relationships_idofcategoryitem_idx ON cf_wt_dish_categoryitem_relationships USING btree (idofcategoryitem);

create index cf_wt_dish_groupitem_relationships_idofdish_idx ON cf_wt_dish_groupitem_relationships USING btree (idofdish);
create index cf_wt_dish_groupitem_relationships_idofgroupitem_idx ON cf_wt_dish_groupitem_relationships USING btree (idofgroupitem);

ALTER TABLE cf_orgregistrychange ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN addressfrom TYPE character varying(512),
  ALTER COLUMN short_address TYPE character varying(512),
  ALTER COLUMN short_addressfrom TYPE character varying(512);

ALTER TABLE cf_orgregistrychange_item ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN addressfrom TYPE character varying(512),
  ALTER COLUMN short_address TYPE character varying(512),
  ALTER COLUMN short_addressfrom TYPE character varying(512);

ALTER TABLE cf_orgs ALTER COLUMN address TYPE character varying(512),
  ALTER COLUMN shortaddress TYPE character varying(512);

insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 34 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 36 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 39 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 48 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 49 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 51 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 52 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 54 from cf_users cu where cu.idofrole = 5;
insert into CF_Permissions (idofuser, idoffunction)
  select cu.idofuser, 56 from cf_users cu where cu.idofrole = 5;

--917
delete from cf_client_guardian_notificationsettings where notifytype = 1700000000;
delete from cf_clientsnotificationsettings where notifytype = 1700000000;

insert into cf_client_guardian_notificationsettings (idofclientguardian, notifytype, createddate) select idofclientguardian,1700000000,1613658652000 from  cf_client_guardian_notificationsettings where notifytype=1100000000;
insert into cf_clientsnotificationsettings (idofclient, notifytype, createddate) select idofclient,1700000000,1613658652000 from  cf_clientsnotificationsettings where notifytype=1100000000;

--! ФИНАЛИЗИРОВАН 18.02.2021, НЕ МЕНЯТЬ