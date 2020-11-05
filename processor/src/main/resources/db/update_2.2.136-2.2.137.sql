--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.137

--Индекс по ид. орг в таблице мигрантов (запрос в синхре AccInc)
CREATE INDEX cf_migrants_org_idx ON cf_migrants USING btree (idoforgvisit);

--Служебная таблица для предотвращения одновременных сеансов синх-ии от одной ОО на разных серверах
create table cf_org_lock
(
  idoforg bigint not null,
  sync integer not null default 0,
  datetime bigint,
  constraint cf_org_lock_pk primary key (idoforg)
);

--Новая таблица проходов по карте вне школы
CREATE TABLE cf_externalevents
(
  idofexternalevent bigserial NOT NULL,
  orgcode character varying(20),
  orgname character varying(256),
  entername character varying(256),
  evttype integer NOT NULL,
  idofclient bigint NOT NULL,
  evtdatetime bigint NOT NULL,
  CONSTRAINT cf_externalevents_pk PRIMARY KEY (idofexternalevent),
  CONSTRAINT cf_externalevents_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient)
  MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

CREATE INDEX cf_externalevents_datetime_event_idx ON cf_externalevents USING btree (evtdatetime);
CREATE INDEX cf_externalevents_idofclient_idx ON cf_externalevents USING btree (idofclient);

ALTER TABLE cf_visitors ALTER COLUMN position TYPE character varying(256);

truncate table cf_threaddumps;

--! ФИНАЛИЗИРОВАН (Семенов, 040717) НЕ МЕНЯТЬ