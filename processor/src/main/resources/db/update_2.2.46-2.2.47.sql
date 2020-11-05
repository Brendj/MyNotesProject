--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.47

CREATE TABLE cf_temporary_orders (
  IdOfOrg bigint not null,
  IdOfClient bigInt not null,
  IdOfComplex int not null,
  PlanDate bigint not null,
  Action int not null,
  IdOfReplaceClient bigInt,
  CreationDate bigint not null,
  ModificationDate bigint,
  IdOfOrder bigint default null,
  IdOfUser bigint not null,
  CONSTRAINT cf_temporary_orders_pk PRIMARY KEY (IdOfOrg, IdOfClient, IdOfComplex, PlanDate),
  CONSTRAINT cf_temporary_orders_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg),
  CONSTRAINT cf_temporary_orders_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

CREATE TABLE cf_thin_client_users (
  IdOfClient bigint not null,
  UserName varchar(64) not null,
  Password varchar(128) not null,
  Role int not null default 1,
  CreationDate bigint not null,
  ModificationDate bigint,
  CONSTRAINT cf_thin_client_users_pk PRIMARY KEY (UserName),
  CONSTRAINT cf_thin_client_users_client FOREIGN KEY (IdOfClient) REFERENCES cf_clients (IdOfClient)
);

-- Поправка бага ECAFE-1179
ALTER TABLE cf_reportinfo ALTER COLUMN reportname TYPE character varying(512);

-- Таблица для хранения поступивших из Реестров изменений
create table CF_RegistryChange (
  IdOfRegistryChange bigserial not null,
  IdOfOrg bigint not null,
  CreateDate bigint not null,
  ClientGUID varchar(40) not null,
  FirstName varchar(64) not null,
  SecondName varchar(128) not null,
  Surname varchar(128) not null,
  GroupName varchar(64) not null,
  FirstNameFrom varchar(64),
  SecondNameFrom varchar(128),
  SurnameFrom varchar(128),
  GroupNameFrom varchar(64),
  IdOfMigrateOrgTo bigint,
  IdOfMigrateOrgFrom bigint,
  IdOfClient bigint,
  Operation integer not null,
  Applied boolean not null default false,
  CONSTRAINT cf_registrychange_pk PRIMARY KEY (IdOfRegistryChange),
  CONSTRAINT cf_registrychange_org FOREIGN KEY (IdOfOrg) REFERENCES cf_orgs (IdOfOrg)
);

-- Таблица для хранения ошибок по поступившим из Реестров изменениям
create table CF_RegistryChange_Errors (
  IdOfRegistryChangeError bigserial not null,
  IdOfOrg bigint not null,
  RevisionCreateDate bigint not null,
  Error varchar(256) not null,
  Comment varchar(256) default '',
  CommentAuthor VARCHAR(64) default '',
  CreateDate bigint not null,
  CommentCreateDate bigint,
  CONSTRAINT CF_RegistryChange_Errors_pk PRIMARY KEY (IdOfRegistryChangeError)
);
--! ФИНАЛИЗИРОВАН (Кадыров, 131021) НЕ МЕНЯТЬ