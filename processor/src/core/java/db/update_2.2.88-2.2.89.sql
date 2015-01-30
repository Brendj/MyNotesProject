--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.88
--! Изменение таблицы "Агрегирование данных отчет Xml"




CREATE TABLE cf_orgregistrychange (
  idoforgregistrychange BIGSERIAL,
  idoforg BIGINT,
  createdate BIGINT NOT NULL,
  operationtype INTEGER NOT NULL,

  organizationtype INTEGER NOT NULL DEFAULT 0,
  organizationtypefrom INTEGER,
  shortname VARCHAR(255) NOT NULL,
  shortnamefrom VARCHAR(255),
  officialname VARCHAR(255) NOT NULL,
  officialnamefrom VARCHAR(255),

  applied BOOLEAN NOT NULL DEFAULT FALSE,

  address VARCHAR(255) NOT NULL,
  addressfrom VARCHAR(255),
  city VARCHAR(255) NOT NULL,
  cityfrom VARCHAR(255),
  region VARCHAR(255) NOT NULL,
  regionfrom VARCHAR(255),

  unom BIGINT,
  unomfrom BIGINT,
  unad BIGINT,
  unadfrom BIGINT,

  guid VARCHAR(255) NOT NULL,
  guidfrom VARCHAR(255),
  additionalid BIGINT NOT NULL,

  CONSTRAINT cf_orgregistrychange_pk PRIMARY KEY (idoforgregistrychange)
);