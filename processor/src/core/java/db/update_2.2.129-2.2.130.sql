--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.130

--Порог снижения баланса, по достижении которого клиенту должно уйти уведомление о необходимости пополнения
alter table cf_clients add column balancetonotify bigint;

-- Таблица льгот ДСЗН
CREATE TABLE cf_categorydiscounts_dszn
(
  idofcategorydiscountdszn SERIAL NOT NULL,
  code INTEGER NOT NULL,
  description CHARACTER VARYING(512) NOT NULL DEFAULT '',
  idofcategorydiscount BIGINT,
  version BIGINT NOT NULL,
  deleted INTEGER NOT NULL,
  CONSTRAINT cf_categorydiscounts_dszn_pk PRIMARY KEY (idofcategorydiscountdszn),
  CONSTRAINT cf_categorydiscounts_dszn_idofcategorydiscount_fk FOREIGN KEY (idofcategorydiscount)
  REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION
);
  CREATE UNIQUE INDEX cf_categorydiscounts_dszn_code_unique_idx
  ON cf_categorydiscounts_dszn USING btree (code)
  WHERE deleted = 0;

--Блокировка изменения льгот ИСПП у клиентов в АРМ
ALTER TABLE cf_categorydiscounts ADD COLUMN blockedchange integer NOT NULL DEFAULT 0;

--Флаг включения здание работает в летний период + флаг включения вариативного питания + флаг сверки льгот ДСЗН с реестрами
alter table cf_orgs add column isWorkInSummerTime integer not null default 0,
                    add column variablefeeding integer not null default 0,
                    add column changesdszn integer not null default 0;

--тип подписки - абонементное или вариативное питание
alter table cf_subscriber_feeding add column FeedingType integer not null default 0;

alter table cf_clients_cycle_diagrams add column FeedingType integer not null default 0;