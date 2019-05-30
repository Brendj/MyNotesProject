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
  idofrnipmessage bigserial NOT NULL,
  version bigint,
  eventtime bigint NOT NULL,  --время отправки
  eventtype integer NOT NULL, --каталог или экспорт платежей
  request text,
  response text,
  requestid character varying(100) NOT NULL, --ид в исходящем пакете
  idofcontragent bigint NOT NULL,
  lastrnipupdate bigint, --время окончания интервала запроса
  processed integer NOT NULL DEFAULT 0, --признак, что асинхронный ответ получен и разобран
  responsemessage character varying(256),
  lastupdate bigint,
  constraint cf_rnip_messages_pk PRIMARY KEY (idofrnipmessage),
  CONSTRAINT cf_rnip_messages_contragent_fk FOREIGN KEY (idofcontragent) REFERENCES cf_contragents(idofcontragent)
);