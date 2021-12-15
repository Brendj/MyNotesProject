--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.96


ALTER TABLE cf_cards ADD COLUMN IdOfOrg BIGINT;
ALTER TABLE cf_cards ADD COLUMN IdOfVisitor BIGINT;
ALTER TABLE cf_cards alter IdOfClient drop not null;


ALTER TABLE cf_enterevents ADD COLUMN IdOfClientGroup BIGINT;
ALTER TABLE cf_orders ADD COLUMN IdOfClientGroup BIGINT;
ALTER TABLE cf_orders ADD COLUMN IdOfPayForClient BIGINT;

-- Постоянное хранилище подсчитанных показателей синхронизации по ОУ
CREATE TABLE cf_synchistory_calc2 (
  idOfSyncHistoryCalc bigint NOT NULL,
  idOfOrg bigint NOT NULL,
  calcDateAt bigint NOT NULL,
  calcType int NOT NULL,
  calcValue varchar NOT NULL,

  CONSTRAINT cf_synchistory_calc2_pk PRIMARY KEY (idOfSyncHistoryCalc)
);

ALTER TABLE CF_Generators ADD COLUMN idOfSyncHistoryCalc BIGINT NOT NULL DEFAULT 0;
update cf_generators set idOfSyncHistoryCalc= (select  case when max(idOfSyncHistoryCalc) is null THEN 0 else (max(idOfSyncHistoryCalc)+1) end  from cf_synchistory_calc2 );



--обновляем орг ид у карт
update cf_cards set idoforg=s.i
from (select c.idofcard a, cl.idoforg as i from cf_cards c inner join cf_clients cl on c.idofclient = cl.idofclient) as s
where s.a = cf_cards.idofcard;



alter table cf_orgs_sync add column LastAccRegistrySync bigint  ;


update cf_cards
set state =1
where state =2;

ALTER TABLE cf_goods_requests_positions ADD COLUMN notified BOOLEAN DEFAULT TRUE;


ALTER TABLE cf_orgs ADD COLUMN OneActiveCard integer;

update cf_orgs set oneactivecard=1;

update cf_cards  set idoforg=c.idoforg from  cf_clients c
where cf_cards.idofclient = c.idofclient;


alter table cf_clientgroups alter column groupname TYPE  character varying  (256);
--! ФИНАЛИЗИРОВАН (Сунгатов, 150526) НЕ МЕНЯТЬ