--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.139

alter table cf_history_card add column idofuser bigint;

--Новые поля для статуса и версии записи в таблице событий от внешних систем
alter table cf_externalevents add column evtStatus integer,
  add column version bigint not null default -1,
  alter column orgcode type character varying(25);

CREATE INDEX cf_externalevents_version_idx ON cf_externalevents USING btree (version);

--! ФИНАЛИЗИРОВАН (Семенов, 170817) НЕ МЕНЯТЬ