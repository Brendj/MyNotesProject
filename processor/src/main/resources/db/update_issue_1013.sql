-- Пакет обновлений issue 1013

insert into cf_clientgroups(idoforg, idofclientgroup, groupname)
select t.idoforg, 1100000060, 'Выбывшие' from cf_orgs t
where not exists(select * from cf_clientgroups cg where cg.idoforg = t.idoforg and cg.idofclientgroup = 1100000060);
