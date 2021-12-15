--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.144

--таблица разрешений сверки с клиента
create table cf_org_syncs_registry
(
  idoforg bigint not null,
  lastsyncstart bigint not null,
  lastsyncend bigint not null,
  constraint cf_org_sync_registry_pk primary key (idoforg),
  constraint cf_org_sync_registry_idoforg_fk foreign key(idoforg)
  references cf_orgs(idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--! ФИНАЛИЗИРОВАН (Семенов, 171004) НЕ МЕНЯТЬ

