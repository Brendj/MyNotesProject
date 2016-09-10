--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.120

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

--скрипт апдейта данных по флагам типов уведомлений
--последние 2 запроса - флаги (которые раннее счтались включенными по умолчанию) по тем клиентам,
-- у кого нет записей в cf_notificationsettings, в том числе записи с notifytype =1
insert into cf_client_guardian_notificationsettings(idofclientguardian, notifytype, createddate)
  select g.idofclientguardian, s.notifytype, extract(epoch from now()) * 1000
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofguardian
  where g.deletedstate = false and s.notifytype <> 1200000000
    union
  select g.idofclientguardian, 1210000000, extract(epoch from now()) * 1000
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofguardian
  where g.deletedstate = false and s.notifytype = 1200000000
    union
  select g.idofclientguardian, 1220000000, extract(epoch from now()) * 1000
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofguardian
  where g.deletedstate = false and s.notifytype = 1200000000
    union
  select g.idofclientguardian, 1230000000, extract(epoch from now()) * 1000
  from cf_clientsnotificationsettings s join cf_client_guardian g on s.idofclient = g.idofguardian
  where g.deletedstate = false and s.notifytype = 1200000000
    union
  select g.idofclientguardian, 1000000000, extract(epoch from now()) * 1000
  from cf_client_guardian g where g.idofguardian in
  (select idofclient from cf_clients c where not exists(select idofclient from cf_clientsnotificationsettings where idofclient = c.idofclient))
    union
  select g.idofclientguardian, 1100000000, extract(epoch from now()) * 1000
  from cf_client_guardian g where g.idofguardian in
  (select idofclient from cf_clients c where not exists(select idofclient from cf_clientsnotificationsettings where idofclient = c.idofclient))
    order by idofclientguardian, notifytype;

--После импорта данных создаем индексы на таблицу флагов оповещений
CREATE INDEX cf_client_guardian_notificationsettings_idofclientguardian_idx
ON cf_client_guardian_notificationsettings USING btree (idofclientguardian);


--Реестры информация об опекунах
CREATE TABLE cf_registrychange_guardians (
  idOfRegistryGuardian BIGSERIAL NOT NULL,
  idofregistrychange BIGINT,
  familyname CHARACTER VARYING(128),
  firstname CHARACTER VARYING(128),
  secondname CHARACTER VARYING(128),
  relationship CHARACTER VARYING(128),
  phonenumber CHARACTER VARYING(128),
  emailaddress CHARACTER VARYING(128),
  CreatedDate BIGINT NOT NULL,
  Applied boolean not null default false,
  CONSTRAINT cf_registrychange_guardians_pk PRIMARY KEY (idOfRegistryGuardian),
  CONSTRAINT cf_registrychange_guardians_registrychange_fk FOREIGN KEY (idofregistrychange) REFERENCES cf_registrychange (idofregistrychange)
);

--ИНдексы на таблицу истороии синхронизации
CREATE INDEX cf_synchistory_calc2_idoforg_idx ON cf_synchistory_calc2 USING btree (idoforg);
CREATE INDEX cf_synchistory_calc2_calcdateat_idx ON cf_synchistory_calc2 USING btree (calcdateat);

--Новый ИД для фото
ALTER TABLE cf_clientphoto
  DROP CONSTRAINT cf_clientphoto_pk,
  ADD COLUMN idofclientphoto BIGSERIAL PRIMARY KEY,
  ADD CONSTRAINT cf_clientphoto_idofclient UNIQUE (idofclient);