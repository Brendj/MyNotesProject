--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.157

--Номер паспорта в таблице клиентов
alter table cf_clients add column passportnumber character varying(20),
  add column passportseries character varying(20);

--Таблица заявок на выдачу карты представителю
create table cf_card_requests
(
  idofcardrequest bigserial NOT NULL,
  guid character varying(36),
  typecard integer NOT NULL,
  idofclient bigint NOT NULL,
  createddate bigint NOT NULL,
  mobile character varying(32),
  cardissuedate bigint,
  idofcard bigint,
  deletedstate integer NOT NULL DEFAULT 0,
  deletedate bigint,
  version bigint NOT NULL,
  CONSTRAINT cf_card_requests_pk PRIMARY KEY (idofcardrequest),
  CONSTRAINT cf_card_requests_idofcard_fk FOREIGN KEY (idofcard)
  REFERENCES cf_cards (idofcard) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_card_requests_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--индексы для ускорения полной синхронизации
CREATE INDEX cf_orgs_orgstructureversion_idx ON cf_orgs USING btree (orgstructureversion);

CREATE INDEX cf_client_guardian_version_idx ON cf_client_guardian USING btree (version);

--Цена и название блюда + комплекса в таблицаз предзаказов
alter table cf_preorder_complex add column complexname character varying(60),
  add column complexprice bigint,
  add column createddate bigint,
  add column lastupdate bigint;

alter table cf_preorder_menudetail add column menudetailname character varying(512),
  add column menudetailprice bigint;

alter table cf_client_guardian add column idofcardrequest bigint,
ADD CONSTRAINT cf_client_guardian_cardrequest_fk FOREIGN KEY (idofcardrequest)
REFERENCES cf_card_requests (idofcardrequest) MATCH SIMPLE
ON UPDATE NO ACTION ON DELETE NO ACTION;

CREATE INDEX cf_categoryorg_orgs_idofcategoryorg_idx ON cf_categoryorg_orgs USING btree (idofcategoryorg);

--! ФИНАЛИЗИРОВАН 21.05.2018, НЕ МЕНЯТЬ