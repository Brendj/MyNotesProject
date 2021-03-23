/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

--952
ALTER TABLE public.cf_clientsms_resending ADD nodename varchar NULL;

CREATE SEQUENCE public.cf_clientsms_node_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_clientsms_node_logging (
    id int8 DEFAULT nextval('cf_clientsms_node_id') NOT NULL,
    idOfSms varchar null,
    params varchar null,
    nodename varchar null
);

CREATE INDEX cf_clientsms_node_idx
    ON cf_clientsms_node_logging
        USING btree
        (idOfSms);

ALTER TABLE public.cf_clientsms_node_logging ADD createdate int8 NULL;