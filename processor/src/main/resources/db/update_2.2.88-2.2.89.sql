--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.88
--! Изменение таблицы "Агрегирование данных отчет Xml"




-- DROP TABLE cf_AccountOperations;

CREATE TABLE cf_account_operations
(
  idofaccountoperation bigint NOT NULL,
  idoforg bigint,
  accountoperationtype integer,
  idofoperation bigint,
  date bigint,
  idofcontract bigint,
  value bigint,
  type integer,
  idoforder bigint,
  staffguid VARCHAR(128),
  idofpos bigint,
  idofcontragent bigint,
  CONSTRAINT cf_account_operations_pk PRIMARY KEY (idofaccountoperation)
)
WITH (
OIDS=FALSE
);


ALTER TABLE cf_contragents ADD COLUMN defaultcontragent bigint;
ALTER TABLE cf_contragents ADD COLUMN paybycashier integer;


--! ФИНАЛИЗИРОВАН (Сунгатов, 150203) НЕ МЕНЯТЬ
