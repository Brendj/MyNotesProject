-- Пакет обновлений issue proaktiv
ALTER TABLE cf_client_dtiszn_discount_info ADD COLUMN isActive boolean DEFAULT true;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.isActive IS 'Признак активности';

--sequence для генерации новых ServiceNumber
CREATE SEQUENCE proaktiv_service_number_seq;
select setval('proaktiv_service_number_seq', 0);

CREATE TABLE cf_proaktiv_message (
  idofproaktivmessage bigserial NOT NULL,
  idofclient int8 NULL,
  idofguardian int8 NULL,
  servicenumber varchar NULL,
  ssoid varchar NULL,
  status int4 null,
  message_type int4 null,
  createddate int8 NULL,
  lastupdate int8 NULL,
  CONSTRAINT cf_proaktiv_message_pk PRIMARY KEY (idofproaktivmessage)
);

CREATE TABLE  cf_proaktiv_message_status (
  idofproaktivmessagestatus bigserial NOT NULL,
  idofproaktivmessage int8 not null,
  status int4 NULL,
  createddate int8 NULL,
  CONSTRAINT cf_proaktiv_message_status_pk PRIMARY KEY (idofproaktivmessagestatus)
);