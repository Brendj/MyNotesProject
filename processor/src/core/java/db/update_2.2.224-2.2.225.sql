--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 225

CREATE SEQUENCE public.cf_cr_cardactionrequests_id_seq
  INCREMENT BY 1
  MINVALUE 1
  MAXVALUE 9223372036854775807
  START 1;


CREATE TABLE public.cf_cr_cardactionrequests (
  id int8 DEFAULT nextval('cf_cr_cardactionrequests_id_seq') NOT NULL,
  requestid varchar NULL,
  contingentid varchar NULL,
  staffid varchar NULL,
  firstname varchar NULL,
  lastname varchar NULL,
  middlename varchar NULL,
  birthday timestamp NULL,
  organization_ids varchar NULL,
  "action" int4 NULL,
  idofclient int8 NULL,
  "comment" varchar NULL,
  processed bool NULL,
  createdate timestamp NULL,
  lastupdate timestamp NULL
);

--! ФИНАЛИЗИРОВАН 24.08.2020, НЕ МЕНЯТЬ