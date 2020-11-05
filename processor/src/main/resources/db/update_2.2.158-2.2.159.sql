--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.159

--Индекс на версию в таблице выходных дней
CREATE INDEX cf_specialdates_version_idx ON cf_specialdates USING btree (version);

--поле статуса предзаказа
alter table cf_preorder_complex add column state integer not null default 0;

--календарь на цикл меню
create table cf_menus_calendar
(
  idofmenuscalendar bigserial NOT NULL,
  guid character varying(36) NOT NULL,
  idoforg bigint NOT NULL,
  idofmenu bigint,
  startdate bigint,
  enddate bigint,
  sixworkdays integer,
  version bigint,
  deletedstate integer not null default 0,
  createddate bigint,
  lastupdate bigint,
  CONSTRAINT cf_menus_calendar_pk PRIMARY KEY (idofmenuscalendar)
);
CREATE INDEX cf_menus_calendar_version_idx ON cf_menus_calendar USING btree (version);
CREATE INDEX cf_menus_calendar_idoforg_idx ON cf_menus_calendar USING btree (idoforg);

--даты календаря на цикл меню
create table cf_menus_calendar_dates
(
  idofmenuscalendardate bigserial NOT NULL,
  idofmenuscalendar bigint,
  date bigint NOT NULL,
  isweekend integer,
  comment character varying(256),
  CONSTRAINT cf_menus_calendar_dates_pk PRIMARY KEY (idofmenuscalendardate),
  CONSTRAINT cf_menus_calendar_dates_menucalendar_fk FOREIGN KEY (idofmenuscalendar)
  REFERENCES cf_menus_calendar (idofmenuscalendar) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_menus_calendar_dates_idofmenuscalendar_idx ON cf_menus_calendar_dates USING btree (idofmenuscalendar);

--! ФИНАЛИЗИРОВАН 19.06.2018, НЕ МЕНЯТЬ