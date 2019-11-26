--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 200

-- 338: Добавление поля времени создания токена для облегчения мониторинга
ALTER TABLE cf_linking_tokens_for_smartwatch
  ADD COLUMN createDate BIGINT;

-- 318: Таблица для взаимодействия с сервисом ЕМИАС
CREATE TABLE cf_emias (
  id   				bigserial,
  guid         		varchar,
  idEventEMIAS 		int8,
  typeEventEMIAS 		int8,
  dateLiberate		int8,
  startDateLiberate	int8,
  endDateLiberate		int8,
  createDate			int8,
  updateDate			int8,
  accepted			bool,
  deletedemiasid		int8,
  version				int8
);

--! ФИНАЛИЗИРОВАН 25.11.2019, НЕ МЕНЯТЬ