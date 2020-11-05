--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.160

--Код товара
alter table cf_menudetails add column itemcode character varying(32);

--Календарь меню - замена idOfMenu на Guid
alter table cf_menus_calendar drop column idofmenu,
  add column guidofmenu CHARACTER VARYING(36);

--Код товара в блюде предзаказа
alter table cf_preorder_menudetail add column itemcode character varying(32),
  add column state integer not null default 0,
  add column idofgoodsrequestposition bigint,
  add column idofregularpreorder bigint;

--ИД заявки для предзаказа
alter table cf_preorder_complex add column idofgoodsrequestposition bigint,
  add column idofregularpreorder bigint;

--вид рациона, тип возрастной категории и флаг ежедневной продажи
alter table cf_goods add column goodtype integer not null default 0,
	add column agegroup integer not null default 0,
	add column dailysale integer not null default 0;

--таблица регулярных заказов
create table cf_regular_preorders (
  idofregularpreorder bigint not null,
  idofclient bigint NOT NULL,
  idofcomplex integer,
  itemcode character varying(32),
  itemName character varying(256),
  amount integer NOT NULL,
  startDate bigint NOT NULL,
  endDate bigint NOT NULL,
  monday integer NOT NULL DEFAULT 0,
  tuesday integer NOT NULL DEFAULT 0,
  wednesday integer NOT NULL DEFAULT 0,
  thursday integer NOT NULL DEFAULT 0,
  friday integer NOT NULL DEFAULT 0,
  saturday integer NOT NULL DEFAULT 0,
  price bigint NOT NULL,
  createddate bigint NOT NULL,
  lastupdate bigint NOT NULL,
  deletedState bigint NOT NULL,
  CONSTRAINT cf_regular_preorders_pk PRIMARY KEY (idofregularpreorder),
  CONSTRAINT cf_regular_preorders_idofclient_fk FOREIGN KEY (idofclient)
  REFERENCES cf_clients (idofclient) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);

--генератор для первичного ключа в таблице регулярных предзаказов
alter table cf_generators add column idofregularpreorder bigint NOT NULL DEFAULT 0,
add column idofspecialdate bigint NOT NULL DEFAULT 0;

--индексы по ид. регулярного заказа в таблицах предзаказов + индекс по дате предзаказа
CREATE INDEX cf_preorder_complex_idofregularpreorder_idx ON cf_preorder_complex USING btree (idofregularpreorder);
CREATE INDEX cf_preorder_menudetail_idofregularpreorder_idx ON cf_preorder_menudetail USING btree (idofregularpreorder);
CREATE INDEX cf_preorder_complex_preorderdate_idx ON cf_preorder_complex USING btree (preorderdate);

--группа в календаре учебных дней
alter table cf_specialdates add column idofclientgroup bigint,
  add column idofspecialdate bigserial not null,
  drop constraint cf_specialdates_pk;

alter table cf_specialdates add CONSTRAINT cf_specialdates_pk PRIMARY KEY (idofspecialdate);

update cf_generators set idOfSpecialDate = (select (coalesce(max(idOfspecialdate), 0)+1) from cf_specialdates );

--Статус перехода УИД карты от одной организации к другой
alter table cf_cards add column transitionstate integer not null default 0;

--дубли уидов
alter table cf_cards drop constraint cf_cards_cardno;

--EP-1407
ALTER TABLE cf_menuexchange ALTER COLUMN menudata TYPE character varying (600000);

-- Таблица для записи результата отправки EnterEvent на ИС "ПОРТАЛ"
CREATE TABLE cf_EnterEvents_Send_Info
(
  idofEnterEvent BIGINT NOT NULL,
  idoforg BIGINT NOT NULL,
  idofclient BIGINT,
  idofcard BIGINT,
  evtDateTime BIGINT NOT NULL,
  sendToExternal INTEGER NOT NULL DEFAULT 0,
  responseCode INTEGER NOT NULL DEFAULT 0,
  directionType INTEGER,
  CONSTRAINT cf_EnterEvents_Send_Info_fk PRIMARY KEY (idofEnterEvent, idoforg)
) WITH (
  OIDS = FALSE
);

CREATE INDEX cf_EnterEvents_Send_Info_evttime_idx ON cf_EnterEvents_Send_Info(evtDateTime);

--! ФИНАЛИЗИРОВАН 08.08.2018, НЕ МЕНЯТЬ