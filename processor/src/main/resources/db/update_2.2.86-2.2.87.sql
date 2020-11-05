--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.86

--Таблица хранения данных о синхронизации
CREATE TABLE cf_orgs_sync
(
  idoforg bigint NOT NULL,
  version bigint NOT NULL,

  idofpacket bigint,

  lastsucbalancesync bigint,
  lastunsucbalancesync bigint,

  clientversion character varying(16),
  remoteaddress character varying(20),


  CONSTRAINT cf_orgs_sync_pk PRIMARY KEY (idoforg),
  CONSTRAINT cf_orgs_sync FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
)
WITH (
OIDS=FALSE
);


insert into cf_orgs_sync
(idoforg, version, IdOfPacket,lastSucBalanceSync,ClientVersion,RemoteAddress,lastUnSucBalanceSync )
  select  idoforg, version,  IdOfPacket,lastSucBalanceSync,ClientVersion,RemoteAddress,lastUnSucBalanceSync
  from cf_orgs;

alter table cf_orgs drop column IdOfPacket
  , drop column lastsucbalancesync
  , drop column clientversion
  , drop column remoteaddress
  , drop column lastunsucbalancesync

--! ФИНАЛИЗИРОВАН (Сунгатов, 150119) НЕ МЕНЯТЬ

