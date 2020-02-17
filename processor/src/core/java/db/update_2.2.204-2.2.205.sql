--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 205

-- 358: Создание таблицы cf_taloon_preorder
CREATE TABLE cf_taloon_preorder
(
    guid character varying(36) NOT NULL,
    idoforg bigint NOT NULL,
    taloondate bigint NOT NULL,
    complexid bigint, -- NOT NULL ?
    complexname character varying(128), -- NOT NULL ?
    goodsname character varying(512),
    goodsguid character varying(36),
    idoforgcreated bigint,
    soldqty integer NOT NULL,
    requestedqty integer NOT NULL,
    shippedqty integer,
    reservedqty integer,
    blockedqty integer,
    price bigint NOT NULL,
    createdtype integer NOT NULL,
    taloonnumber bigint,
    idoforgowner bigint NOT NULL,
    version bigint NOT NULL,
    deletedstate boolean NOT NULL DEFAULT false,
    ispp_state integer NOT NULL DEFAULT 0,
    pp_state integer NOT NULL DEFAULT 0,
    idoftaloonpreorder bigint NOT NULL,
    remarks text,
    comments character varying(128),
    CONSTRAINT cf_taloon_preorder_pk PRIMARY KEY (idoftaloonpreorder)
)
    WITH (
        OIDS=FALSE
    );

CREATE UNIQUE INDEX cf_taloon_preorder_idoforg_taloondate_complexid_goodsguid_idx
    ON cf_taloon_preorder
        USING btree
        (idoforg, taloondate, complexid, goodsguid COLLATE pg_catalog."default", price);

CREATE INDEX cf_taloons_preorder_version_idx
    ON cf_taloon_preorder
        USING btree
        (version);

CREATE INDEX cf_taloons_preorder_guid_idx
    ON cf_taloon_preorder
        USING btree
        (guid COLLATE pg_catalog."default");

-- 358: Добавление id комплекса в cf_goods_requests_positions
ALTER TABLE cf_goods_requests_positions
    ADD complexId integer;