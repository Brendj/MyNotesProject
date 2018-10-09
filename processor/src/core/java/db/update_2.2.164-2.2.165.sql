--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.165

--таблица блокировок баланса после перехода к другому поставщику
ALTER TABLE cf_clientbalance_hold
  add column declarerInn character varying(20),
  add column declarerAccount character varying(20),
  add column declarerBank character varying(90),
  add column declarerBik character varying(20),
  add column declarerCorrAccount character varying(20);

-- Флаг "Режим выдачи нескольких активных карт"
ALTER TABLE cf_clients
ADD COLUMN multiCardMode INTEGER;

-- Флаг "Использование обучающимися нескольких идентификаторов в ОО"
ALTER TABLE cf_orgs
ADD COLUMN multiCardModeEnabled INTEGER;
