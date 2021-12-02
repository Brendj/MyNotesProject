/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

-- Пакет обновлений v 279

CREATE INDEX cf_wa_journal_card_cardno_idx ON cf_wa_journal_card (cardno ASC NULLS LAST);
CREATE INDEX cf_wa_journal_card_longcardno_idx ON cf_wa_journal_card (longcardno ASC NULLS LAST);
CREATE INDEX cf_wa_journal_card_createddate_idx ON cf_wa_journal_card (createddate ASC NULLS LAST);

CREATE TABLE "cf_wt_complex_invisible" (
    "idofcomplex" int8 NOT NULL,
    "idoforg" int8 NOT NULL,
    "version" int8 NOT NULL,
    "deletestate" int4 NOT NULL,
    "createdate" timestamp(6) NOT NULL,
    "create_by_id" int8 NOT NULL,
    "lastupdate" timestamp(6) NOT NULL,
    "update_by_id" int8 NOT NULL,
     CONSTRAINT "cf_wt_complex_invisible_pk" PRIMARY KEY ("idofcomplex", "idoforg"),
     CONSTRAINT "cf_wt_complex_invisible_complex_fk" FOREIGN KEY ("idofcomplex")
         REFERENCES "cf_wt_complexes" ("idofcomplex") ON DELETE NO ACTION ON UPDATE NO ACTION,
     CONSTRAINT "cf_wt_complex_invisible_create_fk" FOREIGN KEY ("create_by_id")
         REFERENCES "cf_users" ("idofuser") ON DELETE NO ACTION ON UPDATE NO ACTION,
     CONSTRAINT "cf_wt_complex_invisible_org_fk" FOREIGN KEY ("idoforg")
         REFERENCES "cf_orgs" ("idoforg") ON DELETE NO ACTION ON UPDATE NO ACTION,
     CONSTRAINT "cf_wt_complex_invisible_update_fk" FOREIGN KEY ("update_by_id")
         REFERENCES "cf_users" ("idofuser") ON DELETE NO ACTION ON UPDATE NO ACTION );

CREATE SEQUENCE "cf_plan_orders_web_idofplanorder_seq"
    INCREMENT BY 500
    MINVALUE 1
    START 1;

