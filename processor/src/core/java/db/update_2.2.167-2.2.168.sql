--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.168

alter table cf_users
  ADD COLUMN idOfGroup bigint,
  ADD COLUMN isGroup boolean NOT NULL DEFAULT false,
  ADD CONSTRAINT cf_users_idofserrole_fk FOREIGN KEY (idofgroup)
  REFERENCES cf_users (idofuser) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

create table cf_usergroups (
  idOfUserGroup bigserial NOT NULL,
  groupName character varying(64),
  createdDate bigint NOT NULL,
  lastUpdate bigint,
  CONSTRAINT cf_usergroups_pk PRIMARY KEY (idOfUserGroup)
) WITH (
OIDS = FALSE
);