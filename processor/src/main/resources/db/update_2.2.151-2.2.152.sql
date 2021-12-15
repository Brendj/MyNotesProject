--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.152

--Новый флаг запрета на оплату при расхождении времени
alter table cf_orgs add column allowRegistryChangeEmployee integer not null default 0;

--Очистка флагов работы ОО в летний период
update cf_orgs set isWorkInSummerTime = 0;

--Таблица с номерами карт, по которым пропускаем проверку ЦП
create table cf_cards_special (
  cardno bigint NOT NULL,
  CONSTRAINT cf_cards_special_pk PRIMARY KEY (cardno)
);

--Признак длинного идентификатора карты
alter table cf_cards add column islonguid boolean;

alter table cf_synchistory add column synctype integer;

--Флаг, что по подписке отправлено уведомление об окончании срока действия
alter table cf_bank_subscriptions add column notificationSent integer;

--Таблица "заявки в службу помощи"
CREATE TABLE cf_helprequests
(
  idofhelprequests bigserial NOT NULL,
  version bigint NOT NULL,
  hibernateversion bigint NOT NULL,
  requestdate bigint NOT NULL, 
  requestupdatedate bigint NOT NULL,
  theme integer NOT NULL DEFAULT 0,
  message character varying(2000),
  declarer character varying(100),
  phone character varying(11) NOT NULL,
  status integer NOT NULL DEFAULT 0,
  requestnumber character varying(15) NOT NULL,
  idoforg bigint NOT NULL,
  guid character varying(36) NOT NULL,
  comment character varying(2000),
  CONSTRAINT cf_helprequests_pk PRIMARY KEY (idofhelprequests),
  CONSTRAINT cf_helprequests_idoforg_fk FOREIGN KEY (idoforg)
      REFERENCES cf_orgs (idoforg) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_helprequests_guid_uq UNIQUE (guid),
  CONSTRAINT cf_helprequests_requestnumber_uq UNIQUE (requestnumber)
);

--Флаг службы помощи
alter table cf_orgs add column helpdeskEnabled integer not null default 0;

--! ФИНАЛИЗИРОВАН, НЕ МЕНЯТЬ