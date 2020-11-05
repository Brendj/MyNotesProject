--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 209

-- 358: Создание таблицы cf_taloon_preorder
CREATE TABLE cf_taloon_preorder
(
    guid character varying(36) NOT NULL,
    idoforg bigint NOT NULL,
    taloondate bigint NOT NULL,
    complexid bigint,
    complexname character varying(128),
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

-- 358: Добавление индекса в cf_preorder_complex
CREATE INDEX cf_preorder_complex_armcomplexid_idx
    ON cf_preorder_complex
        USING btree
        (armcomplexid);

ALTER TABLE CF_OrderDetails ADD COLUMN FRation integer;

-- 370: Таблица для хранения информации о том, отправлено ли было сообщение клиенту по конкретному заказу
CREATE TABLE cf_notification_orders (
    idOfNotificationOrders   			bigserial,
    idOfOrder   						int8,
    idOfClient   						int8,
    createddate 						int8,
    sended       						bool
);
CREATE INDEX cf_notification_orders_idoforder_idx ON cf_notification_orders (idoforder,idofclient);

-- 410: Срок действия меню увеличен с 365 до 730 дней
UPDATE cf_options
SET optiontext='730'
WHERE idofoption=1004;

CREATE INDEX cf_preorder_menudetail_idofgoodsrequestposition_idx
ON cf_preorder_menudetail
USING btree
(idofgoodsrequestposition);

--! ФИНАЛИЗИРОВАН 14.02.2020, НЕ МЕНЯТЬ