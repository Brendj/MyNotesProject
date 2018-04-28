--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.157

--Номер паспорта в таблице клиентов
alter table cf_clients add column passportnumber character varying(20);

--Таблица заявок на выдачу карты представителю
create table cf_card_requests
(
  idofcardrequest bigserial NOT NULL,
  typecard integer NOT NULL,
  idofclient bigint NOT NULL,
  createddate bigint NOT NULL,
  mobile character varying(32),
  cardissuedate bigint,
  deletedstate integer NOT NULL DEFAULT 0,
  version bigint NOT NULL,
  CONSTRAINT cf_card_requests_pk PRIMARY KEY (idofcardrequest)
);