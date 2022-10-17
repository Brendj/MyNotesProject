-- Пакет обновлений issue proaktiv
ALTER TABLE cf_client_dtiszn_discount_info ADD COLUMN isActive boolean DEFAULT true;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.isActive IS 'Признак активности';

--sequence для генерации новых ServiceNumber
CREATE SEQUENCE proaktiv_service_number_seq;
select setval('proaktiv_service_number_seq', 1);

CREATE TABLE cf_proaktiv_message (
  idofproaktivmessage bigserial NOT NULL,
  idofclient int8 NULL,
  idofguardian int8 NULL,
  servicenumber varchar NULL,
  ssoid varchar NULL,
  status character varying (16) NULL,
  message_type int4 null,
  createddate int8 NULL,
  lastupdate int8 NULL,
  CONSTRAINT cf_proaktiv_message_pk PRIMARY KEY (idofproaktivmessage)
);

CREATE TABLE  cf_proaktiv_message_status (
  idofproaktivmessagestatus bigserial NOT NULL,
  idofproaktivmessage int8 not null,
  status character varying (16) NULL,
  createddate int8 NULL,
  CONSTRAINT cf_proaktiv_message_status_pk PRIMARY KEY (idofproaktivmessagestatus),
  CONSTRAINT cf_proaktiv_message_FK foreign key (idofproaktivmessage)
      REFERENCES cf_proaktiv_message (idofproaktivmessage) MATCH SIMPLE
      ON UPDATE NO ACTION ON DELETE NO ACTION
);

ALTER TABLE public.cf_proaktiv_message ADD dtiszncode int4 NULL;

--Параметры в конфигурацию
--ecafe.processor.etp.proactive.isOn=true
--ecafe.processor.etp.proactive.nodes=NNN - отправка в очереди проактива етп
--ecafe.processor.etp.proactive.consumer.node=NNN - слушатель входящей очереди проактива етп
--ecafe.processor.aupd.api.address=адрес сервиса аупд. по умолчанию пром https://mes-api.mos.ru/aupd
--ecafe.processor.aupd.api.apikey=api key сервиса аупд, по умолчанию пром 229a2b81-c7af-4efa-80f7-33629fab3137
--ecafe.processor.zlp.check_benefit_expiration=крон выражение переодичности проверки на истекающий срок льготы
--ecafe.processor.zlp.delete_benefit_expiration=крон выражение переодичности удаления льгот с истекшим сроком
--ecafe.processor.zlp.delete.node=NNN - проверка и удаление льготы с истекшим сроком
