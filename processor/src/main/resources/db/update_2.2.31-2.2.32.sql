--! Пока скрипт не винализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся

-- Пакет обновлений 2.2.32
-- Добавлена настройка текстого сообщения для принтера
--! в таблицу cf_ecafesettings добавлено строковое необязательное значение. занесен в ecafe_processor_derby_postgre.sql
ALTER TABLE CF_ECafeSettings ADD COLUMN SettingText character varying(128);

-- Добавлена сортировка клиентов по номеру контракта
-- Добавлен GUID (последний столбец) клиента в выгрузку клиентов в CSV
-- Исправлено обновление данных клиента из файла

-- История миграции клиента
--!
create table CF_ClientMigrationHistory
(
  IdOfClientMigration bigserial,
  IdOfClient bigint not null,
  IdOfOrg bigint not null,
  RegistrationDate bigint not null,
  CONSTRAINT CF_ClientMigrationHistory_pk PRIMARY KEY (IdOfClientMigration)
);

--! ФИНАЛИЗИРОВАН (Кадыров, 130121) НЕ МЕНЯТЬ