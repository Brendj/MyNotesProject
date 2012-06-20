-- Поле с номером организации в таблице глобального объекта
ALTER TABLE cf_product_guide ADD COLUMN idoforg bigint;
-- Таблица версий распределенных объектов
CREATE TABLE cf_do_version
(
  idofdoobject bigserial,
  distributedobjectclassname character varying(64),
  currentversion bigint,
  CONSTRAINT cf_do_version_pk PRIMARY KEY (idofdoobject )
);
-- Таблица конфликтов для распределенных объектов
CREATE TABLE cf_do_conflicts
(
  idofdoconflict bigserial,
  idoforg bigint,
  distributedobjectclassname character varying(64),
  createconflictdate bigint,
  gversion_inc bigint,
  gversion_cur bigint,
  val_inc character varying(16548),
  val_cur character varying(16548),
  CONSTRAINT cf_do_conflicts_pk PRIMARY KEY (idofdoconflict )
);