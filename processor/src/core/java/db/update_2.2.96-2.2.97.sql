--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.96


ALTER TABLE cf_cards ADD COLUMN IdOfOrg BIGINT;
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

