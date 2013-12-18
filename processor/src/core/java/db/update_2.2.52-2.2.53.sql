--! Пока скрипт не финализирован рекоментовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.53

CREATE TABLE cf_do_org_current_version (
  IdDOOrgCurrentVersion bigserial NOT NULL,
  ObjectId integer not null,
  IdOfOrg bigint not null,
  LastVersion bigint not null,
  CONSTRAINT cf_do_org_current_version_pk PRIMARY KEY (IdDOOrgCurrentVersion)
);
