--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.85

--! Добавление признаков и инф за кого сделали отметку
-- Таблица "Агрегирование данных отчет Xml"
CREATE TABLE cf_daily_formation_registries (
  idOfDailyFormationRegistries BIGSERIAL,
  generatedDate BIGINT NOT NULL,
  idOfContragent BIGINT NOT NULL,
  contragentName VARCHAR(255),
  orgNum VARCHAR(30),
  idOfOrg BIGINT NOT NULL,
  officialName VARCHAR(128),
  address VARCHAR(128),
  totalBalance BIGINT NOT NULL,
  rechargeAmount BIGINT NOT NULL,
  salesAmount BIGINT NOT NULL,
  CONSTRAINT cf_daily_formation_registries_pk PRIMARY KEY (idOfDailyFormationRegistries)
);
