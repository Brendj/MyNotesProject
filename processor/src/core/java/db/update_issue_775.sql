/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE TABLE cf_code_msp
(
    idofcode    bigserial primary key,
    code        INTEGER      not null unique,
    description varchar(512) not null
);

alter table CF_DiscountRules
    add column idofcode bigint references cf_code_msp on delete set null on update no action;

alter table CF_wt_DiscountRules
    add column idofcode bigint references cf_code_msp on delete set null on update no action;