--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 917,911

--911
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