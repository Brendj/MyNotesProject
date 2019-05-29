--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 2.2.182

-- #108
CREATE TABLE cf_kzn_clients_statistic (
  idofkznclientsstatistic bigserial NOT NULL,
  idoforg bigint NOT NULL,
  studentscounttotal bigint NOT NULL,
  studentscountyoung bigint NOT NULL,
  studentscountmiddle bigint NOT NULL,
  studentscountold bigint NOT NULL,
  benefitstudentscountyoung bigint NOT NULL,
  benefitstudentscountmiddle bigint NOT NULL,
  benefitstudentscountold bigint NOT NULL,
  benefitstudentscounttotal bigint NOT NULL,
  employeecount bigint NOT NULL,
  CONSTRAINT cf_kzn_clients_statistic_pk PRIMARY KEY (idofkznclientsstatistic),
  CONSTRAINT cf_kzn_clients_statistic_fk FOREIGN KEY (idoforg) REFERENCES cf_orgs(idoforg)
);
CREATE UNIQUE INDEX cf_kzn_clients_statistic_idoforg_idx ON cf_kzn_clients_statistic (idoforg);

--! ФИНАЛИЗИРОВАН 16.05.2019, НЕ МЕНЯТЬ

--временно добавляю новые объекты
create table cf_rnip_messages (
  idofrnipmessage bigserial not null,
  eventtime bigint,
  eventtype integer,
  request text,
  response text,
  request_id character varying(100),
  response_id character varying(100),
  idofcontragent bigint,
  constraint cf_rnip_messages_pk PRIMARY KEY (idofrnipmessage),
  CONSTRAINT cf_rnip_messages_contragent_fk FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent)
);