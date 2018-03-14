--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.154

--Таблица токенов СУДИР
CREATE TABLE cf_sudir_tokens
(
  access_token character varying(64) NOT NULL,
  token_type character varying(64),
  expires_in integer,
  scope character varying(64),
  refresh_token character varying(64),
  createddate bigint NOT NULL,
  CONSTRAINT cf_sudir_tokens_access_token_pk PRIMARY KEY (access_token)
);

--флаг особенного питания
alter table cf_clients add column specialMenu integer;

--приводим в соответствие длину столбца наименования группы в таблице сверки контингента
ALTER TABLE CF_RegistryChange ALTER COLUMN groupName TYPE character varying(256),
  ALTER COLUMN groupNameFrom TYPE character varying(256);

--комплексы предзаказа
CREATE TABLE cf_preorder_complex
(
  idofpreordercomplex bigserial NOT NULL,
  idofcomplexinfo bigint NOT NULL,
  idofclient bigint NOT NULL,
  preorderdate bigint,
  amount integer,
  version bigint NOT NULL default 0,
  deletedstate integer NOT NULL default 0,
  CONSTRAINT cf_preorder_complex_idofpreordercomplex_pk PRIMARY KEY (idofpreordercomplex),
  CONSTRAINT cf_preorder_complex_idofcomplexinfo_fk FOREIGN KEY (idofcomplexinfo)
  REFERENCES CF_ComplexInfo (idofcomplexinfo) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_preorder_complex_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_preorder_complex_idofclient_idx ON cf_preorder_complex USING btree (idofclient);
CREATE INDEX cf_preorder_complex_idofcomplexinfo_idx ON cf_preorder_complex USING btree (idofcomplexinfo);

--детализация комплексов предзаказа
CREATE TABLE cf_preorder_menudetail
(
  idofpreordermenudetail bigserial NOT NULL,
  idofcomplexinfo bigint NOT NULL,
  idofcomplexinfodetail bigint NOT NULL,
  idofmenudetail bigint NOT NULL,
  idofclient bigint NOT NULL,
  preorderdate bigint,
  amount integer,
  version bigint NOT NULL default 0,
  deletedstate integer NOT NULL default 0,
  CONSTRAINT cf_preorder_menudetail_idofpreordermenudetail_pk PRIMARY KEY (idofpreordermenudetail),
  CONSTRAINT cf_preorder_menudetail_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_preorder_menudetail_idofcomplexinfo_fk FOREIGN KEY (idofcomplexinfo)
  REFERENCES CF_ComplexInfo (idofcomplexinfo) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_preorder_menudetail_idofmenudetail_fk FOREIGN KEY (idofmenudetail)
  REFERENCES CF_MenuDetails (idofmenudetail) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_preorder_menudetail_idofclient_idx ON cf_preorder_menudetail USING btree (idofclient);
CREATE INDEX cf_preorder_menudetail_idofcomplexinfo_idx ON cf_preorder_menudetail USING btree (idofcomplexinfo);
CREATE INDEX cf_preorder_menudetail_idofmenudetail_idx ON cf_preorder_menudetail USING btree (idofmenudetail);

--флаг Специальное питание у комплекса
alter table cf_complexinfo add column usedSpecialMenu integer;