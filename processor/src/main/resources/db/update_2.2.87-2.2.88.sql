--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.87
-- Таблица для хранения синхронизации с Реестрами по ОО
ALTER TABLE cf_daily_formation_registries DROP COLUMN idOfDailyFormationRegistries;
ALTER TABLE cf_daily_formation_registries ADD COLUMN idOfRegistries BIGSERIAL;
ALTER TABLE cf_daily_formation_registries ADD CONSTRAINT cf_daily_formation_registries_pk PRIMARY KEY (idOfRegistries);

ALTER TABLE cf_daily_formation_registries DROP COLUMN idOfContragent;
ALTER TABLE cf_daily_formation_registries ADD COLUMN idOfContragent BIGINT;
ALTER TABLE cf_daily_formation_registries ADD CONSTRAINT cf_daily_org_registries_idOfContragent_fk FOREIGN KEY (idOfContragent) REFERENCES cf_contragents (idofcontragent);

ALTER TABLE cf_daily_formation_registries DROP COLUMN orgNum;
ALTER TABLE cf_daily_formation_registries DROP COLUMN idOfOrg;
ALTER TABLE cf_daily_formation_registries DROP COLUMN officialName;
ALTER TABLE cf_daily_formation_registries DROP COLUMN address;
ALTER TABLE cf_daily_formation_registries DROP COLUMN totalBalance;
ALTER TABLE cf_daily_formation_registries DROP COLUMN rechargeAmount;
ALTER TABLE cf_daily_formation_registries DROP COLUMN salesAmount;

ALTER TABLE cf_daily_formation_registries DROP COLUMN generateddate;
ALTER TABLE cf_daily_formation_registries DROP COLUMN contragentname;

ALTER TABLE cf_daily_formation_registries ADD COLUMN  contragentName VARCHAR(255);
ALTER TABLE cf_daily_formation_registries ADD COLUMN  generatedDate BIGINT NOT NULL;

--! Таблица "Агрегирования данных по организациям отчет xml"
CREATE TABLE cf_daily_org_registries (
  idOfDailyOrgRegistries BIGSERIAL,
  idOfRegistries BIGINT,
  idOfOrg BIGINT,
  orgNum CHARACTER VARYING(30),
  officialName CHARACTER VARYING(128),
  address CHARACTER VARYING(128),
  createdDate BIGINT NOT NULL,
  totalBalance BIGINT NOT NULL,
  rechargeAmount BIGINT NOT NULL,
  salesAmount BIGINT NOT NULL,
  CONSTRAINT cf_daily_org_registries_pk PRIMARY KEY (idOfDailyOrgRegistries),
  CONSTRAINT cf_daily_org_registries_idOfOrg_fk FOREIGN KEY (idOfOrg) REFERENCES cf_orgs (idOfOrg),
  CONSTRAINT cf_daily_org_registries_idOfRegistries_fk FOREIGN KEY (idOfRegistries) references cf_daily_formation_registries(idOfRegistries)
);


--! ФИНАЛИЗИРОВАН (Сунгатов, 150130) НЕ МЕНЯТЬ
