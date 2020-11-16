/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

CREATE TABLE cf_code_msp(
                            idofcode bigserial primary key,
                            code INTEGER not null unique,
                            description varchar(512) not null
);