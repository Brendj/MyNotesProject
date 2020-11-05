--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.93


-- Временное хранение данных о быстрой и полной синхронизации с ОУ
CREATE TABLE cf_synchistory_daily (
  idOfSync CHARACTER VARYING(30) NOT NULL,
  idOfOrg bigint NOT NULL,
  syncDate bigint NOT NULL,

  CONSTRAINT cf_synchistory_daily_pk PRIMARY KEY (idoforg, syncdate)
);

-- Постоянное хранилище подсчитанных показателей синхронизации по ОУ
CREATE TABLE cf_synchistory_calc (
  idOfOrg bigint NOT NULL,
  calcDateAt bigint NOT NULL,
  calcType int NOT NULL,
  calcValue bigint NOT NULL,

  CONSTRAINT cf_synchistory_calc_pk PRIMARY KEY (idOfOrg, calcDateAt, calcType)
);


ALTER TABLE CF_Generators ADD COLUMN IdOfAccountOperations BIGINT NOT NULL DEFAULT 0;
update cf_generators set IdOfAccountOperations= (select  case when max(idofaccountoperation) is null THEN  0 else (max(idofaccountoperation)+1) end  from cf_account_operations );


--! ФИНАЛИЗИРОВАН (Сунгатов, 150402) НЕ МЕНЯТЬ