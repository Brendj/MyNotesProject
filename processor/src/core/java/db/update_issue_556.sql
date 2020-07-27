CREATE TABLE public.cf_refresh_token
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
)
TABLESPACE pg_default;

ALTER TABLE public.cf_refresh_token
    OWNER to postgres;