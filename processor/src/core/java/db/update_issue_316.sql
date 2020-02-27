--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 316

-- Таблица для блокированных на арме предзаказах
CREATE TABLE cf_preorder_status (
  idOfPreorderStatus bigserial NOT NULL,
  date bigint,
  guid character varying(36),
  status integer,
  storno integer NOT NULL,
  version bigint,
  deletedState integer,
  createddate bigint NOT NULL,
  lastupdate bigint,
  idoforgoncreate bigint NOT NULL,
  CONSTRAINT cf_preorder_block_pk PRIMARY KEY (idOfPreorderStatus)
);

CREATE UNIQUE INDEX cf_preorder_status_guid_date_unique
ON cf_preorder_status
USING btree
(guid, date);