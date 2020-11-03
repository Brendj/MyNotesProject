/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE TABLE cf_smartwatch_vendor
(
    idofvendor          bigserial primary key,
    name                varchar(128) not null unique,
    apikey              varchar(36)  not null unique,
    enableService       bool         not null default true,
    cardsigncertnum     integer,
    enablePushes        bool         not null default false,
    entereventsEndPoint varchar(512),
    purchasesendpoint   varchar(512),
    paymentendpoint     varchar(512)
);

COMMENT ON TABLE cf_smartwatch_vendor IS 'Поставщики смарт-часов';
COMMENT ON COLUMN cf_smartwatch_vendor.idofvendor IS 'ID';
COMMENT ON COLUMN cf_smartwatch_vendor.name IS 'Название поставщика';
COMMENT ON COLUMN cf_smartwatch_vendor.apikey IS 'Ключ доступа к REST-контроллеру';
COMMENT ON COLUMN cf_smartwatch_vendor.enableService IS 'Флаг разрешение на обслуживание в REST-контроллере';
COMMENT ON COLUMN cf_smartwatch_vendor.cardsigncertnum IS 'Номер сертификата (ключа ЭЦП часов как карты)';
COMMENT ON COLUMN cf_smartwatch_vendor.enablePushes IS 'Разрешение отправлять оповещения';
COMMENT ON COLUMN cf_smartwatch_vendor.entereventsEndPoint IS 'Адрес для отправки проходов';
COMMENT ON COLUMN cf_smartwatch_vendor.purchasesendpoint IS 'Адрес для отправки покупок';
COMMENT ON COLUMN cf_smartwatch_vendor.paymentendpoint IS 'Адрес для отправки пополнения счёта';

ALTER TABLE cf_smartwatchs
    ADD COLUMN idofvendor integer references cf_smartwatch_vendor(idofvendor) on delete set null;

ALTER TABLE cf_clients
    ADD COLUMN idofvendor integer references cf_smartwatch_vendor(idofvendor) on delete set null;

alter table cf_clients
add column idOfVendor bigint references cf_smartwatch_vendor(idofvendor) on delete set null;

alter table cf_geoplaner_notifications_journal
    add column idOfVendor bigint references cf_smartwatch_vendor(idofvendor);