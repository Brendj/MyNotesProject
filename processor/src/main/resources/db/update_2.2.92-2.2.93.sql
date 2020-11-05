--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.92


-- --! Изменение таблицы cf_account_operations добавление поля IdOfClientPayment
ALTER TABLE cf_account_operations ADD COLUMN IdOfClientPayment bigint;

-- --! Добавление поля notifyviapush в таблицу cf_clients
ALTER TABLE cf_clients ADD COLUMN notifyviapush smallint NOT NULL DEFAULT 0;

--!отчет xml
alter table cf_daily_org_registries alter column officialname type VARCHAR(256);
alter table cf_daily_org_registries alter column address type VARCHAR(256);





--Таблица хранения данных о синхронизации
CREATE TABLE cf_contragents_sync
(
  idofcontragent bigint NOT NULL,
  version bigint NOT NULL,

  lastrnipupdate character varying(30) DEFAULT '',


  CONSTRAINT cf_contragents_sync_pk PRIMARY KEY (idofcontragent),
  CONSTRAINT cf_contragents_sync FOREIGN KEY (idofcontragent)
  REFERENCES cf_contragents (idofcontragent) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);


insert into cf_contragents_sync
(idofcontragent, version, lastrnipupdate)
  select  idofcontragent, version,  lastrnipupdate
  from cf_contragents;

alter table cf_contragents drop column lastrnipupdate;


--! ФИНАЛИЗИРОВАН (Сунгатов, 150326) НЕ МЕНЯТЬ