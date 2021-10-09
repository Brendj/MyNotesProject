/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

--! Пока скрипт не финализирован рекомендовано писать очистку добавляемых колонок таблиц.
--! после финализации они уберутся
--! Информация для разработчика -- информация для пользователя

-- Пакет обновлений 246

-- 775/786
CREATE TABLE cf_code_msp
(
    idofcode             bigserial primary key,
    code                 INTEGER      not null unique,
    description          varchar(512) not null,
    idofcategorydiscount bigint       not null references cf_categorydiscounts (idofcategorydiscount) on update no action on delete cascade
);

alter table CF_DiscountRules
    add column idofcode bigint references cf_code_msp on delete set null on update no action;

alter table CF_wt_DiscountRules
    add column idofcode bigint references cf_code_msp on delete set null on update no action;

create table cf_code_msp_agetypegroup
(
    idofcodemspagetypegroup bigserial primary key,
    idofcode                bigint references cf_code_msp (idofcode) on delete cascade,
    agetypegroup            varchar(128) not null check (agetypegroup not similar to ' *')
);

-- 815
ALTER TABLE CF_feeding_settings ADD COLUMN discountAmount bigint,
                                ADD COLUMN useDiscount integer NOT NULL DEFAULT 0,
                                ALTER COLUMN limitAmount DROP NOT NULL;

--! ФИНАЛИЗИРОВАН 25.12.2020, НЕ МЕНЯТЬ