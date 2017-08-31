--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.141

--Таблица для расписаний комплексов
create table cf_complex_schedules
(
  guid character varying(40) not null,
  idoforg bigint not null,
  idofcomplex bigint not null,
  intervalfrom integer,
  intervalto integer,
  version bigint not null,
  idoforgcreated bigint not null,
  constraint cf_complex_schedules_pk primary key (guid),
  CONSTRAINT cf_complex_schedules_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_complex_schedules_idoforgcreated_fk FOREIGN KEY (idoforgcreated)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);