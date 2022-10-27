-- Пакет обновлений v 294

INSERT INTO CF_Options(IdOfOption, OptionText) VALUES(10025, cast(cast(extract(epoch from now()) * 1000 as bigint) as varchar));

ALTER TABLE cf_client_dtiszn_discount_info
    ADD COLUMN updated_at bigint;
COMMENT ON COLUMN cf_client_dtiszn_discount_info.updated_at
    IS 'Дата последнего изменения записи в системе источнике данных о льготах';

ALTER TABLE cf_orgs ADD COLUMN disableSocCardsReg BOOLEAN NOT NULL DEFAULT TRUE;

--1009
CREATE SEQUENCE public.cf_foodbox_org_lock_seq
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE public.cf_foodbox_org_lock (
   orglockid int8 DEFAULT nextval('cf_foodbox_org_lock_seq') NOT NULL,
   idoforg int8 NULL,
   "version" int8 NULL,
   currentversion int8 NULL,
   CONSTRAINT org_lock_pk PRIMARY KEY (orglockid)
);

COMMENT ON TABLE public.cf_foodbox_org_lock IS 'Таблица синхронизации параллельных заказов фудбокса';
COMMENT ON COLUMN public.cf_foodbox_org_lock.idoforg IS 'Идентификатор организации (ссылка на cf_orgs)';
COMMENT ON COLUMN public.cf_foodbox_org_lock."version" IS 'Номер последнего заказа в очереди на исполнение';
COMMENT ON COLUMN public.cf_foodbox_org_lock.currentversion IS 'Номер последнего исполненного заказа';

--! ФИНАЛИЗИРОВАН 27.10.2022, НЕ МЕНЯТЬ