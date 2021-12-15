--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.108

--Новое поле для краткого наименования
ALTER TABLE cf_orgs ADD COLUMN shortnameinfoservice character varying(128);

UPDATE cf_orgs set shortnameinfoservice = shortname;

--Делаем контракты распределенным объектом
ALTER TABLE cf_contracts ADD COLUMN deletedstate boolean NOT NULL DEFAULT false,
  ADD COLUMN globalversion bigint,
  ADD COLUMN orgowner bigint,
  ADD COLUMN guid character varying(36),
  ADD COLUMN lastupdate bigint,
  ADD COLUMN deletedate bigint,
  ADD COLUMN createddate bigint,
  ADD COLUMN sendall integer DEFAULT 0,
  ADD COLUMN globalversiononcreate bigint;

UPDATE cf_contracts set guid = cast(md5(cast(random() as text) || cast(clock_timestamp() as text)) as uuid),
  createddate = extract(epoch from now()) * 1000,
  globalversion = 1,
  globalversiononcreate = 1;

ALTER TABLE cf_contracts ALTER COLUMN guid SET NOT NULL,
  ALTER COLUMN createddate SET NOT NULL;

--! ФИНАЛИЗИРОВАН (Семенов, 151225) НЕ МЕНЯТЬ