--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.91
--! Таблица "Бронирование литературы из личного кабинета"

CREATE TABLE cf_order_publications
(
  idoforder bigserial NOT NULL,
  idofpublication bigint NOT NULL,
  idofclient bigint NOT NULL,
  status character varying(30),
  globalversion bigint,
  orgowner bigint,
  deletedstate boolean NOT NULL DEFAULT false,
  guid character varying(36) NOT NULL,
  lastupdate bigint,
  deletedate bigint,
  createddate bigint NOT NULL,
  sendall integer DEFAULT 0,
  globalversiononcreate bigint,
  CONSTRAINT cf_order_publications_pkey PRIMARY KEY (idoforder),
  CONSTRAINT cf_order_clients_idofclient_fkey FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_order_publications_idofpublication_fkey FOREIGN KEY (idofpublication)
  REFERENCES cf_publications (idofpublication) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_order_publications_guid_key UNIQUE (guid)
);
