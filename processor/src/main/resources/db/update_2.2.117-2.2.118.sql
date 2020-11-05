--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.118

ALTER TABLE cf_clientphoto
  ADD COLUMN iscanceled INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN isapproved INTEGER NOT NULL DEFAULT 0,
  ADD COLUMN idofclientguardian BIGINT,
  ADD COLUMN lastproceederror CHARACTER VARYING(256),
  ADD COLUMN version BIGINT;
CREATE INDEX cf_clientphoto_version_idx ON cf_clientphoto USING BTREE (version);

--Флаг "Сверка фотографий" у организации
ALTER TABLE cf_orgs ADD COLUMN photoregistrydirective integer NOT NULL DEFAULT 0;

--! ФИНАЛИЗИРОВАН (Семенов, 240816) НЕ МЕНЯТЬ