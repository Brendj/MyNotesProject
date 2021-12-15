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

--таблица связей токен - л/с
CREATE TABLE cf_sudir_token_client
(
  idofsudirtokenclient bigserial,
  access_token character varying(64) NOT NULL,
  contractid bigint,
  CONSTRAINT cf_sudir_token_client_idofsudirtokenclient_pk PRIMARY KEY (idofsudirtokenclient)
);
CREATE INDEX cf_sudir_token_client_access_token_idx ON cf_sudir_token_client USING btree (access_token);

--флаг особенного питания
alter table cf_clients add column specialMenu integer;

--приводим в соответствие длину столбца наименования группы в таблице сверки контингента
ALTER TABLE CF_RegistryChange ALTER COLUMN groupName TYPE character varying(256),
  ALTER COLUMN groupNameFrom TYPE character varying(256);

--генератор для ключа предзаказа
ALTER TABLE CF_Generators ADD COLUMN idOfPreorderComplex BIGINT NOT NULL DEFAULT 0;

--комплексы предзаказа
CREATE TABLE cf_preorder_complex
(
  idofpreordercomplex bigint NOT NULL,
  guid character varying(36),
  armcomplexid integer,
  idofclient bigint NOT NULL,
  preorderdate bigint,
  amount integer,
  usedsum bigint NOT NULL default 0,
  usedamount bigint NOT NULL default 0,
  version bigint NOT NULL default 0,
  deletedstate integer NOT NULL default 0,
  CONSTRAINT cf_preorder_complex_idofpreordercomplex_pk PRIMARY KEY (idofpreordercomplex),
  CONSTRAINT cf_preorder_complex_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_preorder_complex_idofclient_idx ON cf_preorder_complex USING btree (idofclient);
CREATE INDEX cf_preorder_complex_version_idx ON cf_preorder_complex USING btree (version);

--детализация комплексов предзаказа
CREATE TABLE cf_preorder_menudetail
(
  idofpreordermenudetail bigserial NOT NULL,
  guid character varying(36),
  idofclient bigint NOT NULL,
  armidofmenu bigint,
  idofpreordercomplex bigint,
  preorderdate bigint,
  amount integer,
  deletedstate integer NOT NULL default 0,
  CONSTRAINT cf_preorder_menudetail_idofpreordermenudetail_pk PRIMARY KEY (idofpreordermenudetail),
  CONSTRAINT cf_preorder_menudetail_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE
  ON UPDATE NO ACTION ON DELETE NO ACTION
);
CREATE INDEX cf_preorder_menudetail_idofclient_idx ON cf_preorder_menudetail USING btree (idofclient);

--флаг Специальное питание у комплекса
alter table cf_complexinfo add column usedSpecialMenu integer;

--Флаг предзаказы у ОО
alter table cf_orgs add column preordersEnabled integer not null default 0;

--Увеличиваем размерность для меню поставщика
ALTER TABLE cf_menuexchange ALTER COLUMN menudata SET DATA TYPE character varying(200000);

--таблица для хранения связей детализации заказа с предзаказом
CREATE TABLE cf_preorder_linkod
(
  idofpreorderlinkod bigserial NOT NULL,
  preorderGuid character varying(36),
  idOfOrg bigint,
  idOfOrder bigint,
  idOfOrderDetail bigint,
  qty bigint,
  price bigint,
  CONSTRAINT cf_preorder_linkod_idofpreorderlinkod_pk PRIMARY KEY (idofpreorderlinkod)
);

--! ФИНАЛИЗИРОВАН 29.03.2018, НЕ МЕНЯТЬ