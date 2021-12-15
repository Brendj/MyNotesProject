/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 280

CREATE SEQUENCE cf_esp_request_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;


CREATE TABLE cf_esp_request (
	idesprequest bigserial NOT NULL,
	idofclient int8 NULL,
	email varchar NULL,
	idoforg int8 NULL,
	status varchar NULL,
	numberrequest varchar NULL,
	topic varchar NULL,
	message varchar NULL,
	createdate int8 NULL,
	updatedate int8 NULL,
	closeddate int8 NULL,
	solution varchar NULL,
	sd varchar NULL
);

COMMENT ON COLUMN cf_esp_request.idesprequest IS 'Идентификатор записи в этой таблице';
COMMENT ON COLUMN cf_esp_request.idofclient IS 'Идентификатор клиента в таблице cf_clients';
COMMENT ON COLUMN cf_esp_request.email IS 'Почта для связи с клиентом';
COMMENT ON COLUMN cf_esp_request.idoforg IS 'Идентификатор организации заявителя в таблице cf_orgs ';
COMMENT ON COLUMN cf_esp_request.status IS 'Статус заявки';
COMMENT ON COLUMN cf_esp_request.numberrequest IS 'Номер заявки в рамках FOS';
COMMENT ON COLUMN cf_esp_request.topic IS 'Тема обращения';
COMMENT ON COLUMN cf_esp_request.message IS 'Текст обращения';
COMMENT ON COLUMN cf_esp_request.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_esp_request.updatedate IS 'Дата обновления';
COMMENT ON COLUMN cf_esp_request.closeddate IS 'Зата закрытия';
COMMENT ON COLUMN cf_esp_request.solution IS 'Принятое решения по заявке';
COMMENT ON COLUMN cf_esp_request.sd IS 'Номер в системе HPSM';


CREATE SEQUENCE cf_esp_attached_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_esp_attached (
	idespattached bigserial NOT NULL,
	idesprequest int8 NULL,
	filename varchar NULL,
	"path" varchar NULL,
	"number" varchar NULL,
	createdate int8 NULL,
	linkinfos varchar NULL
);

--! ФИНАЛИЗИРОВАН 15.12.2021, НЕ МЕНЯТЬ