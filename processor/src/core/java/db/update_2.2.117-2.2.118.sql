--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.118

ALTER TABLE CF_RegistryChange ADD COLUMN guardiansCount integer;

--Получение ид поля таблицы ClientGuardian через генератор
ALTER TABLE CF_Generators ADD COLUMN idOfClientGuardian BIGINT NOT NULL DEFAULT 0;
update cf_generators set idOfClientGuardian = (select  case when max(idOfClientGuardian) is null THEN 0 else (max(idOfClientGuardian)+1) end  from cf_client_guardian );

--измеенние типа поля с bigserial на bigint
ALTER TABLE cf_client_guardian ALTER COLUMN IdOfClientGuardian DROP DEFAULT;

--новые правила оповещения
CREATE TABLE cf_client_guardian_notificationsettings
(
  idofsetting bigserial NOT NULL,
  idofclientguardian bigint NOT NULL,
  notifytype bigint NOT NULL,
  createddate bigint NOT NULL,
  CONSTRAINT cf_cg_notificationsetting_pk PRIMARY KEY (idofsetting),
  CONSTRAINT cf_cg_notificationsetting_notify UNIQUE (idofclientguardian, notifytype),
  CONSTRAINT cf_cg_notificationsetting_idofclient_fk FOREIGN KEY (idofclientguardian)
  REFERENCES cf_client_guardian (idofclientguardian) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);

--ПРЕДВАРИТЕЛЬНЫЙ скрипт апдейта данных по флагам типов уведомлений
--todo Уточнить, надо ли создавать строки для неактуальных опекунских связей (disabled)
insert into cf_client_guardian_notificationsettings(idofclientguardian, notifytype, createddate)
  select g.idofclientguardian, s.notifytype, s.createddate
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofchildren
  where g.deletedstate = false and s.notifytype <> 1200000000
    union
  select g.idofclientguardian, 1210000000, s.createddate
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofchildren
  where g.deletedstate = false and s.notifytype = 1200000000
    union
  select g.idofclientguardian, 1220000000, s.createddate
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofchildren
  where g.deletedstate = false and s.notifytype = 1200000000
    union
  select g.idofclientguardian, 1230000000, s.createddate
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofchildren
  where g.deletedstate = false and s.notifytype = 1200000000
    order by idofclientguardian, notifytype;

--После импорта данных создаем индексы на таблицу флагов оповещений
CREATE INDEX cf_client_guardian_notificationsettings_idofclientguardian_idx
ON cf_client_guardian_notificationsettings USING btree (idofclientguardian);

ALTER TABLE cf_clientphoto
  ADD COLUMN iscanceled INTEGER NOT NULL DEFAULT FALSE,
  ADD COLUMN isapproved INTEGER NOT NULL DEFAULT FALSE,
  ADD COLUMN idofclientguardian BIGINT,
  ADD COLUMN lastproceederror CHARACTER VARYING(256),
  ADD COLUMN version BIGINT NOT NULL DEFAULT 1;
CREATE INDEX cf_clientphoto_version_idx ON cf_clientphoto USING BTREE (version);

--Флаг "Сверка фотографий" у организации
ALTER TABLE cf_orgs ADD COLUMN photoregistrydirective integer NOT NULL DEFAULT 0;