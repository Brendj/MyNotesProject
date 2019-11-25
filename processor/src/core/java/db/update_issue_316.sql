--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений issue 316

-- Таблица для блокированных на арме предзаказах
CREATE TABLE cf_preorder_block (
  idofpreorderblock bigserial NOT NULL,
  idofpreordercomplex bigint NOT NULL,
  storno integer NOT NULL,
  createddate bigint NOT NULL,
  lastupdate bigint,
  idoforgoncreate bigint NOT NULL,
  CONSTRAINT cf_preorder_block_pk PRIMARY KEY (idofpreorderblock),
  CONSTRAINT cf_preorder_block_preorder_complex_fk FOREIGN KEY (idofpreordercomplex)
  REFERENCES cf_preorder_complex (idofpreordercomplex) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_preorder_block_orgoncreate_fk FOREIGN KEY (idoforgoncreate)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
