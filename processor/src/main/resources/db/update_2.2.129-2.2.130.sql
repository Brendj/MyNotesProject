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
  REFERENCES cf_categorydiscounts (idofcategorydiscount) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION,
  CONSTRAINT cf_categorydiscounts_dszn_code UNIQUE (code)
);

--Блокировка изменения льгот ИСПП у клиентов в АРМ
ALTER TABLE cf_categorydiscounts ADD COLUMN blockedtochange integer NOT NULL DEFAULT 0;

--Флаг включения здание работает в летний период + флаг включения вариативного питания
-- + флаг сверки льгот ДСЗН с реестрами + расширение поля адреса
alter table cf_orgs add column isWorkInSummerTime integer not null default 0,
                    add column variablefeeding integer not null default 0,
                    add column changesdszn integer not null default 0,
                    alter column address type character varying(256);

--тип подписки - абонементное или вариативное питание
alter table cf_subscriber_feeding add column FeedingType integer not null default 0;

alter table cf_clients_cycle_diagrams add column FeedingType integer not null default 0;

--Очищаем таблицы реестров и добавляем колонки для льгот
TRUNCATE cf_registrychange_guardians, cf_registrychange, cf_registrychange_errors;
ALTER TABLE cf_registrychange DROP COLUMN benefitOnAdmission,
                              DROP COLUMN benefitOnAdmissionFrom,
                              ADD COLUMN checkBenefits integer not null default 0,
                              ADD COLUMN benefitDSZN character varying (128),
                              ADD COLUMN benefitDSZNFrom character varying (128),
                              ADD COLUMN newDiscounts character varying (128),
                              ADD COLUMN oldDiscounts character varying (128);

-- Колонки льгот для клиента - последняя льгота ДСЗН, время последнего обновления льготы ДСЗН, флаг исключения клиента из плана питания
ALTER TABLE cf_clients DROP COLUMN benefitOnAdmission,
                       ADD COLUMN categoriesDiscountsDSZN character varying (128) not null default '',
                       ADD COLUMN lastDiscountsUpdate bigint,
                       ADD COLUMN disablePlanCreationDate bigint;

alter table cf_complexinfo add column usedVariableFeeding integer,
                       add column rootComplex integer;

--! ФИНАЛИЗИРОВАН (Семенов, 130317) НЕ МЕНЯТЬ