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