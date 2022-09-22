-- Пакет обновлений issue 1166
CREATE TABLE cf_mezhved_kafka_error (
    idofmezhvedkafkaerror bigserial NOT NULL,
    msg varchar NOT NULL,
    topic varchar NOT NULL,
    error varchar NOT NULL,
    idofapplicationforfood int8 NOT NULL,
    "type" int4 NULL,
    createdate int8 NULL,
    updatedate int8 NULL,
    CONSTRAINT ccf_mezhved_kafka_error_pk PRIMARY KEY (idofmezhvedkafkaerror)
);