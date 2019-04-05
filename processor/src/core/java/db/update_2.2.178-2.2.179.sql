--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.178

CREATE TABLE cf_log_services
(
  idOfLogPacket bigserial NOT NULL,
  idOfService integer NOT NULL,
  createdDate bigint NOT NULL,
  packetBody text,
  CONSTRAINT cf_log_services_pk PRIMARY KEY (idOfLogPacket)
);