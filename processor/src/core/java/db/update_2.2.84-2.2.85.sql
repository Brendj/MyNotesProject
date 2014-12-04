--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.84

--! Добавление признаков и инф за кого сделали отметку
ALTER TABLE cf_enterevents ADD COLUMN childpasschecker INTEGER;
ALTER TABLE cf_enterevents ADD COLUMN childpasscheckerid BIGINT;


ALTER TABLE cf_clientmigrationhistory ADD COLUMN idofoldorg BIGINT;
ALTER TABLE cf_clientmigrationhistory ADD COLUMN idofoldcontragent BIGINT;
ALTER TABLE cf_clientmigrationhistory ADD COLUMN IdOfNewContragent BIGINT;
ALTER TABLE cf_clientmigrationhistory ADD COLUMN balance BIGINT;
ALTER TABLE cf_clientmigrationhistory ADD COLUMN oldGroupName varchar(255);
ALTER TABLE cf_clientmigrationhistory ADD COLUMN newGroupName varchar(255);
ALTER TABLE cf_clientmigrationhistory ADD COLUMN comment varchar(255);


-- Добавлена таблица "Перемещения внутри ОО".
CREATE TABLE CF_ClientGroup_MigrationHistory (
  idOfGroupClientMigration bigserial,
  IdOfClient bigint not null,
  IdOfOrg bigint not null,
  RegistrationDate bigint not null,

  oldGroupId bigint,
  oldGroupName varchar(255),
  newGroupId bigint ,
  newGroupName varchar(255),
  comment varchar(255) ,
  CONSTRAINT CF_ClientMigrationHistory_pk PRIMARY KEY (idOfGroupClientMigration)
);
