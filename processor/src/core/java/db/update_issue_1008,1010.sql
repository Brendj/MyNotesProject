/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

--1008
CREATE SEQUENCE cf_emias_days_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE cf_emias_days (
                               idofemiasday int8 DEFAULT nextval('cf_emias_days_id') NOT NULL,
                               idOfClient int8 NULL,
                               "date" int8 NULL,
                               eat bool null,
                               "version" int8 null,
                               createDate int8 null,
                               updateDate int8 null,
                               idOfOrg int8 NULL
);

CREATE INDEX cf_emias_days_idx
    ON cf_emias_days
        USING btree
        (idOfClient);

CREATE INDEX cf_emias_days_version_idx
    ON cf_emias_days
        USING btree
        ("version");

CREATE INDEX cf_emias_days_org_idx
    ON cf_emias_days
        USING btree
        (idOfOrg);

--1010
ALTER TABLE cf_emias ADD accepteddatetime int8 NULL;