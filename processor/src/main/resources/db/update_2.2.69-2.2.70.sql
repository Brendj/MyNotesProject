--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.70

-- Изменения для новых выгрузок для СМА
alter table cf_orgs add column OrganizationStatus integer NOT NULL DEFAULT 0;

CREATE TABLE cf_bbk (
  idOfBbk BIGSERIAL NOT NULL,
  orgOwner BIGINT,
  guid CHARACTER VARYING(36) UNIQUE NOT NULL,
  deletedState BOOLEAN NOT NULL DEFAULT FALSE,
  globalVersion BIGINT,
  globalVersionOnCreate BIGINT NOT NULL,
  createdDate BIGINT NOT NULL,
  lastUpDate BIGINT,
  deleteDate BIGINT,
  name CHARACTER VARYING(127) NOT NULL,
  note CHARACTER VARYING(255),
  CONSTRAINT cf_bbk_pk PRIMARY KEY (idOfBbk),
  CONSTRAINT cf_bbk_guid_key UNIQUE (guid)
);

-- таблица для библиотеки
-- name String - имя
-- code String - код
CREATE TABLE cf_bbk_details (
  idOfBbkDetails BIGSERIAL NOT NULL,
  orgOwner BIGINT,
  guid CHARACTER VARYING(36) UNIQUE NOT NULL,
  deletedState BOOLEAN NOT NULL DEFAULT FALSE,
  globalVersion BIGINT,
  globalVersionOnCreate BIGINT NOT NULL,
  createdDate BIGINT NOT NULL,
  lastUpDate BIGINT,
  deleteDate BIGINT,
  idOfBbk BIGINT,
  idOfParentBbkDetails BIGINT,
  code CHARACTER VARYING(20) NOT NULL,
  name CHARACTER VARYING(255) NOT NULL,
  SendAll integer DEFAULT 0,
  CONSTRAINT cf_bbk_details_pk PRIMARY KEY (idOfBbkDetails),
  CONSTRAINT cf_bbk_details_guid_key UNIQUE (guid)
);

ALTER TABLE cf_publications ADD COLUMN idOfLang bigint;
ALTER TABLE cf_publications ADD COLUMN idOfBBKDetails bigint;

--! ФИНАЛИЗИРОВАН (Сунгатов, 140819) НЕ МЕНЯТЬ