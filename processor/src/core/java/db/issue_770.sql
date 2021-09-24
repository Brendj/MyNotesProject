/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

--770
CREATE SEQUENCE public.cf_guardian_history_id
    INCREMENT BY 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    START 1;

CREATE TABLE public.cf_client_guardian_history (
                                                   ifofclientguardianhistory int8 DEFAULT nextval('cf_guardian_history_id') NOT NULL,
                                                   idofclientguardian int8 NULL,
                                                   createdate int8 NULL,
                                                   "action" varchar NULL,
                                                   webadress varchar NULL,
                                                   createdfrom int4 NULL,
                                                   guardian varchar NULL,
                                                   idoforg int8 NULL,
                                                   idofpacket int8 NULL,
                                                   idofuser int8 NULL,
                                                   changeparam varchar NULL,
                                                   oldvalue varchar NULL,
                                                   newvalue varchar NULL,
                                                   reason varchar NULL
);
