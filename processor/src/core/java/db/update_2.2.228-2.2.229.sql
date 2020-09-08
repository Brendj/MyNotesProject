--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 229

ALTER TABLE cf_cr_cardactionrequests RENAME COLUMN id TO idcardactionrequest;

ALTER TABLE cf_cr_cardactionrequests ADD previdcardrequest int8 NULL;

CREATE SEQUENCE cf_cr_cardactionrequests_idcardactionrequest_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

ALTER TABLE cf_cr_cardactionrequests ALTER COLUMN idcardactionrequest SET DEFAULT nextval('cf_cr_cardactionrequests_idcardactionrequest_seq');

DROP SEQUENCE cf_cr_cardactionrequests_id_seq;

CREATE SEQUENCE cf_cr_cardactionclient_idcardactionclient_seq;

create TABLE public.cf_cr_cardactionclient (
    idcardactionclient int8 DEFAULT nextval('cf_cr_cardactionclient_idcardactionclient_seq') NOT NULL,
    idcardactionrequest int8,
    idofclient int8 NULL,
    idclientchild int8 null,
    idofcard int8 null,
    "comment" varchar null,
    oldcardstate int4 NULL
);

--613
CREATE SEQUENCE cf_card_sync_idcardsync_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_card_sync (
    idcardsync int8 DEFAULT nextval('cf_card_sync_idcardsync_seq') NOT NULL,
    idofcard int8 NULL,
    idoforg int8 NULL,
    statechange int8 NULL
);

alter table cf_card_sync add primary key (idcardsync);
alter table cf_cr_cardactionclient add primary key (idcardactionclient);
alter table cf_cr_cardactionrequests add primary key (idcardactionrequest);

CREATE INDEX cf_cr_cardactionrequests_requestid_idx ON cf_cr_cardactionrequests (requestid);

--! ФИНАЛИЗИРОВАН 08.09.2020, НЕ МЕНЯТЬ