--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.115

ALTER TABLE cf_discountchangehistory
  ADD COLUMN idoforg BIGINT,
  ADD COLUMN comment CHARACTER VARYING(128) NOT NULL DEFAULT '',
  ADD CONSTRAINT cf_discountchangehistory_idoforg_fk FOREIGN KEY (idoforg)
  REFERENCES cf_orgs (idoforg) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION;
