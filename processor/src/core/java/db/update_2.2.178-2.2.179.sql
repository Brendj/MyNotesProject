--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.179

--таблица для логирования входящих пакетов в соап сервисах
CREATE TABLE cf_log_services
(
  idOfLogPacket bigserial NOT NULL,
  idOfService integer NOT NULL,
  createdDate bigint NOT NULL,
  packetBody text,
  CONSTRAINT cf_log_services_pk PRIMARY KEY (idOfLogPacket)
);

--новое поле state в таблице регуляров
alter table cf_regular_preorders
  add column state integer NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН 16.04.2019, НЕ МЕНЯТЬ