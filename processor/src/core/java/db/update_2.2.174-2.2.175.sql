--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.175

--Новый производственный календарь
CREATE TABLE cf_production_calendar
(
  idOfProductionCalendar bigserial,
  day bigint,
  createdDate bigint,
  lastUpdate bigint,
  CONSTRAINT cf_production_calendar_pk PRIMARY KEY (idOfProductionCalendar),
  CONSTRAINT cf_production_calendar_day_unique UNIQUE (day)
);

--поле "Источник"
alter table cf_client_dtiszn_discount_info
  add column source character varying(5);
