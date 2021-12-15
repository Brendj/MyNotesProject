--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 220

-- 496
ALTER TABLE cf_goods_requests_positions
    ADD COLUMN idofdish bigint,
    ADD COLUMN feedingtype integer;

COMMENT ON COLUMN cf_goods_requests_positions.idofdish IS 'Идентификатор блюда от веб-поставщика';
COMMENT ON COLUMN cf_goods_requests_positions.feedingtype IS 'Тип позиции (0 – общий тип, 1 – ЛП, 2 – ПП, 3 – АП, 4 – ВП, 5 - предзаказы)';

ALTER TABLE CF_RegistryChange ADD COLUMN meshGUID varchar(36);

ALTER TABLE cf_users
    ADD COLUMN idoforg bigint,
    ADD COLUMN idofclient bigint;

COMMENT ON COLUMN cf_users.idoforg IS 'Идентификатор организации';
COMMENT ON COLUMN cf_users.idofclient IS 'Идентификатор клиента';

ALTER TABLE cf_users
    ADD CONSTRAINT cf_users_idofclient_fk
FOREIGN KEY (idofclient)
REFERENCES cf_clients (idofclient) MATCH SIMPLE
ON UPDATE NO ACTION
ON DELETE NO ACTION;

ALTER TABLE cf_users
    ADD CONSTRAINT cf_users_idoforg_fk
FOREIGN KEY (idoforg)
REFERENCES cf_orgs (idoforg) MATCH SIMPLE
ON UPDATE NO ACTION
ON DELETE NO ACTION;

CREATE TABLE cf_refresh_token
(
    refreshtokenhash character varying(128) COLLATE pg_catalog."default" NOT NULL UNIQUE,
    idofuser bigint,
    ipaddress character varying(15) COLLATE pg_catalog."default",
    expiresin bigint DEFAULT (extract(epoch from now()) * 1000) NOT NULL,
    createdat bigint DEFAULT (extract(epoch from now()) * 1000) NOT NULL,

    CONSTRAINT cf_refresh_token_pk PRIMARY KEY (refreshtokenhash),
    CONSTRAINT cf_refresh_token_fk FOREIGN KEY (idofuser)
    REFERENCES public.cf_users (idofuser) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE
)
WITH (
OIDS = FALSE
);

CREATE INDEX cf_clients_meshguid_idx ON cf_clients USING btree (meshguid);

alter table cf_applications_for_food
    add column discountdatestart bigint,
    add column discountdateend bigint;

COMMENT ON COLUMN cf_applications_for_food.discountdatestart is 'Дата начала действия льготы';
COMMENT ON COLUMN cf_applications_for_food.discountdateend is 'Дата окончания действия льготы';

--! ФИНАЛИЗИРОВАН 04.08.2020, НЕ МЕНЯТЬ