DROP TABLE IF EXISTS "cf_plan_orders_web";
CREATE TABLE "cf_plan_orders_web"
(
    "idofplanorder"           bigint                 NOT NULL,
    "plandate"                DATE                   NOT NULL,
    "idoforg"                 bigint                 NOT NULL,
    "idofclient"              bigint                 NOT NULL,
    "groupname"               VARCHAR(256)           NOT NULL,
    "idofcomplex"             bigint                 NOT NULL,
    "qty"                     SMALLINT     DEFAULT 1 NOT NULL,
    "plantype"                SMALLINT     DEFAULT 1 NOT NULL,
    "manualoperation"         SMALLINT     DEFAULT NULL,
    "idofcategorydiscount"    bigint,
    "idofdiscountrule"        bigint,
    "idofpreordercomplex"     bigint,
    "paymentstate"            SMALLINT     DEFAULT NULL,
    "paymenterror"            VARCHAR(256) DEFAULT NULL,
    "idoforder"               bigint,
    "idoforderreservation"    bigint,
    "paydatetime"             TIMESTAMP WITHOUT TIME ZONE,
    "idofclientchange"        bigint,
    "specialstate"            SMALLINT,
    "idofusermanualoperation" bigint,
    "idofuserconfirmtopay"    bigint,
    "idofuserpay"             bigint,
    "version"                 bigint,
    "createdate"              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    "lastupdate"              TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT "cf_plan_orders_web_pk" PRIMARY KEY ("idofplanorder"),
    CONSTRAINT "cf_plan_orders_web_idofclient_fk" FOREIGN KEY ("idofclient") REFERENCES "cf_clients" ("idofclient") ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT "cf_plan_orders_web_idoforg_fk" FOREIGN KEY ("idoforg") REFERENCES "cf_orgs" ("idoforg") ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT "cf_plan_orders_web_idofcomplex_fk" FOREIGN KEY ("idofcomplex") REFERENCES "cf_wt_complexes" ("idofcomplex") ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT "cf_plan_orders_web_idofdiscountrule_fk" FOREIGN KEY ("idofdiscountrule") REFERENCES "cf_wt_discountrules" ("idofrule") ON DELETE NO ACTION ON UPDATE NO ACTION
);
COMMENT ON COLUMN cf_plan_orders_web.idofplanorder IS 'ID записи';
COMMENT ON COLUMN cf_plan_orders_web.plandate IS 'Дата плана питания';
COMMENT ON COLUMN cf_plan_orders_web.idoforg IS 'Идентификатор целевой организации';
COMMENT ON COLUMN cf_plan_orders_web.idofclient IS 'Идентификатор клиента';
COMMENT ON COLUMN cf_plan_orders_web.groupname IS 'Название группы клиента, на момент создания плана';
COMMENT ON COLUMN cf_plan_orders_web.idofcomplex IS 'Идентификатор комплекса';
COMMENT ON COLUMN cf_plan_orders_web.plantype IS 'Тип плана (1-ЛП, 2-ПП, 4-Распр. порции, 8-Предзаказы, 9-Вода, 10-Сут. проба)';
COMMENT ON COLUMN cf_plan_orders_web.manualoperation IS 'Операция оператора (0-Нет, 1-Блок, 2-Оплата, 3-Отмена)';
COMMENT ON COLUMN cf_plan_orders_web.idofcategorydiscount IS 'Идентификатор категории питания, на основе которой построена запись плана питания';
COMMENT ON COLUMN cf_plan_orders_web.idofdiscountrule IS 'Идентификатор правила, на основе которого построена запись плана питания';
COMMENT ON COLUMN cf_plan_orders_web.idofpreordercomplex IS 'Идентификатор предзаказа';
COMMENT ON COLUMN cf_plan_orders_web.paymentstate IS 'Статус оплаты (0-К оплате, 1-Оплачивается, 2-Оплачено, 3-Ошибка оплаты, 4-К отмене, 5-Отменяется, 6-Отменен, 7-Ошибка отмены)';
COMMENT ON COLUMN cf_plan_orders_web.idoforder IS 'Идентификатор сформированного заказа';
COMMENT ON COLUMN cf_plan_orders_web.idoforderreservation IS 'Идентификатор сформированного заказа перед регистрацией';
COMMENT ON COLUMN cf_plan_orders_web.idofclientchange IS 'Идентификатор клиента из резерва';
COMMENT ON COLUMN cf_plan_orders_web.idofusermanualoperation IS 'Идентификатор пользователя, выполнившего операцию оператора';
COMMENT ON COLUMN cf_plan_orders_web.idofuserconfirmtopay IS 'Идентификатор пользователя, подтвердившего оплату (для плана ПП)';
COMMENT ON COLUMN cf_plan_orders_web.idofuserpay IS 'Идентификатор пользователя, выполнившего оплату';
COMMENT ON COLUMN cf_plan_orders_web."version" IS 'Идентификатор версии записи';
COMMENT ON COLUMN cf_plan_orders_web.createdate IS 'Дата создания';
COMMENT ON COLUMN cf_plan_orders_web.lastupdate IS 'Дата последнего изменения';
COMMENT ON COLUMN cf_plan_orders_web.paydatetime IS 'Дата и время оплаты';
COMMENT ON COLUMN cf_plan_orders_web.qty IS 'Количество комплекса, шт';
COMMENT ON COLUMN cf_plan_orders_web.paymenterror IS 'Текст ошибки при оплате/отмене';
COMMENT ON COLUMN cf_plan_orders_web.specialstate IS 'Специальный статус записи (0-Резерв, 1-Исключен из плана, 2-Ограничение на данный комплекс, 3-Распр. порция с отриц. значением, 4-Ограничение ЕМИАС)';

CREATE INDEX "cf_plan_orders_web_idoforg_idx" ON "cf_plan_orders_web" USING btree ("idoforg","plandate", "plantype");

ALTER TABLE cf_users
    ADD COLUMN createddate bigint DEFAULT null;

CREATE TABLE cf_blazons
(
    idofblazon bigserial NOT NULL,
    imagedata bytea NOT NULL,
    lastupdate bigint,
    CONSTRAINT cf_blazons_pkey PRIMARY KEY (idofblazon)
)
    WITH (
        OIDS=FALSE
    );

ALTER TABLE cf_orgs
    ADD COLUMN idofblazon bigint;
ALTER TABLE cf_orgs
    ADD FOREIGN KEY (idofblazon) REFERENCES cf_blazons (idofblazon) ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE "cf_groupnames_to_orgs" ADD COLUMN "disablefromplan" BOOLEAN DEFAULT NULL;
COMMENT ON COLUMN cf_groupnames_to_orgs.disablefromplan IS 'Флаг исключения из плана питания ЛП';

alter table cf_taloon_approval add column lastchangedatetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
alter table cf_taloon_preorder add column lastchangedatetime TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

CREATE FUNCTION update_talon_lastchangedatetime() RETURNS TRIGGER AS $$ BEGIN NEW.lastchangedatetime = now(); RETURN NEW; END; $$ language 'plpgsql';
create trigger updatelastchangedatetime before update on cf_taloon_approval for each row execute procedure update_talon_lastchangedatetime();
create trigger updatelastchangedatetime before update on cf_taloon_preorder for each row execute procedure update_talon_lastchangedatetime();

ALTER TABLE cf_specialdates ADD COLUMN idofuser bigint DEFAULT null;
ALTER TABLE cf_specialdates_history ADD COLUMN idofuser bigint DEFAULT null;

ALTER TABLE cf_etp_outgoing_message
    ALTER COLUMN etpmessageid SET DATA TYPE varchar(32);

--! ФИНАЛИЗИРОВАН 01.12.2021, НЕ МЕНЯТЬ