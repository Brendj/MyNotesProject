-- Таблица версий распределенных объектов
CREATE TABLE cf_do_version
(
  idofdoobject bigserial,
  distributedobjectclassname character varying(64),
  currentversion bigint
);
-- Таблица конфликтов для распределенных объектов
CREATE TABLE cf_do_conflicts
(
  idofdocconflict bigserial,
  distributedobjectclassname character varying(64),
  createconflictdate bigint,
  gversion_inc bigint,
  gversion_cur bigint,
  val_inc character varying(16548),
  val_cur character varying(16548)
);